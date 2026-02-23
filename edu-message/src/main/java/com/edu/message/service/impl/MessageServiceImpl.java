package com.edu.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.exception.BusinessException;
import com.edu.message.constant.MessageConstant;
import com.edu.message.dto.MessageQueryDTO;
import com.edu.message.dto.SendMessageDTO;
import com.edu.message.entity.Message;
import com.edu.message.entity.MessageTemplate;
import com.edu.message.mapper.MessageMapper;
import com.edu.message.mapper.MessageTemplateMapper;
import com.edu.message.service.MessageService;
import com.edu.message.vo.MessageStatVO;
import com.edu.message.vo.MessageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 消息服务实现类
 */
@Slf4j
@Service
public class MessageServiceImpl implements MessageService {
    
    @Autowired
    private MessageMapper messageMapper;
    
    @Autowired
    private MessageTemplateMapper templateMapper;
    
    /**
     * 发送消息（站内信）
     */
    @Override
    public void sendMessage(SendMessageDTO dto) {
        // 1. 查询消息模板
        LambdaQueryWrapper<MessageTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageTemplate::getCode, dto.getTemplateCode())
               .eq(MessageTemplate::getStatus, 1);
        
        MessageTemplate template = templateMapper.selectOne(wrapper);
        if (template == null) {
            log.warn("消息模板不存在：{}", dto.getTemplateCode());
            throw new BusinessException("消息模板不存在");
        }
        
        // 2. 替换模板参数
        String title = replaceParams(template.getTitle(), dto.getParams());
        String content = replaceParams(template.getContent(), dto.getParams());
        
        // 3. 创建消息
        Message message = new Message();
        message.setUserId(dto.getUserId());
        message.setType(dto.getType());
        message.setTitle(title);
        message.setContent(content);
        message.setLinkUrl(dto.getLinkUrl());
        message.setIsRead(0);
        
        messageMapper.insert(message);
        
        log.info("发送站内消息成功：userId={}, type={}, title={}", dto.getUserId(), dto.getType(), title);
    }
    
    /**
     * 分页查询消息列表
     */
    @Override
    public Page<MessageVO> getMessageList(MessageQueryDTO dto, Long userId) {
        // 1. 构建查询条件
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getUserId, userId);
        
        // 消息类型筛选
        if (dto.getType() != null) {
            wrapper.eq(Message::getType, dto.getType());
        }
        
        // 已读状态筛选
        if (dto.getIsRead() != null) {
            wrapper.eq(Message::getIsRead, dto.getIsRead());
        }
        
        // 按创建时间降序
        wrapper.orderByDesc(Message::getCreateTime);
        
        // 2. 分页查询
        Page<Message> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        Page<Message> messagePage = messageMapper.selectPage(page, wrapper);
        
        // 3. 转换为 VO
        Page<MessageVO> voPage = new Page<>();
        BeanUtils.copyProperties(messagePage, voPage, "records");
        
        List<MessageVO> voList = messagePage.getRecords().stream().map(message -> {
            MessageVO vo = new MessageVO();
            BeanUtils.copyProperties(message, vo);
            vo.setTypeName(MessageConstant.getTypeName(message.getType()));
            return vo;
        }).collect(Collectors.toList());
        
        voPage.setRecords(voList);
        
        return voPage;
    }
    
    /**
     * 标记消息为已读
     */
    @Override
    public void markAsRead(Long messageId, Long userId) {
        // 1. 查询消息
        Message message = messageMapper.selectById(messageId);
        if (message == null) {
            throw new BusinessException("消息不存在");
        }
        
        // 2. 校验权限
        if (!message.getUserId().equals(userId)) {
            throw new BusinessException("无权操作该消息");
        }
        
        // 3. 标记为已读
        if (message.getIsRead() == 0) {
            message.setIsRead(1);
            message.setReadTime(LocalDateTime.now());
            messageMapper.updateById(message);
            
            log.info("标记消息为已读：messageId={}, userId={}", messageId, userId);
        }
    }
    
    /**
     * 标记所有消息为已读
     */
    @Override
    public void markAllAsRead(Long userId) {
        // 查询所有未读消息
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getUserId, userId)
               .eq(Message::getIsRead, 0);
        
        List<Message> messages = messageMapper.selectList(wrapper);
        
        // 批量更新
        LocalDateTime now = LocalDateTime.now();
        for (Message message : messages) {
            message.setIsRead(1);
            message.setReadTime(now);
            messageMapper.updateById(message);
        }
        
        log.info("标记所有消息为已读：userId={}, count={}", userId, messages.size());
    }
    
    /**
     * 删除消息
     */
    @Override
    public void deleteMessage(Long messageId, Long userId) {
        // 1. 查询消息
        Message message = messageMapper.selectById(messageId);
        if (message == null) {
            throw new BusinessException("消息不存在");
        }
        
        // 2. 校验权限
        if (!message.getUserId().equals(userId)) {
            throw new BusinessException("无权操作该消息");
        }
        
        // 3. 删除消息
        messageMapper.deleteById(messageId);
        
        log.info("删除消息：messageId={}, userId={}", messageId, userId);
    }
    
    /**
     * 获取消息统计
     */
    @Override
    public MessageStatVO getMessageStat(Long userId) {
        MessageStatVO stat = new MessageStatVO();
        
        // 总未读数
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getUserId, userId)
               .eq(Message::getIsRead, 0);
        Long unreadCount = messageMapper.selectCount(wrapper);
        stat.setUnreadCount(unreadCount);
        
        // 各类型未读数
        stat.setSystemUnreadCount(getUnreadCountByType(userId, MessageConstant.TYPE_SYSTEM));
        stat.setOrderUnreadCount(getUnreadCountByType(userId, MessageConstant.TYPE_ORDER));
        stat.setCourseUnreadCount(getUnreadCountByType(userId, MessageConstant.TYPE_COURSE));
        stat.setCommentUnreadCount(getUnreadCountByType(userId, MessageConstant.TYPE_COMMENT));
        
        return stat;
    }
    
    /**
     * 获取未读消息数
     */
    @Override
    public Long getUnreadCount(Long userId) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getUserId, userId)
               .eq(Message::getIsRead, 0);
        return messageMapper.selectCount(wrapper);
    }
    
    /**
     * 获取指定类型的未读消息数
     */
    private Long getUnreadCountByType(Long userId, Integer type) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getUserId, userId)
               .eq(Message::getType, type)
               .eq(Message::getIsRead, 0);
        return messageMapper.selectCount(wrapper);
    }
    
    /**
     * 替换模板参数
     * 例如：您的订单{orderNo}已创建成功 -> 您的订单202301010001已创建成功
     */
    private String replaceParams(String template, Map<String, Object> params) {
        if (template == null || params == null || params.isEmpty()) {
            return template;
        }
        
        String result = template;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            result = result.replace(placeholder, value);
        }
        
        return result;
    }
}

