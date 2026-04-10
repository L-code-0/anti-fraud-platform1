package com.anti.fraud.modules.analysis.service;

import com.anti.fraud.modules.analysis.vo.LearningReportVO;
import com.anti.fraud.modules.analysis.vo.WeaknessVO;

import java.util.List;
import java.util.Map;

public interface AnalysisService {
    /**
     * 获取学习报告
     */
    LearningReportVO getLearningReport(Long userId);

    /**
     * 获取薄弱知识点
     */
    List<WeaknessVO> getWeaknesses(Long userId);

    /**
     * 更新薄弱点分析
     */
    void updateWeaknessAnalysis(Long userId, String category, boolean isCorrect);

    /**
     * 获取学习推荐
     */
    List<?> getRecommendations(Long userId);

    /**
     * 保存学习报告到数据库
     * @param userId 用户ID
     * @param report 学习报告
     */
    void saveLearningReport(Long userId, LearningReportVO report);

    /**
     * 获取分类掌握情况
     * @param userId 用户ID
     * @return 分类掌握情况列表
     */
    List<Map<String, Object>> getCategoryMastery(Long userId);
}
