package com.anti.fraud.modules.user.service.impl;

import com.anti.fraud.common.enums.UserRole;
import com.anti.fraud.common.exception.BusinessException;
import com.anti.fraud.common.utils.JwtUtils;
import com.anti.fraud.common.utils.RedisUtils;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RedisUtils redisUtils;

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        User user = userMapper.selectByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用，请联系管理员");
        }

        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRoleId());

        // 存入Redis
        redisUtils.set("token:" + token, user.getId(), 24, TimeUnit.HOURS);

        LoginVO loginVO = new LoginVO();
        loginVO.setUserId(user.getId());
        loginVO.setUsername(user.getUsername());
        loginVO.setRealName(user.getRealName());
        loginVO.setAvatar(user.getAvatar());
        loginVO.setRoleId(user.getRoleId());
        loginVO.setRoleName(UserRole.getByCode(user.getRoleId()).getName());
        loginVO.setToken(token);
        loginVO.setPoints(user.getPoints());
        loginVO.setLevel(user.getLevel());

        // 更新登录信息
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);

        return loginVO;
    }

    @Override
    @Transactional
    public void register(RegisterDTO registerDTO) {
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new BusinessException("两次密码输入不一致");
        }

        if (userMapper.selectByUsername(registerDTO.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }

        if (userMapper.selectByPhone(registerDTO.getPhone()) != null) {
            throw new BusinessException("手机号已注册");
        }

        User user = new User();
        user.setUsername(registerDTO.getUsername());
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

        userMapper.insert(user);
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

        userMapper.updateById(user);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
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
        userMapper.updateById(user);
    }

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
        return vo;
    }

    @Override
    public String generatePasswordHash(String password) {
        return passwordEncoder.encode(password);
    }
}
