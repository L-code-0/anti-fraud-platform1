package com.anti.fraud.modules.user.service;

import com.anti.fraud.modules.user.entity.User;
import com.anti.fraud.modules.user.mapper.UserMapper;
import com.anti.fraud.modules.user.service.impl.UserServiceImpl;
import com.anti.fraud.modules.user.vo.UserVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 用户服务单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("用户服务测试")
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        // 初始化测试用户
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setRealName("测试用户");
        testUser.setDepartment("计算机系");
        testUser.setRoleId(1);
        testUser.setPoints(100);
        testUser.setLevel(1);
        testUser.setStudyDuration(3600);
        testUser.setCreateTime(LocalDateTime.now());
        testUser.setUpdateTime(LocalDateTime.now());
    }

    @Test
    @DisplayName("根据ID获取用户信息成功")
    void getUserInfoSuccess() {
        // Given
        when(userMapper.selectById(1L)).thenReturn(testUser);

        // When
        UserVO result = userService.getUserInfo(1L);

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("测试用户", result.getRealName());
    }

    @Test
    @DisplayName("根据用户名获取用户成功")
    void findByUsernameSuccess() {
        // Given
        when(userMapper.selectByUsername("testuser")).thenReturn(testUser);

        // When
        User result = userService.findByUsername("testuser");

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    @DisplayName("根据用户名获取用户 - 用户不存在")
    void findByUsernameNotFound() {
        // Given
        when(userMapper.selectByUsername("nonexistent")).thenReturn(null);

        // When
        User result = userService.findByUsername("nonexistent");

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("更新用户信息")
    void updateUserInfo() {
        // Given
        UserVO userVO = new UserVO();
        userVO.setRealName("更新后的测试用户");
        userVO.setEmail("updated@example.com");

        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        // When
        userService.updateUserInfo(1L, userVO);

        // Then
        verify(userMapper, times(1)).updateById(any(User.class));
    }

    @Test
    @DisplayName("更新用户状态")
    void updateUserStatus() {
        // Given
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        // When
        userService.updateUserStatus(1L, 1);

        // Then
        verify(userMapper, times(1)).updateById(any(User.class));
    }

    @Test
    @DisplayName("生成密码哈希")
    void generatePasswordHash() {
        // When
        String hash = userService.generatePasswordHash("password123");

        // Then
        assertNotNull(hash);
        assertNotEquals("password123", hash);
    }
}
