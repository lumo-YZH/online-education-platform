package com.edu.order.config;

import com.edu.common.constant.MQConstant;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * RabbitMQ 配置类
 * 配置订单超时延迟队列
 */
@Configuration
public class RabbitMQConfig {
    
    /**
     * 延迟交换机
     * 使用 x-delayed-message 类型（需要安装延迟插件）
     */
    @Bean
    public CustomExchange orderDelayExchange() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
        
        return new CustomExchange(
                MQConstant.ORDER_DELAY_EXCHANGE,
                "x-delayed-message",
                true,
                false,
                args
        );
    }
    
    /**
     * 订单超时队列
     */
    @Bean
    public Queue orderTimeoutQueue() {
        return QueueBuilder.durable(MQConstant.ORDER_TIMEOUT_QUEUE).build();
    }
    
    /**
     * 绑定队列到延迟交换机
     */
    @Bean
    public Binding orderTimeoutBinding() {
        return BindingBuilder
                .bind(orderTimeoutQueue())
                .to(orderDelayExchange())
                .with(MQConstant.ORDER_TIMEOUT_ROUTING_KEY)
                .noargs();
    }
}

