package com.campus.forum.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.forum.entity.Post;

public interface PostService extends IService<Post> {

    /** 分页查询帖子列表 */
    Page<Post> pageList(int pageNum, int pageSize, String board, String keyword);

    /** 查看帖子详情（浏览量+1） */
    Post getDetail(Long id);

    /** 发布帖子 */
    void createPost(Post post, Long authorId, String authorName);

    /** 删除帖子 */
    void deletePost(Long postId, Long userId, String role);

    /** 点赞/取消点赞，返回当前点赞数 */
    int toggleLike(Long postId, Long userId);

    /** 收藏/取消收藏 */
    boolean toggleFavorite(Long postId, Long userId);

    /** 检查用户是否已点赞 */
    boolean isLiked(Long postId, Long userId);

    /** 检查用户是否已收藏 */
    boolean isFavorited(Long postId, Long userId);

    /** 获取热门帖子（基于浏览+点赞+回复热度算法） */
    java.util.List<Post> getHotPosts(int limit);

    /** 查询用户自己的帖子 */
    Page<Post> myPosts(int pageNum, int pageSize, Long userId);
    java.util.List<Post> getMyFavorites(Long userId);
}
