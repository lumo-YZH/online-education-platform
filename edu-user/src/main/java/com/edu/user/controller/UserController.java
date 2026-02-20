package com.edu.user.controller;

import com.edu.common.result.Result;
import com.edu.user.dto.UserLoginDTO;
import com.edu.user.dto.UserProfileDTO;
import com.edu.user.dto.UserRegisterDTO;
import com.edu.user.service.UserService;
import com.edu.user.vo.UserInfoVO;
import com.edu.user.vo.UserLoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/user")
@Tag(name = "用户管理", description = "用户注册、登录、信息管理等接口")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "通过手机号和验证码注册新用户")
    public Result<?> register(@Validated @RequestBody UserRegisterDTO dto) {
        userService.register(dto);
        return Result.success("注册成功");
    }
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "支持用户名或手机号登录，返回 Token 和用户信息")
    public Result<UserLoginVO> login(@Validated @RequestBody UserLoginDTO dto) {
        UserLoginVO vo = userService.login(dto);
        return Result.success(vo);
    }
    
    /**
     * 获取用户信息
     */
    @GetMapping("/info")
    @Operation(summary = "获取用户信息", description = "获取当前登录用户的详细信息")
    public Result<UserInfoVO> getUserInfo(HttpServletRequest request) {
        // 从请求属性获取用户ID（由拦截器设置）
        Long userId = (Long) request.getAttribute("userId");
        
        UserInfoVO vo = userService.getUserInfo(userId);
        return Result.success(vo);
    }
    
    /**
     * 更新用户资料
     */
    @PutMapping("/profile")
    @Operation(summary = "更新用户资料", description = "更新当前登录用户的个人资料")
    public Result<?> updateProfile(@RequestBody UserProfileDTO dto, HttpServletRequest request) {
        // 从请求属性获取用户ID（由拦截器设置）
        Long userId = (Long) request.getAttribute("userId");
        
        userService.updateProfile(userId, dto);
        return Result.success("更新成功");
    }
    
    /**
     * 发送验证码
     */
    @PostMapping("/send-code")
    @Operation(summary = "发送验证码", description = "向指定手机号发送验证码（5分钟有效）")
    @Parameter(name = "phone", description = "手机号", required = true)
    public Result<?> sendCode(@RequestParam String phone) {
        userService.sendCode(phone);
        return Result.success("验证码已发送");
    }
    
    /**
     * 退出登录
     */
    @PostMapping("/logout")
    @Operation(summary = "退出登录", description = "退出当前登录状态")
    public Result<?> logout(HttpServletRequest request) {
        // 从请求属性获取用户ID（由拦截器设置）
        Long userId = (Long) request.getAttribute("userId");
        
        // TODO: 可以将 Token 加入黑名单或删除 Redis 中的 Token
        
        log.info("用户退出登录：userId={}", userId);
        return Result.success("退出成功");
    }
}
