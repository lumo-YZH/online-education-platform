package com.edu.comment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 评论实体类
 */
@Data
@TableName("comment")
public class Comment implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 评论ID
     */
    @TableId(type = IdType.AUTO)
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
     * 状态 1-正常 0-删除
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

