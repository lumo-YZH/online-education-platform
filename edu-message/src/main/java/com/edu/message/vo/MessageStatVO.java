package com.edu.message.vo;

import lombok.Data;

/**
 * 消息统计VO
 */
@Data
public class MessageStatVO {
    
    /**
     * 未读消息数
     */
    private Long unreadCount;
    
    /**
     * 系统消息未读数
     */
    private Long systemUnreadCount;
    
    /**
     * 订单消息未读数
     */
    private Long orderUnreadCount;
    
    /**
     * 课程消息未读数
     */
    private Long courseUnreadCount;
    
    /**
     * 评论消息未读数
     */
    private Long commentUnreadCount;
}

