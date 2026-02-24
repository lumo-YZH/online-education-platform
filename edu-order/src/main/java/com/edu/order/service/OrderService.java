package com.edu.order.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.order.dto.CreateOrderDTO;
import com.edu.order.dto.OrderQueryDTO;
import com.edu.order.dto.SeckillMessage;
import com.edu.order.vo.OrderDetailVO;
import com.edu.order.vo.OrderListVO;

/**
 * 订单服务接口
 */
public interface OrderService {
    
    /**
     * 创建订单
     */
    OrderDetailVO createOrder(Long userId, CreateOrderDTO dto);
    
    /**
     * 创建秒杀订单
     */
    OrderDetailVO createSeckillOrder(SeckillMessage message);
    
    /**
     * 分页查询订单列表
     */
    Page<OrderListVO> getOrderList(Long userId, OrderQueryDTO dto);
    
    /**
     * 获取订单详情
     */
    OrderDetailVO getOrderDetail(Long userId, Long orderId);
    
    /**
     * 取消订单
     */
    void cancelOrder(Long userId, Long orderId);
    
    /**
     * 更新订单支付状态
     */
    void updatePayStatus(String orderNo, String tradeNo, Integer payType);
    
    /**
     * 检查用户是否购买了课程
     */
    boolean checkUserPurchased(Long userId, Long courseId);
    
    /**
     * 查询用户的秒杀订单号
     */
    String getSeckillOrderNo(Long userId, Long courseId);
}

