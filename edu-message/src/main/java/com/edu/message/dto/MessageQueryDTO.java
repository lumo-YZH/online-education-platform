package com.edu.message.dto;

import lombok.Data;

/**
 * 消息查询DTO
 */
@Data
public class MessageQueryDTO {
    
    /**
     * 消息类型 1-系统 2-订单 3-课程 4-评论
     */
    private Integer type;
    
    /**
     * 是否已读 1-是 0-否
     */
    private Integer isRead;
    
    /**
     * 页码
     */
    private Integer pageNum = 1;
    
    /**
     * 每页数量
     */
    private Integer pageSize = 10;
}

