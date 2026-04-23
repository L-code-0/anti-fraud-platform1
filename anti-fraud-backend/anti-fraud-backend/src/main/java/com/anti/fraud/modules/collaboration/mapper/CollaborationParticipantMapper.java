package com.anti.fraud.modules.collaboration.mapper;

import com.anti.fraud.modules.collaboration.entity.CollaborationParticipant;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 协作演练参与者Mapper
 */
@Mapper
public interface CollaborationParticipantMapper extends BaseMapper<CollaborationParticipant> {

    /**
     * 根据会话ID查询参与者
     * @param sessionId 会话ID
     * @param page 页码
     * @param size 每页大小
     * @return 参与者列表
     */
    List<CollaborationParticipant> selectBySessionId(@Param("sessionId") Long sessionId, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据会话ID查询参与者总数
     * @param sessionId 会话ID
     * @return 参与者总数
     */
    Integer selectCountBySessionId(@Param("sessionId") Long sessionId);

    /**
     * 根据用户ID查询参与的会话
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 参与的会话列表
     */
    List<CollaborationParticipant> selectByUserId(@Param("userId") Long userId, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据会话ID和用户ID查询参与者
     * @param sessionId 会话ID
     * @param userId 用户ID
     * @return 参与者信息
     */
    CollaborationParticipant selectBySessionIdAndUserId(@Param("sessionId") Long sessionId, @Param("userId") Long userId);

    /**
     * 按会话ID统计参与者角色
     * @param sessionId 会话ID
     * @return 角色统计
     */
    List<Map<String, Object>> selectRoleStatsBySessionId(@Param("sessionId") Long sessionId);

    /**
     * 按会话ID统计参与者状态
     * @param sessionId 会话ID
     * @return 状态统计
     */
    List<Map<String, Object>> selectStatusStatsBySessionId(@Param("sessionId") Long sessionId);
}
