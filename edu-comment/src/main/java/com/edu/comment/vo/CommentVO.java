package com.edu.comment.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论VO
 */
@Data
public class CommentVO implements Serializable {
    
    /**
     * 评论ID
     */
    private Long id;
    
    /**
     * 课程ID
     */
    private Long courseId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 头像
     */
    private String avatar;
    
    /**
     * 父评论ID
     */
    private Long parentId;
    
    /**
     * 回复用户ID
     */
    private Long replyUserId;
    
    /**
     * 回复用户名
     */
    private String replyUsername;
    
    /**
     * 评论内容
     */
    private String content;
    
    /**
     * 点赞数
     */
    private Integer likeCount;
    
    /**
     * 回复数
     */
    private Integer replyCount;
    
    /**
     * 评分 1-5星
     */
    private Integer rating;
    
    /**
     * 是否已点赞
     */
    private Boolean isLiked;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    /**
     * 回复列表（只有顶级评论才有）
     */
    private List<CommentVO> replies;
}

