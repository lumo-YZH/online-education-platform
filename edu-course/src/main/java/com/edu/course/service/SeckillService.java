package com.edu.course.service;

import com.edu.course.vo.SeckillVO;

/**
 * 秒杀服务接口
 */
public interface SeckillService {
    
    /**
     * 秒杀抢课
     * 
     * @param courseId 课程ID
     * @param userId 用户ID
     * @return 秒杀结果
     */
    SeckillVO seckill(Long courseId, Long userId);
    
    /**
     * 预热秒杀库存到 Redis
     * 
     * @param courseId 课程ID
     */
    void warmUpStock(Long courseId);
    
    /**
     * 查询秒杀结果
     * 
     * @param courseId 课程ID
     * @param userId 用户ID
     * @return 秒杀结果
     */
    SeckillVO querySeckillResult(Long courseId, Long userId);
}

