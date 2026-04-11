package com.anti.fraud.security.service;

import com.anti.fraud.common.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Token安全服务
 * 提供Token黑名单、刷新机制、Token验证增强等功能
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TokenSecurityService {

    private final RedisUtils redisUtils;

    // Redis Key 前缀
    private static final String TOKEN_BLACKLIST_PREFIX = "token:blacklist:";
    private static final String REFRESH_TOKEN_PREFIX = "token:refresh:";
    private static final String TOKEN_VERSION_PREFIX = "token:version:";
    private static final String TOKEN_FAMILY_PREFIX = "token:family:";

    // Token版本管理
    @Value("${jwt.secret}")
    private String jwtSecret;

    // Token有效期配置（毫秒）
    @Value("${jwt.expiration:86400000}")
    private long accessTokenExpiration;

    // Refresh Token有效期（天）
    @Value("${jwt.refresh-expiration:7}")
    private int refreshTokenExpiration;

    /**
     * 将Token加入黑名单
     *
     * @param token           JWT Token
     * @param remainingSeconds 剩余过期时间（秒）
     */
    public void blacklistToken(String token, long remainingSeconds) {
        if (remainingSeconds > 0) {
            String blacklistKey = TOKEN_BLACKLIST_PREFIX + hashToken(token);
            redisUtils.set(blacklistKey, "1", remainingSeconds, TimeUnit.SECONDS);
            log.debug("Token已加入黑名单，剩余有效期: {}秒", remainingSeconds);
        }
    }

    /**
     * 检查Token是否在黑名单中
     *
     * @param token JWT Token
     * @return 是否在黑名单
     */
    public boolean isTokenBlacklisted(String token) {
        String blacklistKey = TOKEN_BLACKLIST_PREFIX + hashToken(token);
        return redisUtils.hasKey(blacklistKey);
    }

    /**
     * 生成Refresh Token
     *
     * @param userId 用户ID
     * @param tokenFamily Token家族（用于检测Token被盗用）
     * @return Refresh Token
     */
    public String generateRefreshToken(Long userId, String tokenFamily) {
        String refreshToken = UUID.randomUUID().toString().replace("-", "");
        String refreshKey = REFRESH_TOKEN_PREFIX + refreshToken;

        java.util.Map<String, Object> tokenData = new java.util.HashMap<>();
        tokenData.put("userId", userId.toString());
        tokenData.put("family", tokenFamily);
        tokenData.put("createTime", String.valueOf(System.currentTimeMillis()));
        tokenData.put("version", getTokenVersion(userId).toString());

        redisUtils.hset(refreshKey, tokenData);
        redisUtils.expire(refreshKey, refreshTokenExpiration, TimeUnit.DAYS);

        log.debug("生成Refresh Token: userId={}, tokenFamily={}", userId, tokenFamily);
        return refreshToken;
    }

    /**
     * 验证Refresh Token
     *
     * @param refreshToken Refresh Token
     * @return 验证结果
     */
    public RefreshTokenResult validateRefreshToken(String refreshToken) {
        String refreshKey = REFRESH_TOKEN_PREFIX + refreshToken;

        if (!redisUtils.hasKey(refreshKey)) {
            return new RefreshTokenResult(false, "Refresh Token无效或已过期", null, null);
        }

        java.util.Map<Object, Object> tokenData = redisUtils.hgetAll(refreshKey);
        if (tokenData == null || tokenData.isEmpty()) {
            return new RefreshTokenResult(false, "Refresh Token无效", null, null);
        }

        String userIdStr = (String) tokenData.get("userId");
        String family = (String) tokenData.get("family");
        String storedVersion = (String) tokenData.get("version");

        if (userIdStr == null) {
            return new RefreshTokenResult(false, "Refresh Token无效", null, null);
        }

        Long userId = Long.parseLong(userIdStr);

        // 检查Token版本是否匹配
        Integer currentVersion = getTokenVersion(userId);
        if (storedVersion != null && !storedVersion.equals(currentVersion.toString())) {
            // 版本不匹配，说明密码已修改或账号被登出
            log.warn("Refresh Token版本不匹配: userId={}, stored={}, current={}", userId, storedVersion, currentVersion);
            return new RefreshTokenResult(false, "Token已失效，请重新登录", null, null);
        }

        return new RefreshTokenResult(true, "验证成功", userId, family);
    }

    /**
     * 使用Refresh Token获取新的Access Token
     *
     * @param refreshToken Refresh Token
     * @param tokenGenerator Token生成器（实际生成由调用方执行）
     * @return 新的Token信息
     */
    public RefreshResult refreshAccessToken(String refreshToken, java.util.function.Function<RefreshResult.RefreshRequest, RefreshResult> tokenGenerator) {
        RefreshTokenResult validation = validateRefreshToken(refreshToken);
        if (!validation.isValid()) {
            return RefreshResult.fail(validation.getMessage());
        }

        // 使用完毕后删除旧的Refresh Token（单次使用）
        deleteRefreshToken(refreshToken);

        // 生成新的Token
        return tokenGenerator.apply(new RefreshResult.RefreshRequest(
                validation.getUserId(),
                validation.getFamily(),
                refreshToken
        ));
    }

    /**
     * 删除Refresh Token
     *
     * @param refreshToken Refresh Token
     */
    public void deleteRefreshToken(String refreshToken) {
        String refreshKey = REFRESH_TOKEN_PREFIX + refreshToken;
        redisUtils.delete(refreshKey);
    }

    /**
     * 获取用户Token版本号
     *
     * @param userId 用户ID
     * @return 版本号
     */
    public Integer getTokenVersion(Long userId) {
        String versionKey = TOKEN_VERSION_PREFIX + userId;
        Object version = redisUtils.get(versionKey);
        return version != null ? Integer.parseInt(version.toString()) : 0;
    }

    /**
     * 增加Token版本号（用于密码修改、强制登出等场景）
     *
     * @param userId 用户ID
     */
    public void incrementTokenVersion(Long userId) {
        String versionKey = TOKEN_VERSION_PREFIX + userId;
        Long newVersion = redisUtils.increment(versionKey);
        // 设置较长的过期时间（一年）
        redisUtils.expire(versionKey, 365, TimeUnit.DAYS);
        log.info("用户Token版本更新: userId={}, newVersion={}", userId, newVersion);
    }

    /**
     * 使指定用户的所有Token失效
     *
     * @param userId 用户ID
     */
    public void invalidateAllUserTokens(Long userId) {
        incrementTokenVersion(userId);
        log.info("用户所有Token已失效: userId={}", userId);
    }

    /**
     * 检测Token家族（用于检测Token被盗用的情况）
     *
     * @param userId  用户ID
     * @param token   JWT Token
     * @param family  Token家族
     * @return 是否正常
     */
    public boolean checkTokenFamily(Long userId, String token, String family) {
        String familyKey = TOKEN_FAMILY_PREFIX + userId;
        String storedFamily = (String) redisUtils.get(familyKey);

        if (storedFamily == null) {
            // 首次记录
            redisUtils.set(familyKey, family, refreshTokenExpiration, TimeUnit.DAYS);
            return true;
        }

        if (!storedFamily.equals(family)) {
            // 家族不匹配，可能是Token被盗用
            log.warn("检测到Token家族异常: userId={}, storedFamily={}, newFamily={}", userId, storedFamily, family);
            return false;
        }

        return true;
    }

    /**
     * 更新Token家族
     *
     * @param userId  用户ID
     * @param family  新的Token家族
     */
    public void updateTokenFamily(Long userId, String family) {
        String familyKey = TOKEN_FAMILY_PREFIX + userId;
        redisUtils.set(familyKey, family, refreshTokenExpiration, TimeUnit.DAYS);
    }

    /**
     * 生成Token家族标识
     *
     * @return 新的Token家族
     */
    public String generateTokenFamily() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 获取Token剩余有效期（秒）
     *
     * @param token          JWT Token
     * @param expirationTime 过期时间戳
     * @return 剩余秒数
     */
    public long getTokenRemainingSeconds(String token, long expirationTime) {
        long remaining = expirationTime - System.currentTimeMillis();
        return Math.max(0, remaining / 1000);
    }

    /**
     * Hash Token用于存储（保护Token原文）
     *
     * @param token Token原文
     * @return Hash后的Token
     */
    private String hashToken(String token) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(token.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            log.error("Token Hash失败", e);
            return token;
        }
    }

    /**
     * Refresh Token验证结果
     */
    public static class RefreshTokenResult {
        private final boolean valid;
        private final String message;
        private final Long userId;
        private final String family;

        public RefreshTokenResult(boolean valid, String message, Long userId, String family) {
            this.valid = valid;
            this.message = message;
            this.userId = userId;
            this.family = family;
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }

        public Long getUserId() {
            return userId;
        }

        public String getFamily() {
            return family;
        }
    }

    /**
     * Token刷新结果
     */
    public static class RefreshResult {
        private final boolean success;
        private final String message;
        private final String accessToken;
        private final String refreshToken;
        private final Long expiresIn;

        public RefreshResult(boolean success, String message, String accessToken, String refreshToken, Long expiresIn) {
            this.success = success;
            this.message = message;
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.expiresIn = expiresIn;
        }

        public static RefreshResult success(String accessToken, String refreshToken, long expiresIn) {
            return new RefreshResult(true, "刷新成功", accessToken, refreshToken, expiresIn);
        }

        public static RefreshResult fail(String message) {
            return new RefreshResult(false, message, null, null, null);
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public Long getExpiresIn() {
            return expiresIn;
        }

        public static class RefreshRequest {
            private final Long userId;
            private final String family;
            private final String oldRefreshToken;

            public RefreshRequest(Long userId, String family, String oldRefreshToken) {
                this.userId = userId;
                this.family = family;
                this.oldRefreshToken = oldRefreshToken;
            }

            public Long getUserId() {
                return userId;
            }

            public String getFamily() {
                return family;
            }

            public String getOldRefreshToken() {
                return oldRefreshToken;
            }
        }
    }

    /**
     * 安全登出（使Token失效）
     *
     * @param token     JWT Token
     * @param userId    用户ID
     * @param remainingSeconds Token剩余有效期
     */
    public void secureLogout(String token, Long userId, long remainingSeconds) {
        // 将Token加入黑名单
        blacklistToken(token, remainingSeconds);

        // 增加Token版本号，使所有Refresh Token失效
        incrementTokenVersion(userId);

        log.info("安全登出: userId={}, remainingSeconds={}", userId, remainingSeconds);
    }

    /**
     * 检查是否需要刷新Token
     *
     * @param expirationTime 过期时间
     * @param threshold       阈值（秒）
     * @return 是否需要刷新
     */
    public boolean needsRefresh(long expirationTime, long threshold) {
        long remaining = (expirationTime - System.currentTimeMillis()) / 1000;
        return remaining < threshold;
    }
}

