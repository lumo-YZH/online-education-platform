package com.edu.video.service;

import com.edu.video.dto.PlayProgressDTO;
import com.edu.video.dto.VideoUploadDTO;
import com.edu.video.vo.VideoPlayVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 视频服务接口
 */
public interface VideoService {
    
    /**
     * 上传视频
     * 
     * @param file 视频文件
     * @param dto 视频信息
     * @return 视频ID
     */
    Long uploadVideo(MultipartFile file, VideoUploadDTO dto);
    
    /**
     * 获取视频播放地址（带防盗链签名）
     * 
     * @param videoId 视频ID
     * @param userId 用户ID
     * @return 播放信息
     */
    VideoPlayVO getPlayUrl(Long videoId, Long userId);
    
    /**
     * 记录播放进度
     * 
     * @param userId 用户ID
     * @param dto 播放进度
     */
    void recordProgress(Long userId, PlayProgressDTO dto);
}

