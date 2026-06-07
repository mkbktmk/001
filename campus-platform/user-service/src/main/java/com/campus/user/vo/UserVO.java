package com.campus.user.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户信息 VO（返回给前端，不含密码）
 */
@Data
public class UserVO {

    private Long id;
    private String username;
    private String nickname;
    private String phone;
    private String email;
    private String avatarUrl;
    private String role;
    private String college;
    private String major;
    private String grade;
    private Integer status;
    private LocalDateTime createTime;
}
