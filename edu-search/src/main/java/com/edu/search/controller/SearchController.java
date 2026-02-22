package com.edu.search.controller;

import com.edu.common.result.Result;
import com.edu.search.dto.SearchDTO;
import com.edu.search.service.SearchService;
import com.edu.search.vo.SearchPageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 搜索控制器
 */
@Slf4j
@RestController
@RequestMapping("/search")
@Tag(name = "搜索管理", description = "课程搜索、搜索建议、热搜榜等接口")
public class SearchController {
    
    @Autowired
    private SearchService searchService;
    
    /**
     * 搜索课程
     */
    @PostMapping("/course")
    @Operation(summary = "搜索课程", description = "全文搜索课程，支持高亮、排序、筛选")
    public Result<SearchPageVO> searchCourse(@RequestBody SearchDTO dto, HttpServletRequest request){
        Long userId = (Long) request.getAttribute("userId");
        SearchPageVO result = searchService.searchCourse(dto, userId);
        return Result.success(result);
    }
    
    /**
     * 搜索建议
     */
    @GetMapping("/suggest")
    @Operation(summary = "搜索建议", description = "根据关键词前缀提供搜索建议")
    public Result<List<String>> searchSuggest(@RequestParam String keyword) {
        List<String> suggests = searchService.searchSuggest(keyword);
        return Result.success(suggests);
    }
    
    /**
     * 热搜榜
     */
    @GetMapping("/hot")
    @Operation(summary = "热搜榜", description = "获取热门搜索关键词排行榜")
    public Result<List<String>> getHotSearch(@RequestParam(defaultValue = "10") Integer limit) {
        List<String> hotWords = searchService.getHotSearch(limit);
        return Result.success(hotWords);
    }
    
    /**
     * 搜索历史
     */
    @GetMapping("/history")
    @Operation(summary = "搜索历史", description = "获取用户搜索历史记录")
    public Result<List<String>> getSearchHistory(@RequestParam(defaultValue = "10") Integer limit, HttpServletRequest request){
        Long userId = (Long) request.getAttribute("userId");
        List<String> history = searchService.getSearchHistory(userId, limit);
        return Result.success(history);
    }
    
    /**
     * 清空搜索历史
     */
    @DeleteMapping("/history")
    @Operation(summary = "清空搜索历史", description = "清空用户的搜索历史记录")
    public Result<Void> clearSearchHistory(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        searchService.clearSearchHistory(userId);
        return Result.success();
    }
}

