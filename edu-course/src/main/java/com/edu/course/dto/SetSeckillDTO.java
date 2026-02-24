package com.edu.course.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 设置秒杀课程DTO
 */
@Data
public class SetSeckillDTO {
    
    /**
     * 课程ID
     */
    @NotNull(message = "课程ID不能为空")
    private Long courseId;
    
    /**
     * 秒杀价格
     */
    @NotNull(message = "秒杀价格不能为空")
    private BigDecimal seckillPrice;
    
    /**
     * 秒杀库存
     */
    @NotNull(message = "秒杀库存不能为空")
    private Integer seckillStock;
    
    /**
     * 秒杀开始时间
     */
    @NotNull(message = "秒杀开始时间不能为空")
    private LocalDateTime seckillStartTime;
    
    /**
     * 秒杀结束时间
     */
    @NotNull(message = "秒杀结束时间不能为空")
    private LocalDateTime seckillEndTime;
}

