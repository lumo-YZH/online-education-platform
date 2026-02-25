package com.edu.video.controller;

import com.edu.common.result.Result;
import com.edu.video.service.VideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 视频内部接口控制器
 * 提供给其他服务调用的内部接口
 */
@Slf4j
@RestController
@RequestMapping("/video/internal")
@Tag(name = "视频内部接口", description = "提供给其他服务调用")
public class VideoInternalController {
    
    @Autowired
    private VideoService videoService;
    
    /**
     * 获取用户课程学习进度
     */
    @GetMapping("/course-progress")
    @Operation(summary = "获取用户课程学习进度", description = "内部接口")
    public Result<Integer> getCourseProgress(
            @RequestParam("userId") Long userId,
            @RequestParam("courseId") Long courseId) {
        
        log.info("获取用户课程学习进度：userId={}, courseId={}", userId, courseId);
        Integer progress = videoService.getCourseProgress(userId, courseId);
        return Result.success(progress);
    }
    
    /**
     * 批量获取用户课程学习进度
     */
    @GetMapping("/batch-course-progress")
    @Operation(summary = "批量获取用户课程学习进度", description = "内部接口")
    public Result<Map<Long, Integer>> getBatchCourseProgress(@RequestParam("userId") Long userId) {
        log.info("批量获取用户课程学习进度：userId={}", userId);
        Map<Long, Integer> progressMap = videoService.getBatchCourseProgress(userId);
        return Result.success(progressMap);
    }
}

