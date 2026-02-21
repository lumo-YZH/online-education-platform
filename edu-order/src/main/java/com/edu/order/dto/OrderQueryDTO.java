package com.edu.order.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单查询 DTO
 */
@Data
public class OrderQueryDTO implements Serializable {
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 课程名称（模糊查询）
     */
    private String courseName;
    
    /**
     * 订单状态 0-未支付 1-已支付 2-已取消 3-已退款
     */
    private Integer status;
    
    /**
     * 当前页
     */
    private Integer pageNum = 1;
    
    /**
     * 每页大小
     */
    private Integer pageSize = 10;
}

