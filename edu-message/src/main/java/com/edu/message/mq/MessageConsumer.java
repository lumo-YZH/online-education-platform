package com.edu.message.mq;

import com.edu.message.dto.SendMessageDTO;
import com.edu.message.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 消息队列消费者
 * 监听其他服务发送的消息通知
 */
@Slf4j
@Component
public class MessageConsumer {
    
    @Autowired
    private MessageService messageService;
    
    /**
     * 监听订单消息
     */
    @RabbitListener(queues = "message.order.queue")
    public void handleOrderMessage(SendMessageDTO dto) {
        try {
            log.info("接收到订单消息：{}", dto);
            messageService.sendMessage(dto);
        } catch (Exception e) {
            log.error("处理订单消息失败", e);
        }
    }
    
    /**
     * 监听课程消息
     */
    @RabbitListener(queues = "message.course.queue")
    public void handleCourseMessage(SendMessageDTO dto) {
        try {
            log.info("接收到课程消息：{}", dto);
            messageService.sendMessage(dto);
        } catch (Exception e) {
            log.error("处理课程消息失败", e);
        }
    }
    
    /**
     * 监听评论消息
     */
    @RabbitListener(queues = "message.comment.queue")
    public void handleCommentMessage(SendMessageDTO dto) {
        try {
            log.info("接收到评论消息：{}", dto);
            messageService.sendMessage(dto);
        } catch (Exception e) {
            log.error("处理评论消息失败", e);
        }
    }
}

