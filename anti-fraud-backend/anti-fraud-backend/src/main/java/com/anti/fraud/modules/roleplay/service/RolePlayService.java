package com.anti.fraud.modules.roleplay.service;

import com.anti.fraud.modules.roleplay.entity.RolePlaySession;
import com.anti.fraud.modules.roleplay.entity.RolePlayScore;
import java.util.List;
import java.util.Map;

public interface RolePlayService {

    /**
     * 创建角色扮演会话
     * @param session 会话信息
     * @return 是否成功
     */
    boolean createSession(RolePlaySession session);

    /**
     * 更新角色扮演会话
     * @param session 会话信息
     * @return 是否成功
     */
    boolean updateSession(RolePlaySession session);

    /**
     * 结束角色扮演会话
     * @param sessionId 会话ID
     * @param sessionData 会话数据
     * @return 是否成功
     */
    boolean endSession(Long sessionId, String sessionData);

    /**
     * 获取会话详情
     * @param sessionId 会话ID
     * @return 会话详情
     */
    RolePlaySession getSessionById(Long sessionId);

    /**
     * 获取用户会话列表
     * @param userId 用户ID
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @return 会话列表
     */
    List<RolePlaySession> getUserSessions(Long userId, Integer status, int page, int size);

    /**
     * 获取所有会话列表
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @return 会话列表
     */
    List<RolePlaySession> getAllSessions(Integer status, int page, int size);

    /**
     * 评分角色扮演
     * @param score 评分信息
     * @return 是否成功
     */
    boolean scoreRolePlay(RolePlayScore score);

    /**
     * 获取会话评分
     * @param sessionId 会话ID
     * @return 评分列表
     */
    List<RolePlayScore> getSessionScores(Long sessionId);

    /**
     * 获取用户评分历史
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 评分历史
     */
    List<RolePlayScore> getUserScoreHistory(Long userId, int page, int size);

    /**
     * 计算会话总评分
     * @param sessionId 会话ID
     * @return 总评分
     */
    Integer calculateTotalScore(Long sessionId);

    /**
     * 生成反馈
     * @param sessionId 会话ID
     * @return 反馈信息
     */
    String generateFeedback(Long sessionId);

    /**
     * 获取角色扮演统计信息
     * @param userId 用户ID
     * @return 统计信息
     */
    Map<String, Object> getRolePlayStats(Long userId);

    /**
     * 获取推荐场景
     * @param userId 用户ID
     * @param count 数量
     * @return 推荐场景
     */
    List<Map<String, Object>> getRecommendedScenarios(Long userId, int count);
}
