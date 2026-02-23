package com.edu.comment.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.result.Result;
import com.edu.comment.dto.CommentAddDTO;
import com.edu.comment.dto.CommentQueryDTO;
import com.edu.comment.service.CommentService;
import com.edu.comment.vo.CommentStatVO;
import com.edu.comment.vo.CommentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 评论控制器
 */
@Slf4j
@RestController
@RequestMapping("/comment")
@Tag(name = "评论管理", description = "课程评论、点赞、回复等接口")
public class CommentController {
    
    @Autowired
    private CommentService commentService;
    
    /**
     * 分页查询评论列表
     */
    @PostMapping("/list")
    @Operation(summary = "评论列表", description = "分页查询课程评论列表，支持时间和热度排序")
    public Result<Page<CommentVO>> getCommentList(
            @RequestBody CommentQueryDTO dto,
            HttpServletRequest request) {
        Long userId = Long.valueOf(request.getHeader("userId"));
        log.info("分页查询评论列表：{}", dto);
        Page<CommentVO> page = commentService.getCommentList(dto, userId);
        return Result.success(page);
    }
    
    /**
     * 添加评论
     */
    @PostMapping("/add")
    @Operation(summary = "添加评论", description = "发表课程评论或回复评论")
    public Result<Long> addComment(
            @Validated @RequestBody CommentAddDTO dto,
            HttpServletRequest request) {
        String avatar = request.getHeader("avatar");
        Long userId = Long.valueOf(request.getHeader("userId"));
        String username = request.getHeader("username");
        log.info("添加评论：userId={}, dto={}", userId, dto);
        Long commentId = commentService.addComment(dto, userId, username, avatar);
        return Result.success("评论成功", commentId);
    }
    
    /**
     * 删除评论
     */
    @DeleteMapping("/{commentId}")
    @Operation(summary = "删除评论", description = "删除自己的评论")
    @Parameter(name = "commentId", description = "评论ID", required = true)
    public Result<Void> deleteComment(@PathVariable Long commentId, HttpServletRequest request) {
        Long userId = Long.valueOf(request.getHeader("userId"));
        log.info("删除评论：commentId={}, userId={}", commentId, userId);
        commentService.deleteComment(commentId, userId);
        return Result.success();
    }
    
    /**
     * 点赞/取消点赞
     */
    @PostMapping("/{commentId}/like")
    @Operation(summary = "点赞评论", description = "点赞或取消点赞评论")
    @Parameter(name = "commentId", description = "评论ID", required = true)
    public Result<Void> likeComment(@PathVariable Long commentId, HttpServletRequest request) {
        Long userId = Long.valueOf(request.getHeader("userId"));
        log.info("点赞评论：commentId={}, userId={}", commentId, userId);
        commentService.likeComment(commentId, userId);
        return Result.success();
    }
    
    /**
     * 获取评论统计
     */
    @GetMapping("/stat/{courseId}")
    @Operation(summary = "评论统计", description = "获取课程评论统计信息（总数、平均分、各星级数量）")
    @Parameter(name = "courseId", description = "课程ID", required = true)
    public Result<CommentStatVO> getCommentStat(@PathVariable Long courseId) {
        log.info("获取评论统计：courseId={}", courseId);
        CommentStatVO stat = commentService.getCommentStat(courseId);
        return Result.success(stat);
    }
}
