package com.campus.common.constant;

/**
 * 系统全局常量
 */
public final class Constants {

    private Constants() {}

    // ──────────── Header ────────────

    /** 认证请求头 */
    public static final String AUTH_HEADER = "Authorization";
    /** Token 前缀 */
    public static final String TOKEN_PREFIX = "Bearer ";
    /** 网关转发时携带的用户ID请求头 */
    public static final String HEADER_USER_ID = "X-User-Id";
    /** 网关转发时携带的用户角色请求头 */
    public static final String HEADER_USER_ROLE = "X-User-Role";
    /** 网关转发时携带的用户名请求头 */
    public static final String HEADER_USERNAME = "X-Username";
    /** 网关转发时携带的用户昵称请求头 */
    public static final String HEADER_NICKNAME = "X-Nickname";

    // ──────────── 角色 ────────────

    public static final String ROLE_STUDENT = "student";
    public static final String ROLE_TEACHER = "teacher";
    public static final String ROLE_ADMIN   = "admin";

    // ──────────── 帖子板块 ────────────

    public static final String BOARD_STUDY = "study";
    public static final String BOARD_JOB   = "job";
    public static final String BOARD_LIFE  = "life";
    public static final String BOARD_TECH  = "tech";
    public static final String BOARD_OTHER = "other";

    // ──────────── 失物招领 ────────────

    public static final String LOST_TYPE_LOST  = "lost";
    public static final String LOST_TYPE_FOUND = "found";

    // ──────────── 工单 ────────────

    public static final String COMPLAINT_STATUS_PENDING    = "pending";
    public static final String COMPLAINT_STATUS_PROCESSING = "processing";
    public static final String COMPLAINT_STATUS_DONE       = "done";
    public static final String COMPLAINT_STATUS_REJECTED   = "rejected";

    // ──────────── Redis Key ────────────

    /** 帖子浏览量 */
    public static final String REDIS_POST_VIEW = "post:view:";
    /** 帖子点赞集合 */
    public static final String REDIS_POST_LIKE = "post:like:";
    /** 用户收藏集合 */
    public static final String REDIS_USER_FAV = "user:fav:";
}
