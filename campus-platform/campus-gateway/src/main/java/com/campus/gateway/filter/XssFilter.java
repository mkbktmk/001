package com.campus.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * 简易 XSS 过滤器 — 过滤请求参数中的危险字符
 */
@Slf4j
@Component
public class XssFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        URI uri = request.getURI();
        String rawQuery = uri.getRawQuery();

        if (rawQuery != null && containsXss(rawQuery)) {
            log.warn("XSS attempt blocked: {}", rawQuery);
            exchange.getResponse().setRawStatusCode(400);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -200; // 在鉴权之前执行
    }

    private boolean containsXss(String value) {
        if (value == null) return false;
        // 检测常见 XSS 模式
        String lower = value.toLowerCase();
        return lower.contains("<script") || lower.contains("javascript:")
                || lower.contains("onerror=") || lower.contains("onload=")
                || lower.contains("<iframe") || lower.contains("<object")
                || lower.contains("<embed") || lower.contains("<link");
    }
}
