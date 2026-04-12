package com.anti.fraud.modules.exam.service.impl;

import com.anti.fraud.modules.exam.entity.UserAbility;
import com.anti.fraud.modules.exam.mapper.UserAbilityMapper;
import com.anti.fraud.modules.exam.service.AdaptiveTestService;
import com.anti.fraud.modules.exam.service.QuestionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdaptiveTestServiceImpl implements AdaptiveTestService {

    private final UserAbilityMapper userAbilityMapper;
    private final QuestionService questionService;

    @Override
    public UserAbility getUserAbility(Long userId) {
        try {
            LambdaQueryWrapper<UserAbility> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserAbility::getUserId, userId);
            UserAbility userAbility = userAbilityMapper.selectOne(queryWrapper);
            if (userAbility == null) {
                // 初始化用户能力评估
                userAbility = initializeUserAbility(userId);
                userAbilityMapper.insert(userAbility);
            }
            return userAbility;
        } catch (Exception e) {
            log.error("获取用户能力评估失败: {}", e.getMessage(), e);
            return initializeUserAbility(userId);
        }
    }

    @Override
    public boolean updateUserAbility(UserAbility userAbility) {
        try {
            userAbilityMapper.updateById(userAbility);
            log.info("更新用户能力评估成功: userId={}", userAbility.getUserId());
            return true;
        } catch (Exception e) {
            log.error("更新用户能力评估失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public UserAbility evaluateUserAbility(Long userId, Map<String, Object> testResult) {
        try {
            UserAbility userAbility = getUserAbility(userId);
            
            // 解析测试结果
            int score = (int) testResult.getOrDefault("score", 0);
            String category = (String) testResult.getOrDefault("category", "");
            int correctCount = (int) testResult.getOrDefault("correctCount", 0);
            int totalCount = (int) testResult.getOrDefault("totalCount", 1);
            
            // 更新总体能力水平
            int testCount = userAbility.getTestCount() + 1;
            int totalScore = userAbility.getTotalScore() + score;
            double averageScore = (double) totalScore / testCount;
            int overallLevel = calculateLevel(averageScore);
            
            userAbility.setOverallLevel(overallLevel);
            userAbility.setTotalScore(totalScore);
            userAbility.setTestCount(testCount);
            userAbility.setAverageScore(averageScore);
            userAbility.setLastTestTime(LocalDateTime.now());
            
            // 更新分类能力水平
            if ("电信诈骗".equals(category)) {
                int telecomLevel = calculateLevel((double) correctCount / totalCount * 100);
                userAbility.setTelecomLevel(telecomLevel);
            } else if ("网络诈骗".equals(category)) {
                int onlineLevel = calculateLevel((double) correctCount / totalCount * 100);
                userAbility.setOnlineLevel(onlineLevel);
            } else if ("金融诈骗".equals(category)) {
                int financialLevel = calculateLevel((double) correctCount / totalCount * 100);
                userAbility.setFinancialLevel(financialLevel);
            } else {
                int generalLevel = calculateLevel((double) correctCount / totalCount * 100);
                userAbility.setGeneralLevel(generalLevel);
            }
            
            userAbilityMapper.updateById(userAbility);
            log.info("评估用户能力成功: userId={}, overallLevel={}", userId, overallLevel);
            return userAbility;
        } catch (Exception e) {
            log.error("评估用户能力失败: {}", e.getMessage(), e);
            return getUserAbility(userId);
        }
    }

    @Override
    public List<Map<String, Object>> generateAdaptiveQuestions(Long userId, String category, int count) {
        try {
            UserAbility userAbility = getUserAbility(userId);
            List<Map<String, Object>> questions = new ArrayList<>();
            
            // 根据用户能力水平选择题目
            int userLevel = getUserLevelByCategory(userAbility, category);
            
            // 生成不同难度的题目
            int easyCount = count / 3;
            int mediumCount = count / 3;
            int hardCount = count - easyCount - mediumCount;
            
            // 简单题目
            for (int i = 0; i < easyCount; i++) {
                Map<String, Object> question = generateQuestionByDifficulty(category, "简单", userLevel);
                if (question != null) {
                    questions.add(question);
                }
            }
            
            // 中等题目
            for (int i = 0; i < mediumCount; i++) {
                Map<String, Object> question = generateQuestionByDifficulty(category, "中等", userLevel);
                if (question != null) {
                    questions.add(question);
                }
            }
            
            // 困难题目
            for (int i = 0; i < hardCount; i++) {
                Map<String, Object> question = generateQuestionByDifficulty(category, "困难", userLevel);
                if (question != null) {
                    questions.add(question);
                }
            }
            
            // 打乱题目顺序
            shuffleList(questions);
            
            log.info("生成自适应测试题目成功: userId={}, category={}, count={}", userId, category, count);
            return questions;
        } catch (Exception e) {
            log.error("生成自适应测试题目失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public double calculateQuestionDifficulty(Long userId, Long questionId) {
        try {
            UserAbility userAbility = getUserAbility(userId);
            // 简单的难度计算逻辑
            // 实际项目中应该基于用户历史答题情况和题目本身的难度进行计算
            return 0.5; // 默认中等难度
        } catch (Exception e) {
            log.error("计算题目难度失败: {}", e.getMessage(), e);
            return 0.5;
        }
    }

    @Override
    public Map<String, Object> getAbilityAnalysisReport(Long userId) {
        try {
            UserAbility userAbility = getUserAbility(userId);
            Map<String, Object> report = new HashMap<>();
            
            report.put("overallLevel", userAbility.getOverallLevel());
            report.put("overallLevelText", getLevelText(userAbility.getOverallLevel()));
            report.put("telecomLevel", userAbility.getTelecomLevel());
            report.put("telecomLevelText", getLevelText(userAbility.getTelecomLevel()));
            report.put("onlineLevel", userAbility.getOnlineLevel());
            report.put("onlineLevelText", getLevelText(userAbility.getOnlineLevel()));
            report.put("financialLevel", userAbility.getFinancialLevel());
            report.put("financialLevelText", getLevelText(userAbility.getFinancialLevel()));
            report.put("generalLevel", userAbility.getGeneralLevel());
            report.put("generalLevelText", getLevelText(userAbility.getGeneralLevel()));
            report.put("averageScore", userAbility.getAverageScore());
            report.put("testCount", userAbility.getTestCount());
            report.put("lastTestTime", userAbility.getLastTestTime());
            
            // 生成学习建议
            List<String> suggestions = generateLearningSuggestions(userAbility);
            report.put("suggestions", suggestions);
            
            log.info("获取用户能力分析报告成功: userId={}", userId);
            return report;
        } catch (Exception e) {
            log.error("获取用户能力分析报告失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    public List<Map<String, Object>> recommendLearningContent(Long userId, int count) {
        try {
            UserAbility userAbility = getUserAbility(userId);
            List<Map<String, Object>> recommendations = new ArrayList<>();
            
            // 根据用户能力水平推荐学习内容
            if (userAbility.getTelecomLevel() < 3) {
                recommendations.add(createRecommendation("电信诈骗防范指南", "telecom", "学习电信诈骗的常见手法和防范措施"));
            }
            if (userAbility.getOnlineLevel() < 3) {
                recommendations.add(createRecommendation("网络诈骗防范指南", "online", "学习网络诈骗的常见手法和防范措施"));
            }
            if (userAbility.getFinancialLevel() < 3) {
                recommendations.add(createRecommendation("金融诈骗防范指南", "financial", "学习金融诈骗的常见手法和防范措施"));
            }
            if (userAbility.getGeneralLevel() < 3) {
                recommendations.add(createRecommendation("诈骗防范基础知识", "general", "学习诈骗防范的基础知识和技巧"));
            }
            
            // 限制推荐数量
            if (recommendations.size() > count) {
                recommendations = recommendations.subList(0, count);
            }
            
            log.info("推荐学习内容成功: userId={}, count={}", userId, recommendations.size());
            return recommendations;
        } catch (Exception e) {
            log.error("推荐学习内容失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public double predictUserScore(Long userId, List<Long> questions) {
        try {
            UserAbility userAbility = getUserAbility(userId);
            // 简单的分数预测逻辑
            // 实际项目中应该基于用户历史答题情况和题目难度进行预测
            double baseScore = userAbility.getAverageScore();
            return baseScore;
        } catch (Exception e) {
            log.error("预测用户分数失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    /**
     * 初始化用户能力评估
     */
    private UserAbility initializeUserAbility(Long userId) {
        UserAbility userAbility = new UserAbility();
        userAbility.setUserId(userId);
        userAbility.setOverallLevel(2); // 默认中等水平
        userAbility.setTelecomLevel(2);
        userAbility.setOnlineLevel(2);
        userAbility.setFinancialLevel(2);
        userAbility.setGeneralLevel(2);
        userAbility.setTotalScore(0);
        userAbility.setTestCount(0);
        userAbility.setAverageScore(0.0);
        return userAbility;
    }

    /**
     * 根据分数计算能力水平
     */
    private int calculateLevel(double score) {
        if (score >= 90) {
            return 5; // 优秀
        } else if (score >= 80) {
            return 4; // 良好
        } else if (score >= 60) {
            return 3; // 中等
        } else if (score >= 40) {
            return 2; // 较差
        } else {
            return 1; // 差
        }
    }

    /**
     * 根据分类获取用户能力水平
     */
    private int getUserLevelByCategory(UserAbility userAbility, String category) {
        switch (category) {
            case "电信诈骗":
                return userAbility.getTelecomLevel();
            case "网络诈骗":
                return userAbility.getOnlineLevel();
            case "金融诈骗":
                return userAbility.getFinancialLevel();
            default:
                return userAbility.getGeneralLevel();
        }
    }

    /**
     * 根据难度生成题目
     */
    private Map<String, Object> generateQuestionByDifficulty(String category, String difficulty, int userLevel) {
        // 模拟生成题目
        Map<String, Object> question = new HashMap<>();
        question.put("id", new Random().nextLong());
        question.put("questionText", category + "测试题目（" + difficulty + "）");
        question.put("optionA", "选项A");
        question.put("optionB", "选项B");
        question.put("optionC", "选项C");
        question.put("optionD", "选项D");
        question.put("correctAnswer", "A");
        question.put("explanation", "题目解析");
        question.put("category", category);
        question.put("difficulty", difficulty);
        question.put("score", 2);
        return question;
    }

    /**
     * 打乱列表顺序
     */
    private void shuffleList(List<?> list) {
        Random random = new Random();
        for (int i = list.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Object temp = list.get(i);
            list.set(i, list.get(j));
            list.set(j, temp);
        }
    }

    /**
     * 获取能力水平文本
     */
    private String getLevelText(int level) {
        switch (level) {
            case 5:
                return "优秀";
            case 4:
                return "良好";
            case 3:
                return "中等";
            case 2:
                return "较差";
            case 1:
                return "差";
            default:
                return "未知";
        }
    }

    /**
     * 生成学习建议
     */
    private List<String> generateLearningSuggestions(UserAbility userAbility) {
        List<String> suggestions = new ArrayList<>();
        
        if (userAbility.getTelecomLevel() < 3) {
            suggestions.add("建议加强电信诈骗防范知识的学习");
        }
        if (userAbility.getOnlineLevel() < 3) {
            suggestions.add("建议加强网络诈骗防范知识的学习");
        }
        if (userAbility.getFinancialLevel() < 3) {
            suggestions.add("建议加强金融诈骗防范知识的学习");
        }
        if (userAbility.getGeneralLevel() < 3) {
            suggestions.add("建议加强诈骗防范基础知识的学习");
        }
        if (userAbility.getOverallLevel() >= 4) {
            suggestions.add("您的防诈骗知识掌握良好，继续保持");
        }
        
        return suggestions;
    }

    /**
     * 创建推荐内容
     */
    private Map<String, Object> createRecommendation(String title, String type, String description) {
        Map<String, Object> recommendation = new HashMap<>();
        recommendation.put("title", title);
        recommendation.put("type", type);
        recommendation.put("description", description);
        return recommendation;
    }
}
