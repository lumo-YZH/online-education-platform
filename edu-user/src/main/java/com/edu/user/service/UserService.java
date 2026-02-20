package com.edu.user.service;

import com.edu.user.dto.UserLoginDTO;
import com.edu.user.dto.UserProfileDTO;
import com.edu.user.dto.UserRegisterDTO;
import com.edu.user.vo.UserInfoVO;
import com.edu.user.vo.UserLoginVO;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户注册
     */
    void register(UserRegisterDTO dto);

    /**
     * 用户登录
     */
    UserLoginVO login(UserLoginDTO dto);

    /**
     * 获取用户信息
     */
    UserInfoVO getUserInfo(Long userId);

    /**
     * 更新用户资料
     */
    void updateProfile(Long userId, UserProfileDTO dto);

    /**
     * 发送验证码
     */
    void sendCode(String phone);
}
