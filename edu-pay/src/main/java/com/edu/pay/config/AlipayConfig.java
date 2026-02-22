package com.edu.pay.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 支付宝配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "alipay")
public class AlipayConfig {
    
    /**
     * 应用ID
     */
    private String appId;
    
    /**
     * 应用私钥
     */
    private String privateKey;
    
    /**
     * 支付宝公钥
     */
    private String publicKey;
    
    /**
     * 支付宝网关地址
     */
    private String gatewayUrl;
    
    /**
     * 异步回调地址
     */
    private String notifyUrl;
    
    /**
     * 同步回调地址
     */
    private String returnUrl;
    
    /**
     * 字符编码
     */
    private String charset = "UTF-8";
    
    /**
     * 签名类型
     */
    private String signType = "RSA2";
    
    /**
     * 数据格式
     */
    private String format = "json";
    
    /**
     * 创建 AlipayClient Bean
     */
    @Bean
    public AlipayClient alipayClient() {
        return new DefaultAlipayClient(
            gatewayUrl,
            appId,
            privateKey,
            format,
            charset,
            publicKey,
            signType
        );
    }
}

