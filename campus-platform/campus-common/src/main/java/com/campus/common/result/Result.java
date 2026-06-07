package com.campus.common.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一响应体
 * <p>
 * 所有接口统一返回此格式：
 * <pre>
 * {
 *   "code": 200,
 *   "message": "success",
 *   "data": {...}
 * }
 * </pre>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    private int code;
    private String message;
    private T data;

    // ──────────── 成功响应 ────────────

    public static <T> Result<T> ok() {
        return new Result<>(200, "success", null);
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(200, "success", data);
    }

    public static <T> Result<T> ok(String message, T data) {
        return new Result<>(200, message, data);
    }

    // ──────────── 失败响应 ────────────

    public static <T> Result<T> fail(String message) {
        return new Result<>(500, message, null);
    }

    public static <T> Result<T> fail(int code, String message) {
        return new Result<>(code, message, null);
    }

    // ──────────── 常用状态码快捷方法 ────────────

    public static <T> Result<T> badRequest(String message) {
        return new Result<>(400, message, null);
    }

    public static <T> Result<T> unauthorized(String message) {
        return new Result<>(401, message != null ? message : "未登录或登录已过期", null);
    }

    public static <T> Result<T> forbidden(String message) {
        return new Result<>(403, message != null ? message : "无权限访问", null);
    }

    public static <T> Result<T> notFound(String message) {
        return new Result<>(404, message, null);
    }
}
