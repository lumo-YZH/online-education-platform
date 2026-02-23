package com.edu.course.mq;

import com.edu.common.constant.MQConstant;
import com.edu.course.dto.CourseSyncMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 课程同步消息生产者
 */
@Slf4j
@Component
public class CourseSyncProducer {
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    /**
     * 发送课程同步消息
     * 
     * @param message 课程同步消息
     */
    public void sendCourseSyncMessage(CourseSyncMessage message) {
        try {
            rabbitTemplate.convertAndSend(
                    MQConstant.COURSE_SYNC_EXCHANGE,
                    MQConstant.COURSE_SYNC_ROUTING_KEY,
                    message
            );
            
            log.info("课程同步消息已发送：courseId={}, action={}", message.getId(), message.getAction());
        } catch (Exception e) {
            log.error("发送课程同步消息失败：courseId={}", message.getId(), e);
        }
    }
}

