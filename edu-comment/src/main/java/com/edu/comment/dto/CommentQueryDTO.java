package com.edu.comment.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 评论查询DTO
 */
@Data
public class CommentQueryDTO implements Serializable {
    
    /**
     * 课程ID
     */
    private Long courseId;
    
    /**
     * 页码
     */
    private Integer pageNum = 1;
    
    /**
     * 每页数量
     */
    private Integer pageSize = 10;
    
    /**
     * 排序方式：time-时间排序, hot-热度排序
     */
    private String orderBy = "time";
}

