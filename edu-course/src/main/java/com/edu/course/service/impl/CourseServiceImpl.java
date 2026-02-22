package com.edu.course.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.constant.RedisConstant;
import com.edu.common.exception.BusinessException;
import com.edu.course.dto.CourseQueryDTO;
import com.edu.course.entity.Course;
import com.edu.course.entity.CourseCategory;
import com.edu.course.entity.CourseChapter;
import com.edu.course.entity.CourseSection;
import com.edu.course.entity.CourseTeacher;
import com.edu.course.mapper.CourseCategoryMapper;
import com.edu.course.mapper.CourseChapterMapper;
import com.edu.course.mapper.CourseMapper;
import com.edu.course.mapper.CourseSectionMapper;
import com.edu.course.mapper.CourseTeacherMapper;
import com.edu.course.service.CourseService;
import com.edu.course.vo.CourseCategoryVO;
import com.edu.course.vo.CourseDetailVO;
import com.edu.course.vo.CourseListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 课程服务实现类
 */
@Slf4j
@Service
public class CourseServiceImpl implements CourseService {
    
    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private CourseCategoryMapper categoryMapper;
    
    @Autowired
    private CourseTeacherMapper teacherMapper;
    
    @Autowired
    private CourseChapterMapper chapterMapper;
    
    @Autowired
    private CourseSectionMapper sectionMapper;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 分页查询课程列表
     */
    @Override
    public Page<CourseListVO> getCourseList(CourseQueryDTO dto) {
        // 1. 构建查询条件
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        
        // 课程名称模糊查询
        if (dto.getName() != null && !dto.getName().isEmpty()) {
            wrapper.like(Course::getName, dto.getName());
        }
        
        // 分类筛选
        if (dto.getCategoryId() != null) {
            wrapper.eq(Course::getCategoryId, dto.getCategoryId());
        }
        
        // 讲师筛选
        if (dto.getTeacherId() != null) {
            wrapper.eq(Course::getTeacherId, dto.getTeacherId());
        }
        
        // 难度筛选
        if (dto.getLevel() != null) {
            wrapper.eq(Course::getLevel, dto.getLevel());
        }
        
        // 价格区间筛选
        if (dto.getMinPrice() != null) {
            wrapper.ge(Course::getPrice, dto.getMinPrice());
        }
        if (dto.getMaxPrice() != null) {
            wrapper.le(Course::getPrice, dto.getMaxPrice());
        }
        
        // 只查询上架的课程
        wrapper.eq(Course::getStatus, 1);
        
        // 排序
        String orderBy = dto.getOrderBy();
        String orderType = dto.getOrderType() != null ? dto.getOrderType() : "desc";
        
        if ("sales".equals(orderBy)) {
            wrapper.orderBy(true, "asc".equals(orderType), Course::getSales);
        } else if ("view_count".equals(orderBy)) {
            wrapper.orderBy(true, "asc".equals(orderType), Course::getViewCount);
        } else {
            // 默认按创建时间降序
            wrapper.orderByDesc(Course::getCreateTime);
        }
        
        // 2. 分页查询
        Page<Course> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        Page<Course> coursePage = courseMapper.selectPage(page, wrapper);
        
        // 3. 转换为 VO
        Page<CourseListVO> voPage = new Page<>();
        BeanUtils.copyProperties(coursePage, voPage, "records");
        
        List<CourseListVO> voList = coursePage.getRecords().stream().map(course -> {
            CourseListVO vo = new CourseListVO();
            BeanUtils.copyProperties(course, vo);
            
            // 查询分类名称
            if (course.getCategoryId() != null) {
                CourseCategory category = categoryMapper.selectById(course.getCategoryId());
                if (category != null) {
                    vo.setCategoryName(category.getName());
                }
            }
            
            // 查询讲师信息
            if (course.getTeacherId() != null) {
                CourseTeacher teacher = teacherMapper.selectById(course.getTeacherId());
                if (teacher != null) {
                    vo.setTeacherName(teacher.getName());
                    vo.setTeacherAvatar(teacher.getAvatar());
                }
            }
            
            return vo;
        }).collect(Collectors.toList());
        
        voPage.setRecords(voList);
        
        return voPage;
    }

    /**
     * 获取课程详情
     */
    @Override
    public CourseDetailVO getCourseDetail(Long courseId) {
        // 1. 查询课程基本信息
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new BusinessException("课程不存在");
        }
        
        if (course.getStatus() == 0) {
            throw new BusinessException("课程已下架");
        }
        
        // 2. 转换为 VO
        CourseDetailVO vo = new CourseDetailVO();
        BeanUtils.copyProperties(course, vo);
        
        // 3. 查询分类信息
        if (course.getCategoryId() != null) {
            CourseCategory category = categoryMapper.selectById(course.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getName());
            }
        }
        
        // 4. 查询讲师信息
        if (course.getTeacherId() != null) {
            CourseTeacher teacher = teacherMapper.selectById(course.getTeacherId());
            if (teacher != null) {
                vo.setTeacherName(teacher.getName());
                vo.setTeacherAvatar(teacher.getAvatar());
                vo.setTeacherTitle(teacher.getTitle());
                vo.setTeacherIntro(teacher.getIntro());
            }
        }
        
        // 5. 查询章节和小节
        LambdaQueryWrapper<CourseChapter> chapterWrapper = new LambdaQueryWrapper<>();
        chapterWrapper.eq(CourseChapter::getCourseId, courseId)
                     .orderByAsc(CourseChapter::getSort);
        List<CourseChapter> chapters = chapterMapper.selectList(chapterWrapper);
        
        List<CourseDetailVO.ChapterVO> chapterVOList = new ArrayList<>();
        for (CourseChapter chapter : chapters) {
            CourseDetailVO.ChapterVO chapterVO = new CourseDetailVO.ChapterVO();
            BeanUtils.copyProperties(chapter, chapterVO);
            
            // 查询该章节下的小节
            LambdaQueryWrapper<CourseSection> sectionWrapper = new LambdaQueryWrapper<>();
            sectionWrapper.eq(CourseSection::getChapterId, chapter.getId())
                         .orderByAsc(CourseSection::getSort);
            List<CourseSection> sections = sectionMapper.selectList(sectionWrapper);
            
            List<CourseDetailVO.SectionVO> sectionVOList = sections.stream().map(section -> {
                CourseDetailVO.SectionVO sectionVO = new CourseDetailVO.SectionVO();
                BeanUtils.copyProperties(section, sectionVO);
                return sectionVO;
            }).collect(Collectors.toList());
            
            chapterVO.setSections(sectionVOList);
            chapterVOList.add(chapterVO);
        }
        
        vo.setChapters(chapterVOList);
        
        return vo;
    }

    /**
     * 获取课程分类列表
     */
    @Override
    public List<CourseCategoryVO> getCategoryList() {
        // 查询所有启用的分类
        LambdaQueryWrapper<CourseCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseCategory::getStatus, 1)
               .orderByAsc(CourseCategory::getSort);
        
        List<CourseCategory> categories = categoryMapper.selectList(wrapper);
        
        return categories.stream().map(category -> {
            CourseCategoryVO vo = new CourseCategoryVO();
            BeanUtils.copyProperties(category, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 增加浏览量
     * @param courseId
     */
    @Override
    public void increaseViewCount(Long courseId) {
        Course course = courseMapper.selectById(courseId);
        if(course == null){
            throw new BusinessException("课程不存在");
        }
        course.setViewCount(course.getViewCount() + 1);
        courseMapper.updateById(course);
        log.debug("课程浏览量+1：courseId={}", courseId);
    }
    
    /**
     * 获取热门课程（带缓存）
     * 解决方案：
     * 1. 缓存击穿：使用互斥锁，只允许一个线程查询数据库
     * 2. 缓存雪崩：过期时间加随机值，避免同时过期
     */
    @Override
    public List<CourseListVO> getHotCourses(Integer limit) {
        String cacheKey = RedisConstant.COURSE_HOT_KEY;
        String lockKey = RedisConstant.COURSE_HOT_LOCK_KEY;
        
        // 1. 查询缓存
        List<CourseListVO> cachedList = (List<CourseListVO>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedList != null) {
            log.debug("热门课程缓存命中");
            return cachedList;
        }
        
        // 2. 缓存未命中，尝试获取互斥锁
        try {
            Boolean lock = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", 10, TimeUnit.SECONDS);
            
            if (Boolean.TRUE.equals(lock)) {
                log.info("获取互斥锁成功，开始查询数据库");
                
                try {
                    // 3. 双重检查：再次查询缓存（防止其他线程已经写入）
                    cachedList = (List<CourseListVO>) redisTemplate.opsForValue().get(cacheKey);
                    if (cachedList != null) {
                        log.debug("双重检查：缓存已存在");
                        return cachedList;
                    }
                    
                    // 4. 查询数据库
                    List<CourseListVO> voList = queryHotCoursesFromDB(limit);
                    
                    // 5. 写入缓存（过期时间加随机值，防止雪崩）
                    long expire = RedisConstant.COURSE_HOT_EXPIRE + RandomUtil.randomLong(0, 300);
                    redisTemplate.opsForValue().set(cacheKey, voList, expire, TimeUnit.SECONDS);
                    
                    log.info("热门课程已写入缓存，过期时间：{}秒", expire);
                    return voList;
                    
                } finally {
                    // 6. 释放锁
                    redisTemplate.delete(lockKey);
                    log.debug("释放互斥锁");
                }
                
            } else {
                // 7. 获取锁失败，等待后重试
                log.debug("获取互斥锁失败，等待重试");
                Thread.sleep(50);
                return getHotCourses(limit);
            }
            
        } catch (InterruptedException e) {
            log.error("获取热门课程失败", e);
            throw new BusinessException("获取热门课程失败");
        }
    }
    
    /**
     * 从数据库查询热门课程
     */
    private List<CourseListVO> queryHotCoursesFromDB(Integer limit) {
        log.info("从数据库查询热门课程，limit={}", limit);
        
        // 1. 构建查询条件：按销量降序
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getStatus, 1)
               .orderByDesc(Course::getSales)
               .last("LIMIT " + limit);
        
        List<Course> courses = courseMapper.selectList(wrapper);
        
        // 2. 转换为 VO
        return courses.stream().map(course -> {
            CourseListVO vo = new CourseListVO();
            BeanUtils.copyProperties(course, vo);
            
            // 查询分类名称
            if (course.getCategoryId() != null) {
                CourseCategory category = categoryMapper.selectById(course.getCategoryId());
                if (category != null) {
                    vo.setCategoryName(category.getName());
                }
            }
            
            // 查询讲师信息
            if (course.getTeacherId() != null) {
                CourseTeacher teacher = teacherMapper.selectById(course.getTeacherId());
                if (teacher != null) {
                    vo.setTeacherName(teacher.getName());
                    vo.setTeacherAvatar(teacher.getAvatar());
                }
            }
            
            return vo;
        }).collect(Collectors.toList());
    }
    
    /**
     * 扣减课程库存（用于分布式事务）
     */
    @Override
    public void deductStock(Long courseId, Integer quantity) {
        log.info("开始扣减课程库存：courseId={}, quantity={}", courseId, quantity);
        
        // 1. 查询课程
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new BusinessException("课程不存在");
        }
        
        // 2. 检查库存
        if (course.getStock() == null || course.getStock() < quantity) {
            throw new BusinessException("课程库存不足");
        }
        
        // 3. 扣减库存
        course.setStock(course.getStock() - quantity);
        courseMapper.updateById(course);
        
        log.info("课程库存扣减成功：courseId={}, 剩余库存={}", courseId, course.getStock());
        
    }
    
    /**
     * 恢复课程库存（订单取消时）
     */
    @Override
    public void restoreStock(Long courseId, Integer quantity) {
        log.info("开始恢复课程库存：courseId={}, quantity={}", courseId, quantity);
        
        // 1. 查询课程
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            log.warn("课程不存在，无法恢复库存：courseId={}", courseId);
            return;
        }
        
        // 2. 恢复库存
        course.setStock(course.getStock() + quantity);
        courseMapper.updateById(course);
        
        log.info("课程库存恢复成功：courseId={}, 当前库存={}", courseId, course.getStock());
    }
}


