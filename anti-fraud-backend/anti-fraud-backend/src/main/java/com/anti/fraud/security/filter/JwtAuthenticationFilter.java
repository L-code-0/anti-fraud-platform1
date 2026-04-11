package com.anti.fraud.security.filter;

import com.anti.fraud.common.enums.UserRole;
import com.anti.fraud.common.utils.JwtUtils;
import com.anti.fraud.common.utils.RedisUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtils jwtUtils;
    private final RedisUtils redisUtils;
    private final ObjectMapper objectMapper;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, RedisUtils redisUtils, ObjectMapper objectMapper) {
        this.jwtUtils = jwtUtils;
        this.redisUtils = redisUtils;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getTokenFromRequest(request);

        if (StringUtils.hasText(token) && jwtUtils.validateToken(token)) {
            boolean redisAvailable = isRedisAvailable();

            if (!redisAvailable) {
                log.error("Redis服务不可用，拒绝认证请求");
                sendErrorResponse(response, HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                        "Authentication service temporarily unavailable");
                return;
            }

            try {
                String tokenKey = "token:" + token;
                if (redisUtils.hasKey(tokenKey)) {
                    Long userId = jwtUtils.getUserIdFromToken(token);
                    Integer role = jwtUtils.getRoleFromToken(token);

                    if (userId != null && role != null) {
                        String authority = UserRole.getAuthorityByCode(role);
                        if (authority != null) {
                            SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(authority);
                            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId, null, Collections.singletonList(grantedAuthority));
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        }
                    }
                } else {
                    log.warn("Token不存在于Redis中，可能已被撤销: {}", token.substring(0, Math.min(20, token.length())));
                }
            } catch (RedisConnectionFailureException e) {
                log.error("Redis连接失败，拒绝认证请求", e);
                sendErrorResponse(response, HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                        "Authentication service temporarily unavailable");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isRedisAvailable() {
        try {
            redisUtils.hasKey("health-check");
            return true;
        } catch (Exception e) {
            log.warn("Redis健康检查失败: {}", e.getMessage());
            return false;
        }
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("code", status);
        errorResponse.put("message", message);
        errorResponse.put("timestamp", System.currentTimeMillis());

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
