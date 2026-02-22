package com.edu.pay.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 支付请求 DTO
 */
@Data
@Schema(description = "支付请求参数")
public class PayDTO {
    
    @Schema(description = "订单号")
    private String orderNo;
    
    @Schema(description = "支付金额")
    private BigDecimal amount;
    
    @Schema(description = "支付方式 1-支付宝 2-微信")
    private Integer payType;
    
    @Schema(description = "商品名称")
    private String subject;
    
    @Schema(description = "商品描述")
    private String body;
}

