package com.my.quiz.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// src/main/java/com/my/quiz/config/WebMvcConfig.java
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private final LoginRequiredInterceptor loginRequiredInterceptor;
    public WebMvcConfig(LoginRequiredInterceptor loginRequiredInterceptor) {
        this.loginRequiredInterceptor = loginRequiredInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginRequiredInterceptor)
                .addPathPatterns("/quiz/**", "/member/**", "/admin/**", "/user/my**")
                .excludePathPatterns(
                        "/", "/user/login", "/user/signup",
                        // ↓↓↓ /quiz/play, /quiz/check 예외 제거 (로그인 필요)
                        "/css/**", "/js/**", "/image/**", "/images/**"
                );
    }
}
