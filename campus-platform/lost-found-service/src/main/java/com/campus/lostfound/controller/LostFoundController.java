package com.campus.lostfound.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.constant.Constants;
import com.campus.common.result.Result;
import com.campus.lostfound.entity.LostFound;
import com.campus.lostfound.service.LostFoundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "失物招领接口")
@RestController
@RequestMapping("/lost-found")
@RequiredArgsConstructor
public class LostFoundController {

    private final LostFoundService lostFoundService;

    @Operation(summary = "启事列表")
    @GetMapping("/list")
    public Result<Page<LostFound>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {
        return Result.ok(lostFoundService.pageList(page, size, type, category, keyword));
    }

    @Operation(summary = "启事详情")
    @GetMapping("/detail/{id}")
    public Result<LostFound> detail(@PathVariable Long id) {
        return Result.ok(lostFoundService.getDetail(id));
    }

    @Operation(summary = "发布启事")
    @PostMapping
    public Result<Void> publish(
            @RequestBody LostFound entity,
            @RequestHeader(Constants.HEADER_USER_ID) Long userId,
            @RequestHeader(Constants.HEADER_USERNAME) String username) {
        lostFoundService.publish(entity, userId, username);
        return Result.ok("发布成功", null);
    }

    @Operation(summary = "编辑启事")
    @PutMapping("/{id}")
    public Result<Void> update(
            @PathVariable Long id,
            @RequestBody LostFound entity,
            @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        entity.setId(id);
        lostFoundService.update(entity, userId);
        return Result.ok("修改成功", null);
    }

    @Operation(summary = "删除启事")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @PathVariable Long id,
            @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        lostFoundService.delete(id, userId);
        return Result.ok("删除成功", null);
    }

    @Operation(summary = "我的启事")
    @GetMapping("/my")
    public Result<Page<LostFound>> myList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        return Result.ok(lostFoundService.myPage(page, size, userId));
    }

    @Operation(summary = "标记已找到/已归还")
    @PutMapping("/found/{id}")
    public Result<Void> markFound(
            @PathVariable Long id,
            @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        lostFoundService.markFound(id, userId);
        return Result.ok("已更新状态", null);
    }
}
