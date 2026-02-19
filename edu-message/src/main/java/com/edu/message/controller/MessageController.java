package com.edu.message.controller;

import com.edu.common.result.Result;
import org.springframework.web.bind.annotation.*;

/**
 * 消息控制器
 */
@RestController
@RequestMapping("/message")
public class MessageController {
    
    @PostMapping("/send")
    public Result<?> sendMessage() {
        return Result.success("发送消息接口");
    }
    
    @GetMapping("/list")
    public Result<?> getMessageList() {
        return Result.success("消息列表接口");
    }
    
    @PutMapping("/{id}/read")
    public Result<?> markAsRead(@PathVariable Long id) {
        return Result.success("标记已读接口");
    }
}

