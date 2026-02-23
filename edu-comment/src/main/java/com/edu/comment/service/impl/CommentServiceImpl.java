package com.edu.comment.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.constant.RedisConstant;
import com.edu.common.exception.BusinessException;
import com.edu.comment.dto.CommentAddDTO;
import com.edu.comment.dto.CommentQueryDTO;
import com.edu.comment.entity.Comment;
import com.edu.comment.entity.CommentLike;
import com.edu.comment.mapper.CommentLikeMapper;
import com.edu.comment.mapper.CommentMapper;
import com.edu.comment.service.CommentService;
import com.edu.comment.vo.CommentStatVO;
import com.edu.comment.vo.CommentVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 评论服务实现类
 */
@Slf4j
@Service
public class CommentServiceImpl implements CommentService {
    
    @Autowired
    private CommentMapper commentMapper;
    
    @Autowired
    private CommentLikeMapper commentLikeMapper;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    /**
     * 分页查询评论列表
     */
    @Override
    public Page<CommentVO> getCommentList(CommentQueryDTO dto, Long currentUserId) {
        // 1. 构建查询条件：只查询顶级评论
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getCourseId, dto.getCourseId())
               .eq(Comment::getParentId, 0)
               .eq(Comment::getStatus, 1);
        
        // 2. 排序
        if ("hot".equals(dto.getOrderBy())) {
            // 热度排序：按点赞数降序
            wrapper.orderByDesc(Comment::getLikeCount);
        } else {
            // 默认按时间降序
            wrapper.orderByDesc(Comment::getCreateTime);
        }
        
        // 3. 分页查询
        Page<Comment> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        Page<Comment> commentPage = commentMapper.selectPage(page, wrapper);
        
        // 4. 转换为 VO
        Page<CommentVO> voPage = new Page<>();
        BeanUtils.copyProperties(commentPage, voPage, "records");
        
        List<CommentVO> voList = commentPage.getRecords().stream().map(comment -> {
            CommentVO vo = convertToVO(comment, currentUserId);
            
            // 查询回复列表（只查询前3条）
            List<CommentVO> replies = getTopReplies(comment.getId(), currentUserId, 3);
            vo.setReplies(replies);
            
            return vo;
        }).collect(Collectors.toList());
        
        voPage.setRecords(voList);
        
        return voPage;
    }
    
    /**
     * 添加评论
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addComment(CommentAddDTO dto, Long userId, String username, String avatar) {
        // 1. 校验
        if (dto.getParentId() != null && dto.getParentId() > 0) {
            // 回复评论：检查父评论是否存在
            Comment parentComment = commentMapper.selectById(dto.getParentId());
            if (parentComment == null || parentComment.getStatus() == 0) {
                throw new BusinessException("父评论不存在");
            }
            
            // 检查回复用户是否存在
            if (dto.getReplyUserId() == null) {
                throw new BusinessException("回复用户ID不能为空");
            }
        }
        
        // 2. 创建评论
        Comment comment = new Comment();
        comment.setCourseId(dto.getCourseId());
        comment.setUserId(userId);
        comment.setUsername(username);
        comment.setAvatar(avatar);
        comment.setParentId(dto.getParentId() != null ? dto.getParentId() : 0L);
        comment.setReplyUserId(dto.getReplyUserId());
        comment.setContent(dto.getContent());
        comment.setLikeCount(0);
        comment.setReplyCount(0);
        comment.setStatus(1);
        
        // 只有顶级评论才有评分
        if (comment.getParentId() == 0) {
            comment.setRating(dto.getRating());
        }
        
        commentMapper.insert(comment);
        
        // 3. 如果是回复评论，更新父评论的回复数
        if (dto.getParentId() != null && dto.getParentId() > 0) {
            Comment parentComment = commentMapper.selectById(dto.getParentId());
            parentComment.setReplyCount(parentComment.getReplyCount() + 1);
            commentMapper.updateById(parentComment);
        }
        
        // 4. 删除缓存
        deleteCommentCache(dto.getCourseId());
        
        log.info("添加评论成功：commentId={}, userId={}", comment.getId(), userId);
        
        return comment.getId();
    }
    
    /**
     * 删除评论
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long commentId, Long userId) {
        // 1. 查询评论
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        
        // 2. 校验权限：只能删除自己的评论
        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException("无权删除该评论");
        }
        
        // 3. 逻辑删除
        comment.setStatus(0);
        commentMapper.updateById(comment);
        
        // 4. 如果是回复评论，更新父评论的回复数
        if (comment.getParentId() > 0) {
            Comment parentComment = commentMapper.selectById(comment.getParentId());
            if (parentComment != null) {
                parentComment.setReplyCount(Math.max(0, parentComment.getReplyCount() - 1));
                commentMapper.updateById(parentComment);
            }
        }
        
        // 5. 删除缓存
        deleteCommentCache(comment.getCourseId());
        
        log.info("删除评论成功：commentId={}, userId={}", commentId, userId);
    }
    
    /**
     * 点赞/取消点赞
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void likeComment(Long commentId, Long userId) {
        // 1. 查询评论
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null || comment.getStatus() == 0) {
            throw new BusinessException("评论不存在");
        }
        
        // 2. 查询是否已点赞
        LambdaQueryWrapper<CommentLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommentLike::getCommentId, commentId)
               .eq(CommentLike::getUserId, userId);
        CommentLike existLike = commentLikeMapper.selectOne(wrapper);
        
        if (existLike != null) {
            // 已点赞，取消点赞
            commentLikeMapper.deleteById(existLike.getId());
            comment.setLikeCount(Math.max(0, comment.getLikeCount() - 1));
            log.info("取消点赞：commentId={}, userId={}", commentId, userId);
        } else {
            // 未点赞，添加点赞
            CommentLike like = new CommentLike();
            like.setCommentId(commentId);
            like.setUserId(userId);
            commentLikeMapper.insert(like);
            comment.setLikeCount(comment.getLikeCount() + 1);
            log.info("点赞成功：commentId={}, userId={}", commentId, userId);
        }
        
        // 3. 更新评论点赞数
        commentMapper.updateById(comment);
        
        // 4. 删除缓存
        deleteCommentCache(comment.getCourseId());
    }
    
    /**
     * 获取评论统计
     */
    @Override
    public CommentStatVO getCommentStat(Long courseId) {
        String cacheKey = "comment:stat:" + courseId;
        
        // 1. 查询缓存
        CommentStatVO cachedStat = (CommentStatVO) redisTemplate.opsForValue().get(cacheKey);
        if (cachedStat != null) {
            log.debug("评论统计缓存命中：courseId={}", courseId);
            return cachedStat;
        }
        
        // 2. 查询数据库
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getCourseId, courseId)
               .eq(Comment::getParentId, 0)
               .eq(Comment::getStatus, 1);
        
        List<Comment> comments = commentMapper.selectList(wrapper);
        
        // 3. 统计
        CommentStatVO stat = new CommentStatVO();
        stat.setTotalCount((long) comments.size());
        
        if (comments.isEmpty()) {
            stat.setAvgRating(BigDecimal.ZERO);
            stat.setStar5Count(0L);
            stat.setStar4Count(0L);
            stat.setStar3Count(0L);
            stat.setStar2Count(0L);
            stat.setStar1Count(0L);
        } else {
            // 计算平均评分
            double avgRating = comments.stream()
                    .filter(c -> c.getRating() != null)
                    .mapToInt(Comment::getRating)
                    .average()
                    .orElse(0.0);
            stat.setAvgRating(BigDecimal.valueOf(avgRating).setScale(1, RoundingMode.HALF_UP));
            
            // 统计各星级数量
            Map<Integer, Long> ratingMap = comments.stream()
                    .filter(c -> c.getRating() != null)
                    .collect(Collectors.groupingBy(Comment::getRating, Collectors.counting()));
            
            stat.setStar5Count(ratingMap.getOrDefault(5, 0L));
            stat.setStar4Count(ratingMap.getOrDefault(4, 0L));
            stat.setStar3Count(ratingMap.getOrDefault(3, 0L));
            stat.setStar2Count(ratingMap.getOrDefault(2, 0L));
            stat.setStar1Count(ratingMap.getOrDefault(1, 0L));
        }
        
        // 4. 写入缓存（过期时间加随机值，防止雪崩）
        long expire = 1800 + RandomUtil.randomLong(0, 300);
        redisTemplate.opsForValue().set(cacheKey, stat, expire, TimeUnit.SECONDS);
        
        return stat;
    }
    
    /**
     * 获取评论的前N条回复
     */
    private List<CommentVO> getTopReplies(Long parentId, Long currentUserId, int limit) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getParentId, parentId)
               .eq(Comment::getStatus, 1)
               .orderByDesc(Comment::getCreateTime)
               .last("LIMIT " + limit);
        
        List<Comment> replies = commentMapper.selectList(wrapper);
        
        return replies.stream()
                .map(reply -> convertToVO(reply, currentUserId))
                .collect(Collectors.toList());
    }
    
    /**
     * 转换为 VO
     */
    private CommentVO convertToVO(Comment comment, Long currentUserId) {
        CommentVO vo = new CommentVO();
        BeanUtils.copyProperties(comment, vo);
        
        // 查询当前用户是否已点赞
        if (currentUserId != null) {
            LambdaQueryWrapper<CommentLike> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CommentLike::getCommentId, comment.getId())
                   .eq(CommentLike::getUserId, currentUserId);
            Long count = commentLikeMapper.selectCount(wrapper);
            vo.setIsLiked(count > 0);
        } else {
            vo.setIsLiked(false);
        }
        
        return vo;
    }
    
    /**
     * 删除评论相关缓存
     */
    private void deleteCommentCache(Long courseId) {
        // 删除评论统计缓存
        String statKey = "comment:stat:" + courseId;
        redisTemplate.delete(statKey);
        
        // 删除热门评论缓存
        String hotKey = "comment:hot:" + courseId;
        redisTemplate.delete(hotKey);
        
        log.debug("删除评论缓存：courseId={}", courseId);
    }
}

