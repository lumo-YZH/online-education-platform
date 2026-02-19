package com.edu.order.controller;

import com.edu.common.result.Result;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/order")
public class OrderController {
    
    @PostMapping("/create")
    public Result<?> createOrder() {
        return Result.success("创建订单接口");
    }
    
    @GetMapping("/{id}")
    public Result<?> getOrderDetail(@PathVariable Long id) {
        return Result.success("订单详情接口");
    }
    
    @GetMapping("/my-orders")
    public Result<?> getMyOrders() {
        return Result.success("我的订单接口");
    }
    
    @PutMapping("/{id}/cancel")
    public Result<?> cancelOrder(@PathVariable Long id) {
        return Result.success("取消订单接口");
    }
}

