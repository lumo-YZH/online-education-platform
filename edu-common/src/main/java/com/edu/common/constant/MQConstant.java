package com.edu.common.constant;

/**
 * MQ 常量
 */
public class MQConstant {
    
    // 秒杀订单
    public static final String SECKILL_EXCHANGE = "seckill.exchange";
    public static final String SECKILL_QUEUE = "seckill.order.queue";
    public static final String SECKILL_ROUTING_KEY = "seckill.order";
    
    // 订单超时
    public static final String ORDER_DELAY_EXCHANGE = "order.delay.exchange";
    public static final String ORDER_TIMEOUT_QUEUE = "order.timeout.queue";
    public static final String ORDER_TIMEOUT_ROUTING_KEY = "order.timeout";
    
    // 消息通知
    public static final String MESSAGE_EXCHANGE = "message.exchange";
    public static final String MESSAGE_QUEUE = "message.queue";
    public static final String MESSAGE_ROUTING_KEY = "message.send";
}

