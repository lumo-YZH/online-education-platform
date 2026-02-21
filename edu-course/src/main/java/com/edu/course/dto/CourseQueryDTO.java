package com.edu.course.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 课程查询 DTO
 */
@Data
public class CourseQueryDTO implements Serializable {
    
    /**
     * 课程名称（模糊查询）
     */
    private String name;
    
    /**
     * 分类ID
     */
    private Long categoryId;
    
    /**
     * 讲师ID
     */
    private Long teacherId;
    
    /**
     * 难度 1-入门 2-初级 3-中级 4-高级
     */
    private Integer level;
    
    /**
     * 最低价格
     */
    private Double minPrice;
    
    /**
     * 最高价格
     */
    private Double maxPrice;
    
    /**
     * 排序字段 sales-销量 view_count-浏览量 create_time-最新
     */
    private String orderBy;
    
    /**
     * 排序方式 asc-升序 desc-降序
     */
    private String orderType;
    
    /**
     * 当前页
     */
    private Integer pageNum = 1;
    
    /**
     * 每页大小
     */
    private Integer pageSize = 10;
}


