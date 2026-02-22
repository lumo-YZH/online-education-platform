package com.edu.pay;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 支付服务启动类
 */
@SpringBootApplication(scanBasePackages = {"com.edu.pay", "com.edu.common"})
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.edu.pay.mapper")
public class PayApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(PayApplication.class, args);
    }
}
