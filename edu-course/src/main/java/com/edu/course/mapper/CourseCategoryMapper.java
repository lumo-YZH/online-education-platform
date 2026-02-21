package com.edu.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.course.entity.CourseCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 课程分类 Mapper
 */
@Mapper
public interface CourseCategoryMapper extends BaseMapper<CourseCategory> {
}


