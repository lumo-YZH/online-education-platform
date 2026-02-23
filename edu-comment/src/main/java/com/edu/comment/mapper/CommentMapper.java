package com.edu.comment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.comment.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 评论Mapper
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}

