package com.anti.fraud.modules.collaboration.service;

import com.anti.fraud.modules.collaboration.entity.CollaborationParticipant;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 协作演练参与者服务接口
 */
public interface CollaborationParticipantService extends IService<CollaborationParticipant> {

    /**
     * 添加参与者
     * @param participant 参与者信息
     * @return 是否成功
     */
    boolean addParticipant(CollaborationParticipant participant);

    /**
     * 更新参与者信息
     * @param participant 参与者信息
     * @return 是否成功
     */
    boolean updateParticipant(CollaborationParticipant participant);

    /**
     * 删除参与者
     * @param id 参与者ID
     * @return 是否成功
     */
    boolean deleteParticipant(Long id);

    /**
     * 获取参与者详情
     * @param id 参与者ID
     * @return 参与者详情
     */
    CollaborationParticipant getParticipantById(Long id);

    /**
     * 根据会话ID查询参与者
     * @param sessionId 会话ID
     * @param page 页码
     * @param size 每页大小
     * @return 参与者列表和总数
     */
    Map<String, Object> getParticipantsBySessionId(Long sessionId, int page, int size);

    /**
     * 根据用户ID查询参与的会话
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 参与的会话列表和总数
     */
    Map<String, Object> getParticipantsByUserId(Long userId, int page, int size);

    /**
     * 根据会话ID和用户ID查询参与者
     * @param sessionId 会话ID
     * @param userId 用户ID
     * @return 参与者信息
     */
    CollaborationParticipant getParticipantBySessionIdAndUserId(Long sessionId, Long userId);

    /**
     * 参与者加入会话
     * @param sessionId 会话ID
     * @param userId 用户ID
     * @param username 用户名
     * @param role 角色
     * @return 是否成功
     */
    boolean joinSession(Long sessionId, Long userId, String username, Integer role);

    /**
     * 参与者离开会话
     * @param sessionId 会话ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean leaveSession(Long sessionId, Long userId);

    /**
     * 更新参与者状态
     * @param id 参与者ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateParticipantStatus(Long id, Integer status);

    /**
     * 更新参与者得分
     * @param id 参与者ID
     * @param score 得分
     * @return 是否成功
     */
    boolean updateParticipantScore(Long id, Integer score);

    /**
     * 更新参与者完成情况
     * @param id 参与者ID
     * @param completionStatus 完成情况
     * @return 是否成功
     */
    boolean updateParticipantCompletionStatus(Long id, Integer completionStatus);

    /**
     * 统计会话参与者角色
     * @param sessionId 会话ID
     * @return 角色统计
     */
    Map<String, Object> getRoleStatsBySessionId(Long sessionId);

    /**
     * 统计会话参与者状态
     * @param sessionId 会话ID
     * @return 状态统计
     */
    Map<String, Object> getStatusStatsBySessionId(Long sessionId);
}
