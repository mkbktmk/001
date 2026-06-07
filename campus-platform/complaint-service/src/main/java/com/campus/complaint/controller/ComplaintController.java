package com.campus.complaint.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.constant.Constants;
import com.campus.common.result.Result;
import com.campus.complaint.entity.Complaint;
import com.campus.complaint.service.ComplaintService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "报修投诉接口")
@RestController
@RequestMapping("/complaint")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;

    @Operation(summary = "我的工单列表")
    @GetMapping("/my")
    public Result<Page<Complaint>> myList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        return Result.ok(complaintService.myPage(page, size, userId, status));
    }

    @Operation(summary = "提交工单")
    @PostMapping
    public Result<Void> submit(
            @RequestBody Complaint complaint,
            @RequestHeader(Constants.HEADER_USER_ID) Long userId,
            @RequestHeader(Constants.HEADER_USERNAME) String username) {
        complaintService.submit(complaint, userId, username);
        return Result.ok("提交成功", null);
    }

    // ── 管理员接口 ──

    @Operation(summary = "管理员：全部工单列表")
    @GetMapping("/admin/list")
    public Result<Page<Complaint>> adminList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status) {
        return Result.ok(complaintService.adminPage(page, size, type, status));
    }

    @Operation(summary = "管理员：处理工单")
    @PutMapping("/admin/handle/{id}")
    public Result<Void> handle(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String reply,
            @RequestHeader(Constants.HEADER_USER_ID) Long handlerId,
            @RequestHeader(Constants.HEADER_USERNAME) String handlerName) {
        complaintService.handle(id, status, reply, handlerId, handlerName);
        return Result.ok("处理成功", null);
    }

    // ── 评价 ──

    @Operation(summary = "评价工单")
    @PutMapping("/rate/{id}")
    public Result<Void> rate(
            @PathVariable Long id,
            @RequestParam int rating,
            @RequestParam(required = false) String feedback,
            @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        complaintService.rate(id, userId, rating, feedback != null ? feedback : "");
        return Result.ok("评价成功", null);
    }
}
