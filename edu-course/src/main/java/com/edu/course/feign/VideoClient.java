package com.edu.course.feign;

import com.edu.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.Map;

/**
 * 视频服务 Feign 客户端
 */
@FeignClient(name = "edu-video", path = "/video/internal", fallback = VideoClient.VideoClientFallback.class)
public interface VideoClient {
    
    /**
     * 获取用户课程学习进度
     * 
     * @param userId 用户ID
     * @param courseId 课程ID
     * @return 学习进度（0-100）
     */
    @GetMapping("/course-progress")
    Result<Integer> getCourseProgress(@RequestParam("userId") Long userId,
                                     @RequestParam("courseId") Long courseId);
    
    /**
     * 批量获取用户课程学习进度
     * 
     * @param userId 用户ID
     * @return Map<课程ID, 学习进度>
     */
    @GetMapping("/batch-course-progress")
    Result<Map<Long, Integer>> getBatchCourseProgress(@RequestParam("userId") Long userId);
    
    /**
     * Feign 降级处理
     */
    @Slf4j
    @Component
    class VideoClientFallback implements VideoClient {
        @Override
        public Result<Integer> getCourseProgress(Long userId, Long courseId) {
            log.error("【Feign降级】调用视频服务获取学习进度失败：userId={}, courseId={}", userId, courseId);
            // 降级返回0
            return Result.success(0);
        }
        
        @Override
        public Result<Map<Long, Integer>> getBatchCourseProgress(Long userId) {
            log.error("【Feign降级】调用视频服务批量获取学习进度失败：userId={}", userId);
            // 降级返回空Map
            return Result.success(Collections.emptyMap());
        }
    }
}

