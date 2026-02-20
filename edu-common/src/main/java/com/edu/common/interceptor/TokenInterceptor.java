package com.edu.common.interceptor;

import com.edu.common.constant.RedisConstant;
import com.edu.common.exception.BusinessException;
import com.edu.common.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Token 拦截器
 */
@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 从请求头获取 Token
        String token = request.getHeader("Authorization");
        
        if (token == null || token.isEmpty()) {
            throw new BusinessException(401, "未登录，请先登录");
        }
        
        // 2. 去掉 "Bearer " 前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        // 3. 验证 Token 是否过期
        if (JwtUtil.isTokenExpired(token)) {
            throw new BusinessException(401, "Token 已过期，请重新登录");
        }
        
        // 4. 解析 Token 获取用户ID
        Long userId = JwtUtil.getUserId(token);
        
        // 5. 验证 Token 是否在 Redis 中（防止伪造）
        String tokenKey = RedisConstant.USER_TOKEN_PREFIX + userId;
        String redisToken = (String) redisTemplate.opsForValue().get(tokenKey);
        
        if (redisToken == null || !redisToken.equals(token)) {
            throw new BusinessException(401, "Token 无效，请重新登录");
        }
        
        // 6. 将用户ID存入请求属性，方便后续使用
        request.setAttribute("userId", userId);
        
        log.debug("Token 验证通过，userId={}", userId);
        
        return true;
    }
}

