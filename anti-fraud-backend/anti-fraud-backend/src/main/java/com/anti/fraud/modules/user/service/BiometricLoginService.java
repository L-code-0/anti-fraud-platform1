package com.anti.fraud.modules.user.service;

import com.anti.fraud.modules.user.vo.LoginVO;

import java.util.Map;

/**
 * 生物识别登录服务
 */
public interface BiometricLoginService {
    
    /**
     * 注册生物识别
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @param biometricData 生物识别数据
     * @return 是否注册成功
     */
    boolean registerBiometric(Long userId, String deviceId, String biometricData);
    
    /**
     * 生物识别登录
     * @param deviceId 设备ID
     * @param biometricData 生物识别数据
     * @return 登录结果
     */
    LoginVO biometricLogin(String deviceId, String biometricData);
    
    /**
     * 验证生物识别
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @param biometricData 生物识别数据
     * @return 是否验证成功
     */
    boolean verifyBiometric(Long userId, String deviceId, String biometricData);
    
    /**
     * 取消生物识别注册
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @return 是否取消成功
     */
    boolean unregisterBiometric(Long userId, String deviceId);
    
    /**
     * 检查设备是否已注册生物识别
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @return 是否已注册
     */
    boolean isBiometricRegistered(Long userId, String deviceId);
    
    /**
     * 获取用户已注册生物识别的设备列表
     * @param userId 用户ID
     * @return 设备列表
     */
    Map<String, String> getRegisteredDevices(Long userId);
}
