package com.anti.fraud.modules.sms.service;

/**
 * 短信服务
 */
public interface SmsService {
    
    /**
     * 发送短信验证码
     * @param phone 手机号
     * @param code 验证码
     * @return 是否发送成功
     */
    boolean sendVerificationCode(String phone, String code);
    
    /**
     * 发送通知短信
     * @param phone 手机号
     * @param templateId 模板ID
     * @param params 模板参数
     * @return 是否发送成功
     */
    boolean sendNotification(String phone, String templateId, String... params);
    
    /**
     * 验证短信验证码
     * @param phone 手机号
     * @param code 验证码
     * @return 是否验证成功
     */
    boolean verifyCode(String phone, String code);
    
    /**
     * 生成短信验证码
     * @param length 验证码长度
     * @return 验证码
     */
    String generateCode(int length);
}
