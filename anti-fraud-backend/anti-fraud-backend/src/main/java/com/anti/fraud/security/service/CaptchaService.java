package com.anti.fraud.security.service;

import com.anti.fraud.common.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务
 * 提供短信验证码、邮箱验证码的生成、存储和验证功能
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CaptchaService {

    private final RedisUtils redisUtils;

    // Redis Key 前缀
    private static final String SMS_CODE_PREFIX = "captcha:sms:";
    private static final String EMAIL_CODE_PREFIX = "captcha:email:";
    private static final String CAPTCHA_RATE_LIMIT_PREFIX = "captcha:ratelimit:";

    // 验证码有效期（分钟）
    private static final int SMS_CODE_EXPIRE_MINUTES = 5;
    private static final int EMAIL_CODE_EXPIRE_MINUTES = 30;

    // 验证码发送间隔（分钟）
    private static final int SMS_SEND_INTERVAL_MINUTES = 1;
    private static final int EMAIL_SEND_INTERVAL_MINUTES = 5;

    // 每日发送上限
    private static final int DAILY_SMS_LIMIT = 10;
    private static final int DAILY_EMAIL_LIMIT = 20;

    private final Random random = new Random();

    /**
     * 验证码类型
     */
    public enum CaptchaType {
        SMS("短信验证码"),
        EMAIL("邮箱验证码");

        private final String description;

        CaptchaType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 验证码发送结果
     */
    public static class CaptchaResult {
        private final boolean success;
        private final String message;
        private final String captchaKey;

        public CaptchaResult(boolean success, String message, String captchaKey) {
            this.success = success;
            this.message = message;
            this.captchaKey = captchaKey;
        }

        public static CaptchaResult success(String captchaKey) {
            return new CaptchaResult(true, "发送成功", captchaKey);
        }

        public static CaptchaResult fail(String message) {
            return new CaptchaResult(false, message, null);
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public String getCaptchaKey() {
            return captchaKey;
        }
    }

    /**
     * 生成6位数字验证码
     *
     * @return 验证码
     */
    public String generateCode() {
        return String.format("%06d", random.nextInt(1000000));
    }

    /**
     * 生成指定位数的数字验证码
     *
     * @param length 长度
     * @return 验证码
     */
    public String generateCode(int length) {
        int max = (int) Math.pow(10, length) - 1;
        return String.format("%0" + length + "d", random.nextInt(max + 1));
    }

    /**
     * 发送短信验证码
     *
     * @param phone 手机号
     * @return 发送结果
     */
    public CaptchaResult sendSmsCode(String phone) {
        // 验证手机号格式
        if (!isValidPhone(phone)) {
            return CaptchaResult.fail("手机号格式不正确");
        }

        // 检查发送频率
        CaptchaResult rateCheck = checkSendRate(phone, CaptchaType.SMS);
        if (!rateCheck.isSuccess()) {
            return rateCheck;
        }

        // 检查每日发送上限
        CaptchaResult dailyCheck = checkDailyLimit(phone, CaptchaType.SMS);
        if (!dailyCheck.isSuccess()) {
            return dailyCheck;
        }

        // 生成验证码
        String code = generateCode();
        String captchaKey = generateCaptchaKey(phone, "sms");

        // 存储验证码
        String redisKey = SMS_CODE_PREFIX + phone;
        redisUtils.set(redisKey, code, SMS_CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);

        // 记录发送频率
        recordSendTime(phone, CaptchaType.SMS);

        // 增加每日发送计数
        incrementDailyCount(phone, CaptchaType.SMS);

        // 实际发送短信（这里模拟，实际应调用短信服务）
        log.info("短信验证码已发送: phone={}, code={}", maskPhone(phone), code);

        // TODO: 调用短信网关发送真实短信
        // smsGateway.send(phone, code);

        return CaptchaResult.success(captchaKey);
    }

    /**
     * 发送邮箱验证码
     *
     * @param email 邮箱
     * @return 发送结果
     */
    public CaptchaResult sendEmailCode(String email) {
        // 验证邮箱格式
        if (!isValidEmail(email)) {
            return CaptchaResult.fail("邮箱格式不正确");
        }

        // 检查发送频率
        CaptchaResult rateCheck = checkSendRate(email, CaptchaType.EMAIL);
        if (!rateCheck.isSuccess()) {
            return rateCheck;
        }

        // 检查每日发送上限
        CaptchaResult dailyCheck = checkDailyLimit(email, CaptchaType.EMAIL);
        if (!dailyCheck.isSuccess()) {
            return dailyCheck;
        }

        // 生成验证码
        String code = generateCode(6);
        String captchaKey = generateCaptchaKey(email, "email");

        // 存储验证码
        String redisKey = EMAIL_CODE_PREFIX + email;
        redisUtils.set(redisKey, code, EMAIL_CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);

        // 记录发送频率
        recordSendTime(email, CaptchaType.EMAIL);

        // 增加每日发送计数
        incrementDailyCount(email, CaptchaType.EMAIL);

        log.info("邮箱验证码已发送: email={}, code={}", maskEmail(email), code);

        // TODO: 调用邮件服务发送真实邮件
        // emailService.send(email, "验证码", "您的验证码是: " + code);

        return CaptchaResult.success(captchaKey);
    }

    /**
     * 验证短信验证码
     *
     * @param phone 手机号
     * @param code  验证码
     * @return 是否正确
     */
    public boolean verifySmsCode(String phone, String code) {
        return verifyCode(SMS_CODE_PREFIX + phone, code);
    }

    /**
     * 验证邮箱验证码
     *
     * @param email 邮箱
     * @param code  验证码
     * @return 是否正确
     */
    public boolean verifyEmailCode(String email, String code) {
        return verifyCode(EMAIL_CODE_PREFIX + email, code);
    }

    /**
     * 验证验证码
     *
     * @param redisKey Redis Key
     * @param code     用户输入的验证码
     * @return 是否正确
     */
    private boolean verifyCode(String redisKey, String code) {
        String storedCode = (String) redisUtils.get(redisKey);

        if (storedCode == null) {
            log.debug("验证码已过期或不存在: {}", redisKey);
            return false;
        }

        if (storedCode.equals(code)) {
            // 验证成功后删除验证码（单次使用）
            redisUtils.delete(redisKey);
            log.debug("验证码验证成功: {}", redisKey);
            return true;
        }

        log.debug("验证码错误: {}, input={}, stored={}", redisKey, code, storedCode);
        return false;
    }

    /**
     * 检查发送频率限制
     *
     * @param identifier 标识（手机号或邮箱）
     * @param type       验证码类型
     * @return 检查结果
     */
    private CaptchaResult checkSendRate(String identifier, CaptchaType type) {
        String rateKey = CAPTCHA_RATE_LIMIT_PREFIX + type.name().toLowerCase() + ":" + identifier;
        Long lastSendTime = (Long) redisUtils.get(rateKey);

        int intervalMinutes = type == CaptchaType.SMS ? SMS_SEND_INTERVAL_MINUTES : EMAIL_SEND_INTERVAL_MINUTES;

        if (lastSendTime != null) {
            long timeSinceLastSend = (System.currentTimeMillis() - lastSendTime) / 1000 / 60;
            if (timeSinceLastSend < intervalMinutes) {
                long remainingSeconds = (intervalMinutes - timeSinceLastSend) * 60;
                return CaptchaResult.fail("请" + (remainingSeconds / 60 + 1) + "分钟后重试");
            }
        }

        return CaptchaResult.success(null);
    }

    /**
     * 检查每日发送上限
     *
     * @param identifier 标识
     * @param type       验证码类型
     * @return 检查结果
     */
    private CaptchaResult checkDailyLimit(String identifier, CaptchaType type) {
        String dailyKey = "captcha:daily:" + type.name().toLowerCase() + ":" + getDateKey(identifier);
        Object countObj = redisUtils.get(dailyKey);
        int count = countObj != null ? ((Number) countObj).intValue() : 0;

        int limit = type == CaptchaType.SMS ? DAILY_SMS_LIMIT : DAILY_EMAIL_LIMIT;

        if (count >= limit) {
            return CaptchaResult.fail("今日发送次数已达上限，请明天再试");
        }

        return CaptchaResult.success(null);
    }

    /**
     * 记录发送时间
     *
     * @param identifier 标识
     * @param type       验证码类型
     */
    private void recordSendTime(String identifier, CaptchaType type) {
        String rateKey = CAPTCHA_RATE_LIMIT_PREFIX + type.name().toLowerCase() + ":" + identifier;
        int intervalMinutes = type == CaptchaType.SMS ? SMS_SEND_INTERVAL_MINUTES : EMAIL_SEND_INTERVAL_MINUTES;
        redisUtils.set(rateKey, System.currentTimeMillis(), intervalMinutes, TimeUnit.MINUTES);
    }

    /**
     * 增加每日发送计数
     *
     * @param identifier 标识
     * @param type       验证码类型
     */
    private void incrementDailyCount(String identifier, CaptchaType type) {
        String dailyKey = "captcha:daily:" + type.name().toLowerCase() + ":" + getDateKey(identifier);
        Long count = redisUtils.increment(dailyKey);
        if (count != null && count == 1) {
            // 设置过期时间为后天凌晨
            redisUtils.expire(dailyKey, 48, TimeUnit.HOURS);
        }
    }

    /**
     * 生成日期key（用于每日限制）
     */
    private String getDateKey(String identifier) {
        // 使用hashCode确保相同标识在同一日期得到相同key
        int hash = (identifier + "_" + java.time.LocalDate.now()).hashCode();
        return String.valueOf(Math.abs(hash));
    }

    /**
     * 验证手机号格式
     *
     * @param phone 手机号
     * @return 是否有效
     */
    private boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^1[3-9]\\d{9}$");
    }

    /**
     * 验证邮箱格式
     *
     * @param email 邮箱
     * @return 是否有效
     */
    private boolean isValidEmail(String email) {
        if (email == null) return false;
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }

    /**
     * 生成验证码Key
     *
     * @param identifier 标识
     * @param type      类型
     * @return Key
     */
    private String generateCaptchaKey(String identifier, String type) {
        return type + ":" + identifier.hashCode() + ":" + System.currentTimeMillis();
    }

    /**
     * 手机号脱敏
     *
     * @param phone 手机号
     * @return 脱敏后的手机号
     */
    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 11) return phone;
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    /**
     * 邮箱脱敏
     *
     * @param email 邮箱
     * @return 脱敏后的邮箱
     */
    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return email;
        int atIndex = email.indexOf("@");
        String prefix = email.substring(0, atIndex);
        String suffix = email.substring(atIndex);
        if (prefix.length() <= 3) {
            return prefix.charAt(0) + "***" + suffix;
        }
        return prefix.substring(0, 3) + "***" + suffix;
    }

    /**
     * 获取验证码剩余有效期（秒）
     *
     * @param phone 手机号或邮箱
     * @param type  验证码类型
     * @return 剩余秒数，-1表示不存在
     */
    public long getCaptchaRemainingSeconds(String phone, CaptchaType type) {
        String prefix = type == CaptchaType.SMS ? SMS_CODE_PREFIX : EMAIL_CODE_PREFIX;
        String redisKey = prefix + phone;
        Long expire = redisUtils.getExpire(redisKey);
        return expire != null && expire > 0 ? expire : -1;
    }

    /**
     * 删除验证码（用于测试或管理）
     *
     * @param phone 手机号或邮箱
     * @param type  验证码类型
     */
    public void deleteCaptcha(String phone, CaptchaType type) {
        String prefix = type == CaptchaType.SMS ? SMS_CODE_PREFIX : EMAIL_CODE_PREFIX;
        String redisKey = prefix + phone;
        redisUtils.delete(redisKey);
    }
}
