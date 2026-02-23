package com.edu.message.controller;

import com.alibaba.nacos.api.naming.pojo.healthcheck.impl.Http;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.result.Result;
import com.edu.message.dto.MessageQueryDTO;
import com.edu.message.service.MessageService;
import com.edu.message.vo.MessageStatVO;
import com.edu.message.vo.MessageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 消息控制器
 */
@Slf4j
@RestController
@RequestMapping("/message")
@Tag(name = "消息管理", description = "站内消息、消息通知等接口")
public class MessageController {
    
    @Autowired
    private MessageService messageService;
    
    /**
     * 分页查询消息列表
     */
    @PostMapping("/list")
    @Operation(summary = "消息列表", description = "分页查询用户消息列表")
    public Result<Page<MessageVO>> getMessageList(@RequestBody MessageQueryDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("分页查询消息列表：userId={}, dto={}", userId, dto);
        Page<MessageVO> page = messageService.getMessageList(dto, userId);
        return Result.success(page);
    }
    
    /**
     * 标记消息为已读
     */
    @PutMapping("/{messageId}/read")
    @Operation(summary = "标记已读", description = "标记消息为已读")
    @Parameter(name = "messageId", description = "消息ID", required = true)
    public Result<Void> markAsRead(@PathVariable Long messageId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("标记消息为已读：messageId={}, userId={}", messageId, userId);
        messageService.markAsRead(messageId, userId);
        return Result.success();
    }
    
    /**
     * 标记所有消息为已读
     */
    @PutMapping("/read-all")
    @Operation(summary = "全部已读", description = "标记所有消息为已读")
    public Result<Void> markAllAsRead(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("标记所有消息为已读：userId={}", userId);
        messageService.markAllAsRead(userId);
        return Result.success();
    }
    
    /**
     * 删除消息
     */
    @DeleteMapping("/{messageId}")
    @Operation(summary = "删除消息", description = "删除指定消息")
    @Parameter(name = "messageId", description = "消息ID", required = true)
    public Result<Void> deleteMessage(@PathVariable Long messageId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("删除消息：messageId={}, userId={}", messageId, userId);
        messageService.deleteMessage(messageId, userId);
        return Result.success();
    }
    
    /**
     * 获取消息统计
     */
    @GetMapping("/stat")
    @Operation(summary = "消息统计", description = "获取用户消息统计信息")
    public Result<MessageStatVO> getMessageStat(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("获取消息统计：userId={}", userId);
        MessageStatVO stat = messageService.getMessageStat(userId);
        return Result.success(stat);
    }
    
    /**
     * 获取未读消息数
     */
    @GetMapping("/unread-count")
    @Operation(summary = "未读数量", description = "获取未读消息数量")
    public Result<Long> getUnreadCount(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Long count = messageService.getUnreadCount(userId);
        return Result.success(count);
    }
}
