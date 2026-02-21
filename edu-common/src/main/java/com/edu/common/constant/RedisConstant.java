package com.edu.common.constant;

/**
 * Redis Key 常量
 */
public class RedisConstant {
    
    // 用户相关
    public static final String USER_TOKEN_PREFIX = "user:token:";
    public static final String USER_INFO_PREFIX = "user:info:";
    
    // 课程相关
    public static final String COURSE_INFO_PREFIX = "course:info:";
    public static final String COURSE_HOT_KEY = "course:hot";
    public static final Long COURSE_HOT_EXPIRE = 3600L; // 热门课程缓存过期时间（秒）
    public static final String COURSE_HOT_LOCK_KEY = "lock:course:hot"; // 热门课程互斥锁
    public static final String COURSE_STOCK_PREFIX = "course:stock:";
    
    // 秒杀相关
    public static final String SECKILL_COURSE_PREFIX = "seckill:course:";
    
    // 搜索相关
    public static final String SEARCH_HOT_PREFIX = "search:hot";
    
    // 验证码
    public static final String SMS_CODE_PREFIX = "sms:code:";
}

