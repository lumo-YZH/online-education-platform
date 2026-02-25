package com.edu.gateway.filter;

import com.edu.common.constant.RedisConstant;
import com.edu.common.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * JWT 认证全局过滤器
 * 在网关层统一验证 Token，验证通过后将 userId 传递给下游服务
 */
@Slf4j
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    /**
     * 白名单：不需要验证 Token 的路径
     */
    private static final List<String> WHITE_LIST = Arrays.asList(
            // 用户服务公开接口（带 /user 前缀）
            "/user/register",
            "/user/login",
            "/user/send-code",
            // 课程服务公开接口
            "/course/list",
            "/course/category/list",
            "/course/hot",
            // 搜索服务
            "/search/**"
    );
    
    /**
     * Swagger 文档路径（所有服务都放行）
     */
    private static final List<String> SWAGGER_PATHS = Arrays.asList(
            "doc.html",
            "swagger-resources",
            "v3/api-docs",
            "webjars",
            "swagger-ui",
            "favicon.ico",
            ".js",
            ".css",
            ".png",
            ".jpg",
            ".ico"
    );
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        
        log.debug("网关拦截请求：{}", path);
        
        // 1. 检查是否是 Swagger 文档路径
        if (isSwaggerPath(path)) {
            log.debug("Swagger 文档路径，放行：{}", path);
            return chain.filter(exchange);
        }
        
        // 2. 检查是否在白名单中
        if (isWhiteList(path)) {
            log.debug("白名单路径，放行：{}", path);
            return chain.filter(exchange);
        }
        
        // 2. 获取 Token
        String token = getToken(request);
        if (token == null || token.isEmpty()) {
            log.warn("Token 为空，拒绝访问：{}", path);
            return unauthorized(exchange.getResponse(), "未登录，请先登录");
        }
        
        // 3. 去掉 "Bearer " 前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        try {
            // 4. 验证 Token 是否过期
            if (JwtUtil.isTokenExpired(token)) {
                log.warn("Token 已过期：{}", path);
                return unauthorized(exchange.getResponse(), "Token 已过期，请重新登录");
            }
            
            // 5. 解析 Token 获取用户ID
            Long userId = JwtUtil.getUserId(token);
            
             // 6. 验证 Token 是否在 Redis 中，并获取用户信息
            String tokenKey = RedisConstant.USER_TOKEN_PREFIX + userId;
            String redisToken = (String) redisTemplate.opsForValue().get(tokenKey);
            
            if (redisToken == null || !redisToken.equals(token)) {
                log.warn("Token 无效，userId={}，path={}", userId, path);
                return unauthorized(exchange.getResponse(), "Token 无效，请重新登录");
            }
            
            // 7. 从 Redis 获取用户信息
            String userInfoKey = RedisConstant.USER_INFO_PREFIX + userId;
            Object userInfoObj = redisTemplate.opsForValue().get(userInfoKey);
            
            String username = "";
            String avatar = "";
            
            if (userInfoObj != null && userInfoObj instanceof java.util.Map) {
                // Redis 中存储的是 Map 结构
                java.util.Map<String, Object> userMap = (java.util.Map<String, Object>) userInfoObj;
                username = userMap.get("username") != null ? userMap.get("username").toString() : "";
                avatar = userMap.get("avatar") != null ? userMap.get("avatar").toString() : "";
            }
            
            // 8. Token 验证通过，将用户信息添加到请求头传递给下游服务
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("userId", String.valueOf(userId))
                    .header("username", username)
                    .header("avatar", avatar)
                    .build();
            
            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(mutatedRequest)
                    .build();
            
            log.debug("Token 验证通过，userId={}，username={}，path={}", userId, username, path);
            
            return chain.filter(mutatedExchange);
            
        } catch (Exception e) {
            log.error("Token 验证失败：{}", e.getMessage());
            return unauthorized(exchange.getResponse(), "Token 验证失败");
        }
    }
    
    /**
     * 检查是否在白名单中
     */
    private boolean isWhiteList(String path) {
        return WHITE_LIST.stream().anyMatch(path::startsWith);
    }
    
    /**
     * 检查是否是 Swagger 文档路径
     */
    private boolean isSwaggerPath(String path) {
        return SWAGGER_PATHS.stream().anyMatch(path::contains);
    }
    
    /**
     * 从请求头获取 Token
     */
    private String getToken(ServerHttpRequest request) {
        List<String> headers = request.getHeaders().get("Authorization");
        if (headers != null && !headers.isEmpty()) {
            return headers.get(0);
        }
        return null;
    }
    
    /**
     * 返回 401 未授权
     */
    private Mono<Void> unauthorized(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        String body = String.format("{\"code\":401,\"message\":\"%s\"}", message);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }
    
    @Override
    public int getOrder() {
        // 优先级最高
        return Ordered.HIGHEST_PRECEDENCE;
    }
}

