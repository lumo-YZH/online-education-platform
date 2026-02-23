package com.edu.message.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.message.dto.MessageQueryDTO;
import com.edu.message.dto.SendMessageDTO;
import com.edu.message.vo.MessageStatVO;
import com.edu.message.vo.MessageVO;

import java.util.List;

/**
 * 消息服务接口
 */
public interface MessageService {
    
    /**
     * 发送消息（站内信）
     */
    void sendMessage(SendMessageDTO dto);
    
    /**
     * 分页查询消息列表
     */
    Page<MessageVO> getMessageList(MessageQueryDTO dto, Long userId);
    
    /**
     * 标记消息为已读
     */
    void markAsRead(Long messageId, Long userId);
    
    /**
     * 标记所有消息为已读
     */
    void markAllAsRead(Long userId);
    
    /**
     * 删除消息
     */
    void deleteMessage(Long messageId, Long userId);
    
    /**
     * 获取消息统计
     */
    MessageStatVO getMessageStat(Long userId);
    
    /**
     * 获取未读消息数
     */
    Long getUnreadCount(Long userId);
}

