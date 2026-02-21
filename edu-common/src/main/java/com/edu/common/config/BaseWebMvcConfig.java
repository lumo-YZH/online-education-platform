package com.edu.common.config;

import com.edu.common.interceptor.UserInfoInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 基础配置
 * 自动注册 UserInfoInterceptor 拦截器，排除 Swagger 文档路径
 * 只在 Web MVC 环境下加载（网关是 WebFlux，不需要此配置）
 * 
 * 认证由网关统一处理，服务层拦截器只负责提取用户信息
 */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(WebMvcConfigurer.class)
public class BaseWebMvcConfig implements WebMvcConfigurer {
    
    private final UserInfoInterceptor userInfoInterceptor;
    
    public BaseWebMvcConfig(UserInfoInterceptor userInfoInterceptor) {
        this.userInfoInterceptor = userInfoInterceptor;
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userInfoInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/doc.html",                // Knife4j 文档
                        "/swagger-resources/**",    // Swagger 资源
                        "/v3/api-docs/**",          // OpenAPI 文档
                        "/webjars/**",              // 静态资源
                        "/favicon.ico",             // 图标
                        "/error"                    // 错误页面
                );
    }
}

