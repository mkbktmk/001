package com.campus.user.controller;

import com.campus.common.constant.Constants;
import com.campus.common.result.Result;
import com.campus.user.dto.UpdateProfileDTO;
import com.campus.user.service.UserService;
import com.campus.user.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器 — 用户信息查询
 */
@Tag(name = "用户接口")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "获取当前登录用户信息")
    @GetMapping("/me")
    public Result<UserVO> getCurrentUser(@RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        UserVO vo = userService.getCurrentUser(userId);
        return Result.ok(vo);
    }

    @Operation(summary = "根据ID获取用户信息（Feign内部调用）")
    @GetMapping("/{userId}")
    public Result<UserVO> getUserById(@PathVariable Long userId) {
        UserVO vo = userService.getUserById(userId);
        return Result.ok(vo);
    }

    @Operation(summary = "更新个人信息")
    @PutMapping("/me")
    public Result<UserVO> updateProfile(
            @RequestHeader(Constants.HEADER_USER_ID) Long userId,
            @RequestBody UpdateProfileDTO dto) {
        UserVO vo = userService.updateProfile(userId, dto);
        return Result.ok("修改成功", vo);
    }
}
