package com.edu.pay.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.edu.common.exception.BusinessException;
import com.edu.pay.config.AlipayConfig;
import com.edu.pay.dto.MockPayDTO;
import com.edu.pay.dto.PayDTO;
import com.edu.pay.dto.RefundDTO;
import com.edu.pay.entity.PayRecord;
import com.edu.pay.entity.RefundRecord;
import com.edu.pay.feign.OrderClient;
import com.edu.pay.mapper.PayRecordMapper;
import com.edu.pay.mapper.RefundRecordMapper;
import com.edu.pay.service.PayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付服务实现类
 */
@Slf4j
@Service
public class PayServiceImpl implements PayService {
    
    @Autowired
    private AlipayClient alipayClient;
    
    @Autowired
    private AlipayConfig alipayConfig;
    
    @Autowired
    private PayRecordMapper payRecordMapper;
    
    @Autowired
    private RefundRecordMapper refundRecordMapper;
    
    @Autowired
    private OrderClient orderClient;
    
    /**
     * 支付宝支付
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String alipay(Long userId, PayDTO payDTO) {
        log.info("开始支付宝支付：userId={}, orderNo={}", userId, payDTO.getOrderNo());
        
        try {
            // 1. 检查是否已存在支付记录
            LambdaQueryWrapper<PayRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PayRecord::getOrderNo, payDTO.getOrderNo())
                   .eq(PayRecord::getStatus, 1); // 已支付
            
            Long count = payRecordMapper.selectCount(wrapper);
            if (count > 0) {
                throw new BusinessException("订单已支付，请勿重复支付");
            }
            
            // 2. 创建支付记录
            PayRecord payRecord = new PayRecord();
            payRecord.setOrderNo(payDTO.getOrderNo());
            payRecord.setUserId(userId);
            payRecord.setPayType(payDTO.getPayType());
            payRecord.setAmount(payDTO.getAmount());
            payRecord.setStatus(0); // 待支付
            
            payRecordMapper.insert(payRecord);
            
            // 3. 构建支付宝请求
            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
            
            // 设置回调地址
            request.setNotifyUrl(alipayConfig.getNotifyUrl());
            request.setReturnUrl(alipayConfig.getReturnUrl());
            
            // 设置请求参数
            AlipayTradePagePayModel model = new AlipayTradePagePayModel();
            model.setOutTradeNo(payDTO.getOrderNo()); // 商户订单号
            model.setTotalAmount(payDTO.getAmount().toString()); // 订单金额
            model.setSubject(payDTO.getSubject()); // 订单标题
            model.setBody(payDTO.getBody()); // 订单描述
            model.setProductCode("FAST_INSTANT_TRADE_PAY"); // 产品码
            
            request.setBizModel(model);
            
            // 4. 调用支付宝接口
            AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
            
            if (response.isSuccess()) {
                log.info("支付宝支付表单生成成功：orderNo={}", payDTO.getOrderNo());
                return response.getBody(); // 返回支付表单HTML
            } else {
                log.error("支付宝支付失败：{}", response.getMsg());
                throw new BusinessException("支付失败：" + response.getMsg());
            }
            
        } catch (AlipayApiException e) {
            log.error("支付宝支付异常：orderNo={}", payDTO.getOrderNo(), e);
            throw new BusinessException("支付异常：" + e.getMessage());
        }
    }
    
    /**
     * 支付宝异步回调
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String alipayNotify(Map<String, String> params) {
        log.info("收到支付宝异步回调：{}", params);
        
        try {
            // 1. 验证签名
            boolean signVerified = AlipaySignature.rsaCheckV1(
                params,
                alipayConfig.getPublicKey(),
                alipayConfig.getCharset(),
                alipayConfig.getSignType()
            );
            
            if (!signVerified) {
                log.error("支付宝回调签名验证失败");
                return "failure";
            }
            
            // 2. 获取回调参数
            String tradeNo = params.get("trade_no"); // 支付宝交易号
            String orderNo = params.get("out_trade_no"); // 商户订单号
            String tradeStatus = params.get("trade_status"); // 交易状态
            String totalAmount = params.get("total_amount"); // 交易金额
            
            log.info("支付回调参数：orderNo={}, tradeNo={}, status={}, amount={}", 
                     orderNo, tradeNo, tradeStatus, totalAmount);
            
            // 3. 查询支付记录（幂等性检查）
            LambdaQueryWrapper<PayRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PayRecord::getOrderNo, orderNo);
            PayRecord payRecord = payRecordMapper.selectOne(wrapper);
            
            if (payRecord == null) {
                log.error("支付记录不存在：orderNo={}", orderNo);
                return "failure";
            }
            
            // 如果已经处理过，直接返回成功
            if (payRecord.getStatus() == 1) {
                log.info("订单已处理过，直接返回成功：orderNo={}", orderNo);
                return "success";
            }
            
            // 4. 处理支付成功
            if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                // 更新支付记录
                payRecord.setTradeNo(tradeNo);
                payRecord.setStatus(1); // 支付成功
                payRecord.setPayTime(LocalDateTime.now());
                payRecord.setCallbackTime(LocalDateTime.now());
                payRecord.setCallbackContent(params.toString());
                
                payRecordMapper.updateById(payRecord);
                
                // 5. 通知订单服务更新订单状态
                try {
                    orderClient.updatePayStatus(orderNo, tradeNo, payRecord.getPayType());
                    log.info("订单状态更新成功：orderNo={}", orderNo);
                } catch (Exception e) {
                    log.error("更新订单状态失败：orderNo={}", orderNo, e);
                    // 注意：这里不抛出异常，避免支付宝重复回调
                }
                
                log.info("支付回调处理成功：orderNo={}, tradeNo={}", orderNo, tradeNo);
                return "success";
            }
            
            log.warn("支付状态异常：orderNo={}, status={}", orderNo, tradeStatus);
            return "failure";
            
        } catch (Exception e) {
            log.error("支付回调处理异常", e);
            return "failure";
        }
    }
    
    /**
     * 查询支付状态
     */
    @Override
    public Map<String, Object> queryPayStatus(String orderNo) {
        log.info("查询支付状态：orderNo={}", orderNo);
        
        try {
            // 1. 查询本地支付记录
            LambdaQueryWrapper<PayRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PayRecord::getOrderNo, orderNo);
            PayRecord payRecord = payRecordMapper.selectOne(wrapper);
            
            if (payRecord == null) {
                throw new BusinessException("支付记录不存在");
            }
            
            // 如果本地已支付，直接返回
            if (payRecord.getStatus() == 1) {
                Map<String, Object> result = new HashMap<>();
                result.put("status", 1);
                result.put("tradeNo", payRecord.getTradeNo());
                result.put("payTime", payRecord.getPayTime());
                return result;
            }
            
            // 2. 调用支付宝查询接口
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            AlipayTradeQueryModel model = new AlipayTradeQueryModel();
            model.setOutTradeNo(orderNo);
            request.setBizModel(model);
            
            AlipayTradeQueryResponse response = alipayClient.execute(request);
            
            Map<String, Object> result = new HashMap<>();
            
            if (response.isSuccess()) {
                String tradeStatus = response.getTradeStatus();
                
                // 支付成功
                if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                    // 更新本地支付记录
                    payRecord.setTradeNo(response.getTradeNo());
                    payRecord.setStatus(1);
                    payRecord.setPayTime(LocalDateTime.now());
                    payRecordMapper.updateById(payRecord);
                    
                    // 更新订单状态
                    orderClient.updatePayStatus(orderNo, response.getTradeNo(), payRecord.getPayType());
                    
                    result.put("status", 1);
                    result.put("tradeNo", response.getTradeNo());
                } else {
                    result.put("status", 0);
                }
            } else {
                result.put("status", 0);
            }
            
            return result;
            
        } catch (AlipayApiException e) {
            log.error("查询支付状态异常：orderNo={}", orderNo, e);
            throw new BusinessException("查询支付状态失败");
        }
    }
    
    /**
     * 退款
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean refund(Long userId, RefundDTO refundDTO) {
        log.info("开始退款：userId={}, orderNo={}", userId, refundDTO.getOrderNo());
        
        try {
            // 1. 查询支付记录
            LambdaQueryWrapper<PayRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PayRecord::getOrderNo, refundDTO.getOrderNo())
                   .eq(PayRecord::getUserId, userId)
                   .eq(PayRecord::getStatus, 1); // 已支付
            
            PayRecord payRecord = payRecordMapper.selectOne(wrapper);
            if (payRecord == null) {
                throw new BusinessException("支付记录不存在或未支付");
            }
            
            // 2. 检查是否已退款
            LambdaQueryWrapper<RefundRecord> refundWrapper = new LambdaQueryWrapper<>();
            refundWrapper.eq(RefundRecord::getOrderNo, refundDTO.getOrderNo())
                        .eq(RefundRecord::getStatus, 1); // 退款成功
            
            Long count = refundRecordMapper.selectCount(refundWrapper);
            if (count > 0) {
                throw new BusinessException("订单已退款，请勿重复操作");
            }
            
            // 3. 生成退款单号
            String refundNo = "REFUND" + System.currentTimeMillis() + RandomUtil.randomNumbers(6);
            
            // 4. 创建退款记录
            RefundRecord refundRecord = new RefundRecord();
            refundRecord.setOrderNo(refundDTO.getOrderNo());
            refundRecord.setRefundNo(refundNo);
            refundRecord.setTradeNo(payRecord.getTradeNo());
            refundRecord.setUserId(userId);
            refundRecord.setRefundAmount(refundDTO.getRefundAmount());
            refundRecord.setRefundReason(refundDTO.getRefundReason());
            refundRecord.setStatus(0); // 退款中
            
            refundRecordMapper.insert(refundRecord);
            
            // 5. 调用支付宝退款接口
            AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
            AlipayTradeRefundModel model = new AlipayTradeRefundModel();
            model.setOutTradeNo(refundDTO.getOrderNo()); // 商户订单号
            model.setTradeNo(payRecord.getTradeNo()); // 支付宝交易号
            model.setRefundAmount(refundDTO.getRefundAmount().toString()); // 退款金额
            model.setRefundReason(refundDTO.getRefundReason()); // 退款原因
            model.setOutRequestNo(refundNo); // 退款单号
            
            request.setBizModel(model);
            
            AlipayTradeRefundResponse response = alipayClient.execute(request);
            
            if (response.isSuccess()) {
                // 更新退款记录
                refundRecord.setStatus(1); // 退款成功
                refundRecord.setRefundTime(LocalDateTime.now());
                refundRecordMapper.updateById(refundRecord);
                
                log.info("退款成功：orderNo={}, refundNo={}", refundDTO.getOrderNo(), refundNo);
                return true;
            } else {
                // 更新退款记录
                refundRecord.setStatus(2); // 退款失败
                refundRecordMapper.updateById(refundRecord);
                
                log.error("退款失败：{}", response.getMsg());
                throw new BusinessException("退款失败：" + response.getMsg());
            }
            
        } catch (AlipayApiException e) {
            log.error("退款异常：orderNo={}", refundDTO.getOrderNo(), e);
            throw new BusinessException("退款异常：" + e.getMessage());
        }
    }
    
    /**
     * 模拟支付（仅用于开发环境）
     * 直接更新支付状态，跳过支付宝支付流程
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean mockPay(Long userId, MockPayDTO mockPayDTO) {
        log.info("【模拟支付】开始：userId={}, orderNo={}, success={}", 
                 userId, mockPayDTO.getOrderNo(), mockPayDTO.getSuccess());
        
        // 1. 查询支付记录
        LambdaQueryWrapper<PayRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PayRecord::getOrderNo, mockPayDTO.getOrderNo());
        PayRecord payRecord = payRecordMapper.selectOne(wrapper);
        
        // 2. 如果支付记录不存在，自动创建
        if (payRecord == null) {
            log.info("【模拟支付】支付记录不存在，自动创建：orderNo={}", mockPayDTO.getOrderNo());
            payRecord = new PayRecord();
            payRecord.setOrderNo(mockPayDTO.getOrderNo());
            payRecord.setUserId(userId);
            payRecord.setPayType(1); // 默认支付宝
            payRecord.setAmount(new java.math.BigDecimal("0")); // 金额从订单获取
            payRecord.setStatus(0); // 待支付
            payRecordMapper.insert(payRecord);
        }
        
        // 3. 检查是否已支付（幂等性）
        if (payRecord.getStatus() == 1) {
            log.info("【模拟支付】订单已支付：orderNo={}", mockPayDTO.getOrderNo());
            return true;
        }
        
        // 4. 模拟支付成功
        if (mockPayDTO.getSuccess()) {
            // 生成模拟交易号
            String mockTradeNo = "MOCK" + System.currentTimeMillis() + RandomUtil.randomNumbers(10);
            
            // 更新支付记录
            payRecord.setTradeNo(mockTradeNo);
            payRecord.setStatus(1); // 支付成功
            payRecord.setPayTime(LocalDateTime.now());
            payRecord.setCallbackTime(LocalDateTime.now());
            payRecord.setCallbackContent("模拟支付成功");
            
            payRecordMapper.updateById(payRecord);
            
            // 5. 通知订单服务更新订单状态
            try {
                orderClient.updatePayStatus(mockPayDTO.getOrderNo(), mockTradeNo, payRecord.getPayType());
                log.info("【模拟支付】订单状态更新成功：orderNo={}", mockPayDTO.getOrderNo());
            } catch (Exception e) {
                log.error("【模拟支付】更新订单状态失败：orderNo={}", mockPayDTO.getOrderNo(), e);
                throw new BusinessException("更新订单状态失败");
            }
            
            log.info("【模拟支付】支付成功：orderNo={}, tradeNo={}", mockPayDTO.getOrderNo(), mockTradeNo);
            return true;
        } else {
            // 模拟支付失败
            payRecord.setStatus(2); // 支付失败
            payRecord.setCallbackContent("模拟支付失败");
            payRecordMapper.updateById(payRecord);
            
            log.info("【模拟支付】支付失败：orderNo={}", mockPayDTO.getOrderNo());
            return false;
        }
    }
}

