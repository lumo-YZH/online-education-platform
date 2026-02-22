package com.edu.video.controller;

import com.edu.common.result.Result;
import com.edu.video.dto.PlayProgressDTO;
import com.edu.video.dto.VideoUploadDTO;
import com.edu.video.service.VideoService;
import com.edu.video.vo.VideoPlayVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 视频控制器
 */
@Slf4j
@RestController
@RequestMapping("/video")
@Tag(name = "视频管理")
public class VideoController {
    
    @Autowired
    private VideoService videoService;
    
    /**
     * 上传视频
     */
    @PostMapping("/upload")
    @Operation(summary = "上传视频")
    public Result<Long> uploadVideo(
            @RequestParam("file") @Parameter(description = "视频文件", required = true) MultipartFile file,
            @RequestParam("courseId") @Parameter(description = "课程ID", required = true) Long courseId,
            @RequestParam("sectionId") @Parameter(description = "小节ID", required = true) Long sectionId,
            @RequestParam("title") @Parameter(description = "视频标题", required = true) String title) {
        VideoUploadDTO dto = new VideoUploadDTO();
        dto.setCourseId(courseId);
        dto.setSectionId(sectionId);
        dto.setTitle(title);
        Long videoId = videoService.uploadVideo(file, dto);
        return Result.success(videoId);
    }
    
    /**
     * 获取视频播放地址
     */
    @GetMapping("/{videoId}/play-url")
    @Operation(summary = "获取视频播放地址（带防盗链）")
    public Result<VideoPlayVO> getPlayUrl(@PathVariable Long videoId,
                                           HttpServletRequest request) {
        // 从请求属性获取用户ID（由拦截器设置）
        Long userId = (Long) request.getAttribute("userId");
        VideoPlayVO vo = videoService.getPlayUrl(videoId, userId);
        return Result.success(vo);
    }
    
    /**
     * 记录播放进度
     */
    @PostMapping("/record-progress")
    @Operation(summary = "记录播放进度")
    public Result<?> recordProgress(@RequestBody PlayProgressDTO dto,
                                     HttpServletRequest request) {
        // 从请求属性获取用户ID（由拦截器设置）
        Long userId = (Long) request.getAttribute("userId");
        videoService.recordProgress(userId, dto);
        return Result.success("记录成功");
    }
}
