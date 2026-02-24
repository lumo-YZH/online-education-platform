package com.edu.course.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 秒杀结果VO
 */
@Data
@Builder
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
     * 订单号（成功时返回）
     */
    private String orderNo;
    
    public static SeckillVO success(String message) {
        return new SeckillVO(true, message, null);
    }
    
    public static SeckillVO success(String message, String orderNo) {
        return new SeckillVO(true, message, orderNo);
    }
    
    public static SeckillVO fail(String message) {
        return new SeckillVO(false, message, null);
    }
}

