package com.edu.pay.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 退款请求 DTO
 */
@Data
@Schema(description = "退款请求参数")
public class RefundDTO {
    
    @Schema(description = "订单号")
    private String orderNo;
    
    @Schema(description = "退款金额")
    private BigDecimal refundAmount;
    
    @Schema(description = "退款原因")
    private String refundReason;
}

