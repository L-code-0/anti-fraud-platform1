package com.anti.fraud.modules.collaboration.manager;

import com.anti.fraud.modules.collaboration.algorithm.OTAlgorithm;
import com.anti.fraud.modules.collaboration.model.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 协作会话管理器
 */
@Component
@Slf4j
public class CollaborationSessionManager {

    // 会话存储，key: 会话ID, value: 会话信息
    private final Map<String, CollaborationSession> sessions = new ConcurrentHashMap<>();

    /**
     * 创建协作会话
     * @param sessionId 会话ID
     * @param initialContent 初始内容
     * @param creatorId 创建者ID
     * @param creatorName 创建者名称
     * @return 协作会话
     */
    public CollaborationSession createSession(String sessionId, String initialContent, Long creatorId, String creatorName) {
        CollaborationSession session = new CollaborationSession(sessionId, initialContent, creatorId, creatorName);
        sessions.put(sessionId, session);
        log.info("创建协作会话: {}", sessionId);
        return session;
    }

    /**
     * 获取协作会话
     * @param sessionId 会话ID
     * @return 协作会话
     */
    public CollaborationSession getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    /**
     * 加入协作会话
     * @param sessionId 会话ID
     * @param userId 用户ID
     * @param userName 用户名称
     * @return 是否成功加入
     */
    public boolean joinSession(String sessionId, Long userId, String userName) {
        CollaborationSession session = sessions.get(sessionId);
        if (session != null) {
            session.addUser(userId, userName);
            log.info("用户{}加入协作会话: {}", userId, sessionId);
            return true;
        }
        log.warn("协作会话不存在: {}", sessionId);
        return false;
    }

    /**
     * 离开协作会话
     * @param sessionId 会话ID
     * @param userId 用户ID
     * @return 是否成功离开
     */
    public boolean leaveSession(String sessionId, Long userId) {
        CollaborationSession session = sessions.get(sessionId);
        if (session != null) {
            session.removeUser(userId);
            log.info("用户{}离开协作会话: {}", userId, sessionId);
            return true;
        }
        log.warn("协作会话不存在: {}", sessionId);
        return false;
    }

    /**
     * 处理操作
     * @param sessionId 会话ID
     * @param operation 操作
     * @return 处理后的操作
     */
    public Operation processOperation(String sessionId, Operation operation) {
        CollaborationSession session = sessions.get(sessionId);
        if (session == null) {
            log.warn("协作会话不存在: {}", sessionId);
            return null;
        }

        synchronized (session) {
            // 更新操作版本号
            operation.setVersion(session.getVersion() + 1);

            // 处理并发操作
            List<Operation> pendingOperations = session.getPendingOperations();
            if (!pendingOperations.isEmpty()) {
                for (Operation pendingOp : pendingOperations) {
                    List<Operation> transformedOps = OTAlgorithm.transform(operation, pendingOp);
                    if (transformedOps.size() >= 2) {
                        operation = transformedOps.get(0);
                    }
                }
            }

            // 应用操作到当前内容
            String newContent = OTAlgorithm.applyOperation(session.getContent(), operation);
            session.setContent(newContent);
            session.setVersion(operation.getVersion());

            // 记录已处理的操作
            session.addProcessedOperation(operation);

            log.debug("处理操作: sessionId={}, operation={}, newContent={}", sessionId, operation, newContent);
            return operation;
        }
    }

    /**
     * 广播操作
     * @param sessionId 会话ID
     * @param operation 操作
     * @param excludeUserId 排除的用户ID
     */
    public void broadcastOperation(String sessionId, Operation operation, Long excludeUserId) {
        CollaborationSession session = sessions.get(sessionId);
        if (session == null) {
            log.warn("协作会话不存在: {}", sessionId);
            return;
        }

        // 这里可以通过WebSocket向其他用户广播操作
        // 暂时留空，实际实现时需要集成WebSocket
        log.info("广播操作: sessionId={}, operation={}, excludeUserId={}", sessionId, operation, excludeUserId);
    }

    /**
     * 关闭协作会话
     * @param sessionId 会话ID
     * @return 是否成功关闭
     */
    public boolean closeSession(String sessionId) {
        CollaborationSession session = sessions.remove(sessionId);
        if (session != null) {
            log.info("关闭协作会话: {}", sessionId);
            return true;
        }
        log.warn("协作会话不存在: {}", sessionId);
        return false;
    }

    /**
     * 获取所有协作会话
     * @return 协作会话列表
     */
    public List<CollaborationSession> getAllSessions() {
        return new ArrayList<>(sessions.values());
    }

    /**
     * 协作会话类
     */
    public static class CollaborationSession {

        // 会话ID
        private final String sessionId;

        // 当前内容
        private String content;

        // 版本号
        private int version;

        // 已处理的操作列表
        private final List<Operation> processedOperations = new CopyOnWriteArrayList<>();

        // 待处理的操作列表
        private final List<Operation> pendingOperations = new CopyOnWriteArrayList<>();

        // 在线用户列表，key: 用户ID, value: 用户名称
        private final Map<Long, String> onlineUsers = new ConcurrentHashMap<>();

        // 创建时间
        private final long createTime;

        // 创建者ID
        private final Long creatorId;

        // 创建者名称
        private final String creatorName;

        /**
         * 构造函数
         * @param sessionId 会话ID
         * @param initialContent 初始内容
         * @param creatorId 创建者ID
         * @param creatorName 创建者名称
         */
        public CollaborationSession(String sessionId, String initialContent, Long creatorId, String creatorName) {
            this.sessionId = sessionId;
            this.content = initialContent;
            this.version = 0;
            this.creatorId = creatorId;
            this.creatorName = creatorName;
            this.createTime = System.currentTimeMillis();
            // 添加创建者到在线用户列表
            this.onlineUsers.put(creatorId, creatorName);
        }

        /**
         * 添加用户
         * @param userId 用户ID
         * @param userName 用户名称
         */
        public void addUser(Long userId, String userName) {
            onlineUsers.put(userId, userName);
        }

        /**
         * 移除用户
         * @param userId 用户ID
         */
        public void removeUser(Long userId) {
            onlineUsers.remove(userId);
        }

        /**
         * 添加已处理的操作
         * @param operation 操作
         */
        public void addProcessedOperation(Operation operation) {
            processedOperations.add(operation);
        }

        /**
         * 添加待处理的操作
         * @param operation 操作
         */
        public void addPendingOperation(Operation operation) {
            pendingOperations.add(operation);
        }

        /**
         * 清空待处理的操作
         */
        public void clearPendingOperations() {
            pendingOperations.clear();
        }

        // Getters and Setters

        public String getSessionId() {
            return sessionId;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public List<Operation> getProcessedOperations() {
            return processedOperations;
        }

        public List<Operation> getPendingOperations() {
            return pendingOperations;
        }

        public Map<Long, String> getOnlineUsers() {
            return onlineUsers;
        }

        public long getCreateTime() {
            return createTime;
        }

        public Long getCreatorId() {
            return creatorId;
        }

        public String getCreatorName() {
            return creatorName;
        }

        @Override
        public String toString() {
            return "CollaborationSession{" +
                    "sessionId='" + sessionId + '\'' +
                    ", content='" + content + '\'' +
                    ", version=" + version +
                    ", onlineUsers=" + onlineUsers +
                    ", createTime=" + createTime +
                    ", creatorId=" + creatorId +
                    ", creatorName='" + creatorName + '\'' +
                    '}';
        }
    }
}
