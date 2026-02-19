package com.edu.comment.controller;

import com.edu.common.result.Result;
import org.springframework.web.bind.annotation.*;

/**
 * 评论控制器
 */
@RestController
@RequestMapping("/comment")
public class CommentController {
    
    @PostMapping("/add")
    public Result<?> addComment() {
        return Result.success("添加评论接口");
    }
    
    @GetMapping("/list")
    public Result<?> getCommentList(@RequestParam Long courseId) {
        return Result.success("评论列表接口");
    }
    
    @PostMapping("/{id}/like")
    public Result<?> likeComment(@PathVariable Long id) {
        return Result.success("点赞评论接口");
    }
    
    @PostMapping("/{id}/reply")
    public Result<?> replyComment(@PathVariable Long id) {
        return Result.success("回复评论接口");
    }
}

