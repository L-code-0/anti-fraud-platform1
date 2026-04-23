package com.anti.fraud.modules.websocket.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 心跳管理器
 */
@Component
@Slf4j
public class HeartbeatManager {

    // 会话心跳状态，key: userId, value: 最后一次心跳时间
    private final Map<Long, Long> heartbeatMap = new ConcurrentHashMap<>();

    // 心跳超时时间（毫秒）
    private static final long HEARTBEAT_TIMEOUT = 30000; // 30秒

    // 心跳检查间隔（毫秒）
    private static final long HEARTBEAT_CHECK_INTERVAL = 10000; // 10秒

    // 定时任务线程池
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    /**
     * 初始化心跳管理器
     */
    public void init() {
        // 启动心跳检查任务
        executorService.scheduleAtFixedRate(this::checkHeartbeat, HEARTBEAT_CHECK_INTERVAL, HEARTBEAT_CHECK_INTERVAL, TimeUnit.MILLISECONDS);
        log.info("心跳管理器初始化完成，心跳检查间隔: {}ms，超时时间: {}ms", HEARTBEAT_CHECK_INTERVAL, HEARTBEAT_TIMEOUT);
    }

    /**
     * 注册心跳
     * @param userId 用户ID
     */
    public void registerHeartbeat(Long userId) {
        heartbeatMap.put(userId, System.currentTimeMillis());
        log.debug("用户{}心跳注册成功", userId);
    }

    /**
     * 更新心跳
     * @param userId 用户ID
     */
    public void updateHeartbeat(Long userId) {
        heartbeatMap.put(userId, System.currentTimeMillis());
        log.debug("用户{}心跳更新成功", userId);
    }

    /**
     * 移除心跳
     * @param userId 用户ID
     */
    public void removeHeartbeat(Long userId) {
        heartbeatMap.remove(userId);
        log.debug("用户{}心跳移除成功", userId);
    }

    /**
     * 检查心跳
     */
    private void checkHeartbeat() {
        long currentTime = System.currentTimeMillis();
        for (Map.Entry<Long, Long> entry : heartbeatMap.entrySet()) {
            Long userId = entry.getKey();
            Long lastHeartbeatTime = entry.getValue();
            if (currentTime - lastHeartbeatTime > HEARTBEAT_TIMEOUT) {
                // 心跳超时，关闭连接
                log.warn("用户{}心跳超时，准备关闭连接", userId);
                // 这里需要调用关闭连接的方法，暂时留空
                // TODO: 关闭用户的WebSocket连接
                removeHeartbeat(userId);
            }
        }
    }

    /**
     * 获取心跳状态
     * @param userId 用户ID
     * @return 心跳状态
     */
    public boolean isAlive(Long userId) {
        Long lastHeartbeatTime = heartbeatMap.get(userId);
        if (lastHeartbeatTime == null) {
            return false;
        }
        return System.currentTimeMillis() - lastHeartbeatTime <= HEARTBEAT_TIMEOUT;
    }

    /**
     * 获取所有活跃用户
     * @return 活跃用户ID列表
     */
    public java.util.Set<Long> getAliveUsers() {
        long currentTime = System.currentTimeMillis();
        return heartbeatMap.entrySet().stream()
                .filter(entry -> currentTime - entry.getValue() <= HEARTBEAT_TIMEOUT)
                .map(Map.Entry::getKey)
                .collect(java.util.stream.Collectors.toSet());
    }

    /**
     * 关闭心跳管理器
     */
    public void shutdown() {
        executorService.shutdown();
        log.info("心跳管理器已关闭");
    }
}
