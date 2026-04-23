package com.anti.fraud.config;

import com.anti.fraud.modules.websocket.manager.HeartbeatManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * WebSocket初始化器
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private final HeartbeatManager heartbeatManager;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 初始化心跳管理器
        heartbeatManager.init();
        log.info("WebSocket初始化完成");
    }
}
