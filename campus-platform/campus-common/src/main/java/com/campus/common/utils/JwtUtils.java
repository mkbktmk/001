package com.campus.common.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

public class JwtUtils {
    private static String SECRET = "Y2FtcHVzLXBsYXRmb3JtLWRlZmF1bHQtc2VjcmV0LWtleS0yMDI0LW1pbi1sZW5ndGgtMzJieXRlcys=";
    private static long EXPIRE_MS = 7 * 24 * 60 * 60 * 1000L;

    public static void init(String secret, long expireMs) {
        if (secret != null && !secret.isBlank()) SECRET = secret;
        if (expireMs > 0) EXPIRE_MS = expireMs;
    }
    private static SecretKey getKey() { byte[] kb = io.jsonwebtoken.io.Decoders.BASE64.decode(SECRET); return Keys.hmacShaKeyFor(kb); }

    public static String generateToken(Long userId, String username, String role) {
        return generateToken(userId, username, username, role);
    }
    public static String generateToken(Long userId, String username, String nickname, String role) {
        return Jwts.builder().subject(String.valueOf(userId))
                .claim("username", username).claim("nickname", nickname != null ? nickname : username)
                .claim("role", role).claim("loginTime", System.currentTimeMillis())
                .issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + EXPIRE_MS))
                .signWith(getKey()).compact();
    }
    public static Claims parseToken(String token) {
        try { return Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload(); }
        catch (JwtException e) { return null; }
    }
    public static boolean isValid(String token) { return parseToken(token) != null; }
    public static Long getUserId(String token) { Claims c = parseToken(token); return c != null ? Long.valueOf(c.getSubject()) : null; }
}
