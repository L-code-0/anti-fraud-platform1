package com.anti.fraud.modules.user.service;

import com.anti.fraud.modules.user.entity.LoginDevice;

import java.util.List;

/**
 * 登录设备管理服务
 */
public interface LoginDeviceService {
    
    /**
     * 获取用户的登录设备列表
     * @param userId 用户ID
     * @return 登录设备列表
     */
    List<LoginDevice> getUserDevices(Long userId);
    
    /**
     * 添加登录设备
     * @param device 登录设备信息
     * @return 是否添加成功
     */
    boolean addLoginDevice(LoginDevice device);
    
    /**
     * 更新登录设备
     * @param device 登录设备信息
     * @return 是否更新成功
     */
    boolean updateLoginDevice(LoginDevice device);
    
    /**
     * 标记设备为可信
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @return 是否标记成功
     */
    boolean markDeviceAsTrusted(Long userId, String deviceId);
    
    /**
     * 标记设备为不可信
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @return 是否标记成功
     */
    boolean markDeviceAsUntrusted(Long userId, String deviceId);
    
    /**
     * 禁用设备
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @return 是否禁用成功
     */
    boolean disableDevice(Long userId, String deviceId);
    
    /**
     * 启用设备
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @return 是否启用成功
     */
    boolean enableDevice(Long userId, String deviceId);
    
    /**
     * 移除设备
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @return 是否移除成功
     */
    boolean removeDevice(Long userId, String deviceId);
    
    /**
     * 检查设备是否可信
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @return 是否可信
     */
    boolean isDeviceTrusted(Long userId, String deviceId);
    
    /**
     * 获取设备详情
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @return 设备详情
     */
    LoginDevice getDeviceDetail(Long userId, String deviceId);
    
    /**
     * 检测设备异常
     * @param device 登录设备信息
     * @return 是否异常
     */
    boolean detectDeviceAnomaly(LoginDevice device);
    
    /**
     * 获取异常设备列表
     * @param userId 用户ID
     * @return 异常设备列表
     */
    List<LoginDevice> getAnomalyDevices(Long userId);
    
    /**
     * 处理异常设备
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @param action 处理动作（block、verify、ignore）
     * @return 是否处理成功
     */
    boolean handleAnomalyDevice(Long userId, String deviceId, String action);
}
