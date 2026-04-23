package com.anti.fraud.modules.emotion.mapper;

import com.anti.fraud.modules.emotion.entity.EmotionAnalysis;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 情感分析Mapper
 */
@Mapper
public interface EmotionAnalysisMapper extends BaseMapper<EmotionAnalysis> {

    /**
     * 根据分析ID查询分析
     * @param analysisId 分析ID
     * @return 分析
     */
    EmotionAnalysis selectByAnalysisId(@Param("analysisId") String analysisId);

    /**
     * 根据消息ID查询分析
     * @param messageId 消息ID
     * @return 分析
     */
    EmotionAnalysis selectByMessageId(@Param("messageId") String messageId);

    /**
     * 根据会话ID查询分析列表
     * @param sessionId 会话ID
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @return 分析列表
     */
    List<EmotionAnalysis> selectBySessionId(@Param("sessionId") String sessionId, @Param("status") Integer status, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据用户ID查询分析列表
     * @param userId 用户ID
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @return 分析列表
     */
    List<EmotionAnalysis> selectByUserId(@Param("userId") Long userId, @Param("status") Integer status, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据情感类型查询分析列表
     * @param emotionType 情感类型
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @return 分析列表
     */
    List<EmotionAnalysis> selectByEmotionType(@Param("emotionType") Integer emotionType, @Param("status") Integer status, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 更新分析状态
     * @param id 分析ID
     * @param status 状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 更新分析结果
     * @param id 分析ID
     * @param emotionType 情感类型
     * @param emotionScore 情感得分
     * @param emotionTags 情绪标签
     * @param analysisResult 分析结果
     * @param analysisTime 分析时间
     * @return 影响行数
     */
    int updateAnalysisResult(@Param("id") Long id, @Param("emotionType") Integer emotionType, @Param("emotionScore") Double emotionScore, @Param("emotionTags") String emotionTags, @Param("analysisResult") String analysisResult, @Param("analysisTime") LocalDateTime analysisTime);

    /**
     * 统计分析数量
     * @param emotionType 情感类型
     * @param status 状态
     * @return 分析数量
     */
    Integer countByEmotionType(@Param("emotionType") Integer emotionType, @Param("status") Integer status);

    /**
     * 统计用户分析数量
     * @param userId 用户ID
     * @param status 状态
     * @return 分析数量
     */
    Integer countByUserId(@Param("userId") Long userId, @Param("status") Integer status);

    /**
     * 统计会话分析数量
     * @param sessionId 会话ID
     * @param status 状态
     * @return 分析数量
     */
    Integer countBySessionId(@Param("sessionId") String sessionId, @Param("status") Integer status);

    /**
     * 计算平均情感得分
     * @param emotionType 情感类型
     * @param status 状态
     * @return 平均情感得分
     */
    Double calculateAverageEmotionScore(@Param("emotionType") Integer emotionType, @Param("status") Integer status);

    /**
     * 获取最近的分析
     * @param limit 数量限制
     * @param status 状态
     * @return 分析列表
     */
    List<EmotionAnalysis> selectRecentAnalysis(@Param("limit") Integer limit, @Param("status") Integer status);

    /**
     * 批量插入分析
     * @param analyses 分析列表
     * @return 插入成功数量
     */
    int batchInsert(@Param("analyses") List<EmotionAnalysis> analyses);
}
