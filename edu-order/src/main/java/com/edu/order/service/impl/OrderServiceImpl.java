package com.edu.order.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.exception.BusinessException;
import com.edu.common.result.Result;
import com.edu.order.dto.CreateOrderDTO;
import com.edu.order.dto.OrderQueryDTO;
import com.edu.order.entity.OrderInfo;
import com.edu.order.entity.OrderItem;
import com.edu.order.feign.CourseClient;
import com.edu.order.mapper.OrderInfoMapper;
import com.edu.order.mapper.OrderItemMapper;
import com.edu.order.mq.OrderDelayProducer;
import com.edu.order.service.OrderService;
import com.edu.order.vo.OrderDetailVO;
import com.edu.order.vo.OrderListVO;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单服务实现类
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {
    
    @Autowired
    private OrderInfoMapper orderInfoMapper;
    
    @Autowired
    private OrderItemMapper orderItemMapper;
    
    @Autowired
    private CourseClient courseClient;
    
    @Autowired
    private OrderDelayProducer orderDelayProducer;
    
    /**
     * 创建订单
     */
    @Override
    @GlobalTransactional(name = "create-order", rollbackFor = Exception.class)
    public OrderDetailVO createOrder(Long userId, CreateOrderDTO dto) {
        log.info("开始创建订单：userId={}, courseId={}", userId, dto.getCourseId());
        
        // 1. 查询课程信息
        Result<Map<String, Object>> courseResult = courseClient.getCourseDetail(dto.getCourseId());
        if (courseResult.getCode() != 200 || courseResult.getData() == null) {
            throw new BusinessException("课程不存在");
        }
        
        Map<String, Object> courseData = courseResult.getData();
        String courseName = (String) courseData.get("name");
        String courseCover = (String) courseData.get("cover");
        BigDecimal price = new BigDecimal(courseData.get("price").toString());
        Integer status = (Integer) courseData.get("status");
        
        // 2. 检查课程状态
        if (status == 0) {
            throw new BusinessException("课程已下架");
        }
        
        // 3. 检查用户是否已购买
        LambdaQueryWrapper<OrderInfo> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(OrderInfo::getUserId, userId)
                   .eq(OrderInfo::getCourseId, dto.getCourseId())
                   .eq(OrderInfo::getStatus, 1); // 已支付
        
        Long count = orderInfoMapper.selectCount(checkWrapper);
        if (count > 0) {
            throw new BusinessException("您已购买该课程");
        }
        
        // 4. 扣减课程库存（分布式事务）
        Result<?> deductResult = courseClient.deductStock(dto.getCourseId(), 1);
        if (deductResult.getCode() != 200) {
            throw new BusinessException("扣减库存失败：" + deductResult.getMessage());
        }
        
        // 5. 生成订单号
        String orderNo = generateOrderNo();
        
        // 6. 计算订单金额（暂不考虑优惠券）
        BigDecimal amount = price;
        BigDecimal couponAmount = BigDecimal.ZERO;
        BigDecimal payAmount = amount.subtract(couponAmount);
        
        // 7. 创建订单主表
        OrderInfo order = new OrderInfo();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setCourseId(dto.getCourseId());
        order.setCourseName(courseName);
        order.setCourseCover(courseCover);
        order.setAmount(amount);
        order.setCouponId(dto.getCouponId());
        order.setCouponAmount(couponAmount);
        order.setPayAmount(payAmount);
        order.setStatus(0); // 未支付
        
        orderInfoMapper.insert(order);
        
        // 8. 创建订单明细表
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(order.getId());
        orderItem.setCourseId(dto.getCourseId());
        orderItem.setCourseName(courseName);
        orderItem.setPrice(price);
        orderItem.setQuantity(1); // 课程数量固定为1
        
        orderItemMapper.insert(orderItem);
        
        // 9. 发送订单超时延迟消息（30分钟后自动取消未支付订单）
        orderDelayProducer.sendOrderTimeoutMessage(order.getId());
        
        log.info("订单创建成功：orderNo={}, userId={}, courseId={}", orderNo, userId, dto.getCourseId());
        
        // 10. 返回订单详情
        return getOrderDetail(userId, order.getId());
    }
    
    /**
     * 分页查询订单列表
     */
    @Override
    public Page<OrderListVO> getOrderList(Long userId, OrderQueryDTO dto) {
        // 1. 构建查询条件
        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderInfo::getUserId, userId);
        
        // 订单号查询
        if (dto.getOrderNo() != null && !dto.getOrderNo().isEmpty()) {
            wrapper.eq(OrderInfo::getOrderNo, dto.getOrderNo());
        }
        
        // 课程名称模糊查询
        if (dto.getCourseName() != null && !dto.getCourseName().isEmpty()) {
            wrapper.like(OrderInfo::getCourseName, dto.getCourseName());
        }
        
        // 订单状态筛选
        if (dto.getStatus() != null) {
            wrapper.eq(OrderInfo::getStatus, dto.getStatus());
        }
        
        // 按创建时间降序
        wrapper.orderByDesc(OrderInfo::getCreateTime);
        
        // 2. 分页查询
        Page<OrderInfo> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        Page<OrderInfo> orderPage = orderInfoMapper.selectPage(page, wrapper);
        
        // 3. 转换为 VO
        Page<OrderListVO> voPage = new Page<>();
        BeanUtils.copyProperties(orderPage, voPage, "records");
        
        List<OrderListVO> voList = orderPage.getRecords().stream().map(order -> {
            OrderListVO vo = new OrderListVO();
            BeanUtils.copyProperties(order, vo);
            vo.setStatusDesc(getStatusDesc(order.getStatus()));
            return vo;
        }).collect(Collectors.toList());
        
        voPage.setRecords(voList);
        
        return voPage;
    }
    
    /**
     * 获取订单详情
     */
    @Override
    public OrderDetailVO getOrderDetail(Long userId, Long orderId) {
        // 1. 查询订单主表
        OrderInfo order = orderInfoMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        // 2. 验证订单归属
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权访问该订单");
        }
        
        // 3. 查询订单明细
        LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(OrderItem::getOrderId, orderId);
        List<OrderItem> orderItems = orderItemMapper.selectList(itemWrapper);
        
        // 4. 转换为 VO
        OrderDetailVO vo = new OrderDetailVO();
        BeanUtils.copyProperties(order, vo);
        vo.setStatusDesc(getStatusDesc(order.getStatus()));
        vo.setPayTypeDesc(getPayTypeDesc(order.getPayType()));
        
        // 5. 转换订单明细
        List<OrderDetailVO.OrderItemVO> itemVOList = orderItems.stream().map(item -> {
            OrderDetailVO.OrderItemVO itemVO = new OrderDetailVO.OrderItemVO();
            BeanUtils.copyProperties(item, itemVO);
            return itemVO;
        }).collect(Collectors.toList());
        
        vo.setItems(itemVOList);
        
        return vo;
    }
    
    /**
     * 取消订单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long userId, Long orderId) {
        // 1. 查询订单
        OrderInfo order = orderInfoMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        // 2. 验证订单归属
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作该订单");
        }
        
        // 3. 检查订单状态
        if (order.getStatus() != 0) {
            throw new BusinessException("只能取消未支付的订单");
        }
        
        // 4. 更新订单状态
        order.setStatus(2); // 已取消
        order.setCancelTime(LocalDateTime.now());
        orderInfoMapper.updateById(order);

        // 5. 恢复课程库存
        try {
            courseClient.restoreStock(order.getCourseId(), 1);
            log.info("课程库存已恢复：courseId={}", order.getCourseId());
        } catch (Exception e) {
            log.error("恢复课程库存失败：courseId={}", order.getCourseId(), e);
        }

        log.info("订单已取消：orderNo={}, userId={}", order.getOrderNo(), userId);
    }
    
    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        // 格式：ORDER + 时间戳 + 6位随机数
        return "ORDER" + System.currentTimeMillis() + RandomUtil.randomNumbers(6);
    }
    
    /**
     * 获取订单状态描述
     */
    private String getStatusDesc(Integer status) {
        if (status == null) {
            return "";
        }
        switch (status) {
            case 0:
                return "待支付";
            case 1:
                return "已支付";
            case 2:
                return "已取消";
            case 3:
                return "已退款";
            default:
                return "未知";
        }
    }
    
    /**
     * 获取支付方式描述
     */
    private String getPayTypeDesc(Integer payType) {
        if (payType == null) {
            return "";
        }
        switch (payType) {
            case 1:
                return "支付宝";
            case 2:
                return "微信支付";
            default:
                return "";
        }
    }
    
    /**
     * 更新订单支付状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePayStatus(String orderNo, String tradeNo, Integer payType) {
        log.info("更新订单支付状态：orderNo={}, tradeNo={}, payType={}", orderNo, tradeNo, payType);
        
        // 1. 查询订单
        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderInfo::getId, orderNo);
        OrderInfo order = orderInfoMapper.selectOne(wrapper);
        
        if (order == null) {
            log.error("订单不存在：orderNo={}", orderNo);
            throw new BusinessException("订单不存在");
        }
        
        // 2. 检查订单状态（幂等性）
        if (order.getStatus() == 1) {
            log.info("订单已支付，无需重复处理：orderNo={}", orderNo);
            return;
        }
        
        if (order.getStatus() != 0) {
            log.error("订单状态异常：orderNo={}, status={}", orderNo, order.getStatus());
            throw new BusinessException("订单状态异常");
        }
        
        // 3. 更新订单状态
        order.setStatus(1); // 已支付
        order.setPayType(payType);
        order.setPayTime(LocalDateTime.now());
        orderInfoMapper.updateById(order);
        
        log.info("订单支付状态更新成功：orderNo={}", orderNo);
    }
    
    /**
     * 检查用户是否购买了课程
     */
    @Override
    public boolean checkUserPurchased(Long userId, Long courseId) {
        log.info("检查用户是否购买课程：userId={}, courseId={}", userId, courseId);
        
        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderInfo::getUserId, userId)
               .eq(OrderInfo::getCourseId, courseId)
               .eq(OrderInfo::getStatus, 1); // 已支付
        
        Long count = orderInfoMapper.selectCount(wrapper);
        boolean purchased = count > 0;
        
        log.info("用户购买检查结果：userId={}, courseId={}, purchased={}", userId, courseId, purchased);
        return purchased;
    }
}

