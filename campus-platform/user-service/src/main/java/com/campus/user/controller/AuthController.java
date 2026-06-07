package com.campus.user.controller;

import com.campus.common.constant.Constants;
import com.campus.common.result.Result;
import com.campus.common.utils.JwtUtils;
import com.campus.user.dto.LoginDTO;
import com.campus.user.dto.RegisterDTO;
import com.campus.user.service.CaptchaService;
import com.campus.user.service.UserService;
import com.campus.user.vo.LoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证控制器 — 注册 + 登录
 */
@Tag(name = "认证接口")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final CaptchaService captchaService;

    @Operation(summary = "获取图形验证码")
    @GetMapping("/captcha")
    public Result<Map<String, String>> captcha() {
        CaptchaService.CaptchaResult r = captchaService.generate();
        return Result.ok(Map.of("key", r.key(), "image", r.base64Image()));
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO dto) {
        // 校验验证码
        if (dto.getCaptchaKey() != null && !dto.getCaptchaKey().isBlank()) {
            if (!captchaService.verify(dto.getCaptchaKey(), dto.getCaptchaCode())) {
                return Result.fail("验证码错误或已过期");
            }
        }
        userService.register(dto);
        return Result.ok("注册成功", null);
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        LoginVO vo = userService.login(dto);
        return Result.ok(vo);
    }

    @Operation(summary = "刷新 Token（需要已登录）")
    @PostMapping("/refresh")
    public Result<Map<String, String>> refresh(
            @RequestHeader(Constants.HEADER_USER_ID) Long userId,
            @RequestHeader(Constants.HEADER_USERNAME) String username,
            @RequestHeader(Constants.HEADER_NICKNAME) String nickname,
            @RequestHeader(Constants.HEADER_USER_ROLE) String role) {
        String newToken = JwtUtils.generateToken(userId, username, nickname, role);
        return Result.ok(Map.of("token", newToken));
    }
}
