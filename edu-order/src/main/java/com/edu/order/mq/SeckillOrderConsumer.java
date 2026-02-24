package com.edu.order.mq;

import com.edu.order.dto.SeckillMessage;
import com.edu.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 秒杀订单消费者
 * 监听秒杀消息队列，异步创建秒杀订单
 */
@Slf4j
@Component
public class SeckillOrderConsumer {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    /**
     * 消费秒杀消息，创建秒杀订单
     * 
     * @param message 秒杀消息
     */
    @RabbitListener(queues = "seckill.order.queue")
    public void handleSeckillOrder(SeckillMessage message) {
        try {
            log.info("收到秒杀消息：userId={}, courseId={}", message.getUserId(), message.getCourseId());
            
            // 调用订单服务创建秒杀订单
            orderService.createSeckillOrder(message);
            
        } catch (Exception e) {
            log.error("处理秒杀消息失败：userId={}, courseId={}", 
                message.getUserId(), message.getCourseId(), e);
            
            // 失败回滚：恢复 Redis 库存
            try {
                String stockKey = "seckill:course:" + message.getCourseId();
                redisTemplate.opsForValue().increment(stockKey);
                
                // 删除用户抢购标记
                String userKey = "seckill:user:" + message.getUserId() + ":" + message.getCourseId();
                redisTemplate.delete(userKey);
                
                log.info("秒杀失败，已回滚Redis库存：courseId={}", message.getCourseId());
            } catch (Exception ex) {
                log.error("回滚Redis库存失败：courseId={}", message.getCourseId(), ex);
            }
            
            // 抛出异常，消息会重新入队（根据实际情况决定是否抛出）
            throw new RuntimeException("创建秒杀订单失败", e);
        }
    }
}

