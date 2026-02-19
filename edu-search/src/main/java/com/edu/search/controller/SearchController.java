package com.edu.search.controller;

import com.edu.common.result.Result;
import org.springframework.web.bind.annotation.*;

/**
 * 搜索控制器
 */
@RestController
@RequestMapping("/search")
public class SearchController {
    
    @GetMapping("/course")
    public Result<?> searchCourse(@RequestParam String keyword) {
        return Result.success("搜索课程接口");
    }
    
    @GetMapping("/suggest")
    public Result<?> searchSuggest(@RequestParam String keyword) {
        return Result.success("搜索建议接口");
    }
    
    @GetMapping("/hot")
    public Result<?> getHotSearch() {
        return Result.success("热搜榜接口");
    }
    
    @PostMapping("/history")
    public Result<?> saveSearchHistory() {
        return Result.success("保存搜索历史接口");
    }
}

