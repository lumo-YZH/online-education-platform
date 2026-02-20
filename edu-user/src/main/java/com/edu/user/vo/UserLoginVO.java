package com.edu.user.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 登录返回 VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginVO implements Serializable {
    
    /**
     * Token
     */
    private String token;
    
    /**
     * 用户信息
     */
    private UserInfoVO userInfo;
}

