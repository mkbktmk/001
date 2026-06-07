package com.campus.user.service;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 简易图形验证码 — 内存存储，5 分钟过期
 */
@Service
public class CaptchaService {

    private static final long TTL_MS = 5 * 60 * 1000;
    private final Map<String, CaptchaEntry> store = new ConcurrentHashMap<>();

    /** 验证码结果 */
    public record CaptchaResult(String key, String base64Image) {}

    /** 生成验证码 */
    public CaptchaResult generate() {
        // 清理过期条目
        long now = System.currentTimeMillis();
        store.entrySet().removeIf(e -> e.getValue().expireAt < now);

        String code = randomCode(4);
        String key = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        store.put(key, new CaptchaEntry(code, now + TTL_MS));

        String img = generateImage(code);
        return new CaptchaResult(key, img);
    }

    /** 校验验证码（校验后删除，防止重复使用） */
    public boolean verify(String key, String code) {
        CaptchaEntry entry = store.remove(key);
        if (entry == null) return false;
        if (System.currentTimeMillis() > entry.expireAt) return false;
        return entry.code.equalsIgnoreCase(code);
    }

    // ──── 内部工具 ────

    private record CaptchaEntry(String code, long expireAt) {}

    private String randomCode(int len) {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return sb.toString();
    }

    private String generateImage(String code) {
        int w = 120, h = 44;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();

        // 背景
        g.setColor(new Color(240, 240, 240));
        g.fillRect(0, 0, w, h);

        // 干扰线
        g.setColor(new Color(180, 180, 180));
        for (int i = 0; i < 4; i++) {
            int x1 = (int) (Math.random() * w);
            int y1 = (int) (Math.random() * h);
            int x2 = (int) (Math.random() * w);
            int y2 = (int) (Math.random() * h);
            g.drawLine(x1, y1, x2, y2);
        }

        // 文字
        g.setColor(new Color(50, 50, 50));
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (int i = 0; i < code.length(); i++) {
            int x = 10 + i * 25 + (int) (Math.random() * 6);
            int y = 30 + (int) (Math.random() * 8 - 4);
            g.drawString(String.valueOf(code.charAt(i)), x, y);
        }
        g.dispose();

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ImageIO.write(img, "png", bos);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(bos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("生成验证码失败", e);
        }
    }
}
