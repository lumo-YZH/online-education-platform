package com.edu.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 搜索服务启动类
 */
@SpringBootApplication(scanBasePackages = {"com.edu.search", "com.edu.common"})
@EnableDiscoveryClient
@EnableFeignClients
public class SearchApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(SearchApplication.class, args);
        System.out.println("========================================");
        System.out.println("搜索服务启动成功！");
        System.out.println("Swagger 文档地址：http://localhost:8085/doc.html");
        System.out.println("========================================");
    }
}
