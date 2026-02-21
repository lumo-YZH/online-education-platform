package com.edu.course.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程实体类
 */
@Data
@TableName("course")
public class Course implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 课程ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 课程名称
     */
    private String name;
    
    /**
     * 封面图
     */
    private String cover;
    
    /**
     * 课程描述
     */
    private String description;
    
    /**
     * 分类ID
     */
    private Long categoryId;
    
    /**
     * 讲师ID
     */
    private Long teacherId;
    
    /**
     * 价格
     */
    private BigDecimal price;
    
    /**
     * 原价
     */
    private BigDecimal originalPrice;
    
    /**
     * 库存
     */
    private Integer stock;
    
    /**
     * 销量
     */
    private Integer sales;
    
    /**
     * 浏览量
     */
    private Integer viewCount;
    
    /**
     * 难度 1-入门 2-初级 3-中级 4-高级
     */
    private Integer level;
    
    /**
     * 总时长(秒)
     */
    private Integer duration;
    
    /**
     * 是否秒杀 1-是 0-否
     */
    private Integer isSeckill;
    
    /**
     * 秒杀价格
     */
    private BigDecimal seckillPrice;
    
    /**
     * 秒杀库存
     */
    private Integer seckillStock;
    
    /**
     * 秒杀开始时间
     */
    private LocalDateTime seckillStartTime;
    
    /**
     * 秒杀结束时间
     */
    private LocalDateTime seckillEndTime;
    
    /**
     * 状态 1-上架 0-下架
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}



