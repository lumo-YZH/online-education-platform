package com.edu.search.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 搜索结果 VO
 */
@Data
public class SearchResultVO {
    
    /**
     * 课程ID
     */
    private Long id;
    
    /**
     * 课程名称（高亮）
     */
    private String name;
    
    /**
     * 课程描述（高亮）
     */
    private String description;
    
    /**
     * 封面图
     */
    private String cover;
    
    /**
     * 分类名称
     */
    private String categoryName;
    
    /**
     * 讲师名称（高亮）
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
}

