package com._data._data.community.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${app.upload.base-url}")
    private String baseUrl;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 포스트 이미지
        registry.addResourceHandler(baseUrl + "/post/**")
            .addResourceLocations("file:" + uploadDir + "/post/");
        // 프로필 이미지
        registry.addResourceHandler(baseUrl + "/profile/**")
            .addResourceLocations("file:" + uploadDir + "/profile/");

        // eduinfo 이미지 추가
        registry.addResourceHandler(baseUrl + "/eduinfo/**")
            .addResourceLocations("file:" + uploadDir + "/eduinfo/");
    }
}
