package com.edu.message;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 消息服务启动类
 */
@SpringBootApplication(scanBasePackages = {"com.edu.message", "com.edu.common"})
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.edu.message.mapper")
public class MessageApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(MessageApplication.class, args);
    }
}

