package com.anti.fraud.modules.user.service;

import com.anti.fraud.modules.user.vo.LoginVO;

import java.util.Map;

/**
 * 第三方登录服务
 */
public interface ThirdPartyLoginService {
    
    /**
     * 微信登录
     * @param code 微信授权码
     * @return 登录结果
     */
    LoginVO wechatLogin(String code);
    
    /**
     * 钉钉登录
     * @param code 钉钉授权码
     * @return 登录结果
     */
    LoginVO dingTalkLogin(String code);
    
    /**
     * 获取微信登录二维码
     * @return 二维码信息
     */
    Map<String, String> getWechatQrCode();
    
    /**
     * 获取钉钉登录二维码
     * @return 二维码信息
     */
    Map<String, String> getDingTalkQrCode();
    
    /**
     * 绑定第三方账号
     * @param userId 用户ID
     * @param platform 平台名称
     * @param openId 第三方平台的OpenID
     * @return 是否绑定成功
     */
    boolean bindThirdPartyAccount(Long userId, String platform, String openId);
    
    /**
     * 解绑第三方账号
     * @param userId 用户ID
     * @param platform 平台名称
     * @return 是否解绑成功
     */
    boolean unbindThirdPartyAccount(Long userId, String platform);
    
    /**
     * 获取用户绑定的第三方账号
     * @param userId 用户ID
     * @return 绑定的第三方账号信息
     */
    Map<String, String> getBoundThirdPartyAccounts(Long userId);
}
