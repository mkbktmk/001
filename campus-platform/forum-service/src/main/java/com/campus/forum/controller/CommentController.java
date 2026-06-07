package com.campus.forum.controller;

import com.campus.common.constant.Constants;
import com.campus.common.result.Result;
import com.campus.forum.entity.Comment;
import com.campus.forum.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Tag(name = "回复接口")
@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public Result<Comment> reply(@RequestParam Long postId, @RequestParam(required = false) Long parentId,
            @RequestParam String content, @RequestHeader(Constants.HEADER_USER_ID) Long userId,
            @RequestHeader(value = Constants.HEADER_NICKNAME, defaultValue = "") String nickname,
            @RequestHeader(Constants.HEADER_USERNAME) String username) {
        String dn = !nickname.isBlank() ? URLDecoder.decode(nickname, StandardCharsets.UTF_8) : username;
        return Result.ok(commentService.reply(postId, parentId, content, userId, dn));
    }

    @GetMapping("/list/{postId}")
    public Result<List<Comment>> list(@PathVariable Long postId) {
        return Result.ok(commentService.getCommentsByPostId(postId));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, @RequestHeader(Constants.HEADER_USER_ID) Long userId,
            @RequestHeader(Constants.HEADER_USER_ROLE) String role) {
        commentService.deleteComment(id, userId, role); return Result.ok("删除成功", null);
    }

    @PostMapping("/mute")
    public Result<Void> mute(@RequestParam Long userId, @RequestParam int minutes,
            @RequestHeader(Constants.HEADER_USER_ID) Long adminId) {
        commentService.muteUser(userId, minutes, adminId);
        return Result.ok("禁言成功", null);
    }

    @DeleteMapping("/mute/{userId}")
    public Result<Void> unmute(@PathVariable Long userId) {
        commentService.unmuteUser(userId);
        return Result.ok("已解除禁言", null);
    }
    @GetMapping("/mute/{userId}/status")
    public Result<Boolean> isMuted(@PathVariable Long userId) {
        return Result.ok(commentService.isUserMuted(userId));
    }
}
