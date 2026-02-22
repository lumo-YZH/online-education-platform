package com.edu.search.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.search.entity.SearchHistory;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 搜索历史 Mapper
 */
@Mapper
public interface SearchHistoryMapper extends BaseMapper<SearchHistory> {
    
    /**
     * 查询用户最近的搜索历史（去重）
     */
    @Select("SELECT keyword FROM ( " +
            "    SELECT keyword, MAX(search_time) as last_time " +
            "    FROM search_history " +
            "    WHERE user_id = #{userId} " +
            "    GROUP BY keyword " +
            "    ORDER BY last_time DESC " +
            "    LIMIT #{limit} " +
            ") t")
    List<String> selectRecentHistory(@Param("userId") Long userId, @Param("limit") Integer limit);
    
    /**
     * 插入搜索历史
     */
    @Insert("INSERT INTO search_history(user_id, keyword, search_time) " +
            "VALUES(#{userId}, #{keyword}, NOW())")
    void insertHistory(@Param("userId") Long userId, @Param("keyword") String keyword);
    
    /**
     * 删除用户的搜索历史
     */
    @Delete("DELETE FROM search_history WHERE user_id = #{userId}")
    void deleteByUserId(@Param("userId") Long userId);
}

