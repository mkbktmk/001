package com.campus.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.common.exception.BizException;
import com.campus.common.utils.JwtUtils;
import com.campus.user.dto.LoginDTO;
import com.campus.user.dto.RegisterDTO;
import com.campus.user.dto.UpdateProfileDTO;
import com.campus.user.entity.User;
import com.campus.user.mapper.UserMapper;
import com.campus.user.service.UserService;
import com.campus.user.vo.LoginVO;
import com.campus.user.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

/**
 * 用户 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final DataSource dataSource;

    @Override
    public void register(RegisterDTO dto) {
        // 检查用户名是否已存在
        long count = count(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, dto.getUsername()));
        if (count > 0) {
            throw new BizException("用户名已被注册");
        }

        User user = new User();
        BeanUtils.copyProperties(dto, user);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setStatus(1);

        save(user);
    }

    @Override
    public LoginVO login(LoginDTO dto) {
        // 查用户
        User user = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, dto.getUsername()));
        if (user == null) {
            throw new BizException(400, "用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            throw new BizException(403, "账号已被禁用，请联系管理员");
        }
        // 验密码
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BizException(400, "用户名或密码错误");
        }

        // 生成 Token（含昵称）
        String token = JwtUtils.generateToken(user.getId(), user.getUsername(),
                user.getNickname(), user.getRole());

        return LoginVO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .role(user.getRole())
                .token(token)
                .avatarUrl(user.getAvatarUrl())
                .build();
    }

    @Override
    public UserVO getCurrentUser(Long userId) {
        return getUserById(userId);
    }

    @Override
    public UserVO getUserById(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new BizException(404, "用户不存在");
        }
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }

    @Override
    public UserVO updateProfile(Long userId, UpdateProfileDTO dto) {
        User user = getById(userId);
        if (user == null) {
            throw new BizException(404, "用户不存在");
        }
        if (dto.getNickname() != null) user.setNickname(dto.getNickname());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getAvatarUrl() != null) user.setAvatarUrl(dto.getAvatarUrl());
        if (dto.getCollege() != null) user.setCollege(dto.getCollege());
        if (dto.getMajor() != null) user.setMajor(dto.getMajor());
        if (dto.getGrade() != null) user.setGrade(dto.getGrade());
        updateById(user);

        // 昵称变更时同步到其他服务的展示名
        if (dto.getNickname() != null) {
            syncNicknameToAllServices(user.getId(), user.getUsername(), dto.getNickname());
        }

        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }

    /** 昵称变更时同步到所有服务的展示名字段 */
    private void syncNicknameToAllServices(Long userId, String username, String newNickname) {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        String[][] updates = {
            {"campus_forum.t_post", "author_name", "author_id"},
            {"campus_forum.t_comment", "author_name", "author_id"},
            {"campus_secondhand.t_second_hand", "seller_name", "seller_id"},
            {"campus_secondhand.t_message", "sender_name", "sender_id"},
            {"campus_complaint.t_complaint", "user_name", "user_id"},
            {"campus_lostfound.t_lost_found", "user_name", "user_id"},
            {"campus_news.t_news", "author_name", "author_id"},
        };
        for (String[] u : updates) {
            try {
                jdbc.update("UPDATE " + u[0] + " SET " + u[1] + " = ? WHERE " + u[2] + " = ?",
                        newNickname, userId);
            } catch (Exception e) {
                log.warn("Failed to sync nickname to {}: {}", u[0], e.getMessage());
            }
        }
    }
}
