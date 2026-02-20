package com.edu.course.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.result.Result;
import com.edu.course.dto.CourseQueryDTO;
import com.edu.course.service.CourseService;
import com.edu.course.vo.CourseCategoryVO;
import com.edu.course.vo.CourseDetailVO;
import com.edu.course.vo.CourseListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程控制器
 */
@Slf4j
@RestController
@RequestMapping("/course")
@Tag(name = "课程管理", description = "课程列表、详情、分类等接口")
public class CourseController {
    
    @Autowired
    private CourseService courseService;
    
    /**
     * 分页查询课程列表
     */
    @PostMapping("/list")
    @Operation(summary = "课程列表", description = "分页查询课程列表，支持多条件筛选和排序")
    public Result<Page<CourseListVO>> getCourseList(@RequestBody CourseQueryDTO dto) {
        log.info("分页查询课程列表:{}", dto);
        Page<CourseListVO> page = courseService.getCourseList(dto);
        return Result.success(page);
    }
    
    /**
     * 获取课程详情
     */
    @GetMapping("/{courseId}")
    @Operation(summary = "课程详情", description = "获取课程详细信息，包括章节和小节")
    @Parameter(name = "courseId", description = "课程ID", required = true)
    public Result<CourseDetailVO> getCourseDetail(@PathVariable Long courseId) {
        // 增加浏览量
        courseService.increaseViewCount(courseId);
        
        CourseDetailVO vo = courseService.getCourseDetail(courseId);
        return Result.success(vo);
    }
    
    /**
     * 获取课程分类列表
     */
    @GetMapping("/category/list")
    @Operation(summary = "分类列表", description = "获取所有课程分类")
    public Result<List<CourseCategoryVO>> getCategoryList() {
        List<CourseCategoryVO> list = courseService.getCategoryList();
        return Result.success(list);
    }
}
