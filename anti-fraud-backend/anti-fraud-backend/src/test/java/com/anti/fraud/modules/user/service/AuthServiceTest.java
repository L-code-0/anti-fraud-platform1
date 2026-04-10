package com.anti.fraud.modules.user.service;

import com.anti.fraud.modules.user.entity.User;
import com.anti.fraud.modules.user.mapper.UserMapper;
import com.anti.fraud.common.exception.BusinessException;
import com.anti.fraud.common.utils.JwtUtils;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * 用户认证服务单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("用户认证服务测试")
class AuthServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("$2a$10$encrypted_password");
        testUser.setRealName("测试用户");
        testUser.setEmail("test@example.com");
        testUser.setPhone("13800138000");
        testUser.setStatus(1);
        testUser.setRoleId(1);
        testUser.setCreateTime(LocalDateTime.now());
    }

    @Test
    @DisplayName("用户登录成功测试")
    void loginSuccess() {
        // Given
        when(userMapper.selectByUsername("testuser")).thenReturn(testUser);
        when(jwtUtils.generateToken(testUser.getId(), testUser.getUsername(), testUser.getRoleId()))
                .thenReturn("mock_jwt_token");

        // When
        User result = authService.login("testuser", "password123");

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("测试用户", result.getRealName());
        verify(userMapper, times(1)).selectByUsername("testuser");
        verify(jwtUtils, times(1)).generateToken(testUser.getId(), testUser.getUsername(), testUser.getRoleId());
    }

    @Test
    @DisplayName("用户登录失败 - 用户不存在")
    void loginUserNotFound() {
        // Given
        when(userMapper.selectByUsername("nonexistent")).thenReturn(null);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            authService.login("nonexistent", "password123");
        });
        verify(userMapper, times(1)).selectByUsername("nonexistent");
    }

    @Test
    @DisplayName("用户登录失败 - 密码错误")
    void loginWrongPassword() {
        // Given
        when(userMapper.selectByUsername("testuser")).thenReturn(testUser);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            authService.login("testuser", "wrong_password");
        });
    }

    @Test
    @DisplayName("用户登录失败 - 用户被禁用")
    void loginUserDisabled() {
        // Given
        testUser.setStatus(0);
        when(userMapper.selectByUsername("testuser")).thenReturn(testUser);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            authService.login("testuser", "password123");
        });
    }

    @Test
    @DisplayName("用户注册成功测试")
    void registerSuccess() {
        // Given
        when(userMapper.selectByUsername("newuser")).thenReturn(null);
        when(userMapper.selectByEmail("new@example.com")).thenReturn(null);
        when(userMapper.insert(any(User.class))).thenReturn(1);

        // When
        User result = authService.register("newuser", "password123", "new@example.com");

        // Then
        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        verify(userMapper, times(1)).insert(any(User.class));
    }

    @Test
    @DisplayName("用户注册失败 - 用户名已存在")
    void registerUsernameExists() {
        // Given
        when(userMapper.selectByUsername("existinguser")).thenReturn(testUser);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            authService.register("existinguser", "password123", "new@example.com");
        });
    }

    @Test
    @DisplayName("用户注册失败 - 邮箱已存在")
    void registerEmailExists() {
        // Given
        when(userMapper.selectByUsername("newuser")).thenReturn(null);
        when(userMapper.selectByEmail("existing@example.com")).thenReturn(testUser);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            authService.register("newuser", "password123", "existing@example.com");
        });
    }

    @Test
    @DisplayName("JWT Token验证成功")
    void validateTokenSuccess() {
        // Given
        String token = "valid_jwt_token";
        when(jwtUtils.validateToken(token)).thenReturn(true);
        when(jwtUtils.getUserIdFromToken(token)).thenReturn(1L);
        when(userMapper.selectById(1L)).thenReturn(testUser);

        // When
        User result = authService.validateToken(token);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("JWT Token验证失败 - 无效Token")
    void validateTokenInvalid() {
        // Given
        String token = "invalid_token";
        when(jwtUtils.validateToken(token)).thenReturn(false);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            authService.validateToken(token);
        });
    }

    @Test
    @DisplayName("用户登出测试")
    void logout() {
        // Given
        String token = "user_token";
        
        // When
        authService.logout(token);

        // Then
        verify(jwtUtils, times(1)).invalidateToken(token);
    }
}
