package com.edu.course.controller;

import com.edu.common.result.Result;
import com.edu.course.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 课程内部接口（供其他服务调用）
 */
@Slf4j
@RestController
@RequestMapping("/course/internal")
@Tag(name = "课程内部接口", description = "供其他微服务调用的接口")
public class CourseInternalController {
    
    @Autowired
    private CourseService courseService;
    
    /**
     * 扣减课程库存
     */
    @PostMapping("/deduct-stock")
    @Operation(summary = "扣减课程库存", description = "创建订单时扣减库存")
    public Result<?> deductStock(@RequestParam Long courseId, @RequestParam Integer quantity) {
        courseService.deductStock(courseId, quantity);
        return Result.success("库存扣减成功");
    }
    
    /**
     * 恢复课程库存
     */
    @PostMapping("/restore-stock")
    @Operation(summary = "恢复课程库存", description = "订单取消时恢复库存")
    public Result<?> restoreStock(@RequestParam Long courseId, @RequestParam Integer quantity) {
        courseService.restoreStock(courseId, quantity);
        return Result.success("库存恢复成功");
    }
    
    /**
     * 更新小节的视频ID
     */
    @PostMapping("/update-section-video")
    @Operation(summary = "更新小节的视频ID", description = "视频上传后更新小节关联")
    public Result<?> updateSectionVideo(@RequestParam Long sectionId, @RequestParam Long videoId) {
        log.info("更新小节视频ID：sectionId={}, videoId={}", sectionId, videoId);
        courseService.updateSectionVideo(sectionId, videoId);
        return Result.success("更新成功");
    }
}

