package com.anti.fraud.modules.user.service;

import com.anti.fraud.modules.user.dto.LoginDTO;
import com.anti.fraud.modules.user.dto.RegisterDTO;
import com.anti.fraud.modules.user.entity.User;
import com.anti.fraud.modules.user.mapper.UserMapper;
import com.anti.fraud.modules.user.vo.LoginVO;
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
import static org.mockito.Mockito.*;

/**
 * 用户认证服务单元测试
 * 基于实际 UserService 接口测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("用户认证服务测试")
class AuthServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private UserService userService;

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

    /**
     * 创建登录DTO
     */
    private LoginDTO createLoginDTO(String username, String password) {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername(username);
        loginDTO.setPassword(password);
        return loginDTO;
    }

    /**
     * 创建注册DTO
     */
    private RegisterDTO createRegisterDTO(String username, String password, String email) {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername(username);
        registerDTO.setPassword(password);
        registerDTO.setConfirmPassword(password);
        registerDTO.setRealName("新用户");
        registerDTO.setPhone("13900139000");
        registerDTO.setEmail(email);
        registerDTO.setAgreement(true);
        return registerDTO;
    }

    @Test
    @DisplayName("用户登录成功测试")
    void loginSuccess() {
        // Given
        when(userMapper.selectByUsername("testuser")).thenReturn(testUser);
        when(jwtUtils.generateToken(testUser.getId(), testUser.getUsername(), testUser.getRoleId()))
                .thenReturn("mock_jwt_token");

        LoginDTO loginDTO = createLoginDTO("testuser", "password123");

        // When
        LoginVO result = userService.login(loginDTO);

        // Then
        assertNotNull(result);
        verify(userMapper, times(1)).selectByUsername("testuser");
        verify(jwtUtils, times(1)).generateToken(testUser.getId(), testUser.getUsername(), testUser.getRoleId());
    }

    @Test
    @DisplayName("用户登录失败 - 用户不存在")
    void loginUserNotFound() {
        // Given
        when(userMapper.selectByUsername("nonexistent")).thenReturn(null);

        LoginDTO loginDTO = createLoginDTO("nonexistent", "password123");

        // When & Then
        assertThrows(BusinessException.class, () -> {
            userService.login(loginDTO);
        });
        verify(userMapper, times(1)).selectByUsername("nonexistent");
    }

    @Test
    @DisplayName("用户登录失败 - 用户被禁用")
    void loginUserDisabled() {
        // Given
        testUser.setStatus(0);
        when(userMapper.selectByUsername("testuser")).thenReturn(testUser);

        LoginDTO loginDTO = createLoginDTO("testuser", "password123");

        // When & Then
        assertThrows(BusinessException.class, () -> {
            userService.login(loginDTO);
        });
    }

    @Test
    @DisplayName("用户注册成功测试")
    void registerSuccess() {
        // Given
        when(userMapper.selectByUsername("newuser")).thenReturn(null);
        when(userMapper.insert(any(User.class))).thenReturn(1);

        RegisterDTO registerDTO = createRegisterDTO("newuser", "password123", "new@example.com");

        // When
        userService.register(registerDTO);

        // Then
        verify(userMapper, times(1)).selectByUsername("newuser");
        verify(userMapper, times(1)).insert(any(User.class));
    }

    @Test
    @DisplayName("用户注册失败 - 用户名已存在")
    void registerUsernameExists() {
        // Given
        when(userMapper.selectByUsername("existinguser")).thenReturn(testUser);

        RegisterDTO registerDTO = createRegisterDTO("existinguser", "password123", "new@example.com");

        // When & Then
        assertThrows(BusinessException.class, () -> {
            userService.register(registerDTO);
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
        boolean isValid = jwtUtils.validateToken(token);
        Long userId = jwtUtils.getUserIdFromToken(token);
        User result = userMapper.selectById(userId);

        // Then
        assertTrue(isValid);
        assertEquals(1L, userId);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("JWT Token验证失败 - 无效Token")
    void validateTokenInvalid() {
        // Given
        String token = "invalid_token";
        when(jwtUtils.validateToken(token)).thenReturn(false);

        // When
        boolean isValid = jwtUtils.validateToken(token);

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("根据用户名查询用户")
    void findByUsername() {
        // Given
        when(userMapper.selectByUsername("testuser")).thenReturn(testUser);

        // When
        User result = userService.findByUsername("testuser");

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("测试用户", result.getRealName());
        verify(userMapper, times(1)).selectByUsername("testuser");
    }

    @Test
    @DisplayName("生成密码哈希")
    void generatePasswordHash() {
        // When
        String hash = userService.generatePasswordHash("password123");

        // Then
        assertNotNull(hash);
        assertTrue(hash.startsWith("$2a$") || hash.startsWith("$2b$"));
    }
}
