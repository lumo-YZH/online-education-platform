package com.edu.user.config;

import com.edu.common.config.BaseWebMvcConfig;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * 用户服务 Web MVC 配置
 */
@Configuration
public class WebMvcConfig extends BaseWebMvcConfig {
    
    @Override
    protected List<String> getCustomExcludePatterns() {
        return Arrays.asList(
                "/user/register",           // 注册
                "/user/login",              // 登录
                "/user/send-code"           // 发送验证码
        );
    }
}

