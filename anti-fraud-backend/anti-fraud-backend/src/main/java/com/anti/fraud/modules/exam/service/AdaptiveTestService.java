package com.anti.fraud.modules.exam.service;

import com.anti.fraud.modules.exam.entity.UserAbility;
import java.util.List;
import java.util.Map;

public interface AdaptiveTestService {

    /**
     * 获取用户能力评估
     * @param userId 用户ID
     * @return 用户能力评估
     */
    UserAbility getUserAbility(Long userId);

    /**
     * 更新用户能力评估
     * @param userAbility 用户能力评估
     * @return 是否成功
     */
    boolean updateUserAbility(UserAbility userAbility);

    /**
     * 评估用户能力
     * @param userId 用户ID
     * @param testResult 测试结果
     * @return 评估结果
     */
    UserAbility evaluateUserAbility(Long userId, Map<String, Object> testResult);

    /**
     * 生成自适应测试题目
     * @param userId 用户ID
     * @param category 分类
     * @param count 题目数量
     * @return 题目列表
     */
    List<Map<String, Object>> generateAdaptiveQuestions(Long userId, String category, int count);

    /**
     * 计算题目难度
     * @param userId 用户ID
     * @param questionId 题目ID
     * @return 难度系数
     */
    double calculateQuestionDifficulty(Long userId, Long questionId);

    /**
     * 获取用户能力分析报告
     * @param userId 用户ID
     * @return 分析报告
     */
    Map<String, Object> getAbilityAnalysisReport(Long userId);

    /**
     * 推荐学习内容
     * @param userId 用户ID
     * @param count 推荐数量
     * @return 推荐内容列表
     */
    List<Map<String, Object>> recommendLearningContent(Long userId, int count);

    /**
     * 预测用户分数
     * @param userId 用户ID
     * @param questions 题目列表
     * @return 预测分数
     */
    double predictUserScore(Long userId, List<Long> questions);
}
