package com.campus.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.user.dto.LoginDTO;
import com.campus.user.dto.RegisterDTO;
import com.campus.user.dto.UpdateProfileDTO;
import com.campus.user.entity.User;
import com.campus.user.vo.LoginVO;
import com.campus.user.vo.UserVO;

/**
 * 用户 Service 接口
 */
public interface UserService extends IService<User> {

    /**
     * 注册
     */
    void register(RegisterDTO dto);

    /**
     * 登录
     */
    LoginVO login(LoginDTO dto);

    /**
     * 获取当前用户信息
     */
    UserVO getCurrentUser(Long userId);

    /**
     * 根据ID获取用户信息（供其他微服务 Feign 调用）
     */
    UserVO getUserById(Long userId);

    /** 更新个人信息 */
    UserVO updateProfile(Long userId, UpdateProfileDTO dto);
}
