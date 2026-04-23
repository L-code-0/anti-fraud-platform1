package com.anti.fraud.modules.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * AI服务配置类
 */
@Data
@Component
@ConfigurationProperties(prefix = "ai")
public class AiConfig {

    /**
     * 文心一言配置
     */
    private Wenxin wenxin;

    /**
     * ChatGLM配置
     */
    private Chatglm chatglm;

    /**
     * 默认AI服务提供商：wenxin 或 chatglm
     */
    private String defaultProvider;

    /**
     * 文心一言配置
     */
    @Data
    public static class Wenxin {
        /**
         * API密钥
         */
        private String apiKey;

        /**
         * 模型名称
         */
        private String model;

        /**
         * API请求URL
         */
        private String apiUrl;

        /**
         * 温度参数
         */
        private Double temperature;

        /**
         * 最大token数
         */
        private Integer maxTokens;
    }

    /**
     * ChatGLM配置
     */
    @Data
    public static class Chatglm {
        /**
         * API密钥
         */
        private String apiKey;

        /**
         * 模型名称
         */
        private String model;

        /**
         * API请求URL
         */
        private String apiUrl;

        /**
         * 温度参数
         */
        private Double temperature;

        /**
         * 最大token数
         */
        private Integer maxTokens;
    }
}
