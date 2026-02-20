package com.edu.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@TableName("user_profile")
@AllArgsConstructor
@NoArgsConstructor
public class UserProfile implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId; // 用户id

    private String realName; // 真实姓名

    private Integer gender; //  性别

    private LocalDate birthday; // 生日

    private String province; // 省份

    private String city; //  城市

    private String intro; // 个人简介

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime; // 修改时间
}
