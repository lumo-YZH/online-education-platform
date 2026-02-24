package com.edu.course.config;

import com.edu.common.constant.MQConstant;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 配置类
 */
@Configuration
public class RabbitMQConfig {
    
    /**
     * 课程同步交换机
     */
    @Bean
    public TopicExchange courseSyncExchange() {
        return new TopicExchange(MQConstant.COURSE_SYNC_EXCHANGE, true, false);
    }
    
    /**
     * 课程同步队列
     */
    @Bean
    public Queue courseSyncQueue() {
        return new Queue(MQConstant.COURSE_SYNC_QUEUE, true);
    }
    
    /**
     * 绑定关系
     */
    @Bean
    public Binding courseSyncBinding() {
        return BindingBuilder
                .bind(courseSyncQueue())
                .to(courseSyncExchange())
                .with(MQConstant.COURSE_SYNC_ROUTING_KEY);
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

    /**
     * 秒杀交换机
     */
    @Bean
    public TopicExchange seckillExchange() {
        return new TopicExchange("seckill.exchange", true, false);
    }

    /**
     * 秒杀订单队列
     */
    @Bean
    public Queue seckillOrderQueue() {
        return new Queue("seckill.order.queue", true);
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
}

