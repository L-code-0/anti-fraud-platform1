package com.anti.fraud.modules.analysis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.anti.fraud.modules.analysis.entity.LearningWeakness;
import com.anti.fraud.modules.analysis.mapper.LearningWeaknessMapper;
import com.anti.fraud.modules.analysis.service.AnalysisService;
import com.anti.fraud.modules.analysis.vo.*;
import com.anti.fraud.modules.test.entity.TestRecord;
import com.anti.fraud.modules.test.mapper.TestRecordMapper;
import com.anti.fraud.modules.simulation.entity.SimulationRecord;
import com.anti.fraud.modules.simulation.mapper.SimulationRecordMapper;
import com.anti.fraud.modules.knowledge.entity.KnowledgeContent;
import com.anti.fraud.modules.knowledge.mapper.KnowledgeContentMapper;
import com.anti.fraud.modules.user.entity.User;
import com.anti.fraud.modules.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalysisServiceImpl implements AnalysisService {

    private final LearningWeaknessMapper weaknessMapper;
    private final TestRecordMapper testRecordMapper;
    private final SimulationRecordMapper simulationRecordMapper;
    private final KnowledgeContentMapper knowledgeMapper;
    private final UserMapper userMapper;

    @Override
    public LearningReportVO getLearningReport(Long userId) {
        LearningReportVO report = new LearningReportVO();

        // 计算学习时长
        User user = userMapper.selectById(userId);
        int totalDuration = user != null ? (user.getStudyDuration() != null ? user.getStudyDuration() : 0) : 0;
        report.setTotalStudyDuration(totalDuration);
        report.setTotalStudyDurationStr(formatDuration(totalDuration));

        // 测试统计
        List<TestRecord> testRecords = testRecordMapper.selectList(
                new LambdaQueryWrapper<TestRecord>().eq(TestRecord::getUserId, userId)
        );
        report.setTotalTestCount(testRecords.size());

        double avgScore = testRecords.stream()
                .mapToInt(r -> r.getUserScore() != null ? r.getUserScore().intValue() : 0)
                .average()
                .orElse(0.0);
        report.setAvgTestScore(Math.round(avgScore * 100.0) / 100.0);

        // 演练统计
        long simulationCount = simulationRecordMapper.selectCount(
                new LambdaQueryWrapper<SimulationRecord>().eq(SimulationRecord::getUserId, userId)
        );
        report.setSimulationCount((int) simulationCount);

        // 薄弱点分析
        report.setWeaknesses(getWeaknesses(userId));

        // 分类掌握情况
        report.setCategoryMastery(getCategoryMastery(userId));

        // 学习推荐
        report.setRecommendations(getRecommendations(userId));

        return report;
    }

    @Override
    public List<WeaknessVO> getWeaknesses(Long userId) {
        List<LearningWeakness> weaknesses = weaknessMapper.selectList(
                new LambdaQueryWrapper<LearningWeakness>()
                        .eq(LearningWeakness::getUserId, userId)
                        .lt(LearningWeakness::getCorrectRate, 0.7)
                        .orderByAsc(LearningWeakness::getCorrectRate)
        );

        return weaknesses.stream()
                .map(w -> {
                    WeaknessVO vo = new WeaknessVO();
                    vo.setCategoryName(w.getCategoryName());
                    vo.setCorrectRate(w.getCorrectRate());
                    vo.setWeaknessLevel(w.getWeaknessLevel());
                    vo.setSuggestion(getSuggestion(w.getCorrectRate(), w.getCategoryName()));
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateWeaknessAnalysis(Long userId, String category, boolean isCorrect) {
        LearningWeakness weakness = weaknessMapper.selectOne(
                new LambdaQueryWrapper<LearningWeakness>()
                        .eq(LearningWeakness::getUserId, userId)
                        .eq(LearningWeakness::getCategory, category)
        );

        if (weakness == null) {
            weakness = new LearningWeakness();
            weakness.setUserId(userId);
            weakness.setCategory(category);
            weakness.setCategoryName(getCategoryName(category));
            weakness.setTotalQuestions(1);
            weakness.setWrongQuestions(isCorrect ? 0 : 1);
            weakness.setCorrectRate(isCorrect ? 1.0 : 0.0);
            weakness.setWeaknessLevel(calculateWeaknessLevel(isCorrect ? 1.0 : 0.0));
            weakness.setUpdateTime(LocalDateTime.now());
            weaknessMapper.insert(weakness);
        } else {
            weakness.setTotalQuestions(weakness.getTotalQuestions() + 1);
            if (!isCorrect) {
                weakness.setWrongQuestions(weakness.getWrongQuestions() + 1);
            }
            weakness.setCorrectRate((double)(weakness.getTotalQuestions() - weakness.getWrongQuestions())
                    / weakness.getTotalQuestions());
            weakness.setWeaknessLevel(calculateWeaknessLevel(weakness.getCorrectRate()));
            weakness.setUpdateTime(LocalDateTime.now());
            weaknessMapper.updateById(weakness);
        }
    }

    @Override
    public List<RecommendationVO> getRecommendations(Long userId) {
        List<RecommendationVO> recommendations = new ArrayList<>();

        // 根据薄弱点推荐学习内容
        List<WeaknessVO> weaknesses = getWeaknesses(userId);

        for (WeaknessVO weakness : weaknesses) {
            // 推荐相关知识
            List<KnowledgeContent> knowledgeList = knowledgeMapper.selectList(
                    new LambdaQueryWrapper<KnowledgeContent>()
                            .eq(KnowledgeContent::getStatus, 1)
                            .eq(KnowledgeContent::getAuditStatus, 1)
                            .orderByDesc(KnowledgeContent::getViewCount)
                            .last("LIMIT 2")
            );

            for (KnowledgeContent k : knowledgeList) {
                RecommendationVO vo = new RecommendationVO();
                vo.setType("KNOWLEDGE");
                vo.setResourceId(k.getId());
                vo.setResourceName(k.getTitle());
                vo.setReason("针对" + weakness.getCategoryName() + "薄弱点推荐");
                vo.setPriority(weakness.getWeaknessLevel());
                recommendations.add(vo);
            }
        }

        // 按优先级排序
        recommendations.sort((a, b) -> b.getPriority() - a.getPriority());

        return recommendations.stream().limit(5).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getCategoryMastery(Long userId) {
        List<LearningWeakness> weaknesses = weaknessMapper.selectList(
                new LambdaQueryWrapper<LearningWeakness>()
                        .eq(LearningWeakness::getUserId, userId)
        );

        return weaknesses.stream()
                .map(w -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("categoryName", w.getCategoryName());
                    map.put("totalQuestions", w.getTotalQuestions());
                    map.put("correctQuestions", w.getTotalQuestions() - w.getWrongQuestions());
                    map.put("correctRate", w.getCorrectRate());
                    map.put("masteryLevel", getMasteryLevel(w.getCorrectRate()));
                    return map;
                })
                .collect(Collectors.toList());
    }

    private String formatDuration(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        if (hours > 0) {
            return hours + "小时" + minutes + "分钟";
        } else if (minutes > 0) {
            return minutes + "分钟";
        } else {
            return seconds + "秒";
        }
    }

    private int calculateWeaknessLevel(double correctRate) {
        if (correctRate < 0.4) return 3;
        if (correctRate < 0.6) return 2;
        return 1;
    }

    private String getMasteryLevel(double correctRate) {
        if (correctRate >= 0.9) return "精通";
        if (correctRate >= 0.7) return "熟练";
        if (correctRate >= 0.5) return "一般";
        return "薄弱";
    }

    private String getSuggestion(double correctRate, String categoryName) {
        if (correctRate < 0.4) {
            return "建议重点学习" + categoryName + "相关知识，多做练习题";
        } else if (correctRate < 0.6) {
            return "建议加强" + categoryName + "知识的学习和练习";
        } else {
            return "建议继续巩固" + categoryName + "知识";
        }
    }

    private String getCategoryName(String category) {
        Map<String, String> names = new HashMap<>();
        names.put("telecom_fraud", "电信诈骗");
        names.put("network_fraud", "网络诈骗");
        names.put("campus_loan", "校园贷");
        names.put("part_time_fraud", "兼职诈骗");
        return names.getOrDefault(category, category);
    }

    @Override
    @Transactional
    public void saveLearningReport(Long userId, LearningReportVO report) {
        // 保存薄弱点数据
        if (report.getWeaknesses() != null) {
            for (WeaknessVO weakness : report.getWeaknesses()) {
                // 查找是否已存在该分类的薄弱点记录
                LearningWeakness entity = weaknessMapper.selectOne(
                        new LambdaQueryWrapper<LearningWeakness>()
                                .eq(LearningWeakness::getUserId, userId)
                                .eq(LearningWeakness::getCategoryName, weakness.getCategoryName())
                );
                
                if (entity == null) {
                    // 新增记录
                    entity = new LearningWeakness();
                    entity.setUserId(userId);
                    entity.setCategory(weakness.getCategoryName().toLowerCase().replace(" ", "_"));
                    entity.setCategoryName(weakness.getCategoryName());
                    entity.setTotalQuestions(10); // 默认值，实际应根据真实数据计算
                    entity.setWrongQuestions((int) (10 * (1 - weakness.getCorrectRate())));
                    entity.setCorrectRate(weakness.getCorrectRate());
                    entity.setWeaknessLevel(weakness.getWeaknessLevel());
                    entity.setUpdateTime(LocalDateTime.now());
                    weaknessMapper.insert(entity);
                } else {
                    // 更新记录
                    entity.setCorrectRate(weakness.getCorrectRate());
                    entity.setWeaknessLevel(weakness.getWeaknessLevel());
                    entity.setUpdateTime(LocalDateTime.now());
                    weaknessMapper.updateById(entity);
                }
            }
        }

        // 保存分类掌握情况
        if (report.getCategoryMastery() != null) {
            for (Map<String, Object> mastery : report.getCategoryMastery()) {
                String categoryName = (String) mastery.get("categoryName");
                Integer totalQuestions = (Integer) mastery.get("totalQuestions");
                Double correctRate = (Double) mastery.get("correctRate");
                
                LearningWeakness entity = weaknessMapper.selectOne(
                        new LambdaQueryWrapper<LearningWeakness>()
                                .eq(LearningWeakness::getUserId, userId)
                                .eq(LearningWeakness::getCategoryName, categoryName)
                );
                
                if (entity != null) {
                    // 更新记录
                    entity.setTotalQuestions(totalQuestions);
                    entity.setWrongQuestions((int) (totalQuestions * (1 - correctRate)));
                    entity.setCorrectRate(correctRate);
                    entity.setWeaknessLevel(calculateWeaknessLevel(correctRate));
                    entity.setUpdateTime(LocalDateTime.now());
                    weaknessMapper.updateById(entity);
                } else {
                    // 新增记录
                    entity = new LearningWeakness();
                    entity.setUserId(userId);
                    entity.setCategory(categoryName.toLowerCase().replace(" ", "_"));
                    entity.setCategoryName(categoryName);
                    entity.setTotalQuestions(totalQuestions);
                    entity.setWrongQuestions((int) (totalQuestions * (1 - correctRate)));
                    entity.setCorrectRate(correctRate);
                    entity.setWeaknessLevel(calculateWeaknessLevel(correctRate));
                    entity.setUpdateTime(LocalDateTime.now());
                    weaknessMapper.insert(entity);
                }
            }
        }
    }

}