package com.edu.order.mq;

import com.edu.order.dto.SendMessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 订单消息生产者
 * 发送订单相关的消息通知
 */
@Slf4j
@Component
public class OrderMessageProducer {
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    /**
     * 发送订单消息到消息服务
     * 
     * @param userId 用户ID
     * @param templateCode 模板编码
     * @param params 模板参数
     * @param linkUrl 链接地址
     */
    public void sendOrderMessage(Long userId, String templateCode, Map<String, Object> params, String linkUrl) {
        try {
            SendMessageDTO dto = new SendMessageDTO();
            dto.setUserId(userId);
            dto.setType(2); // 订单消息类型
            dto.setTemplateCode(templateCode);
            dto.setParams(params);
            dto.setLinkUrl(linkUrl);
            
            rabbitTemplate.convertAndSend("message.exchange", "message.order", dto);
            
            log.info("发送订单消息成功：userId={}, templateCode={}", userId, templateCode);
        } catch (Exception e) {
            log.error("发送订单消息失败：userId={}, templateCode={}", userId, templateCode, e);
        }
    }
}

