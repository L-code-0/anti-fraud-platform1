package com.anti.fraud.modules.collaboration.service;

import com.anti.fraud.modules.collaboration.entity.CollaborationMessage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 协作演练消息服务接口
 */
public interface CollaborationMessageService extends IService<CollaborationMessage> {

    /**
     * 发送消息
     * @param message 消息信息
     * @return 是否成功
     */
    boolean sendMessage(CollaborationMessage message);

    /**
     * 更新消息状态
     * @param id 消息ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateMessageStatus(Long id, Integer status);

    /**
     * 删除消息
     * @param id 消息ID
     * @return 是否成功
     */
    boolean deleteMessage(Long id);

    /**
     * 获取消息详情
     * @param id 消息ID
     * @return 消息详情
     */
    CollaborationMessage getMessageById(Long id);

    /**
     * 根据会话ID查询消息
     * @param sessionId 会话ID
     * @param page 页码
     * @param size 每页大小
     * @return 消息列表和总数
     */
    Map<String, Object> getMessagesBySessionId(Long sessionId, int page, int size);

    /**
     * 根据会话ID和消息类型查询消息
     * @param sessionId 会话ID
     * @param messageType 消息类型
     * @param page 页码
     * @param size 每页大小
     * @return 消息列表和总数
     */
    Map<String, Object> getMessagesBySessionIdAndType(Long sessionId, Integer messageType, int page, int size);

    /**
     * 发送系统通知
     * @param sessionId 会话ID
     * @param content 通知内容
     * @return 是否成功
     */
    boolean sendSystemMessage(Long sessionId, String content);

    /**
     * 统计会话消息类型
     * @param sessionId 会话ID
     * @return 消息类型统计
     */
    Map<String, Object> getMessageTypeStatsBySessionId(Long sessionId);
}
