package com.campus.common.exception;

import com.campus.common.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器（仅 Servlet 环境生效，Gateway 的 WebFlux 不会加载）
 */
@Slf4j
@RestControllerAdvice
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class GlobalExceptionHandler {

    /**
     * 业务异常 — 返回对应状态码和提示信息
     */
    @ExceptionHandler(BizException.class)
    public Result<Void> handleBizException(BizException e, HttpServletRequest request) {
        log.warn("业务异常 [{}] {} — {}", e.getCode(), request.getRequestURI(), e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

    /**
     * 参数校验失败（Spring Validation）
     */
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(org.springframework.web.bind.MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("参数校验失败");
        log.warn("参数校验失败: {}", msg);
        return Result.badRequest(msg);
    }

    /**
     * 兜底异常 — 返回 500
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常 [{}] {}", request.getRequestURI(), e.getMessage(), e);
        return Result.fail("服务器内部错误，请稍后重试");
    }
}
