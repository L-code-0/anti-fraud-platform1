package com.anti.fraud.modules.user.service.impl;

import com.anti.fraud.common.exception.BusinessException;
import com.anti.fraud.modules.user.service.BiometricLoginService;
import com.anti.fraud.modules.user.service.UserService;
import com.anti.fraud.modules.user.vo.LoginVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 生物识别登录服务实现
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class BiometricLoginServiceImpl implements BiometricLoginService {
    
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    
    // 模拟存储生物识别数据，实际应该存储在数据库中
    private final Map<String, Map<String, String>> biometricStorage = new ConcurrentHashMap<>();
    
    @Override
    public boolean registerBiometric(Long userId, String deviceId, String biometricData) {
        try {
            log.info("注册生物识别: userId={}, deviceId={}", userId, deviceId);
            
            // 模拟存储生物识别数据
            String key = "biometric:" + userId + ":" + deviceId;
            String encodedData = passwordEncoder.encode(biometricData);
            
            Map<String, String> userDevices = biometricStorage.computeIfAbsent(userId.toString(), k -> new HashMap<>());
            userDevices.put(deviceId, encodedData);
            
            log.info("生物识别注册成功: userId={}, deviceId={}", userId, deviceId);
            return true;
        } catch (Exception e) {
            log.error("注册生物识别失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public LoginVO biometricLogin(String deviceId, String biometricData) {
        try {
            log.info("生物识别登录: deviceId={}", deviceId);
            
            // 模拟查找匹配的用户
            // 实际应该根据deviceId在数据库中查找对应的用户
            for (Map.Entry<String, Map<String, String>> entry : biometricStorage.entrySet()) {
                String userIdStr = entry.getKey();
                Map<String, String> devices = entry.getValue();
                
                if (devices.containsKey(deviceId)) {
                    String storedData = devices.get(deviceId);
                    if (passwordEncoder.matches(biometricData, storedData)) {
                        Long userId = Long.parseLong(userIdStr);
                        // 模拟登录，实际应该调用userService的登录方法
                        LoginVO loginVO = new LoginVO();
                        loginVO.setUserId(userId);
                        loginVO.setUsername("user_" + userId);
                        loginVO.setToken("biometric_token_" + System.currentTimeMillis());
                        loginVO.setRefreshToken("biometric_refresh_token_" + System.currentTimeMillis());
                        
                        log.info("生物识别登录成功: userId={}", userId);
                        return loginVO;
                    }
                }
            }
            
            throw new BusinessException("生物识别验证失败");
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("生物识别登录失败: {}", e.getMessage(), e);
            throw new BusinessException("生物识别登录失败");
        }
    }
    
    @Override
    public boolean verifyBiometric(Long userId, String deviceId, String biometricData) {
        try {
            log.info("验证生物识别: userId={}, deviceId={}", userId, deviceId);
            
            Map<String, String> userDevices = biometricStorage.get(userId.toString());
            if (userDevices == null || !userDevices.containsKey(deviceId)) {
                return false;
            }
            
            String storedData = userDevices.get(deviceId);
            boolean verified = passwordEncoder.matches(biometricData, storedData);
            
            log.info("生物识别验证{}: userId={}, deviceId={}", verified ? "成功" : "失败", userId, deviceId);
            return verified;
        } catch (Exception e) {
            log.error("验证生物识别失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean unregisterBiometric(Long userId, String deviceId) {
        try {
            log.info("取消生物识别注册: userId={}, deviceId={}", userId, deviceId);
            
            Map<String, String> userDevices = biometricStorage.get(userId.toString());
            if (userDevices != null && userDevices.containsKey(deviceId)) {
                userDevices.remove(deviceId);
                if (userDevices.isEmpty()) {
                    biometricStorage.remove(userId.toString());
                }
                log.info("生物识别注销成功: userId={}, deviceId={}", userId, deviceId);
                return true;
            }
            
            return false;
        } catch (Exception e) {
            log.error("取消生物识别注册失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean isBiometricRegistered(Long userId, String deviceId) {
        try {
            log.info("检查设备是否已注册生物识别: userId={}, deviceId={}", userId, deviceId);
            
            Map<String, String> userDevices = biometricStorage.get(userId.toString());
            boolean registered = userDevices != null && userDevices.containsKey(deviceId);
            
            log.info("设备{}已注册生物识别: userId={}, deviceId={}", registered ? "是" : "否", userId, deviceId);
            return registered;
        } catch (Exception e) {
            log.error("检查生物识别注册状态失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public Map<String, String> getRegisteredDevices(Long userId) {
        try {
            log.info("获取用户已注册生物识别的设备列表: userId={}", userId);
            
            Map<String, String> userDevices = biometricStorage.get(userId.toString());
            if (userDevices == null) {
                return new HashMap<>();
            }
            
            // 只返回设备ID，不返回生物识别数据
            Map<String, String> result = new HashMap<>();
            for (String deviceId : userDevices.keySet()) {
                result.put(deviceId, "已注册");
            }
            
            log.info("获取设备列表成功: userId={}, devices={}", userId, result.size());
            return result;
        } catch (Exception e) {
            log.error("获取注册设备列表失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }
}
