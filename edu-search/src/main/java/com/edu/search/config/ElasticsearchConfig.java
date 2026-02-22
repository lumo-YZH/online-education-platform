package com.edu.search.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * Elasticsearch 配置类
 */
@Configuration
@EnableElasticsearchRepositories(basePackages = "com.edu.search.repository")
public class ElasticsearchConfig {
    
    @Value("${spring.elasticsearch.uris}")
    private String elasticsearchUris;
    
    @Bean
    public ElasticsearchClient elasticsearchClient() {
        // 解析 URI
        String host = "localhost";
        int port = 9200;
        
        if (elasticsearchUris != null && !elasticsearchUris.isEmpty()) {
            String uri = elasticsearchUris.replace("http://", "").replace("https://", "");
            String[] parts = uri.split(":");
            if (parts.length > 0) {
                host = parts[0];
            }
            if (parts.length > 1) {
                port = Integer.parseInt(parts[1]);
            }
        }
        
        // 创建 RestClient
        RestClient restClient = RestClient.builder(
            new HttpHost(host, port, "http")
        ).build();
        
        // 创建 Transport
        ElasticsearchTransport transport = new RestClientTransport(
            restClient, 
            new JacksonJsonpMapper()
        );
        
        // 创建 ElasticsearchClient
        return new ElasticsearchClient(transport);
    }
}

