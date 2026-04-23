package com.anti.fraud.modules.user.service;

import com.anti.fraud.modules.user.entity.LoginDevice;
import java.util.List;

/**
 * 登录设备服务接口
 * 提供登录设备管理相关功能
 */
public interface LoginDeviceService {

    /**
     * 记录用户登录设备
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @param deviceInfo 设备信息
     * @param ipAddress IP地址
     * @param location 位置
     * @return 登录设备
     */
    LoginDevice recordLoginDevice(Long userId, String deviceId, String deviceInfo, String ipAddress, String location);

    /**
     * 获取用户的登录设备列表
     * @param userId 用户ID
     * @return 登录设备列表
     */
    List<LoginDevice> getLoginDevices(Long userId);

    /**
     * 标记设备为可信
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @return 是否成功
     */
    boolean markDeviceAsTrusted(Long userId, String deviceId);

    /**
     * 标记设备为不可信
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @return 是否成功
     */
    boolean markDeviceAsUntrusted(Long userId, String deviceId);

    /**
     * 禁用设备
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @return 是否成功
     */
    boolean disableDevice(Long userId, String deviceId);

    /**
     * 启用设备
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @return 是否成功
     */
    boolean enableDevice(Long userId, String deviceId);

    /**
     * 删除设备
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @return 是否成功
     */
    boolean deleteDevice(Long userId, String deviceId);

    /**
     * 获取设备详情
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @return 登录设备
     */
    LoginDevice getDeviceById(Long userId, String deviceId);

    /**
     * 批量更新设备为非活跃
     * @param userId 用户ID
     * @param excludeDeviceId 排除的设备ID
     * @return 影响行数
     */
    int batchUpdateToInactive(Long userId, String excludeDeviceId);
}
