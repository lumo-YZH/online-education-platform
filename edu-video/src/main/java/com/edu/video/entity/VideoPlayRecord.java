package com.edu.video.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 视频播放记录实体类
 */
@Data
@TableName("video_play_record")
public class VideoPlayRecord {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 视频ID
     */
    private Long videoId;
    
    /**
     * 课程ID
     */
    private Long courseId;
    
    /**
     * 播放时长（秒）
     */
    private Integer playTime;
    
    /**
     * 播放进度（%）
     */
    private Integer progress;
    
    /**
     * 最后播放位置（秒）
     */
    private Integer lastPlayPosition;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

