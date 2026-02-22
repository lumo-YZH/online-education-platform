package com.edu.video.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 视频播放 VO
 */
@Data
@Schema(description = "视频播放信息")
public class VideoPlayVO {
    
    @Schema(description = "视频ID")
    private Long id;
    
    @Schema(description = "视频标题")
    private String title;
    
    @Schema(description = "播放地址（带签名）")
    private String playUrl;
    
    @Schema(description = "封面图")
    private String cover;
    
    @Schema(description = "时长（秒）")
    private Integer duration;
    
    @Schema(description = "分辨率")
    private String resolution;
    
    @Schema(description = "上次播放位置（秒）")
    private Integer lastPlayPosition;
}

