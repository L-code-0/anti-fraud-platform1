package com.anti.fraud.modules.exam.service.impl;

import com.anti.fraud.modules.exam.entity.TestReport;
import com.anti.fraud.modules.exam.mapper.TestReportMapper;
import com.anti.fraud.modules.exam.service.TestReportService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class TestReportServiceImpl implements TestReportService {

    private final TestReportMapper testReportMapper;

    @Override
    public boolean createTestReport(TestReport testReport) {
        try {
            testReportMapper.insert(testReport);
            log.info("创建测试报告成功: id={}, userId={}, examId={}", 
                    testReport.getId(), testReport.getUserId(), testReport.getExamId());
            return true;
        } catch (Exception e) {
            log.error("创建测试报告失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean updateTestReport(TestReport testReport) {
        try {
            testReportMapper.updateById(testReport);
            log.info("更新测试报告成功: id={}", testReport.getId());
            return true;
        } catch (Exception e) {
            log.error("更新测试报告失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public TestReport getTestReportById(Long id) {
        try {
            return testReportMapper.selectById(id);
        } catch (Exception e) {
            log.error("获取测试报告详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<TestReport> getUserTestReports(Long userId, int page, int size) {
        try {
            LambdaQueryWrapper<TestReport> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TestReport::getUserId, userId)
                    .orderByDesc(TestReport::getCreateTime);

            IPage<TestReport> pageInfo = new Page<>(page, size);
            testReportMapper.selectPage(pageInfo, queryWrapper);

            return pageInfo.getRecords();
        } catch (Exception e) {
            log.error("获取用户测试报告列表失败: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public TestReport generateDetailedReport(Long userId, Long examId, Map<String, Object> answers) {
        try {
            TestReport testReport = new TestReport();
            testReport.setUserId(userId);
            testReport.setExamId(examId);
            testReport.setExamName("测试考试");
            
            // 解析答题结果
            List<Map<String, Object>> answerList = (List<Map<String, Object>>) answers.get("answers");
            int totalCount = answerList.size();
            int correctCount = 0;
            StringBuilder detailedAnswers = new StringBuilder();
            
            for (Map<String, Object> answer : answerList) {
                boolean isCorrect = (boolean) answer.getOrDefault("isCorrect", false);
                if (isCorrect) {
                    correctCount++;
                }
                detailedAnswers.append("题目: " + answer.get("questionText") + "\n")
                        .append("你的答案: " + answer.get("userAnswer") + "\n")
                        .append("正确答案: " + answer.get("correctAnswer") + "\n")
                        .append("是否正确: " + (isCorrect ? "是" : "否") + "\n")
                        .append("解析: " + answer.get("explanation") + "\n\n");
            }
            
            int score = correctCount * 100 / totalCount;
            double accuracy = (double) correctCount / totalCount * 100;
            
            testReport.setScore(score);
            testReport.setTotalScore(100);
            testReport.setCorrectCount(correctCount);
            testReport.setTotalCount(totalCount);
            testReport.setAccuracy(accuracy);
            testReport.setCategory("综合测试");
            testReport.setDifficulty("中等");
            testReport.setDetailedAnswers(detailedAnswers.toString());
            testReport.setStatus(1); // 1: 已完成
            
            // 生成分析结果
            Map<String, Object> analysis = analyzeTestResult(testReport);
            testReport.setAnalysisResult((String) analysis.get("analysisResult"));
            testReport.setWeakPoints((String) analysis.get("weakPoints"));
            
            // 生成学习建议
            List<String> recommendations = generateLearningRecommendations(testReport);
            testReport.setRecommendations(String.join("\n", recommendations));
            
            testReportMapper.insert(testReport);
            log.info("生成详细测试报告成功: id={}, userId={}, score={}", 
                    testReport.getId(), userId, score);
            return testReport;
        } catch (Exception e) {
            log.error("生成详细测试报告失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> analyzeTestResult(TestReport testReport) {
        Map<String, Object> analysis = new HashMap<>();
        try {
            int score = testReport.getScore();
            double accuracy = testReport.getAccuracy();
            
            // 生成分析结果
            String analysisResult;
            if (score >= 90) {
                analysisResult = "优秀！你的防诈骗知识掌握非常扎实，继续保持。";
            } else if (score >= 80) {
                analysisResult = "良好！你的防诈骗知识掌握较好，仍有提升空间。";
            } else if (score >= 60) {
                analysisResult = "及格！你的防诈骗知识掌握一般，需要加强学习。";
            } else {
                analysisResult = "不及格！你的防诈骗知识掌握较差，需要系统学习。";
            }
            
            // 生成薄弱环节
            List<String> weakPoints = new ArrayList<>();
            if (score < 60) {
                weakPoints.add("电信诈骗防范知识");
                weakPoints.add("网络诈骗防范知识");
                weakPoints.add("金融诈骗防范知识");
            } else if (score < 80) {
                weakPoints.add("电信诈骗防范知识");
                weakPoints.add("网络诈骗防范知识");
            } else if (score < 90) {
                weakPoints.add("金融诈骗防范知识");
            }
            
            analysis.put("analysisResult", analysisResult);
            analysis.put("weakPoints", String.join(", ", weakPoints));
            analysis.put("score", score);
            analysis.put("accuracy", accuracy);
            analysis.put("correctCount", testReport.getCorrectCount());
            analysis.put("totalCount", testReport.getTotalCount());
            
            return analysis;
        } catch (Exception e) {
            log.error("分析测试结果失败: {}", e.getMessage(), e);
            return analysis;
        }
    }

    @Override
    public List<String> generateLearningRecommendations(TestReport testReport) {
        List<String> recommendations = new ArrayList<>();
        try {
            int score = testReport.getScore();
            
            if (score < 60) {
                recommendations.add("系统学习防诈骗基础知识");
                recommendations.add("参加防诈骗培训课程");
                recommendations.add("多做防诈骗测试题");
                recommendations.add("关注最新诈骗手法");
            } else if (score < 80) {
                recommendations.add("针对性学习薄弱环节");
                recommendations.add("参加进阶防诈骗培训");
                recommendations.add("定期复习防诈骗知识");
            } else if (score < 90) {
                recommendations.add("学习最新诈骗手法");
                recommendations.add("分享防诈骗知识给他人");
                recommendations.add("参加防诈骗竞赛活动");
            } else {
                recommendations.add("成为防诈骗宣传员");
                recommendations.add("学习高级防诈骗技巧");
                recommendations.add("帮助他人提高防诈骗意识");
            }
            
            return recommendations;
        } catch (Exception e) {
            log.error("生成学习建议失败: {}", e.getMessage(), e);
            return recommendations;
        }
    }

    @Override
    public Map<String, Object> getTestStats(Long userId) {
        try {
            LambdaQueryWrapper<TestReport> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TestReport::getUserId, userId);
            List<TestReport> reports = testReportMapper.selectList(queryWrapper);
            
            int totalTests = reports.size();
            int passedTests = 0;
            int totalScore = 0;
            double averageScore = 0;
            int highestScore = 0;
            int lowestScore = 100;
            
            for (TestReport report : reports) {
                if (report.getScore() >= 60) {
                    passedTests++;
                }
                totalScore += report.getScore();
                if (report.getScore() > highestScore) {
                    highestScore = report.getScore();
                }
                if (report.getScore() < lowestScore) {
                    lowestScore = report.getScore();
                }
            }
            
            if (totalTests > 0) {
                averageScore = (double) totalScore / totalTests;
            }
            
            double passRate = totalTests > 0 ? (double) passedTests / totalTests * 100 : 0;
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalTests", totalTests);
            stats.put("passedTests", passedTests);
            stats.put("passRate", passRate);
            stats.put("averageScore", averageScore);
            stats.put("highestScore", highestScore);
            stats.put("lowestScore", lowestScore);
            
            // 按分类统计
            Map<String, Integer> categoryStats = new HashMap<>();
            for (TestReport report : reports) {
                String category = report.getCategory();
                categoryStats.put(category, categoryStats.getOrDefault(category, 0) + 1);
            }
            stats.put("categoryStats", categoryStats);
            
            log.info("获取测试统计信息成功: userId={}, totalTests={}, averageScore={}", 
                    userId, totalTests, averageScore);
            return stats;
        } catch (Exception e) {
            log.error("获取测试统计信息失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Object> exportTestReport(Long id) {
        try {
            TestReport testReport = testReportMapper.selectById(id);
            if (testReport == null) {
                return new HashMap<>();
            }
            
            Map<String, Object> exportData = new HashMap<>();
            exportData.put("reportId", testReport.getId());
            exportData.put("userId", testReport.getUserId());
            exportData.put("userName", testReport.getUserName());
            exportData.put("examName", testReport.getExamName());
            exportData.put("score", testReport.getScore());
            exportData.put("totalScore", testReport.getTotalScore());
            exportData.put("correctCount", testReport.getCorrectCount());
            exportData.put("totalCount", testReport.getTotalCount());
            exportData.put("accuracy", testReport.getAccuracy());
            exportData.put("category", testReport.getCategory());
            exportData.put("difficulty", testReport.getDifficulty());
            exportData.put("analysisResult", testReport.getAnalysisResult());
            exportData.put("weakPoints", testReport.getWeakPoints());
            exportData.put("recommendations", testReport.getRecommendations());
            exportData.put("detailedAnswers", testReport.getDetailedAnswers());
            exportData.put("createTime", testReport.getCreateTime());
            
            log.info("导出测试报告成功: id={}", id);
            return exportData;
        } catch (Exception e) {
            log.error("导出测试报告失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Object> compareTestReports(Long reportId1, Long reportId2) {
        try {
            TestReport report1 = testReportMapper.selectById(reportId1);
            TestReport report2 = testReportMapper.selectById(reportId2);
            
            if (report1 == null || report2 == null) {
                return new HashMap<>();
            }
            
            Map<String, Object> comparison = new HashMap<>();
            comparison.put("report1", report1);
            comparison.put("report2", report2);
            comparison.put("scoreDifference", report2.getScore() - report1.getScore());
            comparison.put("accuracyDifference", report2.getAccuracy() - report1.getAccuracy());
            comparison.put("correctCountDifference", report2.getCorrectCount() - report1.getCorrectCount());
            
            // 生成对比分析
            String comparisonAnalysis;
            int scoreDiff = report2.getScore() - report1.getScore();
            if (scoreDiff > 10) {
                comparisonAnalysis = "成绩显著提升，继续保持！";
            } else if (scoreDiff > 0) {
                comparisonAnalysis = "成绩有所提升，再接再厉！";
            } else if (scoreDiff > -10) {
                comparisonAnalysis = "成绩略有下降，需要加强学习。";
            } else {
                comparisonAnalysis = "成绩显著下降，需要系统学习。";
            }
            comparison.put("comparisonAnalysis", comparisonAnalysis);
            
            log.info("比较测试报告成功: reportId1={}, reportId2={}, scoreDiff={}", 
                    reportId1, reportId2, scoreDiff);
            return comparison;
        } catch (Exception e) {
            log.error("比较测试报告失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }
}
