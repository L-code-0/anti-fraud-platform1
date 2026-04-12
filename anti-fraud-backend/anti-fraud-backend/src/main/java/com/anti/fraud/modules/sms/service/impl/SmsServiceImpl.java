package com.anti.fraud.modules.sms.service.impl;

import com.anti.fraud.modules.sms.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 短信服务实现
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService {
    
    private final RedisTemplate<String, String> redisTemplate;
    
    @Value("${sms.code.expire-time}")
    private long codeExpireTime;
    
    @Value("${sms.code.prefix}")
    private String codePrefix;
    
    @Override
    public boolean sendVerificationCode(String phone, String code) {
        try {
            // 存储验证码到Redis
            String key = codePrefix + phone;
            redisTemplate.opsForValue().set(key, code, codeExpireTime, TimeUnit.MINUTES);
            
            // 模拟短信发送
            log.info("Send verification code to {}: {}", phone, code);
            
            // 在实际项目中，这里需要调用真实的短信服务API
            // 例如：阿里云短信服务、腾讯云短信服务等
            
            return true;
        } catch (Exception e) {
            log.error("Send verification code failed: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean sendNotification(String phone, String templateId, String... params) {
        try {
            // 模拟短信发送
            log.info("Send notification to {} with template {} and params: {}", phone, templateId, params);
            
            // 在实际项目中，这里需要调用真实的短信服务API
            
            return true;
        } catch (Exception e) {
            log.error("Send notification failed: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean verifyCode(String phone, String code) {
        try {
            String key = codePrefix + phone;
            String storedCode = redisTemplate.opsForValue().get(key);
            
            if (code.equals(storedCode)) {
                // 验证成功后删除验证码
                redisTemplate.delete(key);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("Verify code failed: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public String generateCode(int length) {
        String digits = "0123456789";
        Random random = new Random();
        StringBuilder code = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            code.append(digits.charAt(random.nextInt(digits.length())));
        }
        return code.toString();
    }
}
