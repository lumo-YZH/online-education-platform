package com.edu.course.config;

import com.edu.common.config.BaseWebMvcConfig;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * 课程服务 Web MVC 配置
 */
@Configuration
public class WebMvcConfig extends BaseWebMvcConfig {
    
    @Override
    protected List<String> getCustomExcludePatterns() {
        return Arrays.asList(
                "/course/list",             // 课程列表
                "/course/{courseId}",       // 课程详情
                "/course/category/list",    // 分类列表
                "/course/hot"               // 热门课程
        );
    }
}



