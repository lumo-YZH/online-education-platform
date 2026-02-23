package com.edu.comment.mq;

import com.edu.message.dto.SendMessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 评论消息生产者
 * 发送评论相关的消息通知
 */
@Slf4j
@Component
public class CommentMessageProducer {
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    /**
     * 发送评论消息到消息服务
     * 
     * @param userId 用户ID
     * @param templateCode 模板编码
     * @param params 模板参数
     * @param linkUrl 链接地址
     */
    public void sendCommentMessage(Long userId, String templateCode, Map<String, Object> params, String linkUrl) {
        try {
            SendMessageDTO dto = new SendMessageDTO();
            dto.setUserId(userId);
            dto.setType(4); // 评论消息类型
            dto.setTemplateCode(templateCode);
            dto.setParams(params);
            dto.setLinkUrl(linkUrl);
            
            rabbitTemplate.convertAndSend("message.exchange", "message.comment", dto);
            
            log.info("发送评论消息成功：userId={}, templateCode={}", userId, templateCode);
        } catch (Exception e) {
            log.error("发送评论消息失败：userId={}, templateCode={}", userId, templateCode, e);
        }
    }
}

