package com.edu.order.mq;

import com.edu.common.constant.MQConstant;
import com.edu.order.entity.OrderInfo;
import com.edu.order.mapper.OrderInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 订单超时消费者
 * 监听订单超时队列，自动取消未支付订单
 */
@Slf4j
@Component
public class OrderTimeoutConsumer {
    
    @Autowired
    private OrderInfoMapper orderInfoMapper;
    
    /**
     * 处理订单超时消息
     * 
     * @param orderId 订单ID
     */
    @RabbitListener(queues = MQConstant.ORDER_TIMEOUT_QUEUE)
    public void handleOrderTimeout(Long orderId) {
        try {
            log.info("收到订单超时消息：orderId={}", orderId);
            
            // 1. 查询订单
            OrderInfo order = orderInfoMapper.selectById(orderId);
            if (order == null) {
                log.warn("订单不存在：orderId={}", orderId);
                return;
            }
            
            // 2. 检查订单状态
            if (order.getStatus() != 0) {
                log.info("订单状态不是未支付，无需取消：orderId={}, status={}", orderId, order.getStatus());
                return;
            }
            
            // 3. 取消订单
            order.setStatus(2); // 已取消
            order.setCancelTime(LocalDateTime.now());
            orderInfoMapper.updateById(order);
            
            log.info("订单超时自动取消成功：orderNo={}, orderId={}", order.getOrderNo(), orderId);
            
        } catch (Exception e) {
            log.error("处理订单超时消息失败：orderId={}", orderId, e);
            // 注意：这里抛出异常会导致消息重新入队，需要根据实际情况处理
            // 可以考虑记录到死信队列或者数据库
        }
    }
}

