package com.anti.fraud.modules.collaboration.service;

import com.anti.fraud.modules.collaboration.entity.CollaborationSession;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 协作演练会话服务接口
 */
public interface CollaborationSessionService extends IService<CollaborationSession> {

    /**
     * 创建会话
     * @param session 会话信息
     * @return 是否成功
     */
    boolean createSession(CollaborationSession session);

    /**
     * 更新会话
     * @param session 会话信息
     * @return 是否成功
     */
    boolean updateSession(CollaborationSession session);

    /**
     * 删除会话
     * @param id 会话ID
     * @return 是否成功
     */
    boolean deleteSession(Long id);

    /**
     * 获取会话详情
     * @param id 会话ID
     * @return 会话详情
     */
    CollaborationSession getSessionById(Long id);

    /**
     * 分页查询会话
     * @param params 查询参数
     * @param page 页码
     * @param size 每页大小
     * @return 会话列表和总数
     */
    Map<String, Object> getSessionList(Map<String, Object> params, int page, int size);

    /**
     * 根据创建者ID查询会话
     * @param creatorId 创建者ID
     * @param page 页码
     * @param size 每页大小
     * @return 会话列表和总数
     */
    Map<String, Object> getSessionsByCreatorId(Long creatorId, int page, int size);

    /**
     * 根据场景ID查询会话
     * @param scenarioId 场景ID
     * @param page 页码
     * @param size 每页大小
     * @return 会话列表和总数
     */
    Map<String, Object> getSessionsByScenarioId(Long scenarioId, int page, int size);

    /**
     * 开始会话
     * @param id 会话ID
     * @return 是否成功
     */
    boolean startSession(Long id);

    /**
     * 结束会话
     * @param id 会话ID
     * @return 是否成功
     */
    boolean endSession(Long id);

    /**
     * 取消会话
     * @param id 会话ID
     * @return 是否成功
     */
    boolean cancelSession(Long id);

    /**
     * 增加当前参与人数
     * @param id 会话ID
     * @return 是否成功
     */
    boolean increaseCurrentParticipants(Long id);

    /**
     * 减少当前参与人数
     * @param id 会话ID
     * @return 是否成功
     */
    boolean decreaseCurrentParticipants(Long id);

    /**
     * 获取进行中的会话
     * @param limit 数量限制
     * @return 进行中的会话列表
     */
    List<CollaborationSession> getActiveSessions(int limit);

    /**
     * 统计会话信息
     * @return 统计信息
     */
    Map<String, Object> getSessionStats();
}
