package com.anti.fraud.modules.user.service.impl;

import com.anti.fraud.modules.user.entity.LoginDevice;
import com.anti.fraud.modules.user.mapper.LoginDeviceMapper;
import com.anti.fraud.modules.user.service.LoginDeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 登录设备管理服务实现
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LoginDeviceServiceImpl implements LoginDeviceService {
    
    private final LoginDeviceMapper loginDeviceMapper;
    
    @Override
    public List<LoginDevice> getUserDevices(Long userId) {
        try {
            log.info("获取用户登录设备列表: userId={}", userId);
            return loginDeviceMapper.selectByUserId(userId);
        } catch (Exception e) {
            log.error("获取用户登录设备列表失败: {}", e.getMessage(), e);
            return List.of();
        }
    }
    
    @Override
    public boolean addLoginDevice(LoginDevice device) {
        try {
            log.info("添加登录设备: userId={}, deviceId={}", device.getUserId(), device.getDeviceId());
            // 检查设备是否已存在
            LoginDevice existingDevice = loginDeviceMapper.selectByDeviceId(device.getDeviceId());
            if (existingDevice != null) {
                // 更新现有设备
                existingDevice.setLastLoginTime(device.getLastLoginTime());
                existingDevice.setIpAddress(device.getIpAddress());
                existingDevice.setLocation(device.getLocation());
                existingDevice.setDeviceInfo(device.getDeviceInfo());
                existingDevice.setIsActive(true);
                loginDeviceMapper.updateById(existingDevice);
                log.info("更新现有登录设备成功");
                return true;
            } else {
                // 新增设备
                device.setIsTrusted(false);
                device.setIsActive(true);
                loginDeviceMapper.insert(device);
                log.info("新增登录设备成功");
                return true;
            }
        } catch (Exception e) {
            log.error("添加登录设备失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean updateLoginDevice(LoginDevice device) {
        try {
            log.info("更新登录设备: userId={}, deviceId={}", device.getUserId(), device.getDeviceId());
            int rows = loginDeviceMapper.updateById(device);
            boolean success = rows > 0;
            log.info("更新登录设备{}", success ? "成功" : "失败");
            return success;
        } catch (Exception e) {
            log.error("更新登录设备失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean markDeviceAsTrusted(Long userId, String deviceId) {
        try {
            log.info("标记设备为可信: userId={}, deviceId={}", userId, deviceId);
            int rows = loginDeviceMapper.updateDeviceStatus(userId, deviceId, true, true);
            boolean success = rows > 0;
            log.info("标记设备为可信{}", success ? "成功" : "失败");
            return success;
        } catch (Exception e) {
            log.error("标记设备为可信失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean markDeviceAsUntrusted(Long userId, String deviceId) {
        try {
            log.info("标记设备为不可信: userId={}, deviceId={}", userId, deviceId);
            int rows = loginDeviceMapper.updateDeviceStatus(userId, deviceId, false, true);
            boolean success = rows > 0;
            log.info("标记设备为不可信{}", success ? "成功" : "失败");
            return success;
        } catch (Exception e) {
            log.error("标记设备为不可信失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean disableDevice(Long userId, String deviceId) {
        try {
            log.info("禁用设备: userId={}, deviceId={}", userId, deviceId);
            int rows = loginDeviceMapper.updateDeviceStatus(userId, deviceId, false, false);
            boolean success = rows > 0;
            log.info("禁用设备{}", success ? "成功" : "失败");
            return success;
        } catch (Exception e) {
            log.error("禁用设备失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean enableDevice(Long userId, String deviceId) {
        try {
            log.info("启用设备: userId={}, deviceId={}", userId, deviceId);
            int rows = loginDeviceMapper.updateDeviceStatus(userId, deviceId, false, true);
            boolean success = rows > 0;
            log.info("启用设备{}", success ? "成功" : "失败");
            return success;
        } catch (Exception e) {
            log.error("启用设备失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean removeDevice(Long userId, String deviceId) {
        try {
            log.info("移除设备: userId={}, deviceId={}", userId, deviceId);
            LoginDevice device = loginDeviceMapper.selectByDeviceId(deviceId);
            if (device != null && device.getUserId().equals(userId)) {
                loginDeviceMapper.deleteById(device.getId());
                log.info("移除设备成功");
                return true;
            }
            log.info("设备不存在或不属于当前用户");
            return false;
        } catch (Exception e) {
            log.error("移除设备失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean isDeviceTrusted(Long userId, String deviceId) {
        try {
            log.info("检查设备是否可信: userId={}, deviceId={}", userId, deviceId);
            LoginDevice device = loginDeviceMapper.selectByDeviceId(deviceId);
            boolean trusted = device != null && device.getUserId().equals(userId) && device.getIsTrusted() != null && device.getIsTrusted();
            log.info("设备{}可信", trusted ? "是" : "否");
            return trusted;
        } catch (Exception e) {
            log.error("检查设备是否可信失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public LoginDevice getDeviceDetail(Long userId, String deviceId) {
        try {
            log.info("获取设备详情: userId={}, deviceId={}", userId, deviceId);
            LoginDevice device = loginDeviceMapper.selectByDeviceId(deviceId);
            if (device != null && device.getUserId().equals(userId)) {
                log.info("获取设备详情成功");
                return device;
            }
            log.info("设备不存在或不属于当前用户");
            return null;
        } catch (Exception e) {
            log.error("获取设备详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean detectDeviceAnomaly(LoginDevice device) {
        try {
            log.info("检测设备异常: userId={}, deviceId={}", device.getUserId(), device.getDeviceId());
            
            // 检查设备是否首次登录
            LoginDevice existingDevice = loginDeviceMapper.selectByDeviceId(device.getDeviceId());
            if (existingDevice == null) {
                log.info("设备首次登录，标记为异常");
                return true;
            }
            
            // 检查IP地址是否异常
            if (!device.getIpAddress().equals(existingDevice.getIpAddress())) {
                log.info("IP地址异常: 新IP={}, 旧IP={}", device.getIpAddress(), existingDevice.getIpAddress());
                return true;
            }
            
            // 检查设备信息是否异常
            if (!device.getDeviceInfo().equals(existingDevice.getDeviceInfo())) {
                log.info("设备信息异常: 新设备信息={}, 旧设备信息={}", device.getDeviceInfo(), existingDevice.getDeviceInfo());
                return true;
            }
            
            // 检查登录时间是否异常（简单实现：如果是凌晨登录，视为异常）
            int hour = device.getLastLoginTime().getHour();
            if (hour >= 0 && hour <= 6) {
                log.info("登录时间异常: 登录时间={}", device.getLastLoginTime());
                return true;
            }
            
            log.info("设备无异常");
            return false;
        } catch (Exception e) {
            log.error("检测设备异常失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<LoginDevice> getAnomalyDevices(Long userId) {
        try {
            log.info("获取异常设备列表: userId={}", userId);
            List<LoginDevice> devices = loginDeviceMapper.selectByUserId(userId);
            // 筛选异常设备（这里简单实现：未标记为可信的设备视为异常）
            return devices.stream()
                    .filter(device -> device.getIsTrusted() == null || !device.getIsTrusted())
                    .toList();
        } catch (Exception e) {
            log.error("获取异常设备列表失败: {}", e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public boolean handleAnomalyDevice(Long userId, String deviceId, String action) {
        try {
            log.info("处理异常设备: userId={}, deviceId={}, action={}", userId, deviceId, action);
            
            LoginDevice device = loginDeviceMapper.selectByDeviceId(deviceId);
            if (device == null || !device.getUserId().equals(userId)) {
                log.info("设备不存在或不属于当前用户");
                return false;
            }
            
            switch (action) {
                case "block":
                    // 禁用设备
                    device.setIsActive(false);
                    device.setIsTrusted(false);
                    loginDeviceMapper.updateById(device);
                    log.info("设备已被阻止");
                    break;
                case "verify":
                    // 标记为可信
                    device.setIsTrusted(true);
                    device.setIsActive(true);
                    loginDeviceMapper.updateById(device);
                    log.info("设备已被验证为可信");
                    break;
                case "ignore":
                    // 忽略异常，保持设备状态不变
                    log.info("设备异常已被忽略");
                    break;
                default:
                    log.warn("未知的处理动作: {}", action);
                    return false;
            }
            
            return true;
        } catch (Exception e) {
            log.error("处理异常设备失败: {}", e.getMessage(), e);
            return false;
        }
    }
}
