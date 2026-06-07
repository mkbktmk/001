package com.campus.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * TraceId 过滤器 — 为每个请求生成唯一 ID，贯穿全链路
 */
@Slf4j
@Component
public class TraceIdFilter implements GlobalFilter, Ordered {

    public static final String TRACE_ID_HEADER = "X-Trace-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 优先使用上游传入的 traceId，否则生成新的
        String traceId = exchange.getRequest().getHeaders().getFirst(TRACE_ID_HEADER);
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        }

        // 注入到下游请求头
        ServerHttpRequest mutated = exchange.getRequest().mutate()
                .header(TRACE_ID_HEADER, traceId)
                .build();

        // 日志记录
        String path = exchange.getRequest().getURI().getPath();
        log.info("[{}] {} {}", traceId, exchange.getRequest().getMethod(), path);

        return chain.filter(exchange.mutate().request(mutated).build());
    }

    @Override
    public int getOrder() {
        return -300; // 最先执行
    }
}
