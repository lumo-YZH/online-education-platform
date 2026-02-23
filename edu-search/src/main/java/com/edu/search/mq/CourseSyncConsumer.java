package com.edu.search.mq;

import cn.hutool.core.bean.BeanUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.DeleteRequest;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import com.edu.common.constant.MQConstant;
import com.edu.search.document.CourseDocument;
import com.edu.search.dto.CourseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 课程数据同步监听器
 * 监听课程服务发送的消息，同步数据到 ES
 */
@Slf4j
@Component
public class CourseSyncConsumer {
    
    @Autowired
    private ElasticsearchClient esClient;
    
    /**
     * 监听课程新增/更新消息
     */
    @RabbitListener(queues = MQConstant.COURSE_SYNC_QUEUE)
    public void handleCourseSync(CourseMessage message) {
        log.info("接收到课程同步消息：courseId={}, action={}", 
                 message.getId(), message.getAction());
        
        try {
            // 检查 ES 连接
            if (!checkElasticsearchConnection()) {
                log.error("Elasticsearch 未连接，跳过同步：courseId={}", message.getId());
                return;
            }
            
            if ("DELETE".equals(message.getAction())) {
                // 删除 ES 文档
                DeleteRequest request = DeleteRequest.of(d -> d
                    .index("course_index")
                    .id(message.getId().toString())
                );
                esClient.delete(request);
                log.info("删除 ES 文档成功：courseId={}", message.getId());
                
            } else {
                // 新增/更新 ES 文档
                CourseDocument doc = new CourseDocument();
                BeanUtil.copyProperties(message, doc);
                
                IndexRequest<CourseDocument> request = IndexRequest.of(i -> i
                    .index("course_index")
                    .id(doc.getId().toString())
                    .document(doc)
                );
                esClient.index(request);
                log.info("同步 ES 文档成功：courseId={}", message.getId());
            }
            
        } catch (Exception e) {
            log.error("同步 ES 失败：courseId={}", message.getId(), e);
            // 可以记录失败日志，后续补偿
        }
    }
    
    /**
     * 检查 Elasticsearch 连接
     */
    private boolean checkElasticsearchConnection() {
        try {
            esClient.ping();
            return true;
        } catch (Exception e) {
            log.error("Elasticsearch 连接失败：{}", e.getMessage());
            return false;
        }
    }
}

