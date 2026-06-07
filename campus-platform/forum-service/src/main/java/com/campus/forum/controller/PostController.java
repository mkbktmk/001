package com.campus.forum.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.constant.Constants;
import com.campus.common.result.Result;
import com.campus.forum.entity.Post;
import com.campus.forum.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Tag(name = "帖子接口")
@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @Operation(summary = "帖子列表")
    @GetMapping("/list")
    public Result<Page<Post>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String board,
            @RequestParam(required = false) String keyword) {
        return Result.ok(postService.pageList(page, size, board, keyword));
    }

    @Operation(summary = "帖子详情")
    @GetMapping("/detail/{id}")
    public Result<Post> detail(@PathVariable Long id) {
        return Result.ok(postService.getDetail(id));
    }

    @Operation(summary = "发布帖子")
    @PostMapping
    public Result<Void> create(
            @RequestBody Post post,
            @RequestHeader(Constants.HEADER_USER_ID) Long userId,
            @RequestHeader(value = Constants.HEADER_NICKNAME, defaultValue = "") String nickname,
            @RequestHeader(Constants.HEADER_USERNAME) String username) {
        String displayName = !nickname.isBlank() ? URLDecoder.decode(nickname, StandardCharsets.UTF_8) : username;
        postService.createPost(post, userId, displayName);
        return Result.ok("发布成功", null);
    }

    @Operation(summary = "删除帖子")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @PathVariable Long id,
            @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        postService.deletePost(id, userId);
        return Result.ok("删除成功", null);
    }

    @Operation(summary = "点赞/取消点赞")
    @PostMapping("/like/{id}")
    public Result<Integer> like(
            @PathVariable Long id,
            @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        return Result.ok(postService.toggleLike(id, userId));
    }

    @Operation(summary = "收藏/取消收藏")
    @PostMapping("/favorite/{id}")
    public Result<Boolean> favorite(
            @PathVariable Long id,
            @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        return Result.ok(postService.toggleFavorite(id, userId));
    }

    @Operation(summary = "查询是否已点赞")
    @GetMapping("/like/{id}/status")
    public Result<Boolean> isLiked(
            @PathVariable Long id,
            @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        return Result.ok(postService.isLiked(id, userId));
    }

    @Operation(summary = "查询是否已收藏")
    @GetMapping("/favorite/{id}/status")
    public Result<Boolean> isFavorited(
            @PathVariable Long id,
            @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        return Result.ok(postService.isFavorited(id, userId));
    }

    @Operation(summary = "热门帖子 Top N")
    @GetMapping("/hot")
    public Result<List<Post>> hot(@RequestParam(defaultValue = "5") int limit) {
        return Result.ok(postService.getHotPosts(limit));
    }

    @Operation(summary = "我的帖子")
    @GetMapping("/my")
    public Result<Page<Post>> myList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        return Result.ok(postService.myPosts(page, size, userId));
    }
}
