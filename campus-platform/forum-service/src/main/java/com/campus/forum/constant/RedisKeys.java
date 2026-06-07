package com.campus.forum.constant;

/**
 * Redis Key 常量
 */
public final class RedisKeys {

    private RedisKeys() {}

    /** 帖子点赞集合 — Set<userId> */
    public static final String POST_LIKE = "post:like:";

    /** 帖子收藏集合 — Set<userId> */
    public static final String POST_FAV = "post:fav:";

    /** 帖子浏览量（String，用于防刷/定时同步） */
    public static final String POST_VIEW = "post:view:";

    public static String likeKey(Long postId) {
        return POST_LIKE + postId;
    }

    public static String favKey(Long postId) {
        return POST_FAV + postId;
    }

    public static String viewKey(Long postId) {
        return POST_VIEW + postId;
    }
}
