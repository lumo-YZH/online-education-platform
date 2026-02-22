package com.edu.order.controller;

import com.edu.common.result.Result;
import com.edu.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单内部接口（供其他服务调用）
 */
@Slf4j
@RestController
@RequestMapping("/order/internal")
@Tag(name = "订单内部接口")
public class OrderInternalController {
    
    @Autowired
    private OrderService orderService;
    
    /**
     * 更新订单支付状态
     */
    @PutMapping("/pay-success/{orderNo}")
    @Operation(summary = "更新订单支付状态")
    public Result<?> updatePayStatus(@PathVariable String orderNo,
                                      @RequestParam String tradeNo,
                                      @RequestParam Integer payType) {
        log.info("更新订单支付状态：orderNo={}, tradeNo={}, payType={}", orderNo, tradeNo, payType);
        orderService.updatePayStatus(orderNo, tradeNo, payType);
        return Result.success();
    }
}

