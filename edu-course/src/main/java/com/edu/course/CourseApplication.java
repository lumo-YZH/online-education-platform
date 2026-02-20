package com.edu.course;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 课程服务启动类
 */
@SpringBootApplication(scanBasePackages = {"com.edu.course", "com.edu.common"})
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.edu.course.mapper")
public class CourseApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(CourseApplication.class, args);
    }
}

