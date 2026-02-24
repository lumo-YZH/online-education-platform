package com.edu.course.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 秒杀结果VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeckillVO {
    
    /**
     * 是否成功
     */
    private Boolean success;
    
    /**
     * 消息
     */
    private String message;
    
    /**
     * 订单ID（成功时返回）
     */
    private Long orderId;
    
    public static SeckillVO success(String message) {
        return new SeckillVO(true, message, null);
    }
    
    public static SeckillVO success(String message, Long orderId) {
        return new SeckillVO(true, message, orderId);
    }
    
    public static SeckillVO fail(String message) {
        return new SeckillVO(false, message, null);
    }
}

