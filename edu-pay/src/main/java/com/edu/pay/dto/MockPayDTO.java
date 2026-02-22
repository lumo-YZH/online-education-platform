package com.edu.pay.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 模拟支付请求 DTO（仅用于开发环境）
 */
@Data
@Schema(description = "模拟支付请求参数")
public class MockPayDTO {
    
    @Schema(description = "订单号")
    private String orderNo;
    
    @Schema(description = "是否支付成功 true-成功 false-失败")
    private Boolean success = true;
}

