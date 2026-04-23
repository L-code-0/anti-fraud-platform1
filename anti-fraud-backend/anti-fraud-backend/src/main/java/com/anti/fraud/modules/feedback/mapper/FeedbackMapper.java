package com.anti.fraud.modules.feedback.mapper;

import com.anti.fraud.modules.feedback.entity.Feedback;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 用户评价Mapper
 */
@Mapper
public interface FeedbackMapper extends BaseMapper<Feedback> {

    /**
     * 根据评价ID查询评价
     * @param feedbackId 评价ID
     * @return 评价
     */
    Feedback selectByFeedbackId(@Param("feedbackId") String feedbackId);

    /**
     * 根据消息ID查询评价
     * @param messageId 消息ID
     * @return 评价
     */
    Feedback selectByMessageId(@Param("messageId") String messageId);

    /**
     * 根据会话ID查询评价列表
     * @param sessionId 会话ID
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @return 评价列表
     */
    List<Feedback> selectBySessionId(@Param("sessionId") String sessionId, @Param("status") Integer status, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据用户ID查询评价列表
     * @param userId 用户ID
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @return 评价列表
     */
    List<Feedback> selectByUserId(@Param("userId") Long userId, @Param("status") Integer status, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据评价类型查询评价列表
     * @param type 评价类型
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @return 评价列表
     */
    List<Feedback> selectByType(@Param("type") Integer type, @Param("status") Integer status, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 更新评价状态
     * @param id 评价ID
     * @param status 状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 更新评价处理信息
     * @param id 评价ID
     * @param handler 处理人
     * @param handleTime 处理时间
     * @param handleResult 处理结果
     * @return 影响行数
     */
    int updateHandleInfo(@Param("id") Long id, @Param("handler") String handler, @Param("handleTime") LocalDateTime handleTime, @Param("handleResult") String handleResult);

    /**
     * 统计评价数量
     * @param type 评价类型
     * @param status 状态
     * @return 评价数量
     */
    Integer countByType(@Param("type") Integer type, @Param("status") Integer status);

    /**
     * 统计用户评价数量
     * @param userId 用户ID
     * @param status 状态
     * @return 评价数量
     */
    Integer countByUserId(@Param("userId") Long userId, @Param("status") Integer status);

    /**
     * 统计会话评价数量
     * @param sessionId 会话ID
     * @param status 状态
     * @return 评价数量
     */
    Integer countBySessionId(@Param("sessionId") String sessionId, @Param("status") Integer status);

    /**
     * 计算平均评分
     * @param type 评价类型
     * @param status 状态
     * @return 平均评分
     */
    Double calculateAverageScore(@Param("type") Integer type, @Param("status") Integer status);

    /**
     * 获取最近的评价
     * @param limit 数量限制
     * @param status 状态
     * @return 评价列表
     */
    List<Feedback> selectRecentFeedback(@Param("limit") Integer limit, @Param("status") Integer status);

    /**
     * 批量插入评价
     * @param feedbacks 评价列表
     * @return 插入成功数量
     */
    int batchInsert(@Param("feedbacks") List<Feedback> feedbacks);
}
