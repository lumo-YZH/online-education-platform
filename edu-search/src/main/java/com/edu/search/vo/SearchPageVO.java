package com.edu.search.vo;

import lombok.Data;

import java.util.List;

/**
 * 分页搜索结果 VO
 */
@Data
public class SearchPageVO {
    
    /**
     * 搜索结果列表
     */
    private List<SearchResultVO> records;
    
    /**
     * 总记录数
     */
    private Long total;
    
    /**
     * 当前页
     */
    private Integer pageNum;
    
    /**
     * 每页大小
     */
    private Integer pageSize;
    
    /**
     * 总页数
     */
    public Integer getTotalPages() {
        if (pageSize == null || pageSize == 0) {
            return 0;
        }
        return (int) Math.ceil((double) total / pageSize);
    }
}

