package com.anti.fraud.modules.collaboration.mapper;

import com.anti.fraud.modules.collaboration.entity.CollaborationMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 协作演练消息Mapper
 */
@Mapper
public interface CollaborationMessageMapper extends BaseMapper<CollaborationMessage> {

    /**
     * 根据会话ID查询消息
     * @param sessionId 会话ID
     * @param page 页码
     * @param size 每页大小
     * @return 消息列表
     */
    List<CollaborationMessage> selectBySessionId(@Param("sessionId") Long sessionId, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据会话ID查询消息总数
     * @param sessionId 会话ID
     * @return 消息总数
     */
    Integer selectCountBySessionId(@Param("sessionId") Long sessionId);

    /**
     * 根据会话ID和消息类型查询消息
     * @param sessionId 会话ID
     * @param messageType 消息类型
     * @param page 页码
     * @param size 每页大小
     * @return 消息列表
     */
    List<CollaborationMessage> selectBySessionIdAndType(@Param("sessionId") Long sessionId, @Param("messageType") Integer messageType, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 按会话ID统计消息类型
     * @param sessionId 会话ID
     * @return 消息类型统计
     */
    List<Map<String, Object>> selectMessageTypeStatsBySessionId(@Param("sessionId") Long sessionId);
}
