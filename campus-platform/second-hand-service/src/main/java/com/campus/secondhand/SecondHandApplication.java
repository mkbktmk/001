package com.campus.secondhand;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.campus.secondhand", "com.campus.common"})
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.campus.secondhand.mapper")
public class SecondHandApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecondHandApplication.class, args);
    }
}
