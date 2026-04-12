package com.anti.fraud.security.filter;

import com.anti.fraud.common.exception.BusinessException;
import com.anti.fraud.common.result.Result;
import com.anti.fraud.security.service.RateLimitService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

/**
 * API限流过滤器
 * 对所有API请求应用频率限制
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 跳过静态资源和Swagger文档
        String path = request.getRequestURI();
        if (isSkipPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 检查API限流
        RateLimitService.RateLimitResult result = rateLimitService.checkApiLimit(request);
        
        if (!result.isAllowed()) {
            // 限流拒绝
            response.setStatus(429); // Too Many Requests
            response.setContentType("application/json; charset=utf-8");
            
            Result<?> errorResult = Result.fail(429, result.getMessage());
            Map<String, Object> responseMap = Map.of(
                    "code", 429,
                    "message", result.getMessage(),
                    "remaining", result.getRemaining(),
                    "limit", result.getLimit(),
                    "resetTime", result.getResetTime()
            );
            
            response.getWriter().write(objectMapper.writeValueAsString(responseMap));
            log.warn("API限流拒绝: path={}, message={}", path, result.getMessage());
            return;
        }

        // 添加限流响应头
        response.setHeader("X-RateLimit-Limit", String.valueOf(result.getLimit()));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(result.getRemaining()));
        response.setHeader("X-RateLimit-Reset", String.valueOf(result.getResetTime()));

        filterChain.doFilter(request, response);
    }

    /**
     * 判断是否跳过限流检查的路径
     */
    private boolean isSkipPath(String path) {
        return path.startsWith("/swagger") ||
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/webjars") ||
               path.startsWith("/favicon.ico") ||
               path.startsWith("/error") ||
               path.startsWith("/actuator");
    }
}
