package com.edu.course.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 课程详情 VO
 */
@Data
public class CourseDetailVO implements Serializable {
    
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
     * 课程描述
     */
    private String description;
    
    /**
     * 分类ID
     */
    private Long categoryId;
    
    /**
     * 分类名称
     */
    private String categoryName;
    
    /**
     * 讲师ID
     */
    private Long teacherId;
    
    /**
     * 讲师姓名
     */
    private String teacherName;
    
    /**
     * 讲师头像
     */
    private String teacherAvatar;
    
    /**
     * 讲师职称
     */
    private String teacherTitle;
    
    /**
     * 讲师简介
     */
    private String teacherIntro;
    
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
     * 秒杀开始时间
     */
    private LocalDateTime seckillStartTime;
    
    /**
     * 秒杀结束时间
     */
    private LocalDateTime seckillEndTime;
    
    /**
     * 章节列表
     */
    private List<ChapterVO> chapters;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 章节 VO
     */
    @Data
    public static class ChapterVO implements Serializable {
        
        /**
         * 章节ID
         */
        private Long id;
        
        /**
         * 章节名称
         */
        private String name;
        
        /**
         * 排序
         */
        private Integer sort;
        
        /**
         * 小节列表
         */
        private List<SectionVO> sections;
    }
    
    /**
     * 小节 VO
     */
    @Data
    public static class SectionVO implements Serializable {
        
        /**
         * 小节ID
         */
        private Long id;
        
        /**
         * 小节名称
         */
        private String name;
        
        /**
         * 视频ID
         */
        private Long videoId;
        
        /**
         * 时长(秒)
         */
        private Integer duration;
        
        /**
         * 是否免费 1-是 0-否
         */
        private Integer isFree;
        
        /**
         * 排序
         */
        private Integer sort;
    }
}

