package com.edu.course.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 讲师实体类
 */
@Data
@TableName("course_teacher")
public class CourseTeacher implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 讲师ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 讲师姓名
     */
    private String name;
    
    /**
     * 头像
     */
    private String avatar;
    
    /**
     * 职称
     */
    private String title;
    
    /**
     * 简介
     */
    private String intro;
    
    /**
     * 状态 1-启用 0-禁用
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

