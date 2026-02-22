package com.edu.pay.service;

import com.edu.pay.dto.MockPayDTO;
import com.edu.pay.dto.PayDTO;
import com.edu.pay.dto.RefundDTO;

import java.util.Map;

/**
 * 支付服务接口
 */
public interface PayService {
    
    /**
     * 支付宝支付
     * 
     * @param userId 用户ID
     * @param payDTO 支付参数
     * @return 支付表单
     */
    String alipay(Long userId, PayDTO payDTO);
    
    /**
     * 支付宝异步回调
     * 
     * @param params 回调参数
     * @return 处理结果
     */
    String alipayNotify(Map<String, String> params);
    
    /**
     * 查询支付状态
     * 
     * @param orderNo 订单号
     * @return 支付状态
     */
    Map<String, Object> queryPayStatus(String orderNo);
    
    /**
     * 退款
     * 
     * @param userId 用户ID
     * @param refundDTO 退款参数
     * @return 退款结果
     */
    boolean refund(Long userId, RefundDTO refundDTO);
    
    /**
     * 模拟支付（仅用于开发环境）
     * 
     * @param userId 用户ID
     * @param mockPayDTO 模拟支付参数
     * @return 支付结果
     */
    boolean mockPay(Long userId, MockPayDTO mockPayDTO);
}

