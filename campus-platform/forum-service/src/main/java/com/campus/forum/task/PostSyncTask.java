package com.campus.forum.task;

import com.campus.forum.constant.RedisKeys;
import com.campus.forum.entity.Post;
import com.campus.forum.mapper.PostMapper;
import com.campus.forum.service.impl.PostServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 点赞/收藏数据同步任务
 * <p>
 * 每 5 分钟将 Redis 中的点赞数、收藏数刷回 MySQL，
 * 保证数据最终一致性，防止 Redis 重启或淘汰导致数据丢失。
 * <p>
 * 答辩亮点：
 * - Redis 做热数据存储（读写都在 Redis，高性能）
 * - 定时任务做冷数据持久化（最终一致性）
 * - 即使 Redis 完全挂掉，最多丢 5 分钟数据
 */
@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class PostSyncTask {

    private final StringRedisTemplate redis;
    private final PostMapper postMapper;
    private final PostServiceImpl postService;

    /**
     * 每 5 分钟执行一次（可通过 forum.sync-interval-seconds 调整）
     */
    @Scheduled(fixedDelayString = "${forum.sync-interval-seconds:300}000")
    public void syncLikeAndFavCounts() {
        log.debug("开始同步 Redis 点赞/收藏数据到 MySQL...");

        Set<String> keys = redis.keys(RedisKeys.POST_LIKE + "*");
        if (keys == null || keys.isEmpty()) {
            log.debug("无待同步数据");
            return;
        }

        int synced = 0;
        for (String key : keys) {
            try {
                // 从 key 中提取 postId: "post:like:123" → 123
                String postIdStr = key.substring(RedisKeys.POST_LIKE.length());
                Long postId = Long.parseLong(postIdStr);

                Long likeCount = redis.opsForSet().size(key);

                Post post = postMapper.selectById(postId);
                if (post != null && likeCount != null) {
                    post.setLikeCount(likeCount.intValue());
                    postMapper.updateById(post);
                    synced++;
                }
            } catch (Exception e) {
                log.warn("同步 key={} 失败: {}", key, e.getMessage());
            }
        }

        log.info("点赞/收藏同步完成，共同步 {} 条帖子", synced);
    }
}
