package com.anti.fraud.modules.behavior.service;

import com.anti.fraud.modules.behavior.entity.BehaviorAnalysis;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 行为分析服务接口
 */
public interface BehaviorAnalysisService extends IService<BehaviorAnalysis> {

    /**
     * 创建行为分析
     * @param behaviorAnalysis 行为分析信息
     * @return 分析ID
     */
    String createBehaviorAnalysis(BehaviorAnalysis behaviorAnalysis);

    /**
     * 更新行为分析
     * @param behaviorAnalysis 行为分析信息
     * @return 是否成功
     */
    boolean updateBehaviorAnalysis(BehaviorAnalysis behaviorAnalysis);

    /**
     * 删除行为分析
     * @param analysisId 分析ID
     * @return 是否成功
     */
    boolean deleteBehaviorAnalysis(String analysisId);

    /**
     * 获取行为分析详情
     * @param analysisId 分析ID
     * @return 行为分析详情
     */
    BehaviorAnalysis getBehaviorAnalysisByAnalysisId(String analysisId);

    /**
     * 分页查询行为分析列表
     * @param behaviorType 行为类型
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @return 行为分析列表和总数
     */
    Map<String, Object> getBehaviorAnalysisList(Integer behaviorType, Integer status, int page, int size);

    /**
     * 根据用户ID查询行为分析列表
     * @param userId 用户ID
     * @param behaviorType 行为类型
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @return 行为分析列表和总数
     */
    Map<String, Object> getBehaviorAnalysisListByUserId(Long userId, Integer behaviorType, Integer status, int page, int size);

    /**
     * 根据时间范围查询行为分析列表
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param behaviorType 行为类型
     * @param status 状态
     * @return 行为分析列表
     */
    List<BehaviorAnalysis> getBehaviorAnalysisListByTimeRange(LocalDateTime startTime, LocalDateTime endTime, Integer behaviorType, Integer status);

    /**
     * 更新分析状态
     * @param analysisId 分析ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateBehaviorAnalysisStatus(String analysisId, Integer status);

    /**
     * 更新分析结果
     * @param analysisId 分析ID
     * @param analysisResult 分析结果
     * @param feedbackContent 反馈内容
     * @return 是否成功
     */
    boolean updateBehaviorAnalysisResult(String analysisId, String analysisResult, String feedbackContent);

    /**
     * 统计行为分析数量
     * @param behaviorType 行为类型
     * @param status 状态
     * @return 行为分析数量
     */
    Integer countBehaviorAnalysis(Integer behaviorType, Integer status);

    /**
     * 统计用户行为分析数量
     * @param userId 用户ID
     * @param behaviorType 行为类型
     * @param status 状态
     * @return 行为分析数量
     */
    Integer countBehaviorAnalysisByUserId(Long userId, Integer behaviorType, Integer status);

    /**
     * 获取最近的行为分析
     * @param limit 数量限制
     * @param behaviorType 行为类型
     * @param status 状态
     * @return 行为分析列表
     */
    List<BehaviorAnalysis> getRecentBehaviorAnalysis(int limit, Integer behaviorType, Integer status);

    /**
     * 批量创建行为分析
     * @param analyses 行为分析列表
     * @return 成功创建的数量
     */
    int batchCreateBehaviorAnalysis(List<BehaviorAnalysis> analyses);

    /**
     * 获取行为分析统计信息
     * @return 统计信息
     */
    Map<String, Object> getBehaviorAnalysisStatistics();

    /**
     * 分析用户行为
     * @param analysisId 分析ID
     * @return 是否成功
     */
    boolean analyzeUserBehavior(String analysisId);

    /**
     * 记录用户浏览行为
     * @param userId 用户ID
     * @param username 用户名
     * @param content 浏览内容
     * @param duration 停留时间（秒）
     * @return 分析ID
     */
    String recordBrowseBehavior(Long userId, String username, String content, Integer duration);

    /**
     * 记录用户点击行为
     * @param userId 用户ID
     * @param username 用户名
     * @param content 点击内容
     * @param position 点击位置
     * @return 分析ID
     */
    String recordClickBehavior(Long userId, String username, String content, String position);

    /**
     * 记录用户停留行为
     * @param userId 用户ID
     * @param username 用户名
     * @param content 停留内容
     * @param duration 停留时间（秒）
     * @return 分析ID
     */
    String recordStayBehavior(Long userId, String username, String content, Integer duration);

    /**
     * 记录用户搜索行为
     * @param userId 用户ID
     * @param username 用户名
     * @param keyword 搜索关键词
     * @param resultCount 搜索结果数量
     * @return 分析ID
     */
    String recordSearchBehavior(Long userId, String username, String keyword, Integer resultCount);

    /**
     * 记录用户答题行为
     * @param userId 用户ID
     * @param username 用户名
     * @param questionId 题目ID
     * @param answer 用户答案
     * @param correctAnswer 正确答案
     * @param isCorrect 是否正确
     * @param timeUsed 用时（秒）
     * @return 分析ID
     */
    String recordAnswerBehavior(Long userId, String username, String questionId, String answer, String correctAnswer, Boolean isCorrect, Integer timeUsed);

    /**
     * 记录用户演练行为
     * @param userId 用户ID
     * @param username 用户名
     * @param exerciseId 演练ID
     * @param action 演练动作
     * @param result 演练结果
     * @return 分析ID
     */
    String recordExerciseBehavior(Long userId, String username, String exerciseId, String action, String result);
}
