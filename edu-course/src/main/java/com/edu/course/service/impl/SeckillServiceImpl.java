package com.edu.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.edu.common.constant.RedisConstant;
import com.edu.common.exception.BusinessException;
import com.edu.course.dto.SeckillMessage;
import com.edu.course.entity.Course;
import com.edu.course.mapper.CourseMapper;
import com.edu.course.mq.SeckillProducer;
import com.edu.course.service.SeckillService;
import com.edu.course.vo.SeckillVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * 秒杀服务实现类
 */
@Slf4j
@Service
public class SeckillServiceImpl implements SeckillService {
    
    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private SeckillProducer seckillProducer;
    
    @Autowired
    private com.edu.course.feign.OrderClient orderClient;
    
    /**
     * Lua 脚本：扣减库存（保证原子性）
     * 返回值：1-成功，0-库存不足，-1-库存key不存在
     */
    private static final String SECKILL_SCRIPT =
            "if redis.call('exists', KEYS[1]) == 0 then " +
            "   return -1 " +
            "end " +
            "local stock = tonumber(redis.call('get', KEYS[1])) " +
            "if stock <= 0 then " +
            "   return 0 " +
            "else " +
            "   redis.call('decr', KEYS[1]) " +
            "   return 1 " +
            "end";
    
    /**
     * 秒杀抢课
     */
    @Override
    public SeckillVO seckill(Long courseId, Long userId) {
        log.info("秒杀抢课：courseId={}, userId={}", courseId, userId);
        
        // 1. 查询课程信息
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            return SeckillVO.fail("课程不存在");
        }
        
        // 2. 检查是否是秒杀课程
        if (course.getIsSeckill() == null || course.getIsSeckill() == 0) {
            return SeckillVO.fail("该课程不是秒杀课程");
        }
        
        // 3. 检查秒杀时间
        LocalDateTime now = LocalDateTime.now();
        if (course.getSeckillStartTime() == null || course.getSeckillEndTime() == null) {
            return SeckillVO.fail("秒杀时间未设置");
        }
        
        if (now.isBefore(course.getSeckillStartTime())) {
            return SeckillVO.fail("秒杀还未开始");
        }
        
        if (now.isAfter(course.getSeckillEndTime())) {
            return SeckillVO.fail("秒杀已结束");
        }
        
        // 4. 检查用户是否已经抢购过（防止重复抢购）
        String userKey = RedisConstant.SECKILL_USER_PREFIX + userId + ":" + courseId;
        Boolean hasOrdered = redisTemplate.hasKey(userKey);
        if (Boolean.TRUE.equals(hasOrdered)) {
            return SeckillVO.fail("您已经抢购过该课程");
        }
        
        // 5. 使用 Lua 脚本扣减 Redis 库存（保证原子性）
        String stockKey = RedisConstant.SECKILL_COURSE_PREFIX + courseId;
        
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(SECKILL_SCRIPT);
        script.setResultType(Long.class);
        
        Long result = redisTemplate.execute(script, Collections.singletonList(stockKey));
        
        if (result == null || result == -1) {
            // 库存 key 不存在，需要预热
            log.warn("秒杀库存未预热：courseId={}", courseId);
            warmUpStock(courseId);
            return SeckillVO.fail("系统繁忙，请稍后重试");
        }
        
        if (result == 0) {
            // 库存不足
            log.info("秒杀库存不足：courseId={}, userId={}", courseId, userId);
            return SeckillVO.fail("课程已抢完");
        }
        
        // 6. 扣减成功，标记用户已抢购（24小时过期）
        redisTemplate.opsForValue().set(userKey, "1", 24, TimeUnit.HOURS);
        
        // 7. 发送 MQ 消息，异步创建订单
        SeckillMessage message = new SeckillMessage(
            userId,
            courseId,
            course.getName(),
            course.getCover(),
            course.getSeckillPrice()
        );
        
        seckillProducer.sendSeckillMessage(message);
        
        log.info("秒杀成功，已发送MQ消息：courseId={}, userId={}", courseId, userId);
        
        return SeckillVO.success("抢课成功，正在生成订单");
    }
    
    /**
     * 预热秒杀库存到 Redis
     */
    @Override
    public void warmUpStock(Long courseId) {
        log.info("预热秒杀库存：courseId={}", courseId);
        
        // 1. 查询课程信息
        Course course = courseMapper.selectById(courseId);
        if (course == null || course.getIsSeckill() == 0) {
            log.warn("课程不存在或不是秒杀课程：courseId={}", courseId);
            return;
        }
        
        // 2. 将秒杀库存写入 Redis
        String stockKey = RedisConstant.SECKILL_COURSE_PREFIX + courseId;
        redisTemplate.opsForValue().set(stockKey, course.getSeckillStock());
        
        // 3. 设置过期时间（秒杀结束后1小时）
        if (course.getSeckillEndTime() != null) {
            long expireSeconds = java.time.Duration.between(
                LocalDateTime.now(),
                course.getSeckillEndTime().plusHours(1)
            ).getSeconds();
            
            if (expireSeconds > 0) {
                redisTemplate.expire(stockKey, expireSeconds, TimeUnit.SECONDS);
            }
        }
        
        log.info("秒杀库存预热成功：courseId={}, stock={}", courseId, course.getSeckillStock());
    }
    
    /**
     * 查询秒杀结果
     */
    @Override
    public SeckillVO querySeckillResult(Long courseId, Long userId) {
        // 检查用户是否已抢购
        String userKey = RedisConstant.SECKILL_USER_PREFIX + userId + ":" + courseId;
        Boolean hasOrdered = redisTemplate.hasKey(userKey);
        
        if (Boolean.TRUE.equals(hasOrdered)) {
            // 查询订单ID
            try {
                com.edu.common.result.Result<String> result = orderClient.getSeckillOrderNo(userId, courseId);
                if (result != null && result.getCode() == 200 && result.getData() != null) {
                    return SeckillVO.success("抢购成功", result.getData());
                } else {
                    return SeckillVO.success( "抢购成功，订单生成中");
                }
            } catch (Exception e) {
                log.error("查询订单ID失败：userId={}, courseId={}", userId, courseId, e);
                return SeckillVO.success("抢购成功，订单生成中");
            }
        } else {
            return SeckillVO.fail("未抢购或抢购失败");
        }
    }
}

