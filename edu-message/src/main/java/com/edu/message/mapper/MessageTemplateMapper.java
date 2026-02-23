package com.edu.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.message.entity.MessageTemplate;
import org.apache.ibatis.annotations.Mapper;

/**
 * 消息模板Mapper
 */
@Mapper
public interface MessageTemplateMapper extends BaseMapper<MessageTemplate> {
}

