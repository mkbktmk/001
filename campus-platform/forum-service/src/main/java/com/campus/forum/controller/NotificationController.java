package com.campus.forum.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.constant.Constants;
import com.campus.common.result.Result;
import com.campus.forum.entity.Notification;
import com.campus.forum.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "通知接口")
@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "通知列表")
    @GetMapping("/list")
    public Result<Page<Notification>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        return Result.ok(notificationService.listByUser(page, size, userId));
    }

    @Operation(summary = "未读数量")
    @GetMapping("/unread")
    public Result<Map<String, Integer>> unread(
            @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        return Result.ok(Map.of("count", notificationService.unreadCount(userId)));
    }

    @Operation(summary = "标记已读")
    @PutMapping("/read/{id}")
    public Result<Void> read(
            @PathVariable Long id,
            @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        notificationService.markRead(id, userId);
        return Result.ok(null);
    }
}
