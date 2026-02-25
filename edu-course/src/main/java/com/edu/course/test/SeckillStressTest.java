package com.edu.course.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 秒杀接口压力测试工具
 * 用于测试 Sentinel 限流效果
 */
@Slf4j
public class SeckillStressTest {
    
    // 配置项
    private static final String BASE_URL = "http://localhost:8080";  // 网关地址
    private static final String SECKILL_URL = BASE_URL + "/course/seckill/do";  // 注意：路径是 /do
    private static final Long COURSE_ID = 2L;  // 课程ID
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjQsInVzZXJuYW1lIjoibHVtbyIsInN1YiI6Imx1bW8iLCJpYXQiOjE3NzIwMDAzNTUsImV4cCI6MTc3MjYwNTE1NX0.gx_h5W_iaspZ88ExyC2LstoutyYLtB19XAwgWDD35OQ";  // 替换为真实的 Token
    
    // 压测参数
    private static final int THREAD_COUNT = 500;  // 并发线程数
    private static final int REQUEST_PER_THREAD = 5;  // 每个线程发送的请求数
    
    // 统计
    private static final AtomicInteger successCount = new AtomicInteger(0);
    private static final AtomicInteger failCount = new AtomicInteger(0);
    private static final AtomicInteger blockedCount = new AtomicInteger(0);
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("========================================");
        System.out.println("秒杀接口压力测试");
        System.out.println("========================================");
        System.out.println("目标地址: " + SECKILL_URL);
        System.out.println("并发线程数: " + THREAD_COUNT);
        System.out.println("每线程请求数: " + REQUEST_PER_THREAD);
        System.out.println("总请求数: " + (THREAD_COUNT * REQUEST_PER_THREAD));
        System.out.println("========================================");
        System.out.println();
        
        // 创建线程池
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        
        // 记录开始时间
        long startTime = System.currentTimeMillis();
        
        // 启动压测
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int userId = i + 2000;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < REQUEST_PER_THREAD; j++) {
                        sendRequest(userId);
                        // 稍微延迟，避免瞬间发送
                        Thread.sleep(1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // 等待所有线程完成
        latch.await();
        executor.shutdown();
        
        // 记录结束时间
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // 输出统计结果
        System.out.println();
        System.out.println("========================================");
        System.out.println("压测结果统计");
        System.out.println("========================================");
        System.out.println("总请求数: " + (THREAD_COUNT * REQUEST_PER_THREAD));
        System.out.println("成功请求: " + successCount.get());
        System.out.println("失败请求: " + failCount.get());
        System.out.println("被限流请求: " + blockedCount.get());
        System.out.println("总耗时: " + duration + " ms");
        System.out.println("平均 QPS: " + (THREAD_COUNT * REQUEST_PER_THREAD * 1000 / duration));
        System.out.println("========================================");
    }
    
    /**
     * 发送秒杀请求
     */
    private static void sendRequest(int userId) {
        RestTemplate restTemplate = new RestTemplate();
        
        try {
            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + TOKEN);
            
            // 构建请求体（注意：只需要 courseId，userId 从 Token 中获取）
            String requestBody = String.format("{\"courseId\":%d}", COURSE_ID);
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            
            // 发送请求
            ResponseEntity<String> response = restTemplate.exchange(
                SECKILL_URL,
                HttpMethod.POST,
                entity,
                String.class
            );
            
            String responseBody = response.getBody();
            
            // 判断结果
            if (responseBody != null && responseBody.contains("抢课成功")) {
                successCount.incrementAndGet();
                System.out.println("✓ 线程" + userId + " 抢课成功");
            } else if (responseBody != null && (responseBody.contains("当前抢购人数过多") || responseBody.contains("稍后再试"))) {
                blockedCount.incrementAndGet();
                System.out.println("⊗ 线程" + userId + " 被限流");
            } else {
                failCount.incrementAndGet();
                System.out.println("✗ 线程" + userId + " 抢课失败: " + responseBody);
            }
            
        } catch (Exception e) {
            failCount.incrementAndGet();
            System.out.println("✗ 线程" + userId + " 请求异常: " + e.getMessage());
        }
    }
}

