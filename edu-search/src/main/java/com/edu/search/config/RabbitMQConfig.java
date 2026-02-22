package com.edu.search.config;

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
        return new TopicExchange("course.sync.exchange", true, false);
    }
    
    /**
     * 课程同步队列
     */
    @Bean
    public Queue courseSyncQueue() {
        return new Queue("course.sync.queue", true);
    }
    
    /**
     * 绑定关系
     */
    @Bean
    public Binding courseSyncBinding() {
        return BindingBuilder
            .bind(courseSyncQueue())
            .to(courseSyncExchange())
            .with("course.sync.#");
    }
    
    /**
     * 消息转换器（JSON）
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}

