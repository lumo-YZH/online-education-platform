package com.edu.comment.dto;

import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

/**
 * 添加评论DTO
 */
@Data
public class CommentAddDTO implements Serializable {
    
    /**
     * 课程ID
     */
    @NotNull(message = "课程ID不能为空")
    private Long courseId;
    
    /**
     * 父评论ID（回复评论时传）
     */
    private Long parentId;
    
    /**
     * 回复用户ID（回复评论时传）
     */
    private Long replyUserId;
    
    /**
     * 评论内容
     */
    @NotBlank(message = "评论内容不能为空")
    private String content;
    
    /**
     * 评分 1-5星（只有顶级评论才有评分）
     */
    @Min(value = 1, message = "评分最低1星")
    @Max(value = 5, message = "评分最高5星")
    private Integer rating;
}

