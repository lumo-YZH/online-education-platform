package com.edu.order.feign;

import com.edu.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * 课程服务 Feign 客户端
 */
@FeignClient(name = "edu-course", path = "/course")
public interface CourseClient {
    
    /**
     * 获取课程信息（用于创建订单）
     */
    @GetMapping("/{courseId}")
    Result<Map<String, Object>> getCourseDetail(@PathVariable("courseId") Long courseId);
}

