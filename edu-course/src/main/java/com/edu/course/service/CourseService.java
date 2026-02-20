package com.edu.course.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.course.dto.CourseQueryDTO;
import com.edu.course.vo.CourseCategoryVO;
import com.edu.course.vo.CourseDetailVO;
import com.edu.course.vo.CourseListVO;

import java.util.List;

/**
 * 课程服务接口
 */
public interface CourseService {
    
    /**
     * 分页查询课程列表
     */
    Page<CourseListVO> getCourseList(CourseQueryDTO dto);
    
    /**
     * 获取课程详情
     */
    CourseDetailVO getCourseDetail(Long courseId);
    
    /**
     * 获取课程分类列表
     */
    List<CourseCategoryVO> getCategoryList();
    
    /**
     * 增加课程浏览量
     */
    void increaseViewCount(Long courseId);
}

