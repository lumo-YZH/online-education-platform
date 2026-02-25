package com.edu.order.controller;

import com.edu.common.result.Result;
import com.edu.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单内部接口控制器
 * 提供给其他服务调用的内部接口
 */
@Slf4j
@RestController
@RequestMapping("/order/internal")
@Tag(name = "订单内部接口", description = "提供给其他服务调用")
public class OrderInternalController {
    
    @Autowired
    private OrderService orderService;
    
    /**
     * 检查用户是否购买了课程
     */
    @GetMapping("/check-purchased")
    @Operation(summary = "检查用户是否购买了课程", description = "内部接口")
    public Result<Boolean> checkUserPurchased(
            @RequestParam("userId") Long userId,
            @RequestParam("courseId") Long courseId) {
        
        log.info("检查用户是否购买课程：userId={}, courseId={}", userId, courseId);
        boolean purchased = orderService.checkUserPurchased(userId, courseId);
        return Result.success(purchased);
    }
    
    /**
     * 查询用户的秒杀订单号
     */
    @GetMapping("/seckill-order")
    @Operation(summary = "查询秒杀订单号", description = "内部接口")
    public Result<String> getSeckillOrderNo(
            @RequestParam("userId") Long userId,
            @RequestParam("courseId") Long courseId) {
        
        log.info("查询秒杀订单ID：userId={}, courseId={}", userId, courseId);
        String orderNo = orderService.getSeckillOrderNo(userId, courseId);
        return Result.success(orderNo);
    }
    
    /**
     * 获取用户已购买的课程ID列表
     */
    @GetMapping("/purchased-courses")
    @Operation(summary = "获取用户已购买的课程ID列表", description = "内部接口")
    public Result<java.util.List<Long>> getUserPurchasedCourseIds(@RequestParam("userId") Long userId) {
        log.info("查询用户已购买的课程ID列表：userId={}", userId);
        java.util.List<Long> courseIds = orderService.getUserPurchasedCourseIds(userId);
        return Result.success(courseIds);
    }
}
