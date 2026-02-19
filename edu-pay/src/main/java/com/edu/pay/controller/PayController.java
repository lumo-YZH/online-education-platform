package com.edu.pay.controller;

import com.edu.common.result.Result;
import org.springframework.web.bind.annotation.*;

/**
 * 支付控制器
 */
@RestController
@RequestMapping("/pay")
public class PayController {
    
    @PostMapping("/alipay")
    public Result<?> alipay() {
        return Result.success("支付宝支付接口");
    }
    
    @PostMapping("/callback")
    public Result<?> payCallback() {
        return Result.success("支付回调接口");
    }
    
    @PostMapping("/refund")
    public Result<?> refund() {
        return Result.success("退款接口");
    }
}

