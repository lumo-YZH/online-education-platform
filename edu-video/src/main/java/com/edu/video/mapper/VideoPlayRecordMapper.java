package com.edu.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.video.entity.VideoPlayRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 视频播放记录 Mapper
 */
@Mapper
public interface VideoPlayRecordMapper extends BaseMapper<VideoPlayRecord> {
}

