package com.campus.forum.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.common.exception.BizException;
import com.campus.forum.constant.RedisKeys;
import com.campus.forum.entity.Post;
import com.campus.forum.mapper.CommentMapper;
import com.campus.forum.mapper.PostMapper;
import com.campus.forum.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 帖子 Service — Redis Set 实现点赞/收藏
 *
 * <pre>
 * Redis 数据结构:
 *   post:like:{postId}  → Set(userId)   点赞用户集合
 *   post:fav:{postId}   → Set(userId)   收藏用户集合
 *
 * 同步策略:
 *   定时任务每5分钟将 Redis 计数刷回 MySQL (t_post.like_count / t_post_like / t_post_favorite)
 *   保证最终一致性，Redis 挂了最多丢 5 分钟的点赞数据
 * </pre>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    private final CommentMapper commentMapper;
    private final StringRedisTemplate redis;

    // ──────────── 查询 ────────────

    @Override
    public Page<Post> pageList(int pageNum, int pageSize, String board, String keyword) {
        Page<Post> result = page(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<Post>()
                        .eq(Post::getStatus, 1)
                        .eq(board != null && !board.isBlank(), Post::getBoard, board)
                        .and(keyword != null && !keyword.isBlank(), q -> q
                                .like(Post::getTitle, keyword).or().like(Post::getContent, keyword))
                        .orderByDesc(Post::getIsPinned)
                        .orderByDesc(Post::getCreateTime));

        // 从 Redis 实时补齐点赞/收藏数
        result.getRecords().forEach(this::fillRedisCounts);

        return result;
    }

    @Override
    public Post getDetail(Long id) {
        Post post = getById(id);
        if (post == null || post.getStatus() == 0) {
            throw new BizException(404, "帖子不存在或已删除");
        }
        // 浏览量 Redis 计数
        redis.opsForValue().increment(RedisKeys.viewKey(id));
        post.setViewCount(post.getViewCount() + 1);
        updateById(post);

        fillRedisCounts(post);
        return post;
    }

    // ──────────── 写入 ────────────

    @Override
    public void createPost(Post post, Long authorId, String authorName) {
        post.setAuthorId(authorId);
        post.setAuthorName(authorName);
        post.setViewCount(0);
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setStatus(1);
        save(post);
    }

    @Override
    @Transactional
    public void deletePost(Long postId, Long userId, String role) {
        Post post = getById(postId);
        if (post == null) {
            throw new BizException(404, "帖子不存在");
        }
        if (!post.getAuthorId().equals(userId) && !"admin".equals(role)) {
            throw new BizException(403, "只能删除自己的帖子");
        }
        post.setStatus(0);
        updateById(post);
    }

    // ──────────── 点赞（Redis Set）────────────

    @Override
    public int toggleLike(Long postId, Long userId) {
        Post post = getById(postId);
        if (post == null || post.getStatus() == 0) {
            throw new BizException(404, "帖子不存在或已删除");
        }

        String key = RedisKeys.likeKey(postId);
        String member = userId.toString();

        // SISMEMBER 判断是否已点赞
        Boolean isMember = redis.opsForSet().isMember(key, member);

        if (Boolean.TRUE.equals(isMember)) {
            // 已点赞 → 取消
            redis.opsForSet().remove(key, member);
        } else {
            // 未点赞 → 点赞
            redis.opsForSet().add(key, member);
        }

        // 返回当前点赞数
        Long count = redis.opsForSet().size(key);
        int likeCount = count != null ? count.intValue() : 0;

        // 立即刷新 DB 计数（保证列表展示准确）
        post.setLikeCount(likeCount);
        updateById(post);

        return likeCount;
    }

    @Override
    public boolean isLiked(Long postId, Long userId) {
        String key = RedisKeys.likeKey(postId);
        return Boolean.TRUE.equals(redis.opsForSet().isMember(key, userId.toString()));
    }

    /**
     * 批量查询当前用户对多个帖子的点赞状态
     */
    public List<Long> filterLikedPostIds(List<Long> postIds, Long userId) {
        if (postIds == null || postIds.isEmpty()) return Collections.emptyList();
        return postIds.stream()
                .filter(pid -> Boolean.TRUE.equals(
                        redis.opsForSet().isMember(RedisKeys.likeKey(pid), userId.toString())))
                .collect(Collectors.toList());
    }

    // ──────────── 收藏（Redis Set）────────────

    @Override
    public boolean toggleFavorite(Long postId, Long userId) {
        Post post = getById(postId);
        if (post == null || post.getStatus() == 0) {
            throw new BizException(404, "帖子不存在或已删除");
        }

        String key = RedisKeys.favKey(postId);
        String member = userId.toString();
        Boolean isMember = redis.opsForSet().isMember(key, member);

        if (Boolean.TRUE.equals(isMember)) {
            redis.opsForSet().remove(key, member);
            return false;   // 已取消收藏
        } else {
            redis.opsForSet().add(key, member);
            return true;    // 已收藏
        }
    }

    @Override
    public boolean isFavorited(Long postId, Long userId) {
        String key = RedisKeys.favKey(postId);
        return Boolean.TRUE.equals(redis.opsForSet().isMember(key, userId.toString()));
    }

    // ──────────── 辅助 ────────────

    /**
     * 用 Redis 实时计数覆盖 DB 中的 like_count（前端列表展示用）
     */
    private void fillRedisCounts(Post post) {
        Long likeCount = redis.opsForSet().size(RedisKeys.likeKey(post.getId()));
        if (likeCount != null && likeCount > 0) {
            post.setLikeCount(likeCount.intValue());
        }
    }

    // ──────────── 热门帖子 ────────────

    @Override
    public List<Post> getHotPosts(int limit) {
        // 热度 = 浏览量*1 + 点赞数*3 + 回复数*5（简单加权）
        List<Post> all = lambdaQuery()
                .eq(Post::getStatus, 1)
                .orderByDesc(Post::getCreateTime)
                .list();
        // 补齐 Redis 计数后排序
        all.forEach(this::fillRedisCounts);
        all.sort((a, b) -> {
            int scoreA = a.getViewCount() * 1 + a.getLikeCount() * 3 + a.getCommentCount() * 5;
            int scoreB = b.getViewCount() * 1 + b.getLikeCount() * 3 + b.getCommentCount() * 5;
            return Integer.compare(scoreB, scoreA);
        });
        return all.stream().limit(limit).collect(Collectors.toList());
    }

    @Override
    public Page<Post> myPosts(int pageNum, int pageSize, Long userId) {
        Page<Post> result = page(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<Post>()
                        .eq(Post::getAuthorId, userId)
                        .eq(Post::getStatus, 1)
                        .orderByDesc(Post::getCreateTime));
        result.getRecords().forEach(this::fillRedisCounts);
        return result;
    }

    // ──────────── 供 SyncTask 调用的同步方法 ────────────

    /**
     * 获取所有需要同步的帖子ID集合（扫描 Redis 中的 like 和 fav key）
     */
    public Set<String> getAllPostKeys() {
        Set<String> keys = redis.keys(RedisKeys.POST_LIKE + "*");
        return keys != null ? keys : Collections.emptySet();
    }

    /**
     * 将单条帖子的 Redis 计数同步到 MySQL
     */
    public void syncPostCounts(Long postId) {
        String likeKey = RedisKeys.likeKey(postId);
        Long likeCount = redis.opsForSet().size(likeKey);

        Post post = getById(postId);
        if (post != null && likeCount != null) {
            post.setLikeCount(likeCount.intValue());
            updateById(post);
        }
    }
@Override    public java.util.List<Post> getMyFavorites(Long userId) {        java.util.Set<String> keys = redis.keys("post:fav:*");        if (keys == null || keys.isEmpty()) return java.util.List.of();        java.util.List<Long> ids = new java.util.ArrayList<>();        for (String k : keys) {            if (Boolean.TRUE.equals(redis.opsForSet().isMember(k, userId.toString()))) {                try { ids.add(Long.parseLong(k.replace("post:fav:", ""))); } catch (Exception ignored) {}            }        }        if (ids.isEmpty()) return java.util.List.of();        java.util.List<Post> posts = listByIds(ids);        posts.forEach(this::fillRedisCounts);        return posts;    }
}
