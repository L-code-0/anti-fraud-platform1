package com.anti.fraud.modules.websocket.interceptor;

import com.anti.fraud.common.utils.JwtUtils;
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
    
    private final JwtUtils jwtUtils;
    
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, 
                                 WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // 从请求参数中获取token
        String token = request.getURI().getQuery();
        if (token != null && token.startsWith("token=")) {
            token = token.substring(6);
            try {
                // 验证token
                Long userId = jwtUtils.getUserIdFromToken(token);
                if (userId != null) {
                    // 将用户ID存储到会话属性中
                    attributes.put("userId", userId);
                    log.info("WebSocket认证成功: userId={}", userId);
                    return true;
                }
            } catch (Exception e) {
                log.error("WebSocket认证失败: {}", e.getMessage());
            }
        }
        log.warn("WebSocket认证失败: 未提供有效的token");
        return false;
    }
    
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, 
                              WebSocketHandler wsHandler, Exception exception) {
        // 握手完成后的处理
    }
}
