package com.campus.common.exception;

import lombok.Getter;

/**
 * 业务异常 — 统一异常类
 * <p>
 * 用法：
 * <pre>{@code
 * if (user == null) {
 *     throw new BizException("用户不存在");
 * }
 * throw new BizException(400, "参数错误");
 * }</pre>
 */
@Getter
public class BizException extends RuntimeException {

    private final int code;

    public BizException(String message) {
        super(message);
        this.code = 500;
    }

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
        this.code = 500;
    }

    public BizException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}
