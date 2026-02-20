package com.edu.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.user.entity.UserProfile;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户资料 Mapper
 */
@Mapper
public interface UserProfileMapper extends BaseMapper<UserProfile> {
}

