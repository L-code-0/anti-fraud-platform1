package com.anti.fraud.modules.user.service;

import com.anti.fraud.modules.user.vo.LoginVO;

import java.util.Map;

/**
 * 单点登录(SSO)服务
 */
public interface SSOService {
    
    /**
     * 生成SSO登录URL
     * @param redirectUri 回调地址
     * @param state 状态参数
     * @return SSO登录URL
     */
    String generateSSOLoginUrl(String redirectUri, String state);
    
    /**
     * 处理SSO回调
     * @param code 授权码
     * @param state 状态参数
     * @return 登录结果
     */
    LoginVO handleSSOCallback(String code, String state);
    
    /**
     * 验证SSO令牌
     * @param token SSO令牌
     * @return 验证结果
     */
    Map<String, Object> validateSSOToken(String token);
    
    /**
     * 生成SSO令牌
     * @param userId 用户ID
     * @return SSO令牌
     */
    String generateSSOToken(Long userId);
    
    /**
     * 注销SSO会话
     * @param token SSO令牌
     * @return 是否注销成功
     */
    boolean logoutSSO(String token);
    
    /**
     * 获取SSO配置信息
     * @return 配置信息
     */
    Map<String, String> getSSOConfig();
}
