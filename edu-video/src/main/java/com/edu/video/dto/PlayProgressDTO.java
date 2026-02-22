package com.edu.video.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 播放进度记录 DTO
 */
@Data
@Schema(description = "播放进度记录参数")
public class PlayProgressDTO {
    
    @Schema(description = "视频ID")
    private Long videoId;
    
    @Schema(description = "课程ID")
    private Long courseId;
    
    @Schema(description = "播放时长（秒）")
    private Integer playTime;
    
    @Schema(description = "最后播放位置（秒）")
    private Integer lastPlayPosition;
}

