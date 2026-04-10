package com.anti.fraud.common.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 安全响应头配置
 * 配置各种安全相关的HTTP响应头
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class SecurityHeadersConfig {

    /**
     * 安全响应头过滤器
     * 在每个请求上添加安全相关的HTTP响应头
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public FilterRegistrationBean<SecurityHeadersFilter> securityHeadersFilter() {
        FilterRegistrationBean<SecurityHeadersFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new SecurityHeadersFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registrationBean.setName("securityHeadersFilter");
        return registrationBean;
    }

    /**
     * 安全响应头过滤器实现
     */
    public static class SecurityHeadersFilter extends OncePerRequestFilter {

        // 允许的Content-Type值
        private static final List<String> SAFE_CONTENT_TYPES = Arrays.asList(
                "application/json",
                "application/xml",
                "text/html",
                "text/plain",
                "text/css",
                "text/javascript",
                "application/javascript",
                "image/png",
                "image/jpeg",
                "image/gif",
                "image/svg+xml"
        );

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                        FilterChain filterChain) throws ServletException, IOException {
            
            // 跳过静态资源和API文档
            String path = request.getRequestURI();
            if (isStaticResource(path)) {
                filterChain.doFilter(request, response);
                return;
            }

            // 添加安全响应头
            addSecurityHeaders(response);

            filterChain.doFilter(request, response);
        }

        /**
         * 添加所有安全响应头
         */
        private void addSecurityHeaders(HttpServletResponse response) {
            // 1. X-Content-Type-Options: 防止MIME类型嗅探
            response.setHeader("X-Content-Type-Options", "nosniff");

            // 2. X-Frame-Options: 防止点击劫持
            response.setHeader("X-Frame-Options", "DENY");

            // 3. X-XSS-Protection: XSS防护（现代浏览器已支持CSP，此头主要用于兼容旧浏览器）
            response.setHeader("X-XSS-Protection", "1; mode=block");

            // 4. Strict-Transport-Security (HSTS): 强制使用HTTPS
            response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");

            // 5. Content-Security-Policy (CSP): 内容安全策略
            response.setHeader("Content-Security-Policy", 
                    "default-src 'self'; " +
                    "script-src 'self' 'unsafe-inline' 'unsafe-eval' https://cdn.jsdelivr.net https://cdn.bootcdn.net; " +
                    "style-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net https://cdn.bootcdn.net; " +
                    "img-src 'self' data: https:; " +
                    "font-src 'self' data: https://cdn.jsdelivr.net https://cdn.bootcdn.net; " +
                    "connect-src 'self' https:; " +
                    "frame-ancestors 'none';");

            // 6. X-Content-Security-Policy (旧版浏览器兼容)
            response.setHeader("X-Content-Security-Policy", 
                    "default-src 'self'; script-src 'self' 'unsafe-inline' 'unsafe-eval';");

            // 7. Referrer-Policy: 控制Referer头
            response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

            // 8. Permissions-Policy: 限制浏览器功能权限
            response.setHeader("Permissions-Policy", 
                    "geolocation=(), " +
                    "microphone=(), " +
                    "camera=(), " +
                    "payment=()");

            // 9. Cache-Control: 敏感页面不缓存
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, proxy-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");

            // 10. Remove X-Powered-By 头（已在服务器配置，但保险起见）
            response.setHeader("X-Powered-By", "");
        }

        /**
         * 判断是否为静态资源
         */
        private boolean isStaticResource(String path) {
            return path.contains("/webjars/") ||
                   path.contains("/static/") ||
                   path.contains("/public/") ||
                   path.endsWith(".js") ||
                   path.endsWith(".css") ||
                   path.endsWith(".png") ||
                   path.endsWith(".jpg") ||
                   path.endsWith(".ico") ||
                   path.endsWith(".svg") ||
                   path.contains("/doc.html") ||
                   path.contains("/swagger");
        }
    }

    /**
     * CORS配置
     */
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 允许的来源（生产环境应该配置具体域名）
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        
        // 允许的方法
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        // 允许的请求头
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // 允许携带凭证
        configuration.setAllowCredentials(true);
        
        // 预检请求缓存时间
        configuration.setMaxAge(3600L);
        
        // 暴露的响应头
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "X-CSRF-Token",
                "X-Requested-With",
                "X-Device-ID",
                "Content-Disposition"
        ));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
