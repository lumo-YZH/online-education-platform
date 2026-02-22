package com.edu.video.feign;

import com.edu.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 订单服务 Feign 客户端
 */
@FeignClient(name = "edu-order", path = "/order/internal", fallback = OrderClient.OrderClientFallback.class)
public interface OrderClient {
    
    /**
     * 检查用户是否购买了课程
     * 
     * @param userId 用户ID
     * @param courseId 课程ID
     * @return 是否购买
     */
    @GetMapping("/check-purchased")
    Result<Boolean> checkUserPurchased(@RequestParam("userId") Long userId,
                                        @RequestParam("courseId") Long courseId);
    
    /**
     * Feign 降级处理
     */
    @Slf4j
    @Component
    class OrderClientFallback implements OrderClient {
        @Override
        public Result<Boolean> checkUserPurchased(Long userId, Long courseId) {
            log.error("【Feign降级】调用订单服务失败：userId={}, courseId={}", userId, courseId);
            // 降级返回错误，提示用户稍后重试
            return Result.error(500, "订单服务暂时不可用，请稍后重试");
        }
    }
}

