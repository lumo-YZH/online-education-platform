package com.edu.video.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 视频实体类
 */
@Data
@TableName("video")
public class Video {
    
    /**
     * 视频ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 课程ID
     */
    private Long courseId;
    
    /**
     * 小节ID
     */
    private Long sectionId;
    
    /**
     * 视频标题
     */
    private String title;
    
    /**
     * 视频地址（MinIO）
     */
    private String url;
    
    /**
     * 封面图
     */
    private String cover;
    
    /**
     * 时长（秒）
     */
    private Integer duration;
    
    /**
     * 文件大小（字节）
     */
    private Long size;
    
    /**
     * 格式（mp4/m3u8）
     */
    private String format;
    
    /**
     * 分辨率（720p/1080p）
     */
    private String resolution;
    
    /**
     * 状态 1-正常 0-删除 2-转码中
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

