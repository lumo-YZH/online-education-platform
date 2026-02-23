package com.edu.common.interceptor;

import com.edu.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 用户信息拦截器
 * 从网关传递的请求头中获取用户信息（userId、username、avatar）
 * 只在 Web MVC 环境下加载（网关是 WebFlux，不需要此拦截器）
 */
@Slf4j
@Component
@ConditionalOnClass(HttpServletRequest.class)
public class UserInfoInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头获取网关传递的用户信息
        String userIdHeader = request.getHeader("userId");
        String username = request.getHeader("username");
        String avatar = request.getHeader("avatar");
        
        // 如果没有 userId，说明是公开接口（网关已放行）或直接访问，直接通过
        if (userIdHeader == null || userIdHeader.isEmpty()) {
            log.debug("公开接口或直接访问，无需用户信息，path={}", request.getRequestURI());
            return true;
        }
        
        try {
            Long userId = Long.parseLong(userIdHeader);
            // 将用户信息存入请求属性，方便后续使用
            request.setAttribute("userId", userId);
            request.setAttribute("username", username != null ? username : "");
            request.setAttribute("avatar", avatar != null ? avatar : "");
            
            log.debug("用户信息拦截器：userId={}, username={}, path={}", userId, username, request.getRequestURI());
            return true;
        } catch (NumberFormatException e) {
            log.error("userId 格式错误：{}", userIdHeader);
            throw new BusinessException(401, "用户信息无效");
        }
    }
}

