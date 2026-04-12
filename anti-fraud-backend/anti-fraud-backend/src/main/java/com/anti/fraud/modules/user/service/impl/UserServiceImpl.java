package com.anti.fraud.modules.user.service.impl;

import com.anti.fraud.common.enums.UserRole;
import com.anti.fraud.common.exception.BusinessException;
import com.anti.fraud.common.utils.JwtUtils;
import com.anti.fraud.common.utils.RedisUtils;
import com.anti.fraud.common.utils.security.PasswordStrengthUtil;
import com.anti.fraud.modules.user.dto.LoginDTO;
import com.anti.fraud.modules.user.dto.RegisterDTO;
import com.anti.fraud.modules.user.entity.User;
import com.anti.fraud.modules.user.mapper.UserMapper;
import com.anti.fraud.modules.user.service.UserService;
import com.anti.fraud.modules.user.vo.LoginVO;
import com.anti.fraud.modules.user.vo.UserVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 * 提供用户登录、注册、信息管理等功能
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RedisUtils redisUtils;
    private final PasswordStrengthUtil passwordStrengthUtil;

    // Token相关配置
    private static final int TOKEN_EXPIRE_HOURS = 24;
    private static final int REFRESH_TOKEN_EXPIRE_DAYS = 7;

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        User user = userMapper.selectByUsername(loginDTO.getUsername());
        if (user == null) {
            log.warn("登录失败: 用户不存在, username={}", loginDTO.getUsername());
            throw new BusinessException("用户名或密码错误");
        }

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            log.warn("登录失败: 密码错误, username={}", loginDTO.getUsername());
            throw new BusinessException("用户名或密码错误");
        }

        if (user.getStatus() == 0) {
            log.warn("登录失败: 账号被禁用, username={}", loginDTO.getUsername());
            throw new BusinessException("账号已被禁用，请联系管理员");
        }

        // 生成JWT Token
        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRoleId());

        // 存入Redis，设置过期时间
        redisUtils.set("token:" + token, user.getId(), TOKEN_EXPIRE_HOURS, TimeUnit.HOURS);

        // 生成并存储Refresh Token
        String refreshToken = generateRefreshToken(user.getId());
        redisUtils.set("refresh:" + user.getId(), refreshToken, REFRESH_TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);

        LoginVO loginVO = new LoginVO();
        loginVO.setUserId(user.getId());
        loginVO.setUsername(user.getUsername());
        loginVO.setRealName(user.getRealName());
        loginVO.setAvatar(user.getAvatar());
        loginVO.setRoleId(user.getRoleId());
        loginVO.setRoleName(UserRole.getByCode(user.getRoleId()).getName());
        loginVO.setToken(token);
        loginVO.setRefreshToken(refreshToken);
        loginVO.setPoints(user.getPoints());
        loginVO.setLevel(user.getLevel());

        // 更新登录信息
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);

        log.info("用户登录成功: userId={}, username={}", user.getId(), user.getUsername());
        return loginVO;
    }

    @Override
    @Transactional
    public void register(RegisterDTO registerDTO) {
        // 1. 密码一致性验证
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new BusinessException("两次密码输入不一致");
        }

        // 2. 密码强度验证
        PasswordStrengthUtil.ValidationResult passwordValidation = 
                passwordStrengthUtil.validateForRegistration(registerDTO.getPassword());
        if (!passwordValidation.isValid()) {
            throw new BusinessException(passwordValidation.getMessage());
        }

        // 3. 检查用户名是否存在
        if (userMapper.selectByUsername(registerDTO.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }

        // 4. 检查手机号是否已注册
        if (registerDTO.getPhone() != null && userMapper.selectByPhone(registerDTO.getPhone()) != null) {
            throw new BusinessException("手机号已注册");
        }

        // 5. 创建用户
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        // 使用BCrypt加密密码
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setRealName(registerDTO.getRealName());
        user.setPhone(registerDTO.getPhone());
        user.setEmail(registerDTO.getEmail());
        user.setUserNo(registerDTO.getUserNo());
        user.setDepartmentId(registerDTO.getDepartmentId());
        user.setClassId(registerDTO.getClassId());
        user.setRoleId(UserRole.STUDENT.getCode());
        user.setPoints(0);
        user.setLevel(1);
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        userMapper.insert(user);
        log.info("新用户注册成功: userId={}, username={}", user.getId(), user.getUsername());
    }

    @Override
    public UserVO getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return convertToVO(user);
    }

    @Override
    @Transactional
    public void updateUserInfo(Long userId, UserVO userVO) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        user.setRealName(userVO.getRealName());
        user.setAvatar(userVO.getAvatar());
        user.setEmail(userVO.getEmail());
        user.setGender(userVO.getGender());
        user.setUpdateTime(LocalDateTime.now());

        userMapper.updateById(user);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            log.warn("修改密码失败: 原密码错误, userId={}", userId);
            throw new BusinessException("原密码错误");
        }

        // 验证新密码强度
        PasswordStrengthUtil.ValidationResult passwordValidation = 
                passwordStrengthUtil.validateForRegistration(newPassword);
        if (!passwordValidation.isValid()) {
            throw new BusinessException(passwordValidation.getMessage());
        }

        // 检查新旧密码是否相同
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new BusinessException("新密码不能与原密码相同");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);

        log.info("用户修改密码成功: userId={}", userId);
    }

    @Override
    public Page<UserVO> getUserPage(Integer page, Integer size, String keyword, Integer roleId) {
        Page<User> pageParam = new Page<>(page, size);

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(User::getUsername, keyword)
                    .or().like(User::getRealName, keyword);
        }
        if (roleId != null) {
            wrapper.eq(User::getRoleId, roleId);
        }
        wrapper.orderByDesc(User::getCreateTime);

        Page<User> userPage = userMapper.selectPage(pageParam, wrapper);

        // 手动转换避免类型不兼容
        Page<UserVO> result = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        result.setRecords(userPage.getRecords().stream().map(this::convertToVO).collect(Collectors.toList()));
        return result;
    }

    @Override
    @Transactional
    public void updateUserStatus(Long userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setStatus(status);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
        log.info("用户状态更新: userId={}, status={}", userId, status);
    }

    @Override
    public String generatePasswordHash(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public UserVO getCurrentUserInfo() {
        // 从SecurityContext获取当前用户ID
        Long userId = getCurrentUserId();
        return getUserInfo(userId);
    }

    /**
     * 获取当前登录用户ID
     * 实际实现应该从SecurityContext获取
     */
    private Long getCurrentUserId() {
        // TODO: 从SecurityContext获取当前用户ID
        // 这里暂时返回null，实际使用时需要从JWT Token中解析
        return null;
    }

    /**
     * 生成Refresh Token
     */
    private String generateRefreshToken(Long userId) {
        String refreshToken = java.util.UUID.randomUUID().toString().replace("-", "");
        redisUtils.set("refresh:" + userId, refreshToken, REFRESH_TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);
        return refreshToken;
    }

    /**
     * 验证Refresh Token
     */
    public boolean validateRefreshToken(Long userId, String refreshToken) {
        String storedToken = (String) redisUtils.get("refresh:" + userId);
        return refreshToken != null && refreshToken.equals(storedToken);
    }

    /**
     * 使所有Refresh Token失效（用于密码修改后强制重新登录）
     */
    public void invalidateAllRefreshTokens(Long userId) {
        redisUtils.delete("refresh:" + userId);
        log.info("用户所有Refresh Token已失效: userId={}", userId);
    }

    /**
     * 实体转换为VO
     */
    private UserVO convertToVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setAvatar(user.getAvatar());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setUserNo(user.getUserNo());
        vo.setGender(user.getGender());
        vo.setDepartmentId(user.getDepartmentId());
        vo.setClassId(user.getClassId());
        vo.setRoleId(user.getRoleId());
        vo.setRoleName(UserRole.getByCode(user.getRoleId()).getName());
        vo.setPoints(user.getPoints());
        vo.setLevel(user.getLevel());
        vo.setStatus(user.getStatus());
        return vo;
    }

    @Override
    @Transactional
    public void mergeAccounts(Long sourceUserId, Long targetUserId, String password) {
        // 1. 验证源用户和目标用户存在
        User sourceUser = userMapper.selectById(sourceUserId);
        User targetUser = userMapper.selectById(targetUserId);
        
        if (sourceUser == null) {
            throw new BusinessException("源用户不存在");
        }
        
        if (targetUser == null) {
            throw new BusinessException("目标用户不存在");
        }
        
        // 2. 验证目标用户密码
        if (!passwordEncoder.matches(password, targetUser.getPassword())) {
            throw new BusinessException("目标用户密码错误");
        }
        
        // 3. 合并用户数据（这里简化处理，实际需要合并积分、学习记录等）
        // 例如：合并积分
        targetUser.setPoints(targetUser.getPoints() + sourceUser.getPoints());
        
        // 4. 更新目标用户
        userMapper.updateById(targetUser);
        
        // 5. 软删除源用户
        softDeleteUser(sourceUserId);
        
        log.info("账号合并成功: sourceUserId={}, targetUserId={}", sourceUserId, targetUserId);
    }

    @Override
    @Transactional
    public void deleteAccount(Long userId, String password) {
        // 1. 验证用户存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 2. 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException("密码错误");
        }
        
        // 3. 软删除用户
        softDeleteUser(userId);
        
        log.info("账号注销成功: userId={}", userId);
    }

    @Override
    @Transactional
    public void softDeleteUser(Long userId) {
        // 1. 获取用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 2. 标记为已删除（这里假设User实体有deleted字段）
        // 如果没有deleted字段，可以修改status为0（禁用）
        user.setStatus(0); // 禁用用户
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
        
        // 3. 清理相关数据
        // 清理Token
        redisUtils.delete("token:*" + userId);
        // 清理Refresh Token
        redisUtils.delete("refresh:" + userId);
        
        log.info("用户已软删除: userId={}", userId);
    }
}
