package com.edu.video.feign;

import com.edu.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 课程服务 Feign 客户端
 */
@FeignClient(name = "edu-course", path = "/course/internal", fallback = CourseClient.CourseClientFallback.class)
public interface CourseClient {
    
    /**
     * 更新小节的视频ID
     * 
     * @param sectionId 小节ID
     * @param videoId 视频ID
     * @return 结果
     */
    @PostMapping("/update-section-video")
    Result<?> updateSectionVideo(@RequestParam("sectionId") Long sectionId,
                                 @RequestParam("videoId") Long videoId);
    
    /**
     * Feign 降级处理
     */
    @Slf4j
    @Component
    class CourseClientFallback implements CourseClient {
        @Override
        public Result<?> updateSectionVideo(Long sectionId, Long videoId) {
            log.error("【Feign降级】调用课程服务更新小节视频ID失败：sectionId={}, videoId={}", sectionId, videoId);
            return Result.error("更新小节视频ID失败");
        }
    }
}

