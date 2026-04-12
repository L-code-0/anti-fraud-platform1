package com.anti.fraud.modules.exam.service;

import com.anti.fraud.modules.exam.entity.TestReport;
import java.util.List;
import java.util.Map;

public interface TestReportService {

    /**
     * 创建测试报告
     * @param testReport 测试报告信息
     * @return 是否成功
     */
    boolean createTestReport(TestReport testReport);

    /**
     * 更新测试报告
     * @param testReport 测试报告信息
     * @return 是否成功
     */
    boolean updateTestReport(TestReport testReport);

    /**
     * 获取测试报告详情
     * @param id 报告ID
     * @return 测试报告详情
     */
    TestReport getTestReportById(Long id);

    /**
     * 获取用户测试报告列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 测试报告列表
     */
    List<TestReport> getUserTestReports(Long userId, int page, int size);

    /**
     * 生成详细测试报告
     * @param userId 用户ID
     * @param examId 考试ID
     * @param answers 答题结果
     * @return 测试报告
     */
    TestReport generateDetailedReport(Long userId, Long examId, Map<String, Object> answers);

    /**
     * 分析测试结果
     * @param testReport 测试报告
     * @return 分析结果
     */
    Map<String, Object> analyzeTestResult(TestReport testReport);

    /**
     * 生成学习建议
     * @param testReport 测试报告
     * @return 学习建议
     */
    List<String> generateLearningRecommendations(TestReport testReport);

    /**
     * 获取测试统计信息
     * @param userId 用户ID
     * @return 统计信息
     */
    Map<String, Object> getTestStats(Long userId);

    /**
     * 导出测试报告
     * @param id 报告ID
     * @return 导出数据
     */
    Map<String, Object> exportTestReport(Long id);

    /**
     * 比较测试报告
     * @param reportId1 报告1 ID
     * @param reportId2 报告2 ID
     * @return 比较结果
     */
    Map<String, Object> compareTestReports(Long reportId1, Long reportId2);
}
