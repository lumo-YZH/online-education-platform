package com.edu.comment;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 评论服务启动类
 */
@SpringBootApplication(scanBasePackages = {"com.edu.comment", "com.edu.common"})
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.edu.comment.mapper")
public class CommentApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(CommentApplication.class, args);
    }
}

