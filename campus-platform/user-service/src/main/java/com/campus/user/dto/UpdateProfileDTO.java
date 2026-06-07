package com.campus.user.dto;

import lombok.Data;

/**
 * 个人信息更新 DTO
 */
@Data
public class UpdateProfileDTO {

    private String nickname;
    private String phone;
    private String email;
    private String avatarUrl;
    private String college;
    private String major;
    private String grade;
}
