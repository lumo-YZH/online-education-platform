 package com.edu.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 创建订单 DTO
 */
@Data
public class CreateOrderDTO implements Serializable {
    
    /**
     * 课程ID
     */
    @NotNull(message = "课程ID不能为空")
    private Long courseId;
    
    /**
     * 优惠券ID（可选）
     */
    private Long couponId;
}

