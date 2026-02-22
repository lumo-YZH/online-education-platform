package com.edu.pay.feign;

import com.edu.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 订单服务 Feign 客户端
 */
@FeignClient(name = "edu-order", path = "/order/internal")
public interface OrderClient {
    
    /**
     * 更新订单支付状态
     * 
     * @param orderNo 订单号
     * @param tradeNo 第三方交易号
     * @param payType 支付方式
     * @return 结果
     */
    @PutMapping("/pay-success/{orderNo}")
    Result<?> updatePayStatus(@PathVariable("orderNo") String orderNo,
                              @RequestParam("tradeNo") String tradeNo,
                              @RequestParam("payType") Integer payType);
}

