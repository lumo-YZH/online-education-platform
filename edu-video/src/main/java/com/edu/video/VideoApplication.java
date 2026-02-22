package com.edu.video;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 视频服务启动类
 */
@SpringBootApplication(scanBasePackages = {"com.edu.video", "com.edu.common"})
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.edu.video.mapper")
public class VideoApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(VideoApplication.class, args);
    }
}
