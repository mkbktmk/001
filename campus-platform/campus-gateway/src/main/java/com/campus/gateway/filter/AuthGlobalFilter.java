package com.campus.gateway.filter;

import com.campus.common.constant.Constants;
import com.campus.common.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 全局鉴权过滤器
 * <p>
 * 拦截所有请求，校验 JWT Token，将用户信息写入转发请求头。
 * 白名单路径直接放行。
 */
@Slf4j
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    /** 白名单：无需登录即可访问 */
    private static final List<String> WHITE_LIST = List.of(
            "/auth/login",
            "/auth/register",
            "/auth/captcha",
            "/news/list",
            "/news/detail",
            "/second-hand/list",
            "/second-hand/detail",
            "/lost-found/list",
            "/lost-found/detail",
            "/post/list",
            "/post/detail",
            "/post/hot",
            "/forum/list",
            "/forum/detail",
            "/uploads",           // 上传的图片
            "/doc.html",          // Knife4j 文档
            "/v3/api-docs",
            "/webjars",
            "/favicon.ico"
    );

    /** 教师及以上可操作 */
    private static final List<String> TEACHER_PATHS = List.of(
            "/news/publish"
    );

    /** 管理员专属 */
    private static final List<String> ADMIN_PATHS = List.of(
            "/user/admin",
            "/complaint/admin"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // ── 1. 白名单放行 ──
        if (isWhitePath(path)) {
            return chain.filter(exchange);
        }

        // ── 2. 提取 Token ──
        String token = extractToken(request);
        if (token == null || token.isBlank()) {
            return unauthorized(exchange, "未登录，请先登录");
        }

        // ── 3. 校验 Token ──
        Claims claims = JwtUtils.parseToken(token);
        if (claims == null) {
            return unauthorized(exchange, "登录已过期，请重新登录");
        }

        // ── 4. 角色权限校验 ──
        String userId = claims.getSubject();
        String username = claims.get("username", String.class);
        String nickname = claims.get("nickname", String.class);
        // URL 编码避免 HTTP Header 中文乱码
        String encodedNickname = URLEncoder.encode(nickname != null ? nickname : username, StandardCharsets.UTF_8);
        String role = claims.get("role", String.class);

        if (isAdminPath(path) && !"admin".equals(role)) {
            return forbidden(exchange, "仅管理员可操作");
        }
        if (isTeacherPath(path) && !"admin".equals(role) && !"teacher".equals(role)) {
            return forbidden(exchange, "仅教师/管理员可操作");
        }

        // ── 5. 将用户信息写入请求头，转发给下游微服务 ──
        ServerHttpRequest mutatedRequest = request.mutate()
                .header(Constants.HEADER_USER_ID, userId)
                .header(Constants.HEADER_USERNAME, username)
                .header(Constants.HEADER_NICKNAME, encodedNickname)
                .header(Constants.HEADER_USER_ROLE, role)
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    @Override
    public int getOrder() {
        return -100; // 优先级最高
    }

    // ──────────── 辅助方法 ────────────

    private boolean isWhitePath(String path) {
        return WHITE_LIST.stream().anyMatch(path::startsWith);
    }

    private boolean isTeacherPath(String path) {
        return TEACHER_PATHS.stream().anyMatch(path::startsWith);
    }

    private boolean isAdminPath(String path) {
        return ADMIN_PATHS.stream().anyMatch(path::startsWith);
    }

    private Mono<Void> forbidden(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = String.format("{\"code\":403,\"message\":\"%s\",\"data\":null}", message);
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    private String extractToken(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith(Constants.TOKEN_PREFIX)) {
            return authHeader.substring(Constants.TOKEN_PREFIX.length());
        }
        return null;
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = String.format("{\"code\":401,\"message\":\"%s\",\"data\":null}", message);
        DataBuffer buffer = response.bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}
