package com.anti.fraud.config;

import com.anti.fraud.common.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * WebSocket认证拦截器
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        try {
            // 从请求参数中获取JWT Token
            String token = request.getURI().getQuery();
            if (token == null || token.isEmpty()) {
                log.warn("WebSocket连接失败：缺少JWT Token");
                return false;
            }

            // 验证JWT Token
            String userIdStr = jwtUtil.getClaim(token, "userId");
            if (userIdStr == null || userIdStr.isEmpty()) {
                log.warn("WebSocket连接失败：无效的JWT Token");
                return false;
            }

            Long userId = Long.valueOf(userIdStr);
            if (userId == null) {
                log.warn("WebSocket连接失败：无效的用户ID");
                return false;
            }

            // 验证Token是否过期
            if (jwtUtil.isExpired(token)) {
                log.warn("WebSocket连接失败：JWT Token已过期");
                return false;
            }

            // 将用户ID存储到会话属性中
            attributes.put("userId", userId);
            log.info("用户{}的WebSocket连接认证成功", userId);
            return true;
        } catch (Exception e) {
            log.error("WebSocket认证失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception ex) {
        // 握手后的处理，如记录日志等
        if (ex != null) {
            log.error("WebSocket握手失败: {}", ex.getMessage(), ex);
        }
    }
}
