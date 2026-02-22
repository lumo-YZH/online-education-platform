package com.edu.video.config;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO 配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinIOConfig {
    
    /**
     * MinIO 服务地址
     */
    private String endpoint;
    
    /**
     * 访问密钥
     */
    private String accessKey;
    
    /**
     * 密钥
     */
    private String secretKey;
    
    /**
     * 桶名称
     */
    private String bucketName;
    
    /**
     * 创建 MinioClient Bean
     */
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}

