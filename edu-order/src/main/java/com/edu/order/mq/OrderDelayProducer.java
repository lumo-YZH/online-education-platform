package com.edu.order.mq;

import com.edu.common.constant.MQConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 订单延迟消息生产者
 * 使用消息 TTL 实现延迟（不需要延迟插件）
 */
@Slf4j
@Component
public class OrderDelayProducer {
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    /**
     * 订单超时时间（毫秒）：30分钟
     */
    private static final Long ORDER_TIMEOUT_DELAY = 30 * 60 * 1000L;
    
    /**
     * 发送订单超时延迟消息
     * 使用消息 TTL 实现延迟
     * 
     * @param orderId 订单ID
     */
    public void sendOrderTimeoutMessage(Long orderId) {
        try {
            rabbitTemplate.convertAndSend(
                    MQConstant.ORDER_DELAY_EXCHANGE,
                    MQConstant.ORDER_TIMEOUT_ROUTING_KEY,
                    orderId,
                    message -> {
                        // 设置消息过期时间（TTL）
                        message.getMessageProperties().setExpiration(ORDER_TIMEOUT_DELAY.toString());
                        return message;
                    }
            );
            
            log.info("订单超时延迟消息已发送：orderId={}, delay={}ms", orderId, ORDER_TIMEOUT_DELAY);
        } catch (Exception e) {
            log.error("发送订单超时延迟消息失败：orderId={}", orderId, e);
        }
    }
}

