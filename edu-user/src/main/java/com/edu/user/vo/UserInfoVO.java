package com.edu.user.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户信息 VO
 */
@Data
public class UserInfoVO implements Serializable {
    
    /**
     * 用户ID
     */
    private Long id;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 手机号（脱敏）
     */
    private String phone;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 头像
     */
    private String avatar;
    
    /**
     * 昵称
     */
    private String nickname;
    
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
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

