package com.edu.course.config;

import com.edu.course.service.CourseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 缓存预热组件
 * 应用启动时预加载热门数据到 Redis
 */
@Slf4j
@Component
public class CacheWarmUpRunner implements ApplicationRunner {
    
    @Autowired
    private CourseService courseService;
    
    @Override
    public void run(ApplicationArguments args) {
        try {
            log.info("开始预热缓存...");
            
            // 预热热门课程缓存
            courseService.getHotCourses(10);
            
            log.info("缓存预热完成：热门课程已加载");
        } catch (Exception e) {
            log.error("缓存预热失败", e);
        }
    }
}

