package com.edu.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.video.entity.Video;
import org.apache.ibatis.annotations.Mapper;

/**
 * 视频 Mapper
 */
@Mapper
public interface VideoMapper extends BaseMapper<Video> {
}

