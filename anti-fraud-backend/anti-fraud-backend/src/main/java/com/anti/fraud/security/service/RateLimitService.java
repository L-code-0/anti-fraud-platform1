package com.anti.fraud.security.service;

import com.anti.fraud.common.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * 请求频率限制服务
 * 基于Redis实现滑动窗口算法的请求频率限制
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RateLimitService {

    private final RedisUtils redisUtils;

    // Redis Key 前缀
    private static final String RATE_LIMIT_PREFIX = "ratelimit:";

    // 限制类型枚举
    public enum LimitType {
        IP,           // 按IP限制
        USER,         // 按用户限制
        USER_IP,      // 按用户+IP组合限制
        GLOBAL        // 全局限制
    }

    /**
     * 频率限制结果
     */
    public static class RateLimitResult {
        private final boolean allowed;
        private final long remaining;
        private final long limit;
        private final long resetTime;
        private final String message;

        public RateLimitResult(boolean allowed, long remaining, long limit, long resetTime, String message) {
            this.allowed = allowed;
            this.remaining = remaining;
            this.limit = limit;
            this.resetTime = resetTime;
            this.message = message;
        }

        public static RateLimitResult allow(long remaining, long limit, long resetTime) {
            return new RateLimitResult(true, remaining, limit, resetTime, null);
        }

        public static RateLimitResult deny(long remaining, long limit, long resetTime, String message) {
            return new RateLimitResult(false, remaining, limit, resetTime, message);
        }

        public boolean isAllowed() {
            return allowed;
        }

        public long getRemaining() {
            return remaining;
        }

        public long getLimit() {
            return limit;
        }

        public long getResetTime() {
            return resetTime;
        }

        public String getMessage() {
            return message;
        }
    }

    /**
     * 限制配置
     */
    public static class LimitConfig {
        private final int maxRequests;      // 时间窗口内最大请求数
        private final long windowSeconds;  // 时间窗口大小（秒）

        public LimitConfig(int maxRequests, long windowSeconds) {
            this.maxRequests = maxRequests;
            this.windowSeconds = windowSeconds;
        }

        public int getMaxRequests() {
            return maxRequests;
        }

        public long getWindowSeconds() {
            return windowSeconds;
        }
    }

    // 预定义限制配置
    public static final LimitConfig LOGIN_LIMIT = new LimitConfig(5, 300);           // 5分钟内5次
    public static final LimitConfig REGISTER_LIMIT = new LimitConfig(3, 3600);        // 1小时内3次
    public static final LimitConfig SMS_CODE_LIMIT = new LimitConfig(5, 3600);       // 1小时内5次
    public static final LimitConfig API_LIMIT = new LimitConfig(100, 60);           // 1分钟内100次
    public static final LimitConfig STRICT_API_LIMIT = new LimitConfig(30, 60);       // 1分钟内30次
    public static final LimitConfig SEARCH_LIMIT = new LimitConfig(20, 60);          // 1分钟内20次
    public static final LimitConfig EXPORT_LIMIT = new LimitConfig(5, 600);          // 10分钟内5次
    public static final LimitConfig REPORT_LIMIT = new LimitConfig(10, 600);         // 10分钟内10次
    public static final LimitConfig ADMIN_API_LIMIT = new LimitConfig(200, 60);      // 管理员API 1分钟内200次
    public static final LimitConfig USER_API_LIMIT = new LimitConfig(60, 60);        // 普通用户API 1分钟内60次

    /**
     * 检查请求是否允许
     *
     * @param request HTTP请求
     * @param type    限制类型
     * @param key     限制标识
     * @param config  限制配置
     * @return 限制结果
     */
    public RateLimitResult checkRateLimit(HttpServletRequest request, LimitType type, String key, LimitConfig config) {
        String identifier = buildIdentifier(request, type, key);
        return checkRateLimit(identifier, config);
    }

    /**
     * 检查请求是否允许
     *
     * @param identifier 标识
     * @param config     限制配置
     * @return 限制结果
     */
    public RateLimitResult checkRateLimit(String identifier, LimitConfig config) {
        String redisKey = RATE_LIMIT_PREFIX + identifier;

        // 使用Redis INCR + EXPIRE 实现滑动窗口
        Long currentCount = redisUtils.increment(redisKey);

        if (currentCount == null) {
            currentCount = 1L;
        }

        // 首次请求，设置过期时间
        if (currentCount == 1) {
            redisUtils.expire(redisKey, config.getWindowSeconds(), TimeUnit.SECONDS);
        }

        long remaining = Math.max(0, config.getMaxRequests() - currentCount);
        long resetTime = System.currentTimeMillis() + config.getWindowSeconds() * 1000;

        if (currentCount > config.getMaxRequests()) {
            long retryAfter = getExpireSeconds(redisKey);
            log.warn("请求频率超限: identifier={}, count={}, limit={}", identifier, currentCount, config.getMaxRequests());
            return RateLimitResult.deny(
                    0,
                    config.getMaxRequests(),
                    resetTime,
                    "请求过于频繁，请在" + retryAfter + "秒后重试"
            );
        }

        return RateLimitResult.allow(remaining, config.getMaxRequests(), resetTime);
    }

    /**
     * 构建限流标识
     */
    private String buildIdentifier(HttpServletRequest request, LimitType type, String key) {
        String ip = getClientIp(request);

        switch (type) {
            case IP:
                return "ip:" + ip;
            case USER:
                return "user:" + key;
            case USER_IP:
                return "user:" + key + ":ip:" + ip;
            case GLOBAL:
                return "global:" + key;
            default:
                return "ip:" + ip;
        }
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp(HttpServletRequest request) {
        String[] headerNames = {
                "X-Forwarded-For",
                "X-Real-IP",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_CLIENT_IP",
                "HTTP_X_FORWARDED_FOR"
        };

        String ip = null;
        for (String header : headerNames) {
            ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                break;
            }
        }

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 处理代理链
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        // IPv6本地地址
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }

        return ip;
    }

    /**
     * 获取Key剩余过期时间（秒）
     */
    private long getExpireSeconds(String redisKey) {
        Long expire = redisUtils.getExpire(redisKey);
        return expire != null && expire > 0 ? expire : 0;
    }

    /**
     * 重置限制计数
     *
     * @param request HTTP请求
     * @param type    限制类型
     * @param key     限制标识
     */
    public void resetLimit(HttpServletRequest request, LimitType type, String key) {
        String identifier = buildIdentifier(request, type, key);
        String redisKey = RATE_LIMIT_PREFIX + identifier;
        redisUtils.delete(redisKey);
        log.debug("重置频率限制: {}", identifier);
    }

    /**
     * 获取当前请求计数
     *
     * @param request HTTP请求
     * @param type    限制类型
     * @param key     限制标识
     * @return 当前计数
     */
    public long getCurrentCount(HttpServletRequest request, LimitType type, String key) {
        String identifier = buildIdentifier(request, type, key);
        String redisKey = RATE_LIMIT_PREFIX + identifier;
        Object count = redisUtils.get(redisKey);
        return count != null ? ((Number) count).longValue() : 0;
    }

    /**
     * 登录频率限制
     */
    public RateLimitResult checkLoginLimit(HttpServletRequest request, String username) {
        String ip = getClientIp(request);
        String identifier = "login:ip:" + ip + ":user:" + username;
        return checkRateLimit(identifier, LOGIN_LIMIT);
    }

    /**
     * 注册频率限制
     */
    public RateLimitResult checkRegisterLimit(HttpServletRequest request) {
        return checkRateLimit(request, LimitType.IP, "register", REGISTER_LIMIT);
    }

    /**
     * 短信验证码频率限制
     */
    public RateLimitResult checkSmsCodeLimit(HttpServletRequest request, String phone) {
        String identifier = "sms:phone:" + phone + ":ip:" + getClientIp(request);
        return checkRateLimit(identifier, SMS_CODE_LIMIT);
    }

    /**
     * API频率限制
     */
    public RateLimitResult checkApiLimit(HttpServletRequest request) {
        return checkRateLimit(request, LimitType.IP, "api", API_LIMIT);
    }

    /**
     * 敏感API频率限制
     */
    public RateLimitResult checkStrictApiLimit(HttpServletRequest request) {
        return checkRateLimit(request, LimitType.IP, "strict_api", STRICT_API_LIMIT);
    }

    /**
     * 搜索频率限制
     */
    public RateLimitResult checkSearchLimit(HttpServletRequest request) {
        return checkRateLimit(request, LimitType.IP, "search", SEARCH_LIMIT);
    }

    /**
     * 导出频率限制
     */
    public RateLimitResult checkExportLimit(HttpServletRequest request) {
        return checkRateLimit(request, LimitType.IP, "export", EXPORT_LIMIT);
    }

    /**
     * 举报频率限制
     */
    public RateLimitResult checkReportLimit(HttpServletRequest request) {
        return checkRateLimit(request, LimitType.IP, "report", REPORT_LIMIT);
    }

    /**
     * 管理员API频率限制
     */
    public RateLimitResult checkAdminApiLimit(HttpServletRequest request) {
        return checkRateLimit(request, LimitType.IP, "admin_api", ADMIN_API_LIMIT);
    }

    /**
     * 普通用户API频率限制
     */
    public RateLimitResult checkUserApiLimit(HttpServletRequest request) {
        return checkRateLimit(request, LimitType.IP, "user_api", USER_API_LIMIT);
    }

    /**
     * 基于路径的API频率限制
     */
    public RateLimitResult checkPathBasedLimit(HttpServletRequest request, String path, LimitConfig config) {
        String ip = getClientIp(request);
        String identifier = "path:" + path + ":ip:" + ip;
        return checkRateLimit(identifier, config);
    }

    /**
     * 基于用户角色的API频率限制
     */
    public RateLimitResult checkRoleBasedLimit(HttpServletRequest request, String role, LimitConfig config) {
        String ip = getClientIp(request);
        String identifier = "role:" + role + ":ip:" + ip;
        return checkRateLimit(identifier, config);
    }
}
