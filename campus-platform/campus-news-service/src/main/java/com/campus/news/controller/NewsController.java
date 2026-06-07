package com.campus.news.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.constant.Constants;
import com.campus.common.result.Result;
import com.campus.news.entity.News;
import com.campus.news.service.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "资讯接口")
@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @Operation(summary = "资讯列表（公开）")
    @GetMapping("/list")
    public Result<Page<News>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category) {
        return Result.ok(newsService.pageList(page, size, category));
    }

    @Operation(summary = "资讯详情（公开）")
    @GetMapping("/detail/{id}")
    public Result<News> detail(@PathVariable Long id) {
        return Result.ok(newsService.getDetail(id));
    }

    @Operation(summary = "发布资讯（教师/管理员）")
    @PostMapping("/publish")
    public Result<Void> publish(
            @RequestBody News news,
            @RequestHeader(Constants.HEADER_USER_ID) Long userId,
            @RequestHeader(Constants.HEADER_USERNAME) String username) {
        newsService.publish(news, userId, username);
        return Result.ok("发布成功", null);
    }

    @Operation(summary = "编辑资讯")
    @PutMapping("/{id}")
    public Result<Void> update(
            @PathVariable Long id,
            @RequestBody News news,
            @RequestHeader(Constants.HEADER_USER_ID) Long userId,
            @RequestHeader(Constants.HEADER_USER_ROLE) String role) {
        news.setId(id);
        newsService.updateNews(news, userId, role);
        return Result.ok("编辑成功", null);
    }

    @Operation(summary = "下架资讯")
    @PutMapping("/offline/{id}")
    public Result<Void> offline(
            @PathVariable Long id,
            @RequestHeader(Constants.HEADER_USER_ID) Long userId,
            @RequestHeader(Constants.HEADER_USER_ROLE) String role) {
        newsService.offline(id, userId, role);
        return Result.ok("已下架", null);
    }
}
