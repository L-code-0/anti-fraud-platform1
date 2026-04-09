package com.anti.fraud.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * CORS 跨域配置属性类
 * 从配置文件读取允许的域名列表，支持环境变量配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "cors")
public class CorsProperties {

    /**
     * 允许的源域名模式列表
     * 多个值用逗号分隔
     * 示例: http://localhost:3000,http://127.0.0.1:3000
     * 
     * 【安全警告】生产环境禁止使用通配符 *
     * 必须在 application-prod.yml 中显式配置允许的域名
     */
    private String allowedPatterns = "http://localhost:*,http://127.0.0.1:*";

    /**
     * 允许的 HTTP 方法
     */
    private String allowedMethods = "GET,POST,PUT,DELETE,OPTIONS";

    /**
     * 预检请求缓存时间（秒）
     */
    private Long maxAge = 3600L;

    /**
     * 是否允许携带凭证（cookies）
     */
    private Boolean allowCredentials = true;

    /**
     * 获取解析后的域名列表
     */
    public List<String> getAllowedOrigins() {
        if (allowedPatterns == null || allowedPatterns.isEmpty()) {
            return Arrays.asList();
        }
        return Arrays.asList(allowedPatterns.split(","));
    }

    /**
     * 获取解析后的方法列表
     */
    public List<String> getAllowedMethodsList() {
        if (allowedMethods == null || allowedMethods.isEmpty()) {
            return Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS");
        }
        return Arrays.asList(allowedMethods.split(","));
    }
}
