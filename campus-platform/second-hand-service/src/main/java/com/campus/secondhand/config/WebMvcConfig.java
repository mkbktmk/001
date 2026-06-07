package com.campus.secondhand.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 静态资源映射 — 将 /uploads/** 映射到本地 uploads 目录
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${upload.path:uploads}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String absPath = new java.io.File(uploadPath).getAbsoluteFile().toPath().toUri().toString();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(absPath);
    }
}
