package com.edu.common.config;

import com.edu.common.interceptor.TokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Web MVC 基础配置
 * 各微服务继承此类并重写 getExcludePathPatterns() 方法来自定义排除路径
 */
public abstract class BaseWebMvcConfig implements WebMvcConfigurer {
    
    @Autowired
    private TokenInterceptor tokenInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 获取通用排除路径
        List<String> excludePatterns = new ArrayList<>(getCommonExcludePatterns());
        
        // 添加各微服务自定义的排除路径
        excludePatterns.addAll(getCustomExcludePatterns());
        
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(excludePatterns.toArray(new String[0]));
    }
    
    /**
     * 通用排除路径（所有微服务都需要排除的路径）
     */
    private List<String> getCommonExcludePatterns() {
        return Arrays.asList(
                "/doc.html",                // Knife4j 文档
                "/swagger-resources/**",    // Swagger 资源
                "/v3/api-docs/**",          // OpenAPI 文档
                "/webjars/**",              // 静态资源
                "/favicon.ico",             // 图标
                "/error"                    // 错误页面
        );
    }
    
    /**
     * 各微服务自定义排除路径
     * 子类重写此方法返回需要排除的路径列表
     */
    protected abstract List<String> getCustomExcludePatterns();
}

