package com.edu.user.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 用户资料更新 DTO
 */
@Data
public class UserProfileDTO implements Serializable {
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 头像
     */
    private String avatar;
    
    /**
     * 真实姓名
     */
    private String realName;
    
    /**
     * 性别 1-男 2-女 0-未知
     */
    private Integer gender;
    
    /**
     * 生日
     */
    private LocalDate birthday;
    
    /**
     * 省份
     */
    private String province;
    
    /**
     * 城市
     */
    private String city;
    
    /**
     * 个人简介
     */
    private String intro;
}

