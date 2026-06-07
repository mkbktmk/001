package com.campus.common.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

/**
 * JWT 工具类
 * <p>
 * 提供 Token 生成、解析、校验等常用方法。
 * 密钥可通过 Nacos 配置中心动态下发。
 */
public class JwtUtils {

    /** 默认密钥（Base64 编码，至少48字节 → HS384） */
    private static String SECRET = "Y2FtcHVzLXBsYXRmb3JtLWRlZmF1bHQtc2VjcmV0LWtleS0yMDI0LW1pbi1sZW5ndGgtMzJieXRlcys=";

    /** Token 默认过期时间：7天（毫秒） */
    private static long EXPIRE_MS = 7 * 24 * 60 * 60 * 1000L;

    /**
     * 初始化密钥和过期时间（由 Nacos 配置回调或启动时调用）
     */
    public static void init(String secret, long expireMs) {
        if (secret != null && !secret.isBlank()) {
            SECRET = secret;
        }
        if (expireMs > 0) {
            EXPIRE_MS = expireMs;
        }
    }

    private static SecretKey getKey() {
        byte[] keyBytes = io.jsonwebtoken.io.Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // ──────────── 生成 Token ────────────

    /**
     * 生成 JWT Token
     * @param userId   用户ID
     * @param username 用户名
     * @param role     角色
     */
    /** 兼容旧调用（无昵称） */
    public static String generateToken(Long userId, String username, String role) {
        return generateToken(userId, username, username, role);
    }

    public static String generateToken(Long userId, String username, String nickname, String role) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .claim("nickname", nickname != null ? nickname : username)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRE_MS))
                .signWith(getKey())
                .compact();
    }

    /**
     * 生成带自定义 Claims 的 Token
     */
    public static String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRE_MS))
                .signWith(getKey())
                .compact();
    }

    // ──────────── 解析 Token ────────────

    /**
     * 解析 Token，返回 Claims。解析失败返回 null。
     */
    public static Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            return null;
        }
    }

    /**
     * 校验 Token 是否有效
     */
    public static boolean isValid(String token) {
        return parseToken(token) != null;
    }

    // ──────────── 快捷取值 ────────────

    public static Long getUserId(String token) {
        Claims claims = parseToken(token);
        if (claims == null) return null;
        return Long.valueOf(claims.getSubject());
    }

    public static String getUsername(String token) {
        Claims claims = parseToken(token);
        if (claims == null) return null;
        return claims.get("username", String.class);
    }

    public static String getRole(String token) {
        Claims claims = parseToken(token);
        if (claims == null) return null;
        return claims.get("role", String.class);
    }
}
