package com.edu.course.controller;

import com.edu.common.result.Result;
import org.springframework.web.bind.annotation.*;

/**
 * 课程控制器
 */
@RestController
@RequestMapping("/course")
public class CourseController {
    
    @GetMapping("/list")
    public Result<?> getCourseList() {
        return Result.success("课程列表接口");
    }
    
    @GetMapping("/{id}")
    public Result<?> getCourseDetail(@PathVariable Long id) {
        return Result.success("课程详情接口");
    }
    
    @GetMapping("/{id}/chapters")
    public Result<?> getCourseChapters(@PathVariable Long id) {
        return Result.success("课程章节接口");
    }
    
    @PostMapping("/seckill")
    public Result<?> seckill() {
        return Result.success("秒杀抢课接口");
    }
    
    @GetMapping("/hot")
    public Result<?> getHotCourses() {
        return Result.success("热门课程接口");
    }
}

