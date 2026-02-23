package com.edu.order.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 发送消息DTO
 */
@Data
public class SendMessageDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 消息类型 1-系统 2-订单 3-课程 4-评论
     */
    private Integer type;
    
    /**
     * 模板编码
     */
    private String templateCode;
    
    /**
     * 模板参数
     */
    private Map<String, Object> params;
    
    /**
     * 链接地址
     */
    private String linkUrl;
}

