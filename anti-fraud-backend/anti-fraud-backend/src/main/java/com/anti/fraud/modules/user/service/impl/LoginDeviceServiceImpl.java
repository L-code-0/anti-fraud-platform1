package com.anti.fraud.modules.user.service.impl;

import com.anti.fraud.modules.user.entity.LoginDevice;
import com.anti.fraud.modules.user.mapper.LoginDeviceMapper;
import com.anti.fraud.modules.user.service.LoginDeviceService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 登录设备服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LoginDeviceServiceImpl implements LoginDeviceService {

    private final LoginDeviceMapper loginDeviceMapper;

    @Override
    @Transactional
    public LoginDevice recordLoginDevice(Long userId, String deviceId, String deviceInfo, String ipAddress, String location) {
        // 检查设备是否已存在
        LoginDevice existingDevice = loginDeviceMapper.selectByDeviceId(deviceId);
        
        if (existingDevice != null) {
            // 更新现有设备信息
            existingDevice.setLastLoginTime(LocalDateTime.now().toString());
            existingDevice.setIpAddress(ipAddress);
            existingDevice.setLocation(location);
            existingDevice.setIsActive(true);
            existingDevice.setUpdateTime(LocalDateTime.now());
            loginDeviceMapper.updateById(existingDevice);
            
            // 批量更新其他设备为非活跃
            batchUpdateToInactive(userId, deviceId);
            
            log.info("设备登录更新: userId={}, deviceId={}", userId, deviceId);
            return existingDevice;
        } else {
            // 创建新设备记录
            LoginDevice newDevice = new LoginDevice();
            newDevice.setUserId(userId);
            newDevice.setDeviceId(deviceId);
            newDevice.setDeviceInfo(deviceInfo);
            newDevice.setIpAddress(ipAddress);
            newDevice.setLocation(location);
            newDevice.setLastLoginTime(LocalDateTime.now().toString());
            newDevice.setIsTrusted(false); // 默认不可信
            newDevice.setIsActive(true);
            newDevice.setCreateTime(LocalDateTime.now());
            newDevice.setUpdateTime(LocalDateTime.now());
            
            loginDeviceMapper.insert(newDevice);
            
            // 批量更新其他设备为非活跃
            batchUpdateToInactive(userId, deviceId);
            
            log.info("新设备登录: userId={}, deviceId={}", userId, deviceId);
            return newDevice;
        }
    }

    @Override
    public List<LoginDevice> getLoginDevices(Long userId) {
        return loginDeviceMapper.selectByUserId(userId);
    }

    @Override
    @Transactional
    public boolean markDeviceAsTrusted(Long userId, String deviceId) {
        return updateDeviceStatus(userId, deviceId, true, true);
    }

    @Override
    @Transactional
    public boolean markDeviceAsUntrusted(Long userId, String deviceId) {
        return updateDeviceStatus(userId, deviceId, false, true);
    }

    @Override
    @Transactional
    public boolean disableDevice(Long userId, String deviceId) {
        return updateDeviceStatus(userId, deviceId, false, false);
    }

    @Override
    @Transactional
    public boolean enableDevice(Long userId, String deviceId) {
        return updateDeviceStatus(userId, deviceId, false, true);
    }

    @Override
    @Transactional
    public boolean deleteDevice(Long userId, String deviceId) {
        LambdaQueryWrapper<LoginDevice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LoginDevice::getUserId, userId)
                .eq(LoginDevice::getDeviceId, deviceId);
        
        int result = loginDeviceMapper.delete(queryWrapper);
        boolean success = result > 0;
        
        if (success) {
            log.info("设备删除成功: userId={}, deviceId={}", userId, deviceId);
        } else {
            log.warn("设备删除失败: userId={}, deviceId={}", userId, deviceId);
        }
        
        return success;
    }

    @Override
    public LoginDevice getDeviceById(Long userId, String deviceId) {
        LambdaQueryWrapper<LoginDevice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LoginDevice::getUserId, userId)
                .eq(LoginDevice::getDeviceId, deviceId);
        return loginDeviceMapper.selectOne(queryWrapper);
    }

    @Override
    @Transactional
    public int batchUpdateToInactive(Long userId, String excludeDeviceId) {
        int result = loginDeviceMapper.batchUpdateToInactive(userId, excludeDeviceId);
        log.info("批量更新设备为非活跃: userId={}, excludeDeviceId={}, 影响行数={}", userId, excludeDeviceId, result);
        return result;
    }

    /**
     * 更新设备状态
     */
    private boolean updateDeviceStatus(Long userId, String deviceId, Boolean isTrusted, Boolean isActive) {
        int result = loginDeviceMapper.updateDeviceStatus(userId, deviceId, isTrusted, isActive);
        boolean success = result > 0;
        
        if (success) {
            log.info("设备状态更新: userId={}, deviceId={}, isTrusted={}, isActive={}", 
                    userId, deviceId, isTrusted, isActive);
        } else {
            log.warn("设备状态更新失败: userId={}, deviceId={}", userId, deviceId);
        }
        
        return success;
    }
}
