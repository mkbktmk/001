package com.campus.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.result.Result;
import com.campus.user.entity.User;
import com.campus.user.service.UserService;
import com.campus.user.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理员接口 — 网关已做角色拦截（仅 admin 可访问 /user/admin/**）
 */
@Tag(name = "管理员接口")
@RestController
@RequestMapping("/user/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @Operation(summary = "用户列表")
    @GetMapping("/list")
    public Result<Page<UserVO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<User> result = userService.lambdaQuery()
                .orderByDesc(User::getCreateTime)
                .page(new Page<>(page, size));
        Page<UserVO> voPage = new Page<>(page, size, result.getTotal());
        List<UserVO> vos = result.getRecords().stream().map(u -> {
            UserVO vo = new UserVO();
            BeanUtils.copyProperties(u, vo);
            return vo;
        }).collect(Collectors.toList());
        voPage.setRecords(vos);
        return Result.ok(voPage);
    }

    @Operation(summary = "禁用/启用用户")
    @PutMapping("/{id}/status")
    public Result<Void> toggleStatus(@PathVariable Long id, @RequestParam int status) {
        User user = userService.getById(id);
        if (user == null) return Result.fail("用户不存在");
        user.setStatus(status);
        userService.updateById(user);
        return Result.ok(status == 1 ? "已启用" : "已禁用", null);
    }
}
