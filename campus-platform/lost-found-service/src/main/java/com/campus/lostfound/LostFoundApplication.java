package com.campus.lostfound;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.campus.lostfound", "com.campus.common"})
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.campus.lostfound.mapper")
public class LostFoundApplication {

    public static void main(String[] args) {
        SpringApplication.run(LostFoundApplication.class, args);
    }
}
