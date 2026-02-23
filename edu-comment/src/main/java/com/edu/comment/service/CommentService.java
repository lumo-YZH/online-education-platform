package com.edu.comment.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.comment.dto.CommentAddDTO;
import com.edu.comment.dto.CommentQueryDTO;
import com.edu.comment.vo.CommentStatVO;
import com.edu.comment.vo.CommentVO;

/**
 * 评论服务接口
 */
public interface CommentService {
    
    /**
     * 分页查询评论列表
     */
    Page<CommentVO> getCommentList(CommentQueryDTO dto, Long currentUserId);
    
    /**
     * 添加评论
     */
    Long addComment(CommentAddDTO dto, Long userId, String username, String avatar);
    
    /**
     * 删除评论
     */
    void deleteComment(Long commentId, Long userId);
    
    /**
     * 点赞/取消点赞
     */
    void likeComment(Long commentId, Long userId);
    
    /**
     * 获取评论统计
     */
    CommentStatVO getCommentStat(Long courseId);
}

