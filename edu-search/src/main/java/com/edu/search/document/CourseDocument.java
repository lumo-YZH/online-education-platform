package com.edu.search.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程 ES 文档
 */
@Data
@Document(indexName = "course_index")
public class CourseDocument {
    
    @Id
    private Long id;
    
    /**
     * 课程名称（分词）
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String name;
    
    /**
     * 课程描述（分词）
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String description;
    
    /**
     * 封面图
     */
    @Field(type = FieldType.Keyword)
    private String cover;
    
    /**
     * 分类ID
     */
    @Field(type = FieldType.Long)
    private Long categoryId;
    
    /**
     * 分类名称
     */
    @Field(type = FieldType.Keyword)
    private String categoryName;
    
    /**
     * 讲师ID
     */
    @Field(type = FieldType.Long)
    private Long teacherId;
    
    /**
     * 讲师名称（分词）
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String teacherName;
    
    /**
     * 价格
     */
    @Field(type = FieldType.Double)
    private BigDecimal price;
    
    /**
     * 销量
     */
    @Field(type = FieldType.Integer)
    private Integer sales;
    
    /**
     * 浏览量
     */
    @Field(type = FieldType.Integer)
    private Integer viewCount;
    
    /**
     * 难度等级
     */
    @Field(type = FieldType.Integer)
    private Integer level;
    
    /**
     * 状态
     */
    @Field(type = FieldType.Integer)
    private Integer status;
    
    /**
     * 创建时间
     */
    @Field(type = FieldType.Date)
    private LocalDateTime createTime;
}

