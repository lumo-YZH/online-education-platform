package com.edu.video.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.edu.common.exception.BusinessException;
import com.edu.common.result.Result;
import com.edu.video.dto.PlayProgressDTO;
import com.edu.video.dto.VideoUploadDTO;
import com.edu.video.entity.Video;
import com.edu.video.entity.VideoPlayRecord;
import com.edu.video.feign.OrderClient;
import com.edu.video.mapper.VideoMapper;
import com.edu.video.mapper.VideoPlayRecordMapper;
import com.edu.video.service.VideoService;
import com.edu.video.vo.VideoPlayVO;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * 视频服务实现类
 */
@Slf4j
@Service
public class VideoServiceImpl implements VideoService {
    
    @Autowired
    private VideoMapper videoMapper;
    
    @Autowired
    private VideoPlayRecordMapper playRecordMapper;
    
    @Autowired
    private MinioClient minioClient;
    
    @Autowired
    private OrderClient orderClient;
    
    @Value("${minio.bucket-name}")
    private String bucketName;
    
    @Value("${minio.endpoint}")
    private String minioEndpoint;
    
    @Value("${video.sign-secret}")
    private String signSecret;
    
    /**
     * 上传视频
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long uploadVideo(MultipartFile file, VideoUploadDTO dto) {
        log.info("开始上传视频：title={}, courseId={}", dto.getTitle(), dto.getCourseId());
        
        try {
            // 1. 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = "video/" + IdUtil.simpleUUID() + suffix;
            
            // 2. 上传到 MinIO
            InputStream inputStream = file.getInputStream();
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build()
            );
            
            // 3. 构建访问 URL
            String url = minioEndpoint + "/" + bucketName + "/" + fileName;
            
            // 4. 保存视频信息到数据库
            Video video = new Video();
            video.setCourseId(dto.getCourseId());
            video.setSectionId(dto.getSectionId());
            video.setTitle(dto.getTitle());
            video.setUrl(url);
            video.setSize(file.getSize());
            video.setFormat(suffix.substring(1)); // 去掉点号
            video.setStatus(1); // 正常
            
            videoMapper.insert(video);
            
            log.info("视频上传成功：videoId={}, url={}", video.getId(), url);
            return video.getId();
            
        } catch (Exception e) {
            log.error("视频上传失败", e);
            throw new BusinessException("视频上传失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取视频播放地址（带防盗链签名）
     */
    @Override
    public VideoPlayVO getPlayUrl(Long videoId, Long userId) {
        log.info("获取视频播放地址：videoId={}, userId={}", videoId, userId);
        
        // 1. 查询视频信息
        Video video = videoMapper.selectById(videoId);
        if (video == null) {
            throw new BusinessException("视频不存在");
        }
        
        // 2. 检查用户是否购买了课程
        Result<Boolean> result = orderClient.checkUserPurchased(userId, video.getCourseId());
        log.info("检查购买结果：code={}, data={}", result.getCode(), result.getData());
        
        if (result.getCode() != 200) {
            throw new BusinessException("检查购买状态失败：" + result.getMessage());
        }
        
        if (result.getData() == null || !result.getData()) {
            throw new BusinessException("请先购买课程");
        }
        
        // 3. 生成带签名的播放 URL（有效期 1 小时）
        long expireTime = System.currentTimeMillis() + 3600 * 1000;
        String sign = DigestUtil.md5Hex(video.getUrl() + expireTime + signSecret);
        String playUrl = video.getUrl() + "?expire=" + expireTime + "&sign=" + sign;
        
        // 4. 查询上次播放位置
        LambdaQueryWrapper<VideoPlayRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VideoPlayRecord::getUserId, userId)
               .eq(VideoPlayRecord::getVideoId, videoId);
        VideoPlayRecord record = playRecordMapper.selectOne(wrapper);
        
        // 5. 构建返回结果
        VideoPlayVO vo = new VideoPlayVO();
        BeanUtils.copyProperties(video, vo);
        vo.setPlayUrl(playUrl);
        vo.setLastPlayPosition(record != null ? record.getLastPlayPosition() : 0);
        
        log.info("视频播放地址生成成功：videoId={}", videoId);
        return vo;
    }
    
    /**
     * 记录播放进度
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordProgress(Long userId, PlayProgressDTO dto) {
        log.info("记录播放进度：userId={}, videoId={}, position={}", 
                 userId, dto.getVideoId(), dto.getLastPlayPosition());
        
        // 1. 查询是否已有记录
        LambdaQueryWrapper<VideoPlayRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VideoPlayRecord::getUserId, userId)
               .eq(VideoPlayRecord::getVideoId, dto.getVideoId());
        VideoPlayRecord record = playRecordMapper.selectOne(wrapper);
        
        if (record == null) {
            // 2. 新建记录
            record = new VideoPlayRecord();
            record.setUserId(userId);
            record.setVideoId(dto.getVideoId());
            record.setCourseId(dto.getCourseId());
            record.setPlayTime(dto.getPlayTime());
            record.setLastPlayPosition(dto.getLastPlayPosition());
            
            // 计算进度百分比（需要视频总时长）
            Video video = videoMapper.selectById(dto.getVideoId());
            if (video != null && video.getDuration() != null && video.getDuration() > 0) {
                record.setProgress((int) ((dto.getLastPlayPosition() * 100.0) / video.getDuration()));
            }
            
            playRecordMapper.insert(record);
        } else {
            // 3. 更新记录
            record.setPlayTime(record.getPlayTime() + dto.getPlayTime());
            record.setLastPlayPosition(dto.getLastPlayPosition());
            
            // 更新进度百分比
            Video video = videoMapper.selectById(dto.getVideoId());
            if (video != null && video.getDuration() != null && video.getDuration() > 0) {
                record.setProgress((int) ((dto.getLastPlayPosition() * 100.0) / video.getDuration()));
            }
            
            playRecordMapper.updateById(record);
        }
        
        log.info("播放进度记录成功：userId={}, videoId={}", userId, dto.getVideoId());
    }
}

