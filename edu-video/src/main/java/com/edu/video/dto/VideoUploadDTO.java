package com.edu.video.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 视频上传 DTO
 */
@Data
@Schema(description = "视频上传参数")
public class VideoUploadDTO {
    
    @Schema(description = "课程ID")
    private Long courseId;
    
    @Schema(description = "小节ID")
    private Long sectionId;
    
    @Schema(description = "视频标题")
    private String title;
}

