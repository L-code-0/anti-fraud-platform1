package com.anti.fraud.modules.collaboration.service.impl;

import com.anti.fraud.modules.collaboration.manager.CollaborationSessionManager;
import com.anti.fraud.modules.collaboration.model.Operation;
import com.anti.fraud.modules.collaboration.service.CollaborationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 协作服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CollaborationServiceImpl implements CollaborationService {

    private final CollaborationSessionManager collaborationSessionManager;

    @Override
    public CollaborationSessionManager.CollaborationSession createSession(String sessionId, String initialContent, Long creatorId, String creatorName) {
        try {
            return collaborationSessionManager.createSession(sessionId, initialContent, creatorId, creatorName);
        } catch (Exception e) {
            log.error("创建协作会话失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public CollaborationSessionManager.CollaborationSession getSession(String sessionId) {
        try {
            return collaborationSessionManager.getSession(sessionId);
        } catch (Exception e) {
            log.error("获取协作会话失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean joinSession(String sessionId, Long userId, String userName) {
        try {
            return collaborationSessionManager.joinSession(sessionId, userId, userName);
        } catch (Exception e) {
            log.error("加入协作会话失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean leaveSession(String sessionId, Long userId) {
        try {
            return collaborationSessionManager.leaveSession(sessionId, userId);
        } catch (Exception e) {
            log.error("离开协作会话失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Operation processOperation(String sessionId, Operation operation) {
        try {
            return collaborationSessionManager.processOperation(sessionId, operation);
        } catch (Exception e) {
            log.error("处理操作失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void broadcastOperation(String sessionId, Operation operation, Long excludeUserId) {
        try {
            collaborationSessionManager.broadcastOperation(sessionId, operation, excludeUserId);
        } catch (Exception e) {
            log.error("广播操作失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public boolean closeSession(String sessionId) {
        try {
            return collaborationSessionManager.closeSession(sessionId);
        } catch (Exception e) {
            log.error("关闭协作会话失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<CollaborationSessionManager.CollaborationSession> getAllSessions() {
        try {
            return collaborationSessionManager.getAllSessions();
        } catch (Exception e) {
            log.error("获取所有协作会话失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public Map<Long, String> getOnlineUsers(String sessionId) {
        try {
            CollaborationSessionManager.CollaborationSession session = collaborationSessionManager.getSession(sessionId);
            if (session != null) {
                return session.getOnlineUsers();
            }
            return null;
        } catch (Exception e) {
            log.error("获取在线用户失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public String getSessionContent(String sessionId) {
        try {
            CollaborationSessionManager.CollaborationSession session = collaborationSessionManager.getSession(sessionId);
            if (session != null) {
                return session.getContent();
            }
            return null;
        } catch (Exception e) {
            log.error("获取会话内容失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public int getSessionVersion(String sessionId) {
        try {
            CollaborationSessionManager.CollaborationSession session = collaborationSessionManager.getSession(sessionId);
            if (session != null) {
                return session.getVersion();
            }
            return -1;
        } catch (Exception e) {
            log.error("获取会话版本号失败: {}", e.getMessage(), e);
            return -1;
        }
    }

    @Override
    public List<Operation> getOperationHistory(String sessionId, int limit) {
        try {
            CollaborationSessionManager.CollaborationSession session = collaborationSessionManager.getSession(sessionId);
            if (session != null) {
                List<Operation> operations = session.getProcessedOperations();
                if (limit > 0 && operations.size() > limit) {
                    return operations.subList(operations.size() - limit, operations.size());
                }
                return operations;
            }
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("获取操作历史失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
}
