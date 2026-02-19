package com.edu.user.controller;

import com.edu.common.result.Result;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/user")
public class UserController {
    
    @PostMapping("/register")
    public Result<?> register() {
        return Result.success("注册接口");
    }
    
    @PostMapping("/login")
    public Result<?> login() {
        return Result.success("登录接口");
    }
    
    @GetMapping("/info")
    public Result<?> getUserInfo() {
        return Result.success("获取用户信息接口");
    }
    
    @PutMapping("/profile")
    public Result<?> updateProfile() {
        return Result.success("更新资料接口");
    }
    
    @GetMapping("/study-records")
    public Result<?> getStudyRecords() {
        return Result.success("学习记录接口");
    }
}

