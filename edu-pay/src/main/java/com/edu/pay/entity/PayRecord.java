package com.edu.pay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付记录实体类
 */
@Data
@TableName("pay_record")
public class PayRecord {
    
    /**
     * 支付ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 第三方交易号
     */
    private String tradeNo;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 支付方式 1-支付宝 2-微信
     */
    private Integer payType;
    
    /**
     * 支付金额
     */
    private BigDecimal amount;
    
    /**
     * 状态 0-待支付 1-支付成功 2-支付失败
     */
    private Integer status;
    
    /**
     * 支付时间
     */
    private LocalDateTime payTime;
    
    /**
     * 回调时间
     */
    private LocalDateTime callbackTime;
    
    /**
     * 回调内容
     */
    private String callbackContent;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

