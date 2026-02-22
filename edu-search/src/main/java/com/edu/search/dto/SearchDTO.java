package com.edu.search.dto;

import lombok.Data;

/**
 * 搜索请求 DTO
 */
@Data
public class SearchDTO {
    
    /**
     * 搜索关键词
     */
    private String keyword;
    
    /**
     * 分类ID
     */
    private Long categoryId;
    
    /**
     * 价格区间
     */
    private Double minPrice;
    private Double maxPrice;
    
    /**
     * 难度等级
     */
    private Integer level;
    
    /**
     * 排序字段：score-相关度, sales-销量, price-价格, create_time-最新
     */
    private String orderBy = "score";
    
    /**
     * 排序方式：asc, desc
     */
    private String orderType = "desc";
    
    /**
     * 分页参数
     */
    private Integer pageNum = 1;
    private Integer pageSize = 20;
}

