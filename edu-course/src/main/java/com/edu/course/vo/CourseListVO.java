package com.edu.course.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程列表 VO
 */
@Data
public class CourseListVO implements Serializable {
    
    /**
     * 课程ID
     */
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
     * 分类名称
     */
    private String categoryName;
    
    /**
     * 讲师姓名
     */
    private String teacherName;
    
    /**
     * 讲师头像
     */
    private String teacherAvatar;
    
    /**
     * 价格
     */
    private BigDecimal price;
    
    /**
     * 原价
     */
    private BigDecimal originalPrice;
    
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
     * 创建时间
     */
    private LocalDateTime createTime;
}



