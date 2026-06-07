package com.campus.user.feign;

import com.campus.common.result.Result;
import com.campus.user.vo.UserVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 用户服务 Feign 客户端接口
 * <p>
 * 其他微服务引入此接口即可调用用户服务。
 * 注：此接口放在 user-service 中作为 SDK 供其他服务引用，
 *     或者各服务自行定义（推荐后者，避免循环依赖）。
 *     这里给出标准写法，各服务可复制到自己的 feign 包下。
 */
@FeignClient(name = "user-service", path = "/user")
public interface UserFeignClient {

    @GetMapping("/{userId}")
    Result<UserVO> getUserById(@PathVariable Long userId);
}
