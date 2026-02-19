package com.edu.video;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 视频服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class VideoApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(VideoApplication.class, args);
    }
}

