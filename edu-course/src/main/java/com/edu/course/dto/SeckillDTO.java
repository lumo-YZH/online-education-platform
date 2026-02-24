package com.edu.course.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 秒杀请求DTO
 */
@Data
public class SeckillDTO {
    
    /**
     * 课程ID
     */
    @NotNull(message = "课程ID不能为空")
    private Long courseId;
}

