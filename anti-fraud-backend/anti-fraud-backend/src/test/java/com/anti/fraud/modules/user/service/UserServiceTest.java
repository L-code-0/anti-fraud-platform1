package com.anti.fraud.modules.user.service;

import com.anti.fraud.common.exception.BusinessException;
import com.anti.fraud.modules.user.dto.LoginDTO;
import com.anti.fraud.modules.user.dto.RegisterDTO;
import com.anti.fraud.modules.user.entity.User;
import com.anti.fraud.modules.user.mapper.UserMapper;
import com.anti.fraud.modules.user.service.impl.UserServiceImpl;
import com.anti.fraud.modules.user.vo.LoginVO;
import com.anti.fraud.modules.user.vo.UserVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private com.anti.fraud.common.utils.JwtUtils jwtUtils;
    
    @Mock
    private com.anti.fraud.common.utils.RedisUtils redisUtils;
    
    @Mock
    private com.anti.fraud.common.utils.security.PasswordStrengthUtil passwordStrengthUtil;

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
        assertNotNull(result.getRealName()); // 真实姓名可能被脱敏
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
        // Given
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
        
        // When
        String hash = userService.generatePasswordHash("password123");

        // Then
        assertNotNull(hash);
        assertNotEquals("password123", hash);
    }

    @Test
    @DisplayName("登录成功")
    void loginSuccess() {
        // Given
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("testuser");
        loginDTO.setPassword("password123");
        
        testUser.setStatus(1); // 账号启用
        
        when(userMapper.selectByUsername("testuser")).thenReturn(testUser);
        when(passwordEncoder.matches("password123", testUser.getPassword())).thenReturn(true);
        when(jwtUtils.generateToken(anyLong(), anyString(), anyInt())).thenReturn("test-token");
        
        // When
        LoginVO result = userService.login(loginDTO);
        
        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("测试用户", result.getRealName());
        assertNotNull(result.getToken());
    }

    @Test
    @DisplayName("登录失败 - 用户不存在")
    void loginUserNotFound() {
        // Given
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("nonexistent");
        loginDTO.setPassword("password123");
        
        when(userMapper.selectByUsername("nonexistent")).thenReturn(null);
        
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.login(loginDTO);
        });
        assertEquals("用户名或密码错误", exception.getMessage());
    }

    @Test
    @DisplayName("登录失败 - 密码错误")
    void loginPasswordError() {
        // Given
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("testuser");
        loginDTO.setPassword("wrongpassword");
        
        when(userMapper.selectByUsername("testuser")).thenReturn(testUser);
        when(passwordEncoder.matches("wrongpassword", testUser.getPassword())).thenReturn(false);
        
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.login(loginDTO);
        });
        assertEquals("用户名或密码错误", exception.getMessage());
    }

    @Test
    @DisplayName("登录失败 - 账号被禁用")
    void loginAccountDisabled() {
        // Given
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("testuser");
        loginDTO.setPassword("password123");
        
        testUser.setStatus(0); // 账号禁用
        
        when(userMapper.selectByUsername("testuser")).thenReturn(testUser);
        when(passwordEncoder.matches("password123", testUser.getPassword())).thenReturn(true);
        
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.login(loginDTO);
        });
        assertEquals("账号已被禁用，请联系管理员", exception.getMessage());
    }

    @Test
    @DisplayName("注册成功")
    void registerSuccess() {
        // Given
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("newuser");
        registerDTO.setPassword("Password123!");
        registerDTO.setConfirmPassword("Password123!");
        registerDTO.setRealName("新用户");
        registerDTO.setPhone("13800138000");
        
        when(userMapper.selectByUsername("newuser")).thenReturn(null);
        when(userMapper.selectByPhone("13800138000")).thenReturn(null);
        
        com.anti.fraud.common.utils.security.PasswordStrengthUtil.ValidationResult validationResult = 
                com.anti.fraud.common.utils.security.PasswordStrengthUtil.ValidationResult.success(
                        com.anti.fraud.common.utils.security.PasswordStrengthUtil.PasswordStrength.STRONG
                );
        when(passwordStrengthUtil.validateForRegistration("Password123!")).thenReturn(validationResult);
        
        when(passwordEncoder.encode("Password123!")).thenReturn("encoded-password");
        when(userMapper.insert(any(User.class))).thenReturn(1);
        
        // When
        userService.register(registerDTO);
        
        // Then
        verify(userMapper, times(1)).insert(any(User.class));
    }

    @Test
    @DisplayName("注册失败 - 密码不一致")
    void registerPasswordMismatch() {
        // Given
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("newuser");
        registerDTO.setPassword("Password123!");
        registerDTO.setConfirmPassword("Password1234!");
        
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.register(registerDTO);
        });
        assertEquals("两次密码输入不一致", exception.getMessage());
    }

    @Test
    @DisplayName("注册失败 - 用户名已存在")
    void registerUsernameExists() {
        // Given
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("testuser");
        registerDTO.setPassword("Password123!");
        registerDTO.setConfirmPassword("Password123!");
        
        when(userMapper.selectByUsername("testuser")).thenReturn(testUser);
        when(passwordStrengthUtil.validateForRegistration("Password123!")).thenReturn(
                com.anti.fraud.common.utils.security.PasswordStrengthUtil.ValidationResult.success(
                        com.anti.fraud.common.utils.security.PasswordStrengthUtil.PasswordStrength.STRONG
                )
        );
        
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.register(registerDTO);
        });
        assertEquals("用户名已存在", exception.getMessage());
    }

    @Test
    @DisplayName("注册失败 - 手机号已注册")
    void registerPhoneExists() {
        // Given
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("newuser");
        registerDTO.setPassword("Password123!");
        registerDTO.setConfirmPassword("Password123!");
        registerDTO.setPhone("13800138000");
        
        when(userMapper.selectByUsername("newuser")).thenReturn(null);
        when(userMapper.selectByPhone("13800138000")).thenReturn(testUser);
        when(passwordStrengthUtil.validateForRegistration("Password123!")).thenReturn(
                com.anti.fraud.common.utils.security.PasswordStrengthUtil.ValidationResult.success(
                        com.anti.fraud.common.utils.security.PasswordStrengthUtil.PasswordStrength.STRONG
                )
        );
        
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.register(registerDTO);
        });
        assertEquals("手机号已注册", exception.getMessage());
    }
}
