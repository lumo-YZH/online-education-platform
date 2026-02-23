package com.edu.comment.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 评论统计VO
 */
@Data
public class CommentStatVO implements Serializable {
    
    /**
     * 总评论数
     */
    private Long totalCount;
    
    /**
     * 平均评分
     */
    private BigDecimal avgRating;
    
    /**
     * 5星数量
     */
    private Long star5Count;
    
    /**
     * 4星数量
     */
    private Long star4Count;
    
    /**
     * 3星数量
     */
    private Long star3Count;
    
    /**
     * 2星数量
     */
    private Long star2Count;
    
    /**
     * 1星数量
     */
    private Long star1Count;
}

