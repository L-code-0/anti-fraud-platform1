package com.anti.fraud.common.config;

import com.anti.fraud.modules.websocket.handler.WebSocketHandler;
import com.anti.fraud.modules.websocket.interceptor.WebSocketAuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket配置
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    
    private final WebSocketHandler webSocketHandler;
    private final WebSocketAuthInterceptor webSocketAuthInterceptor;
    
    public WebSocketConfig(WebSocketHandler webSocketHandler, WebSocketAuthInterceptor webSocketAuthInterceptor) {
        this.webSocketHandler = webSocketHandler;
        this.webSocketAuthInterceptor = webSocketAuthInterceptor;
    }
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 注册WebSocket端点，允许跨域
        registry.addHandler(webSocketHandler, "/ws")
                .addInterceptors(webSocketAuthInterceptor)
                .setAllowedOrigins("*");
    }
}
