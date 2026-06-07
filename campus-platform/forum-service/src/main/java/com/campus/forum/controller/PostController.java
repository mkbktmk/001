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
import java.util.Map;

@Tag(name = "帖子接口")
@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/list")
    public Result<Page<Post>> list(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String board,
            @RequestParam(required = false) String keyword) {
        return Result.ok(postService.pageList(page, size, board, keyword));
    }

    @GetMapping("/detail/{id}")
    public Result<Post> detail(@PathVariable Long id) { return Result.ok(postService.getDetail(id)); }

    @PostMapping
    public Result<Void> create(@RequestBody Post post,
            @RequestHeader(Constants.HEADER_USER_ID) Long userId,
            @RequestHeader(value = Constants.HEADER_NICKNAME, defaultValue = "") String nickname,
            @RequestHeader(Constants.HEADER_USERNAME) String username) {
        String dn = !nickname.isBlank() ? URLDecoder.decode(nickname, StandardCharsets.UTF_8) : username;
        postService.createPost(post, userId, dn);
        return Result.ok("发布成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id,
            @RequestHeader(Constants.HEADER_USER_ID) Long userId,
            @RequestHeader(Constants.HEADER_USER_ROLE) String role) {
        postService.deletePost(id, userId, role); return Result.ok("删除成功", null);
    }

    @PostMapping("/like/{id}")
    public Result<Integer> like(@PathVariable Long id, @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        return Result.ok(postService.toggleLike(id, userId));
    }

    @PostMapping("/favorite/{id}")
    public Result<Boolean> favorite(@PathVariable Long id, @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        return Result.ok(postService.toggleFavorite(id, userId));
    }

    @GetMapping("/like/{id}/status")
    public Result<Boolean> isLiked(@PathVariable Long id, @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        return Result.ok(postService.isLiked(id, userId));
    }

    @GetMapping("/favorite/{id}/status")
    public Result<Boolean> isFavorited(@PathVariable Long id, @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        return Result.ok(postService.isFavorited(id, userId));
    }

    @GetMapping("/hot")
    public Result<List<Post>> hot(@RequestParam(defaultValue = "5") int limit) {
        return Result.ok(postService.getHotPosts(limit));
    }

    @GetMapping("/my")
    public Result<Page<Post>> myList(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size, @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        return Result.ok(postService.myPosts(page, size, userId));
    }

    @GetMapping("/favorites")
    public Result<List<Post>> myFavorites(@RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        return Result.ok(postService.getMyFavorites(userId));
    }
}
