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
 * 配置订单超时队列和秒杀订单队列
 */
@Configuration
public class RabbitMQConfig {
    
    /**
     * 订单超时队列（真正消费的队列）
     */
    @Bean
    public Queue orderTimeoutQueue() {
        return QueueBuilder.durable(MQConstant.ORDER_TIMEOUT_QUEUE).build();
    }
    
    /**
     * 订单超时交换机
     */
    @Bean
    public DirectExchange orderTimeoutExchange() {
        return new DirectExchange(MQConstant.ORDER_DELAY_EXCHANGE, true, false);
    }
    
    /**
     * 绑定订单超时队列到交换机
     */
    @Bean
    public Binding orderTimeoutBinding() {
        return BindingBuilder
                .bind(orderTimeoutQueue())
                .to(orderTimeoutExchange())
                .with(MQConstant.ORDER_TIMEOUT_ROUTING_KEY);
    }
    
    /**
     * 秒杀订单队列
     */
    @Bean
    public Queue seckillOrderQueue() {
        return QueueBuilder.durable("seckill.order.queue").build();
    }
    
    /**
     * 秒杀交换机
     */
    @Bean
    public TopicExchange seckillExchange() {
        return new TopicExchange("seckill.exchange", true, false);
    }
    
    /**
     * 绑定秒杀订单队列到交换机
     */
    @Bean
    public Binding seckillOrderBinding() {
        return BindingBuilder
                .bind(seckillOrderQueue())
                .to(seckillExchange())
                .with("seckill.order");
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

