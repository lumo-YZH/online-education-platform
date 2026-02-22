package com.edu.search.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程同步消息 DTO
 */
@Data
public class CourseMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 操作类型：CREATE, UPDATE, DELETE
     */
    private String action;
    
    /**
     * 课程ID
     */
    private Long id;
    
    /**
     * 课程名称
     */
    private String name;
    
    /**
     * 课程描述
     */
    private String description;
    
    /**
     * 封面图
     */
    private String cover;
    
    /**
     * 分类ID
     */
    private Long categoryId;
    
    /**
     * 分类名称
     */
    private String categoryName;
    
    /**
     * 讲师ID
     */
    private Long teacherId;
    
    /**
     * 讲师名称
     */
    private String teacherName;
    
    /**
     * 价格
     */
    private BigDecimal price;
    
    /**
     * 销量
     */
    private Integer sales;
    
    /**
     * 浏览量
     */
    private Integer viewCount;
    
    /**
     * 难度等级
     */
    private Integer level;
    
    /**
     * 状态
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

