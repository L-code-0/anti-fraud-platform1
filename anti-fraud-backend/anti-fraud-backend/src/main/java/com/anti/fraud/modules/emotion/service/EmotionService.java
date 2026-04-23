package com.anti.fraud.modules.emotion.service;

import com.anti.fraud.modules.emotion.entity.EmotionAnalysis;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 情感分析服务接口
 */
public interface EmotionService extends IService<EmotionAnalysis> {

    /**
     * 创建情感分析
     * @param emotionAnalysis 情感分析信息
     * @return 分析ID
     */
    String createEmotionAnalysis(EmotionAnalysis emotionAnalysis);

    /**
     * 更新情感分析
     * @param emotionAnalysis 情感分析信息
     * @return 是否成功
     */
    boolean updateEmotionAnalysis(EmotionAnalysis emotionAnalysis);

    /**
     * 删除情感分析
     * @param analysisId 分析ID
     * @return 是否成功
     */
    boolean deleteEmotionAnalysis(String analysisId);

    /**
     * 获取情感分析详情
     * @param analysisId 分析ID
     * @return 情感分析详情
     */
    EmotionAnalysis getEmotionAnalysisByAnalysisId(String analysisId);

    /**
     * 根据消息ID获取情感分析
     * @param messageId 消息ID
     * @return 情感分析
     */
    EmotionAnalysis getEmotionAnalysisByMessageId(String messageId);

    /**
     * 分页查询情感分析列表
     * @param emotionType 情感类型
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @return 情感分析列表和总数
     */
    Map<String, Object> getEmotionAnalysisList(Integer emotionType, Integer status, int page, int size);

    /**
     * 根据用户ID查询情感分析列表
     * @param userId 用户ID
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @return 情感分析列表和总数
     */
    Map<String, Object> getEmotionAnalysisListByUserId(Long userId, Integer status, int page, int size);

    /**
     * 根据会话ID查询情感分析列表
     * @param sessionId 会话ID
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @return 情感分析列表和总数
     */
    Map<String, Object> getEmotionAnalysisListBySessionId(String sessionId, Integer status, int page, int size);

    /**
     * 更新情感分析状态
     * @param analysisId 分析ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateEmotionAnalysisStatus(String analysisId, Integer status);

    /**
     * 更新情感分析结果
     * @param analysisId 分析ID
     * @param emotionType 情感类型
     * @param emotionScore 情感得分
     * @param emotionTags 情绪标签
     * @param analysisResult 分析结果
     * @return 是否成功
     */
    boolean updateEmotionAnalysisResult(String analysisId, Integer emotionType, Double emotionScore, String emotionTags, String analysisResult);

    /**
     * 统计情感分析数量
     * @param emotionType 情感类型
     * @param status 状态
     * @return 情感分析数量
     */
    Integer countEmotionAnalysis(Integer emotionType, Integer status);

    /**
     * 统计用户情感分析数量
     * @param userId 用户ID
     * @param status 状态
     * @return 情感分析数量
     */
    Integer countEmotionAnalysisByUserId(Long userId, Integer status);

    /**
     * 统计会话情感分析数量
     * @param sessionId 会话ID
     * @param status 状态
     * @return 情感分析数量
     */
    Integer countEmotionAnalysisBySessionId(String sessionId, Integer status);

    /**
     * 计算平均情感得分
     * @param emotionType 情感类型
     * @param status 状态
     * @return 平均情感得分
     */
    Double calculateAverageEmotionScore(Integer emotionType, Integer status);

    /**
     * 获取最近的情感分析
     * @param limit 数量限制
     * @param status 状态
     * @return 情感分析列表
     */
    List<EmotionAnalysis> getRecentEmotionAnalysis(int limit, Integer status);

    /**
     * 批量创建情感分析
     * @param analyses 情感分析列表
     * @return 成功创建的数量
     */
    int batchCreateEmotionAnalysis(List<EmotionAnalysis> analyses);

    /**
     * 获取情感分析统计信息
     * @return 统计信息
     */
    Map<String, Object> getEmotionAnalysisStatistics();

    /**
     * 分析文本情感
     * @param text 文本
     * @return 情感分析结果
     */
    EmotionAnalysis analyzeTextEmotion(String text);

    /**
     * 根据情感分析结果调整回答策略
     * @param emotionAnalysis 情感分析结果
     * @return 回答策略
     */
    Map<String, Object> adjustResponseStrategy(EmotionAnalysis emotionAnalysis);
}
