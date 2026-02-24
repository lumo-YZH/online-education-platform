package com.edu.course.mq;

import com.edu.course.dto.SeckillMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 秒杀消息生产者
 * 发送秒杀消息到 MQ，由订单服务消费
 */
@Slf4j
@Component
public class SeckillProducer {
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    /**
     * 发送秒杀消息
     * 
     * @param message 秒杀消息
     */
    public void sendSeckillMessage(SeckillMessage message) {
        try {
            rabbitTemplate.convertAndSend(
                "seckill.exchange",
                "seckill.order",
                message
            );
            
            log.info("发送秒杀消息成功：userId={}, courseId={}", message.getUserId(), message.getCourseId());
        } catch (Exception e) {
            log.error("发送秒杀消息失败：userId={}, courseId={}", message.getUserId(), message.getCourseId(), e);
            throw new RuntimeException("发送秒杀消息失败", e);
        }
    }
}

