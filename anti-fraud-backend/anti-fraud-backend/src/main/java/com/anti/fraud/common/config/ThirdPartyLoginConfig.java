package com.anti.fraud.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 第三方登录配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "third-party")
public class ThirdPartyLoginConfig {

    /**
     * 微信登录配置
     */
    private WechatConfig wechat;

    /**
     * 钉钉登录配置
     */
    private DingTalkConfig dingtalk;

    @Data
    public static class WechatConfig {
        /**
         * 微信App ID
         */
        private String appId;

        /**
         * 微信App Secret
         */
        private String appSecret;

        /**
         * 微信回调地址
         */
        private String redirectUri;

        /**
         * 微信授权 scope
         */
        private String scope;
    }

    @Data
    public static class DingTalkConfig {
        /**
         * 钉钉App ID
         */
        private String appId;

        /**
         * 钉钉App Secret
         */
        private String appSecret;

        /**
         * 钉钉回调地址
         */
        private String redirectUri;

        /**
         * 钉钉授权 scope
         */
        private String scope;
    }
}
