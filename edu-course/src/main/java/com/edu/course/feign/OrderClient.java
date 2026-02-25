package com.edu.course.feign;

import com.edu.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;

/**
 * 订单服务 Feign 客户端
 */
@FeignClient(name = "edu-order", path = "/order/internal", fallback = OrderClient.OrderClientFallback.class)
public interface OrderClient {
    
    /**
     * 查询用户的秒杀订单号
     * 
     * @param userId 用户ID
     * @param courseId 课程ID
     * @return 订单号（如果订单还未创建则返回null）
     */
    @GetMapping("/seckill-order")
    Result<String> getSeckillOrderNo(@RequestParam("userId") Long userId,
                                    @RequestParam("courseId") Long courseId);
    
    /**
     * 获取用户已购买的课程ID列表
     * 
     * @param userId 用户ID
     * @return 课程ID列表
     */
    @GetMapping("/purchased-courses")
    Result<List<Long>> getUserPurchasedCourseIds(@RequestParam("userId") Long userId);
    
    /**
     * Feign 降级处理
     */
    @Slf4j
    @Component
    class OrderClientFallback implements OrderClient {
        @Override
        public Result<String> getSeckillOrderNo(Long userId, Long courseId) {
            log.error("【Feign降级】调用订单服务失败：userId={}, courseId={}", userId, courseId);
            // 降级返回null，表示订单还在生成中
            return Result.success(null);
        }
        
        @Override
        public Result<List<Long>> getUserPurchasedCourseIds(Long userId) {
            log.error("【Feign降级】调用订单服务获取已购买课程失败：userId={}", userId);
            // 降级返回空列表
            return Result.success(Collections.emptyList());
        }
    }
}

