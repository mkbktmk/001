package com.campus.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 注册请求 DTO
 */
@Data
public class RegisterDTO {

    @NotBlank(message = "用户名（学号/工号）不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 32, message = "密码长度为6-32位")
    private String password;

    @NotBlank(message = "昵称不能为空")
    private String nickname;

    private String phone;
    private String email;

    @NotBlank(message = "角色不能为空")
    private String role;   // student / teacher

    private String college;
    private String major;
    private String grade;

    // 验证码
    private String captchaKey;
    private String captchaCode;
}
