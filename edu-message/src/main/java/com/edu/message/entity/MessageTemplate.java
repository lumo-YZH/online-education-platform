package com.edu.message.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息模板实体类
 */
@Data
@TableName("message_template")
public class MessageTemplate implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 模板ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 模板编码
     */
    private String code;
    
    /**
     * 模板名称
     */
    private String name;
    
    /**
     * 类型 1-站内信 2-邮件 3-短信
     */
    private Integer type;
    
    /**
     * 标题
     */
    private String title;
    
    /**
     * 内容模板
     */
    private String content;
    
    /**
     * 状态 1-启用 0-禁用
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

