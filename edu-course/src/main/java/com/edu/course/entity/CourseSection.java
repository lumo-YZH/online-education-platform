package com.edu.course.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 课程小节实体类
 */
@Data
@TableName("course_section")
public class CourseSection implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 小节ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 章节ID
     */
    private Long chapterId;
    
    /**
     * 课程ID
     */
    private Long courseId;
    
    /**
     * 小节名称
     */
    private String name;
    
    /**
     * 视频ID
     */
    private Long videoId;
    
    /**
     * 时长(秒)
     */
    private Integer duration;
    
    /**
     * 是否免费 1-是 0-否
     */
    private Integer isFree;
    
    /**
     * 排序
     */
    private Integer sort;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}


