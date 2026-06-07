package com.campus.user.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 登录响应 VO
 */
@Data
@Builder
public class LoginVO {

    private Long userId;
    private String username;
    private String nickname;
    private String role;
    private String token;
    private String avatarUrl;
}
