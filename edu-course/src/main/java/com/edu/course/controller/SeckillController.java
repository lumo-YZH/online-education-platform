package com.edu.course.controller;

import com.edu.common.result.Result;
import com.edu.course.dto.SeckillDTO;
import com.edu.course.service.SeckillService;
import com.edu.course.vo.SeckillVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 秒杀控制器
 */
@Slf4j
@RestController
@RequestMapping("/course/seckill")
@Tag(name = "秒杀管理", description = "秒杀抢课相关接口")
public class SeckillController {
    
    @Autowired
    private SeckillService seckillService;
    
    /**
     * 秒杀抢课
     */
    @PostMapping("/do")
    @Operation(summary = "秒杀抢课", description = "参与秒杀抢购课程")
    public Result<SeckillVO> seckill(
            @Validated @RequestBody SeckillDTO dto,
            HttpServletRequest request) {
        
        // 从请求属性中获取 userId（由拦截器设置）
        Long userId = (Long) request.getAttribute("userId");

        log.info("秒杀抢课请求：courseId={}, userId={}", dto.getCourseId(), userId);
        
        SeckillVO result = seckillService.seckill(dto.getCourseId(), userId);
        
        if (result.getSuccess()) {
            return Result.success(result);
        } else {
            return Result.error(result.getMessage());
        }
    }
    
    /**
     * 查询秒杀结果
     */
    @GetMapping("/result/{courseId}")
    @Operation(summary = "查询秒杀结果", description = "查询用户的秒杀结果")
    @Parameter(name = "courseId", description = "课程ID", required = true)
    public Result<SeckillVO> querySeckillResult(
            @PathVariable Long courseId,
            HttpServletRequest request) {

        // 从请求属性中获取 userId
        Long userId = (Long) request.getAttribute("userId");

        log.info("查询秒杀结果：courseId={}, userId={}", courseId, userId);
        
        SeckillVO result = seckillService.querySeckillResult(courseId, userId);
        return Result.success(result);
    }
    
    /**
     * 预热秒杀库存（管理员接口）
     */
    @PostMapping("/warmup/{courseId}")
    @Operation(summary = "预热库存", description = "预热秒杀库存到Redis（管理员接口）")
    @Parameter(name = "courseId", description = "课程ID", required = true)
    public Result<Void> warmUpStock(@PathVariable Long courseId) {
        log.info("预热秒杀库存：courseId={}", courseId);
        seckillService.warmUpStock(courseId);
        return Result.success();
    }
}

