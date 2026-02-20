package com.edu.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    // MyBatis Plus 已经提供了基础的 CRUD 方法
}

