package com.edu.course.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 课程分类 VO
 */
@Data
public class CourseCategoryVO implements Serializable {
    
    /**
     * 分类ID
     */
    private Long id;
    
    /**
     * 分类名称
     */
    private String name;
    
    /**
     * 父分类ID
     */
    private Long parentId;
    
    /**
     * 排序
     */
    private Integer sort;
    
    /**
     * 图标
     */
    private String icon;
}



