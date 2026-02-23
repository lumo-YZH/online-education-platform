package com.edu.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.message.entity.Message;
import org.apache.ibatis.annotations.Mapper;

/**
 * 消息Mapper
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {
}

