package com.edu.video.controller;

import com.edu.common.result.Result;
import org.springframework.web.bind.annotation.*;

/**
 * 视频控制器
 */
@RestController
@RequestMapping("/video")
public class VideoController {
    
    @PostMapping("/upload")
    public Result<?> uploadVideo() {
        return Result.success("上传视频接口");
    }
    
    @GetMapping("/{id}/play-url")
    public Result<?> getPlayUrl(@PathVariable Long id) {
        return Result.success("获取播放地址接口");
    }
    
    @PostMapping("/record-progress")
    public Result<?> recordProgress() {
        return Result.success("记录播放进度接口");
    }
}

