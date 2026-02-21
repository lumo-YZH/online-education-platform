package com.edu.order.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.result.Result;
import com.edu.order.dto.CreateOrderDTO;
import com.edu.order.dto.OrderQueryDTO;
import com.edu.order.service.OrderService;
import com.edu.order.vo.OrderDetailVO;
import com.edu.order.vo.OrderListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 订单控制器
 */
@Slf4j
@RestController
@RequestMapping("/order")
@Tag(name = "订单管理", description = "订单创建、查询、取消等接口")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    /**
     * 创建订单
     */
    @PostMapping("/create")
    @Operation(summary = "创建订单", description = "根据课程ID创建订单")
    public Result<OrderDetailVO> createOrder(@Validated @RequestBody CreateOrderDTO dto, 
                                             HttpServletRequest request) {
        // 从请求属性获取用户ID（由拦截器设置）
        Long userId = (Long) request.getAttribute("userId");
        
        OrderDetailVO vo = orderService.createOrder(userId, dto);
        return Result.success(vo);
    }
    
    /**
     * 订单列表
     */
    @PostMapping("/list")
    @Operation(summary = "订单列表", description = "分页查询当前用户的订单列表")
    public Result<Page<OrderListVO>> getOrderList(@RequestBody OrderQueryDTO dto, HttpServletRequest request) {
        // 从请求属性获取用户ID（由拦截器设置）
        Long userId = (Long) request.getAttribute("userId");
        
        Page<OrderListVO> page = orderService.getOrderList(userId, dto);
        return Result.success(page);
    }
    
    /**
     * 订单详情
     */
    @GetMapping("/{orderId}")
    @Operation(summary = "订单详情", description = "获取订单详细信息")
    @Parameter(name = "orderId", description = "订单ID", required = true)
    public Result<OrderDetailVO> getOrderDetail(@PathVariable Long orderId, 
                                                HttpServletRequest request) {
        // 从请求属性获取用户ID（由拦截器设置）
        Long userId = (Long) request.getAttribute("userId");
        
        OrderDetailVO vo = orderService.getOrderDetail(userId, orderId);
        return Result.success(vo);
    }
    
    /**
     * 取消订单
     */
    @PostMapping("/cancel/{orderId}")
    @Operation(summary = "取消订单", description = "取消未支付的订单")
    @Parameter(name = "orderId", description = "订单ID", required = true)
    public Result<?> cancelOrder(@PathVariable Long orderId, HttpServletRequest request) {
        // 从请求属性获取用户ID（由拦截器设置）
        Long userId = (Long) request.getAttribute("userId");
        
        orderService.cancelOrder(userId, orderId);
        return Result.success("订单已取消");
    }
}
