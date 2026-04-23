package com.anti.fraud.modules.collaboration.service;

import com.anti.fraud.modules.collaboration.manager.CollaborationSessionManager;
import com.anti.fraud.modules.collaboration.model.Operation;

import java.util.List;
import java.util.Map;

/**
 * 协作服务接口
 */
public interface CollaborationService {

    /**
     * 创建协作会话
     * @param sessionId 会话ID
     * @param initialContent 初始内容
     * @param creatorId 创建者ID
     * @param creatorName 创建者名称
     * @return 会话信息
     */
    CollaborationSessionManager.CollaborationSession createSession(String sessionId, String initialContent, Long creatorId, String creatorName);

    /**
     * 获取协作会话
     * @param sessionId 会话ID
     * @return 会话信息
     */
    CollaborationSessionManager.CollaborationSession getSession(String sessionId);

    /**
     * 加入协作会话
     * @param sessionId 会话ID
     * @param userId 用户ID
     * @param userName 用户名称
     * @return 是否成功加入
     */
    boolean joinSession(String sessionId, Long userId, String userName);

    /**
     * 离开协作会话
     * @param sessionId 会话ID
     * @param userId 用户ID
     * @return 是否成功离开
     */
    boolean leaveSession(String sessionId, Long userId);

    /**
     * 处理操作
     * @param sessionId 会话ID
     * @param operation 操作
     * @return 处理后的操作
     */
    Operation processOperation(String sessionId, Operation operation);

    /**
     * 广播操作
     * @param sessionId 会话ID
     * @param operation 操作
     * @param excludeUserId 排除的用户ID
     */
    void broadcastOperation(String sessionId, Operation operation, Long excludeUserId);

    /**
     * 关闭协作会话
     * @param sessionId 会话ID
     * @return 是否成功关闭
     */
    boolean closeSession(String sessionId);

    /**
     * 获取所有协作会话
     * @return 会话列表
     */
    List<CollaborationSessionManager.CollaborationSession> getAllSessions();

    /**
     * 获取会话的在线用户
     * @param sessionId 会话ID
     * @return 在线用户列表
     */
    Map<Long, String> getOnlineUsers(String sessionId);

    /**
     * 获取会话的内容
     * @param sessionId 会话ID
     * @return 会话内容
     */
    String getSessionContent(String sessionId);

    /**
     * 获取会话的版本号
     * @param sessionId 会话ID
     * @return 版本号
     */
    int getSessionVersion(String sessionId);

    /**
     * 获取会话的操作历史
     * @param sessionId 会话ID
     * @param limit 数量限制
     * @return 操作历史列表
     */
    List<Operation> getOperationHistory(String sessionId, int limit);
}
