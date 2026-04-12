package com.anti.fraud.modules.analysis.service;

import com.anti.fraud.modules.analysis.entity.DrillAnalysis;
import java.util.List;
import java.util.Map;

public interface DrillAnalysisService {

    /**
     * 创建演练分析
     * @param analysis 分析信息
     * @return 是否成功
     */
    boolean createAnalysis(DrillAnalysis analysis);

    /**
     * 更新演练分析
     * @param analysis 分析信息
     * @return 是否成功
     */
    boolean updateAnalysis(DrillAnalysis analysis);

    /**
     * 获取分析详情
     * @param id 分析ID
     * @return 分析详情
     */
    DrillAnalysis getAnalysisById(Long id);

    /**
     * 获取用户演练分析列表
     * @param userId 用户ID
     * @param drillType 演练类型
     * @param page 页码
     * @param size 每页大小
     * @return 分析列表
     */
    List<DrillAnalysis> getUserAnalysisList(Long userId, String drillType, int page, int size);

    /**
     * 分析演练数据
     * @param userId 用户ID
     * @param drillId 演练ID
     * @param drillType 演练类型
     * @param drillName 演练名称
     * @param score 分数
     * @return 分析结果
     */
    Map<String, Object> analyzeDrillData(Long userId, Long drillId, String drillType, String drillName, int score);

    /**
     * 与他人对比分析
     * @param userId 用户ID
     * @param drillType 演练类型
     * @return 对比结果
     */
    Map<String, Object> compareWithOthers(Long userId, String drillType);

    /**
     * 进步分析
     * @param userId 用户ID
     * @param drillType 演练类型
     * @param period 周期 (week, month, year)
     * @return 进步分析结果
     */
    Map<String, Object> analyzeProgress(Long userId, String drillType, String period);

    /**
     * 获取演练统计信息
     * @param userId 用户ID
     * @return 统计信息
     */
    Map<String, Object> getDrillStats(Long userId);

    /**
     * 生成分析报告
     * @param userId 用户ID
     * @param drillType 演练类型
     * @return 分析报告
     */
    Map<String, Object> generateAnalysisReport(Long userId, String drillType);

    /**
     * 获取排名信息
     * @param drillType 演练类型
     * @param limit 数量限制
     * @return 排名信息
     */
    List<Map<String, Object>> getRanking(String drillType, int limit);

    /**
     * 获取用户历史最高分
     * @param userId 用户ID
     * @param drillType 演练类型
     * @return 最高分
     */
    Integer getHighestScore(Long userId, String drillType);

    /**
     * 获取用户历史平均分
     * @param userId 用户ID
     * @param drillType 演练类型
     * @return 平均分
     */
    Double getAverageScore(Long userId, String drillType);
}
