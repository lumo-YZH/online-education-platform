package com.edu.order.config;

import com.edu.common.constant.MQConstant;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * RabbitMQ 配置类
 * 配置订单超时延迟队列和消息转换器
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
    
    /**
     * 消息转换器（JSON）
     * 支持 LocalDateTime 等 Java 8 时间类型
     */
    @Bean
    public MessageConverter messageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        // 注册 JavaTimeModule 支持 Java 8 时间类型
        objectMapper.registerModule(new JavaTimeModule());
        // 禁用将日期写为时间戳
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}

