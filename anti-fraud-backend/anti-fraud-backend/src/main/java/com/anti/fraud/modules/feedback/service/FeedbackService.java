package com.anti.fraud.modules.feedback.service;

import com.anti.fraud.modules.feedback.entity.Feedback;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 用户评价服务接口
 */
public interface FeedbackService extends IService<Feedback> {

    /**
     * 创建评价
     * @param feedback 评价信息
     * @return 评价ID
     */
    String createFeedback(Feedback feedback);

    /**
     * 更新评价
     * @param feedback 评价信息
     * @return 是否成功
     */
    boolean updateFeedback(Feedback feedback);

    /**
     * 删除评价
     * @param feedbackId 评价ID
     * @return 是否成功
     */
    boolean deleteFeedback(String feedbackId);

    /**
     * 获取评价详情
     * @param feedbackId 评价ID
     * @return 评价详情
     */
    Feedback getFeedbackByFeedbackId(String feedbackId);

    /**
     * 根据消息ID获取评价
     * @param messageId 消息ID
     * @return 评价
     */
    Feedback getFeedbackByMessageId(String messageId);

    /**
     * 分页查询评价列表
     * @param type 评价类型
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @return 评价列表和总数
     */
    Map<String, Object> getFeedbackList(Integer type, Integer status, int page, int size);

    /**
     * 根据用户ID查询评价列表
     * @param userId 用户ID
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @return 评价列表和总数
     */
    Map<String, Object> getFeedbackListByUserId(Long userId, Integer status, int page, int size);

    /**
     * 根据会话ID查询评价列表
     * @param sessionId 会话ID
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @return 评价列表和总数
     */
    Map<String, Object> getFeedbackListBySessionId(String sessionId, Integer status, int page, int size);

    /**
     * 更新评价状态
     * @param feedbackId 评价ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateFeedbackStatus(String feedbackId, Integer status);

    /**
     * 处理评价
     * @param feedbackId 评价ID
     * @param handler 处理人
     * @param handleResult 处理结果
     * @return 是否成功
     */
    boolean handleFeedback(String feedbackId, String handler, String handleResult);

    /**
     * 统计评价数量
     * @param type 评价类型
     * @param status 状态
     * @return 评价数量
     */
    Integer countFeedback(Integer type, Integer status);

    /**
     * 统计用户评价数量
     * @param userId 用户ID
     * @param status 状态
     * @return 评价数量
     */
    Integer countFeedbackByUserId(Long userId, Integer status);

    /**
     * 统计会话评价数量
     * @param sessionId 会话ID
     * @param status 状态
     * @return 评价数量
     */
    Integer countFeedbackBySessionId(String sessionId, Integer status);

    /**
     * 计算平均评分
     * @param type 评价类型
     * @param status 状态
     * @return 平均评分
     */
    Double calculateAverageScore(Integer type, Integer status);

    /**
     * 获取最近的评价
     * @param limit 数量限制
     * @param status 状态
     * @return 评价列表
     */
    List<Feedback> getRecentFeedback(int limit, Integer status);

    /**
     * 批量创建评价
     * @param feedbacks 评价列表
     * @return 成功创建的数量
     */
    int batchCreateFeedback(List<Feedback> feedbacks);

    /**
     * 获取评价统计信息
     * @return 统计信息
     */
    Map<String, Object> getFeedbackStatistics();
}
