package com.edu.order.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单详情 VO
 */
@Data
public class OrderDetailVO implements Serializable {
    
    /**
     * 订单ID
     */
    private Long id;
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 课程ID
     */
    private Long courseId;
    
    /**
     * 课程名称
     */
    private String courseName;
    
    /**
     * 课程封面
     */
    private String courseCover;
    
    /**
     * 订单金额
     */
    private BigDecimal amount;
    
    /**
     * 优惠券ID
     */
    private Long couponId;
    
    /**
     * 优惠金额
     */
    private BigDecimal couponAmount;
    
    /**
     * 实付金额
     */
    private BigDecimal payAmount;
    
    /**
     * 支付方式 1-支付宝 2-微信
     */
    private Integer payType;
    
    /**
     * 支付方式描述
     */
    private String payTypeDesc;
    
    /**
     * 状态 0-未支付 1-已支付 2-已取消 3-已退款
     */
    private Integer status;
    
    /**
     * 状态描述
     */
    private String statusDesc;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 支付时间
     */
    private LocalDateTime payTime;
    
    /**
     * 取消时间
     */
    private LocalDateTime cancelTime;
    
    /**
     * 退款时间
     */
    private LocalDateTime refundTime;
    
    /**
     * 订单明细列表
     */
    private List<OrderItemVO> items;
    
    /**
     * 订单明细 VO
     */
    @Data
    public static class OrderItemVO implements Serializable {
        
        /**
         * 明细ID
         */
        private Long id;
        
        /**
         * 课程ID
         */
        private Long courseId;
        
        /**
         * 课程名称
         */
        private String courseName;
        
        /**
         * 价格
         */
        private BigDecimal price;
        
        /**
         * 数量
         */
        private Integer quantity;
    }
}

