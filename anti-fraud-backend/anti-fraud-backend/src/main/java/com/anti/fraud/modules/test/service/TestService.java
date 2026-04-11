package com.anti.fraud.modules.test.service;

import com.anti.fraud.modules.test.entity.UserAbility;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.Map;

public interface TestService extends IService<UserAbility> {

    /**
     * 初始化用户能力水平
     */
    UserAbility initUserAbility(Long userId);

    /**
     * 获取用户能力水平
     */
    UserAbility getUserAbility(Long userId);

    /**
     * 自适应测试组卷
     */
    Map<String, Object> adaptiveTest(Long userId, Integer questionCount);

    /**
     * 提交测试答案并评估能力
     */
    Map<String, Object> submitTest(Long userId, Long paperId, Map<String, Object> answers);

    /**
     * 生成详细测试报告
     */
    Map<String, Object> generateTestReport(Long userId, Long testId);

    /**
     * 分析用户学习薄弱点
     */
    Map<String, Object> analyzeWeaknesses(Long userId);

    /**
     * 推荐个性化学习内容
     */
    Map<String, Object> recommendLearningContent(Long userId);
}
