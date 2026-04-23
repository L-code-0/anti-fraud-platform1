package com.anti.fraud.modules.collaboration.mapper;

import com.anti.fraud.modules.collaboration.entity.CollaborationSession;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 协作演练会话Mapper
 */
@Mapper
public interface CollaborationSessionMapper extends BaseMapper<CollaborationSession> {

    /**
     * 根据条件查询会话列表
     * @param params 查询参数
     * @param page 页码
     * @param size 每页大小
     * @return 会话列表
     */
    List<CollaborationSession> selectByCondition(@Param("params") Map<String, Object> params, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据条件查询会话总数
     * @param params 查询参数
     * @return 会话总数
     */
    Integer selectCountByCondition(@Param("params") Map<String, Object> params);

    /**
     * 根据创建者ID查询会话
     * @param creatorId 创建者ID
     * @param page 页码
     * @param size 每页大小
     * @return 会话列表
     */
    List<CollaborationSession> selectByCreatorId(@Param("creatorId") Long creatorId, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据场景ID查询会话
     * @param scenarioId 场景ID
     * @param page 页码
     * @param size 每页大小
     * @return 会话列表
     */
    List<CollaborationSession> selectByScenarioId(@Param("scenarioId") Long scenarioId, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 增加当前参与人数
     * @param id 会话ID
     */
    void increaseCurrentParticipants(@Param("id") Long id);

    /**
     * 减少当前参与人数
     * @param id 会话ID
     */
    void decreaseCurrentParticipants(@Param("id") Long id);

    /**
     * 获取进行中的会话
     * @param limit 数量限制
     * @return 进行中的会话列表
     */
    List<CollaborationSession> selectActiveSessions(@Param("limit") Integer limit);

    /**
     * 按状态统计会话数量
     * @return 状态统计
     */
    List<Map<String, Object>> selectStatusStats();
}
