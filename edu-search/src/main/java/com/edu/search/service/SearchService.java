package com.edu.search.service;

import com.edu.search.dto.SearchDTO;
import com.edu.search.vo.SearchPageVO;

import java.util.List;

/**
 * 搜索服务接口
 */
public interface SearchService {
    
    /**
     * 搜索课程
     */
    SearchPageVO searchCourse(SearchDTO dto, Long userId);
    
    /**
     * 搜索建议（自动补全）
     */
    List<String> searchSuggest(String keyword);
    
    /**
     * 获取热搜榜
     */
    List<String> getHotSearch(Integer limit);
    
    /**
     * 获取用户搜索历史
     */
    List<String> getSearchHistory(Long userId, Integer limit);
    
    /**
     * 清空搜索历史
     */
    void clearSearchHistory(Long userId);
}

