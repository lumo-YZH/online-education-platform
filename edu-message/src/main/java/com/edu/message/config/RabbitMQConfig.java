package com.edu.message.config;

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
     * 消息交换机
     */
    @Bean
    public TopicExchange messageExchange() {
        return new TopicExchange("message.exchange", true, false);
    }

    /**
     * 订单消息队列
     */
    @Bean
    public Queue orderMessageQueue() {
        return new Queue("message.order.queue", true);
    }

    /**
     * 课程消息队列
     */
    @Bean
    public Queue courseMessageQueue() {
        return new Queue("message.course.queue", true);
    }

    /**
     * 评论消息队列
     */
    @Bean
    public Queue commentMessageQueue() {
        return new Queue("message.comment.queue", true);
    }

    /**
     * 绑定订单消息队列
     */
    @Bean
    public Binding orderMessageBinding() {
        return BindingBuilder
                .bind(orderMessageQueue())
                .to(messageExchange())
                .with("message.order");
    }

    /**
     * 绑定课程消息队列
     */
    @Bean
    public Binding courseMessageBinding() {
        return BindingBuilder
                .bind(courseMessageQueue())
                .to(messageExchange())
                .with("message.course");
    }

    /**
     * 绑定评论消息队列
     */
    @Bean
    public Binding commentMessageBinding() {
        return BindingBuilder
                .bind(commentMessageQueue())
                .to(messageExchange())
                .with("message.comment");
    }

    /**
     * 消息转换器（JSON）
     */
    @Bean
    public MessageConverter messageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
