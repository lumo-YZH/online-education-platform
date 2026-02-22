package com.edu.search.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import com.edu.common.exception.BusinessException;
import com.edu.search.document.CourseDocument;
import com.edu.search.dto.SearchDTO;
import com.edu.search.mapper.SearchHistoryMapper;
import com.edu.search.service.SearchService;
import com.edu.search.vo.SearchPageVO;
import com.edu.search.vo.SearchResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 搜索服务实现类
 */
@Slf4j
@Service
public class SearchServiceImpl implements SearchService {
    
    @Autowired
    private ElasticsearchClient esClient;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private SearchHistoryMapper searchHistoryMapper;
    
    private static final String HOT_SEARCH_KEY = "search:hot";
    
    /**
     * 搜索课程
     */
    @Override
    public SearchPageVO searchCourse(SearchDTO dto, Long userId) {
        log.info("搜索课程：keyword={}, userId={}", dto.getKeyword(), userId);
        
        // 检查 ES 连接
        if (!checkElasticsearchConnection()) {
            log.warn("Elasticsearch 未连接，返回空结果");
            SearchPageVO emptyResult = new SearchPageVO();
            emptyResult.setRecords(new ArrayList<>());
            emptyResult.setTotal(0L);
            emptyResult.setPageNum(dto.getPageNum());
            emptyResult.setPageSize(dto.getPageSize());
            return emptyResult;
        }
        
        try {
            // 1. 构建查询条件
            BoolQuery.Builder boolQuery = new BoolQuery.Builder();
            
            // 关键词搜索（多字段匹配）
            if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
                boolQuery.must(Query.of(q -> q
                    .multiMatch(m -> m
                        .query(dto.getKeyword())
                        .fields("name^3", "description^2", "teacherName^1")
                    )
                ));
                
                // 记录热搜词
                recordHotSearch(dto.getKeyword());
                
                // 保存搜索历史
                if (userId != null) {
                    saveSearchHistory(userId, dto.getKeyword());
                }
            }
            
            // 分类筛选
            if (dto.getCategoryId() != null) {
                boolQuery.filter(Query.of(q -> q
                    .term(t -> t.field("categoryId").value(dto.getCategoryId()))
                ));
            }
            
            // 价格区间筛选
            if (dto.getMinPrice() != null || dto.getMaxPrice() != null) {
                boolQuery.filter(Query.of(q -> q
                    .range(r -> {
                        var rangeBuilder = r.field("price");
                        if (dto.getMinPrice() != null) {
                            rangeBuilder.gte(co.elastic.clients.json.JsonData.of(dto.getMinPrice()));
                        }
                        if (dto.getMaxPrice() != null) {
                            rangeBuilder.lte(co.elastic.clients.json.JsonData.of(dto.getMaxPrice()));
                        }
                        return rangeBuilder;
                    })
                ));
            }
            
            // 难度筛选
            if (dto.getLevel() != null) {
                boolQuery.filter(Query.of(q -> q
                    .term(t -> t.field("level").value(dto.getLevel()))
                ));
            }
            
            // 只查询上架的课程
            boolQuery.filter(Query.of(q -> q
                .term(t -> t.field("status").value(1))
            ));
            
            // 2. 构建搜索请求
            var searchBuilder = new co.elastic.clients.elasticsearch.core.SearchRequest.Builder()
                .index("course_index")
                .query(boolQuery.build()._toQuery())
                .from((dto.getPageNum() - 1) * dto.getPageSize())
                .size(dto.getPageSize());
            
            // 排序规则
            String orderBy = dto.getOrderBy();
            SortOrder sortOrder = "asc".equals(dto.getOrderType()) ? SortOrder.Asc : SortOrder.Desc;
            
            if ("sales".equals(orderBy)) {
                searchBuilder.sort(s -> s.field(f -> f.field("sales").order(sortOrder)));
            } else if ("price".equals(orderBy)) {
                searchBuilder.sort(s -> s.field(f -> f.field("price").order(sortOrder)));
            } else if ("create_time".equals(orderBy)) {
                searchBuilder.sort(s -> s.field(f -> f.field("createTime").order(sortOrder)));
            }
            // 默认按相关度排序（_score）
            
            // 3. 高亮设置
            searchBuilder.highlight(h -> h
                .fields("name", HighlightField.of(hf -> hf
                    .preTags("<em class='highlight'>")
                    .postTags("</em>")
                ))
                .fields("description", HighlightField.of(hf -> hf
                    .preTags("<em class='highlight'>")
                    .postTags("</em>")
                ))
                .fields("teacherName", HighlightField.of(hf -> hf
                    .preTags("<em class='highlight'>")
                    .postTags("</em>")
                ))
            );
            
            // 4. 执行搜索
            SearchResponse<CourseDocument> response = esClient.search(
                searchBuilder.build(), 
                CourseDocument.class
            );
            
            // 5. 处理结果
            List<SearchResultVO> records = new ArrayList<>();
            for (Hit<CourseDocument> hit : response.hits().hits()) {
                CourseDocument doc = hit.source();
                if (doc == null) continue;
                
                SearchResultVO vo = new SearchResultVO();
                BeanUtils.copyProperties(doc, vo);
                
                // 处理高亮
                Map<String, List<String>> highlight = hit.highlight();
                if (highlight != null) {
                    if (highlight.containsKey("name")) {
                        vo.setName(highlight.get("name").get(0));
                    }
                    if (highlight.containsKey("description")) {
                        vo.setDescription(highlight.get("description").get(0));
                    }
                    if (highlight.containsKey("teacherName")) {
                        vo.setTeacherName(highlight.get("teacherName").get(0));
                    }
                }
                
                records.add(vo);
            }
            
            // 6. 构建分页结果
            SearchPageVO pageVO = new SearchPageVO();
            pageVO.setRecords(records);
            pageVO.setTotal(response.hits().total().value());
            pageVO.setPageNum(dto.getPageNum());
            pageVO.setPageSize(dto.getPageSize());
            
            log.info("搜索完成：关键词={}, 结果数={}", dto.getKeyword(), records.size());
            return pageVO;
            
        } catch (Exception e) {
            log.error("搜索失败", e);
            throw new BusinessException("搜索失败：" + e.getMessage());
        }
    }
    
    /**
     * 搜索建议（自动补全）
     */
    @Override
    public List<String> searchSuggest(String keyword) {
        log.info("获取搜索建议：keyword={}", keyword);
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        try {
            // 使用前缀查询
            SearchResponse<CourseDocument> response = esClient.search(s -> s
                .index("course_index")
                .query(q -> q
                    .bool(b -> b
                        .should(sh -> sh
                            .prefix(p -> p.field("name").value(keyword))
                        )
                        .should(sh -> sh
                            .prefix(p -> p.field("teacherName").value(keyword))
                        )
                        .filter(f -> f.term(t -> t.field("status").value(1)))
                    )
                )
                .size(10)
                .source(src -> src.filter(f -> f.includes("name")))
            , CourseDocument.class);
            
            // 提取课程名称作为建议
            List<String> suggests = response.hits().hits().stream()
                .map(hit -> hit.source().getName())
                .distinct()
                .limit(10)
                .collect(Collectors.toList());
            
            log.info("搜索建议：keyword={}, 建议数={}", keyword, suggests.size());
            return suggests;
            
        } catch (Exception e) {
            log.error("获取搜索建议失败", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 获取热搜榜（Redis ZSet）
     */
    @Override
    public List<String> getHotSearch(Integer limit) {
        log.info("获取热搜榜：limit={}", limit);
        
        // 从 Redis ZSet 获取热搜词（按分数降序）
        Set<Object> hotWords = redisTemplate.opsForZSet()
            .reverseRange(HOT_SEARCH_KEY, 0, limit - 1);
        
        if (hotWords == null || hotWords.isEmpty()) {
            return new ArrayList<>();
        }
        
        return hotWords.stream()
            .map(Object::toString)
            .collect(Collectors.toList());
    }
    
    /**
     * 获取用户搜索历史
     */
    @Override
    public List<String> getSearchHistory(Long userId, Integer limit) {
        log.info("获取搜索历史：userId={}, limit={}", userId, limit);
        
        // 从数据库查询最近的搜索记录
        return searchHistoryMapper.selectRecentHistory(userId, limit);
    }
    
    /**
     * 清空搜索历史
     */
    @Override
    public void clearSearchHistory(Long userId) {
        log.info("清空搜索历史：userId={}", userId);
        searchHistoryMapper.deleteByUserId(userId);
    }
    
    /**
     * 记录热搜词（Redis ZSet）
     */
    private void recordHotSearch(String keyword) {
        try {
            // 增加搜索次数（分数+1）
            redisTemplate.opsForZSet().incrementScore(HOT_SEARCH_KEY, keyword, 1);
            
            // 设置过期时间（7天）
            redisTemplate.expire(HOT_SEARCH_KEY, 7, TimeUnit.DAYS);
            
        } catch (Exception e) {
            log.error("记录热搜词失败", e);
        }
    }
    
    /**
     * 保存搜索历史
     */
    private void saveSearchHistory(Long userId, String keyword) {
        try {
            searchHistoryMapper.insertHistory(userId, keyword);
        } catch (Exception e) {
            log.error("保存搜索历史失败", e);
        }
    }
    
    /**
     * 检查 Elasticsearch 连接
     */
    private boolean checkElasticsearchConnection() {
        try {
            esClient.ping();
            return true;
        } catch (Exception e) {
            log.error("Elasticsearch 连接失败：{}", e.getMessage());
            return false;
        }
    }
}

