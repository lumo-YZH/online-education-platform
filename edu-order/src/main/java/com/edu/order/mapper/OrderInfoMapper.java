package com.edu.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.order.entity.OrderInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单 Mapper
 */
@Mapper
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {
}

