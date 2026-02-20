package com.edu.user.service.impl;

import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.edu.common.constant.RedisConstant;
import com.edu.common.exception.BusinessException;
import com.edu.common.util.JwtUtil;
import com.edu.user.dto.UserLoginDTO;
import com.edu.user.dto.UserProfileDTO;
import com.edu.user.dto.UserRegisterDTO;
import com.edu.user.entity.User;
import com.edu.user.entity.UserProfile;
import com.edu.user.mapper.UserMapper;
import com.edu.user.mapper.UserProfileMapper;
import com.edu.user.service.UserService;
import com.edu.user.vo.UserInfoVO;
import com.edu.user.vo.UserLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserProfileMapper userProfileMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(UserRegisterDTO dto) {
        // 1. 验证验证码
        String codeKey = RedisConstant.SMS_CODE_PREFIX + dto.getPhone();
        String code = (String) redisTemplate.opsForValue().get(codeKey);
        if (code == null || !code.equals(dto.getCode())) {
            throw new BusinessException("验证码错误或已过期");
        }

        // 2. 检查用户名是否存在
        LambdaQueryWrapper<User> usernameWrapper = new LambdaQueryWrapper<>();
        usernameWrapper.eq(User::getUsername, dto.getUsername());
        if (userMapper.selectCount(usernameWrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }

        // 3. 检查手机号是否存在
        LambdaQueryWrapper<User> phoneWrapper = new LambdaQueryWrapper<>();
        phoneWrapper.eq(User::getPhone, dto.getPhone());
        if (userMapper.selectCount(phoneWrapper) > 0) {
            throw new BusinessException("手机号已注册");
        }

        // 4. 创建用户
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setPhone(dto.getPhone());
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername());
        user.setAvatar("https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png");
        user.setStatus(1);

        userMapper.insert(user);

        // 5. 创建用户资料
        UserProfile profile = new UserProfile();
        profile.setUserId(user.getId());
        userProfileMapper.insert(profile);

        // 6. 删除验证码
        redisTemplate.delete(codeKey);

        log.info("用户注册成功：{}", dto.getUsername());
    }

    @Override
    public UserLoginVO login(UserLoginDTO dto) {
        // 1. 查询用户（支持用户名或手机号登录）
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, dto.getUsername())
                .or()
                .eq(User::getPhone, dto.getUsername());

        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 2. 验证密码
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("密码错误");
        }

        // 3. 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }

        // 4. 生成 Token
        String token = JwtUtil.generateToken(user.getId(), user.getUsername());

        // 5. 将 Token 存入 Redis（7天过期）
        String tokenKey = RedisConstant.USER_TOKEN_PREFIX + user.getId();
        redisTemplate.opsForValue().set(tokenKey, token, 7, TimeUnit.DAYS);

        // 6. 缓存用户信息到 Redis
        String userKey = RedisConstant.USER_INFO_PREFIX + user.getId();
        redisTemplate.opsForValue().set(userKey, user, 7, TimeUnit.DAYS);

        // 7. 构建返回结果
        UserInfoVO userInfo = getUserInfo(user.getId());

        log.info("用户登录成功：{}", user.getUsername());

        return new UserLoginVO(token, userInfo);
    }

    @Override
    public UserInfoVO getUserInfo(Long userId) {
        // 1. 先从 Redis 获取
        String userKey = RedisConstant.USER_INFO_PREFIX + userId;
        User cachedUser = (User) redisTemplate.opsForValue().get(userKey);

        User user;
        if (cachedUser != null) {
            user = cachedUser;
        } else {
            // 2. Redis 没有，从数据库查询
            user = userMapper.selectById(userId);
            if (user == null) {
                throw new BusinessException("用户不存在");
            }
            // 3. 存入 Redis
            redisTemplate.opsForValue().set(userKey, user, 7, TimeUnit.DAYS);
        }

        // 4. 查询用户资料
        LambdaQueryWrapper<UserProfile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserProfile::getUserId, userId);
        UserProfile profile = userProfileMapper.selectOne(wrapper);

        // 5. 组装返回数据
        UserInfoVO vo = new UserInfoVO();
        BeanUtils.copyProperties(user, vo);

        if (profile != null) {
            vo.setRealName(profile.getRealName());
            vo.setGender(profile.getGender());
            vo.setBirthday(profile.getBirthday());
            vo.setProvince(profile.getProvince());
            vo.setCity(profile.getCity());
            vo.setIntro(profile.getIntro());
        }

        // 6. 手机号脱敏
        if (user.getPhone() != null) {
            vo.setPhone(DesensitizedUtil.mobilePhone(user.getPhone()));
        }

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProfile(Long userId, UserProfileDTO dto) {
        // 1. 更新用户基本信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (dto.getNickname() != null) {
            user.setNickname(dto.getNickname());
        }
        if (dto.getAvatar() != null) {
            user.setAvatar(dto.getAvatar());
        }

        userMapper.updateById(user);

        // 2. 更新用户资料
        LambdaQueryWrapper<UserProfile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserProfile::getUserId, userId);
        UserProfile profile = userProfileMapper.selectOne(wrapper);

        if (profile == null) {
            profile = new UserProfile();
            profile.setUserId(userId);
        }

        if (dto.getRealName() != null) {
            profile.setRealName(dto.getRealName());
        }
        if (dto.getGender() != null) {
            profile.setGender(dto.getGender());
        }
        if (dto.getBirthday() != null) {
            profile.setBirthday(dto.getBirthday());
        }
        if (dto.getProvince() != null) {
            profile.setProvince(dto.getProvince());
        }
        if (dto.getCity() != null) {
            profile.setCity(dto.getCity());
        }
        if (dto.getIntro() != null) {
            profile.setIntro(dto.getIntro());
        }

        if (profile.getId() == null) {
            userProfileMapper.insert(profile);
        } else {
            userProfileMapper.updateById(profile);
        }

        // 3. 删除 Redis 缓存
        String userKey = RedisConstant.USER_INFO_PREFIX + userId;
        redisTemplate.delete(userKey);

        log.info("用户资料更新成功：userId={}", userId);
    }

    @Override
    public void sendCode(String phone) {
        // 1. 生成6位验证码
        String code = RandomUtil.randomNumbers(6);

        // 2. 存入 Redis（5分钟过期）
        String key = RedisConstant.SMS_CODE_PREFIX + phone;
        redisTemplate.opsForValue().set(key, code, 5, TimeUnit.MINUTES);

        // 3. 发送短信（这里模拟，实际需要对接阿里云短信服务）
        log.info("发送验证码：phone={}, code={}", phone, code);

        // TODO: 实际项目中需要对接阿里云短信服务
        // AliyunSmsUtil.sendCode(phone, code);
    }
}
