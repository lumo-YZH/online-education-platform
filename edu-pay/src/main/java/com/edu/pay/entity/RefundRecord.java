package com.edu.pay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 退款记录实体类
 */
@Data
@TableName("refund_record")
public class RefundRecord {
    
    /**
     * 退款ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 退款单号
     */
    private String refundNo;
    
    /**
     * 第三方交易号
     */
    private String tradeNo;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 退款金额
     */
    private BigDecimal refundAmount;
    
    /**
     * 退款原因
     */
    private String refundReason;
    
    /**
     * 状态 0-退款中 1-退款成功 2-退款失败
     */
    private Integer status;
    
    /**
     * 退款时间
     */
    private LocalDateTime refundTime;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

