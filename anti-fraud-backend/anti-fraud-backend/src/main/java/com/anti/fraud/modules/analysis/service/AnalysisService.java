package com.anti.fraud.modules.analysis.service;

import com.anti.fraud.modules.analysis.vo.LearningReportVO;
import com.anti.fraud.modules.analysis.vo.WeaknessVO;

import java.util.List;

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
}

