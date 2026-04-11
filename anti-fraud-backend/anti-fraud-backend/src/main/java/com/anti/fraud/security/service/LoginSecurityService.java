package com.anti.fraud.security.service;

import com.anti.fraud.common.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 登录安全服务
 * 提供登录失败次数限制、账号锁定、异地登录检测等功能
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LoginSecurityService {

    private final RedisUtils redisUtils;

    // Redis Key 前缀
    private static final String LOGIN_FAIL_PREFIX = "security:login:fail:";
    private static final String ACCOUNT_LOCK_PREFIX = "security:account:lock:";
    private static final String LOGIN_DEVICE_PREFIX = "security:login:device:";
    private static final String LOGIN_LOCATION_PREFIX = "security:login:location:";
    private static final String TOKEN_BLACKLIST_PREFIX = "security:token:blacklist:";

    // 配置常量
    private static final int MAX_LOGIN_FAIL_COUNT = 5;           // 最大失败次数
    private static final int LOGIN_FAIL_EXPIRE_HOURS = 24;        // 失败计数过期时间（小时）
    private static final int ACCOUNT_LOCK_MINUTES = 30;           // 账号锁定时间（分钟）
    private static final int LOCATION_ALERT_EXPIRE_DAYS = 7;       // 异地登录提醒过期时间（天）
    private static final int DEVICE_EXPIRE_DAYS = 30;              // 设备信息过期时间（天）

    /**
     * 检查账号是否被锁定
     *
     * @param identifier 用户标识（用户名或用户ID）
     * @return 是否被锁定
     */
    public boolean isAccountLocked(String identifier) {
        String lockKey = ACCOUNT_LOCK_PREFIX + identifier;
        return redisUtils.hasKey(lockKey);
    }

    /**
     * 获取账号锁定剩余时间（秒）
     *
     * @param identifier 用户标识
     * @return 剩余锁定时间，-1表示未锁定
     */
    public long getLockRemainingSeconds(String identifier) {
        String lockKey = ACCOUNT_LOCK_PREFIX + identifier;
        Long ttl = redisUtils.getExpire(lockKey);
        return ttl != null && ttl > 0 ? ttl : -1;
    }

    /**
     * 记录登录失败
     *
     * @param identifier 用户标识
     * @return 是否已触发锁定
     */
    public boolean recordLoginFailure(String identifier) {
        String failKey = LOGIN_FAIL_PREFIX + identifier;

        // 增加失败次数
        Long failCount = redisUtils.increment(failKey);

        // 设置过期时间
        if (failCount != null && failCount == 1) {
            redisUtils.expire(failKey, LOGIN_FAIL_EXPIRE_HOURS, TimeUnit.HOURS);
        }

        // 检查是否达到锁定阈值
        if (failCount != null && failCount >= MAX_LOGIN_FAIL_COUNT) {
            lockAccount(identifier);
            log.warn("账号 {} 因连续登录失败 {} 次已被锁定{}分钟",
                    identifier, failCount, ACCOUNT_LOCK_MINUTES);
            return true;
        }

        log.debug("账号 {} 登录失败，当前失败次数: {}", identifier, failCount);
        return false;
    }

    /**
     * 锁定账号
     *
     * @param identifier 用户标识
     */
    public void lockAccount(String identifier) {
        String lockKey = ACCOUNT_LOCK_PREFIX + identifier;
        redisUtils.set(lockKey, "locked", ACCOUNT_LOCK_MINUTES, TimeUnit.MINUTES);

        // 清除失败计数
        redisUtils.delete(LOGIN_FAIL_PREFIX + identifier);

        log.info("账号 {} 已被锁定，锁定时长: {} 分钟", identifier, ACCOUNT_LOCK_MINUTES);
    }

    /**
     * 解锁账号
     *
     * @param identifier 用户标识
     */
    public void unlockAccount(String identifier) {
        redisUtils.delete(ACCOUNT_LOCK_PREFIX + identifier);
        redisUtils.delete(LOGIN_FAIL_PREFIX + identifier);
        log.info("账号 {} 已解锁", identifier);
    }

    /**
     * 重置登录失败计数
     *
     * @param identifier 用户标识
     */
    public void resetLoginFailure(String identifier) {
        redisUtils.delete(LOGIN_FAIL_PREFIX + identifier);
    }

    /**
     * 获取登录失败次数
     *
     * @param identifier 用户标识
     * @return 失败次数
     */
    public int getLoginFailureCount(String identifier) {
        String failKey = LOGIN_FAIL_PREFIX + identifier;
        Object count = redisUtils.get(failKey);
        return count != null ? ((Number) count).intValue() : 0;
    }

    /**
     * 获取剩余登录尝试次数
     *
     * @param identifier 用户标识
     * @return 剩余尝试次数
     */
    public int getRemainingLoginAttempts(String identifier) {
        int currentFail = getLoginFailureCount(identifier);
        return Math.max(0, MAX_LOGIN_FAIL_COUNT - currentFail);
    }

    /**
     * 记录设备登录信息
     *
     * @param userId       用户ID
     * @param deviceId     设备ID
     * @param deviceInfo   设备信息（浏览器、操作系统等）
     * @param ipAddress    IP地址
     * @param location     登录地点
     */
    public void recordDeviceLogin(Long userId, String deviceId, String deviceInfo, String ipAddress, String location) {
        String deviceKey = LOGIN_DEVICE_PREFIX + userId + ":" + deviceId;

        java.util.Map<String, Object> deviceData = new java.util.HashMap<>();
        deviceData.put("deviceInfo", deviceInfo);
        deviceData.put("ipAddress", ipAddress);
        deviceData.put("location", location);
        deviceData.put("lastLoginTime", String.valueOf(System.currentTimeMillis()));
        deviceData.put("loginCount", String.valueOf(getDeviceLoginCount(userId, deviceId) + 1));

        redisUtils.hset(deviceKey, deviceData);
        redisUtils.expire(deviceKey, DEVICE_EXPIRE_DAYS, TimeUnit.DAYS);

        log.debug("记录设备登录: userId={}, deviceId={}, location={}", userId, deviceId, location);
    }

    /**
     * 获取设备登录次数
     */
    public int getDeviceLoginCount(Long userId, String deviceId) {
        String deviceKey = LOGIN_DEVICE_PREFIX + userId + ":" + deviceId;
        String count = (String) redisUtils.hget(deviceKey, "loginCount");
        return count != null ? Integer.parseInt(count) : 0;
    }

    /**
     * 获取用户的所有登录设备
     *
     * @param userId 用户ID
     * @return 设备信息列表
     */
    public java.util.List<java.util.Map<String, String>> getUserDevices(Long userId) {
        // 简化实现，实际应使用Redis的SCAN命令
        return new java.util.ArrayList<>();
    }

    /**
     * 检查是否为新设备登录
     *
     * @param userId   用户ID
     * @param deviceId 设备ID
     * @return 是否为新设备
     */
    public boolean isNewDevice(Long userId, String deviceId) {
        String deviceKey = LOGIN_DEVICE_PREFIX + userId + ":" + deviceId;
        return !redisUtils.hasKey(deviceKey);
    }

    /**
     * 检查异地登录
     *
     * @param userId    用户ID
     * @param ipAddress 当前IP
     * @param location  当前地点
     * @return 是否为异地登录
     */
    public boolean checkAbnormalLocation(Long userId, String ipAddress, String location) {
        String locationKey = LOGIN_LOCATION_PREFIX + userId;

        Object lastLocationObj = redisUtils.get(locationKey);
        if (lastLocationObj == null) {
            // 首次登录，记录当前位置
            redisUtils.set(locationKey, location, LOCATION_ALERT_EXPIRE_DAYS, TimeUnit.DAYS);
            return false;
        }

        String lastLocation = lastLocationObj.toString();
        if (!lastLocation.equals(location)) {
            // 异地登录，发送提醒
            sendLocationAlert(userId, lastLocation, location, ipAddress);
            // 更新位置记录
            redisUtils.set(locationKey, location, LOCATION_ALERT_EXPIRE_DAYS, TimeUnit.DAYS);
            return true;
        }

        return false;
    }

    /**
     * 发送异地登录提醒
     */
    private void sendLocationAlert(Long userId, String lastLocation, String newLocation, String ipAddress) {
        // 实际实现应该调用通知服务发送邮件或短信
        log.warn("异地登录提醒: userId={}, 上次登录地点={}, 当前登录地点={}, IP={}",
                userId, lastLocation, newLocation, ipAddress);
    }

    /**
     * 记录登录日志
     *
     * @param userId      用户ID
     * @param username    用户名
     * @param success     是否成功
     * @param ipAddress   IP地址
     * @param deviceInfo  设备信息
     * @param failReason  失败原因（如果失败）
     */
    public void recordLoginLog(Long userId, String username, boolean success, String ipAddress,
                               String deviceInfo, String failReason) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String logKey = "security:login:log:" + timestamp + ":" + UUID.randomUUID().toString().substring(0, 8);

        java.util.Map<String, Object> logData = new java.util.HashMap<>();
        logData.put("userId", userId != null ? userId.toString() : "unknown");
        logData.put("username", username);
        logData.put("success", String.valueOf(success));
        logData.put("ipAddress", ipAddress);
        logData.put("deviceInfo", deviceInfo);
        logData.put("timestamp", timestamp);
        if (failReason != null) {
            logData.put("failReason", failReason);
        }

        redisUtils.hset(logKey, logData);
        redisUtils.expire(logKey, 30, TimeUnit.DAYS);

        log.info("登录日志: username={}, success={}, ip={}, device={}",
                username, success, ipAddress, deviceInfo);
    }

    /**
     * 将Token加入黑名单
     *
     * @param token       JWT Token
     * @param userId      用户ID
     * @param expireSeconds 剩余过期时间（秒）
     */
    public void blacklistToken(String token, Long userId, long expireSeconds) {
        String blacklistKey = TOKEN_BLACKLIST_PREFIX + token;
        redisUtils.set(blacklistKey, userId.toString(), expireSeconds, TimeUnit.SECONDS);
        log.debug("Token已加入黑名单: userId={}", userId);
    }

    /**
     * 检查Token是否在黑名单中
     *
     * @param token JWT Token
     * @return 是否在黑名单
     */
    public boolean isTokenBlacklisted(String token) {
        String blacklistKey = TOKEN_BLACKLIST_PREFIX + token;
        return redisUtils.hasKey(blacklistKey);
    }

    /**
     * 设备绑定（可选功能）
     *
     * @param userId       用户ID
     * @param deviceId     设备ID
     * @param maxDevices   最大绑定设备数
     * @return 是否绑定成功
     */
    public boolean bindDevice(Long userId, String deviceId, int maxDevices) {
        // 简化实现
        log.info("设备绑定: userId={}, deviceId={}", userId, deviceId);
        return true;
    }

    /**
     * 登录安全检查结果
     */
    public static class LoginSecurityResult {
        private final boolean allowed;
        private final String message;
        private final int remainingAttempts;
        private final long lockRemainingSeconds;

        public LoginSecurityResult(boolean allowed, String message, int remainingAttempts, long lockRemainingSeconds) {
            this.allowed = allowed;
            this.message = message;
            this.remainingAttempts = remainingAttempts;
            this.lockRemainingSeconds = lockRemainingSeconds;
        }

        public boolean isAllowed() {
            return allowed;
        }

        public String getMessage() {
            return message;
        }

        public int getRemainingAttempts() {
            return remainingAttempts;
        }

        public long getLockRemainingSeconds() {
            return lockRemainingSeconds;
        }
    }

    /**
     * 执行登录前安全检查
     *
     * @param identifier 用户标识
     * @return 安全检查结果
     */
    public LoginSecurityResult checkLoginSecurity(String identifier) {
        // 检查是否被锁定
        if (isAccountLocked(identifier)) {
            long remainingSeconds = getLockRemainingSeconds(identifier);
            int remainingMinutes = (int) Math.ceil(remainingSeconds / 60.0);
            return new LoginSecurityResult(
                    false,
                    "账号已被锁定，请" + remainingMinutes + "分钟后重试",
                    0,
                    remainingSeconds
            );
        }

        // 检查失败次数
        int failCount = getLoginFailureCount(identifier);
        if (failCount > 0) {
            int remaining = getRemainingLoginAttempts(identifier);
            if (remaining <= 2) {
                return new LoginSecurityResult(
                        true,
                        "密码错误，您还有" + remaining + "次尝试机会",
                        remaining,
                        -1
                );
            }
        }

        return new LoginSecurityResult(true, "可以尝试登录", MAX_LOGIN_FAIL_COUNT, -1);
    }

    /**
     * 登录成功后清理安全记录
     *
     * @param identifier 用户标识
     */
    public void onLoginSuccess(String identifier) {
        resetLoginFailure(identifier);
        // 不解锁账号，只重置失败计数
        log.debug("登录成功，清理安全记录: {}", identifier);
    }

    /**
     * 生成设备ID
     *
     * @return 设备ID
     */
    public String generateDeviceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
