package com.edu.pay.controller;

import com.edu.common.result.Result;
import com.edu.pay.dto.MockPayDTO;
import com.edu.pay.dto.PayDTO;
import com.edu.pay.dto.RefundDTO;
import com.edu.pay.service.PayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付控制器
 */
@Slf4j
@RestController
@RequestMapping("/pay")
@Tag(name = "支付管理")
public class PayController {
    
    @Autowired
    private PayService payService;
    
    /**
     * 支付宝支付
     */
    @PostMapping("/alipay")
    @Operation(summary = "支付宝支付")
    public String alipay(@RequestBody PayDTO payDTO, HttpServletRequest request) {
        // 从请求属性获取用户ID（由拦截器设置）
        Long userId = (Long) request.getAttribute("userId");
        return payService.alipay(userId, payDTO);
    }
    
    /**
     * 支付宝异步回调
     */
    @PostMapping("/alipay/notify")
    @Operation(summary = "支付宝异步回调")
    public String alipayNotify(HttpServletRequest request) {
        // 获取所有回调参数
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        
        return payService.alipayNotify(params);
    }
    
    /**
     * 支付宝同步回调
     */
    @GetMapping("/alipay/return")
    @Operation(summary = "支付宝同步回调")
    public String alipayReturn(HttpServletRequest request) {
        log.info("支付宝同步回调");
        
        // 获取订单号
        String orderNo = request.getParameter("out_trade_no");
        
        // 返回支付成功页面（这里简单返回HTML，实际应该跳转到前端页面）
        return "<html><body><h1>支付成功！</h1><p>订单号：" + orderNo + "</p></body></html>";
    }
    
    /**
     * 查询支付状态
     */
    @GetMapping("/status/{orderNo}")
    @Operation(summary = "查询支付状态")
    public Result<Map<String, Object>> queryPayStatus(@PathVariable String orderNo) {
        Map<String, Object> result = payService.queryPayStatus(orderNo);
        return Result.success(result);
    }
    
    /**
     * 退款
     */
    @PostMapping("/refund")
    @Operation(summary = "退款")
    public Result<Boolean> refund(@RequestBody RefundDTO refundDTO, HttpServletRequest request) {
        // 从请求属性获取用户ID（由拦截器设置）
        Long userId = (Long) request.getAttribute("userId");
        boolean success = payService.refund(userId, refundDTO);
        return Result.success(success);
    }
    
    /**
     * 模拟支付（仅用于开发环境）
     */
    @PostMapping("/mock-pay")
    @Operation(summary = "模拟支付（开发环境专用）", description = "跳过支付宝支付流程，直接模拟支付成功")
    public Result<Boolean> mockPay(@RequestBody MockPayDTO mockPayDTO, HttpServletRequest request) {
        // 从请求属性获取用户ID（由拦截器设置）
        Long userId = (Long) request.getAttribute("userId");
        boolean success = payService.mockPay(userId, mockPayDTO);
        return Result.success(success);
    }
}
