package com.anti.fraud.drill.service.impl;

import com.anti.fraud.drill.entity.DrillScenario;
import com.anti.fraud.drill.entity.DrillRecord;
import com.anti.fraud.drill.mapper.DrillScenarioMapper;
import com.anti.fraud.drill.mapper.DrillRecordMapper;
import com.anti.fraud.drill.service.DrillService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class DrillServiceImpl extends ServiceImpl<DrillScenarioMapper, DrillScenario> implements DrillService {

    @Autowired
    private DrillScenarioMapper drillScenarioMapper;

    @Autowired
    private DrillRecordMapper drillRecordMapper;

    @Override
    public Map<String, Object> getScenarios() {
        List<DrillScenario> scenarios = drillScenarioMapper.selectList(
            new QueryWrapper<DrillScenario>()
                .eq("status", 1)
                .eq("deleted", 0)
        );
        
        // 如果没有数据，返回默认场景
        if (scenarios.isEmpty()) {
            scenarios = getDefaultScenarios();
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("scenarios", scenarios);
        result.put("total", scenarios.size());
        
        return result;
    }

    @Override
    public DrillScenario getScenarioById(Long id) {
        return drillScenarioMapper.selectById(id);
    }

    @Override
    public Map<String, Object> submitDrill(Long userId, Long scenarioId, Map<String, Object> answers) {
        DrillScenario scenario = drillScenarioMapper.selectById(scenarioId);
        if (scenario == null) {
            throw new RuntimeException("场景不存在");
        }
        
        // 计算得分
        int score = calculateScore(answers, scenario.getCorrectAnswer());
        int correctRate = calculateCorrectRate(answers, scenario.getCorrectAnswer());
        
        // 生成反馈
        String feedback = generateFeedback(answers, scenario.getFeedback());
        
        // 保存演练记录
        DrillRecord record = new DrillRecord();
        record.setUserId(userId);
        record.setScenarioId(scenarioId);
        record.setScenarioName(scenario.getScenarioName());
        record.setScore(score);
        record.setCorrectRate(correctRate);
        record.setDuration(15); // 模拟时长
        record.setAnswers(answers.toString());
        record.setFeedback(feedback);
        record.setStatus(1);
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        drillRecordMapper.insert(record);
        
        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("score", score);
        result.put("correctRate", correctRate);
        result.put("feedback", feedback);
        result.put("recordId", record.getId());
        result.put("pass", score >= 60);
        
        return result;
    }

    @Override
    public Map<String, Object> getDrillRecords(Long userId) {
        List<DrillRecord> records = drillRecordMapper.selectList(
            new QueryWrapper<DrillRecord>()
                .eq("user_id", userId)
                .eq("deleted", 0)
                .orderByDesc("create_time")
        );
        
        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", records.size());
        
        return result;
    }

    @Override
    public Map<String, Object> getDrillStats(Long userId) {
        List<DrillRecord> records = drillRecordMapper.selectList(
            new QueryWrapper<DrillRecord>()
                .eq("user_id", userId)
                .eq("deleted", 0)
        );
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalDrills", records.size());
        
        if (!records.isEmpty()) {
            int totalScore = 0;
            int totalCorrectRate = 0;
            int passedCount = 0;
            int totalDuration = 0;
            
            for (DrillRecord record : records) {
                totalScore += record.getScore();
                totalCorrectRate += record.getCorrectRate();
                if (record.getScore() >= 60) {
                    passedCount++;
                }
                totalDuration += record.getDuration();
            }
            
            stats.put("averageScore", totalScore / records.size());
            stats.put("averageCorrectRate", totalCorrectRate / records.size());
            stats.put("passRate", (passedCount * 100) / records.size());
            stats.put("totalDuration", totalDuration);
        } else {
            stats.put("averageScore", 0);
            stats.put("averageCorrectRate", 0);
            stats.put("passRate", 0);
            stats.put("totalDuration", 0);
        }
        
        return stats;
    }

    @Override
    public Map<String, Object> generateDrillReport(Long recordId) {
        DrillRecord record = drillRecordMapper.selectById(recordId);
        if (record == null) {
            throw new RuntimeException("记录不存在");
        }
        
        Map<String, Object> report = new HashMap<>();
        report.put("record", record);
        
        // 生成分析
        Map<String, Object> analysis = new HashMap<>();
        analysis.put("scoreLevel", getScoreLevel(record.getScore()));
        analysis.put("correctRateLevel", getCorrectRateLevel(record.getCorrectRate()));
        analysis.put("suggestions", generateSuggestions(record.getScore(), record.getCorrectRate()));
        
        report.put("analysis", analysis);
        
        return report;
    }

    private List<DrillScenario> getDefaultScenarios() {
        List<DrillScenario> scenarios = new ArrayList<>();
        
        // 电信诈骗场景
        DrillScenario scenario1 = new DrillScenario();
        scenario1.setId(1L);
        scenario1.setScenarioName("电信诈骗演练");
        scenario1.setScenarioDesc("模拟接到冒充公检法的电话，学习如何识别和应对");
        scenario1.setDifficulty(1);
        scenario1.setDuration(15);
        scenario1.setScenarioType("电信诈骗");
        scenario1.setStatus(1);
        scenarios.add(scenario1);
        
        // 网络诈骗场景
        DrillScenario scenario2 = new DrillScenario();
        scenario2.setId(2L);
        scenario2.setScenarioName("网络诈骗演练");
        scenario2.setScenarioDesc("模拟网购诈骗场景，学习如何保护个人信息");
        scenario2.setDifficulty(2);
        scenario2.setDuration(20);
        scenario2.setScenarioType("网络诈骗");
        scenario2.setStatus(1);
        scenarios.add(scenario2);
        
        // 短信诈骗场景
        DrillScenario scenario3 = new DrillScenario();
        scenario3.setId(3L);
        scenario3.setScenarioName("短信诈骗演练");
        scenario3.setScenarioDesc("模拟收到钓鱼短信，学习如何辨别真伪");
        scenario3.setDifficulty(3);
        scenario3.setDuration(15);
        scenario3.setScenarioType("短信诈骗");
        scenario3.setStatus(1);
        scenarios.add(scenario3);
        
        // 综合反诈场景
        DrillScenario scenario4 = new DrillScenario();
        scenario4.setId(4L);
        scenario4.setScenarioName("综合反诈演练");
        scenario4.setScenarioDesc("多种诈骗场景混合，全面提升应对能力");
        scenario4.setDifficulty(4);
        scenario4.setDuration(30);
        scenario4.setScenarioType("综合诈骗");
        scenario4.setStatus(1);
        scenarios.add(scenario4);
        
        return scenarios;
    }

    private int calculateScore(Map<String, Object> answers, String correctAnswer) {
        // 模拟计算得分
        return new Random().nextInt(50) + 50;
    }

    private int calculateCorrectRate(Map<String, Object> answers, String correctAnswer) {
        // 模拟计算正确率
        return new Random().nextInt(30) + 70;
    }

    private String generateFeedback(Map<String, Object> answers, String feedback) {
        // 模拟生成反馈
        return "您的表现良好，继续保持警惕！";
    }

    private String getScoreLevel(int score) {
        if (score >= 90) return "优秀";
        if (score >= 80) return "良好";
        if (score >= 70) return "中等";
        if (score >= 60) return "及格";
        return "不及格";
    }

    private String getCorrectRateLevel(int correctRate) {
        if (correctRate >= 90) return "优秀";
        if (correctRate >= 80) return "良好";
        if (correctRate >= 70) return "中等";
        if (correctRate >= 60) return "及格";
        return "不及格";
    }

    private List<String> generateSuggestions(int score, int correctRate) {
        List<String> suggestions = new ArrayList<>();
        
        if (score < 70) {
            suggestions.add("加强反诈知识学习，了解常见诈骗手法");
            suggestions.add("多参加演练，提高应对能力");
        }
        
        if (correctRate < 80) {
            suggestions.add("仔细分析诈骗特征，提高识别能力");
            suggestions.add("遇到可疑情况及时报警");
        }
        
        suggestions.add("定期关注反诈新闻，了解最新诈骗手法");
        suggestions.add("向身边的人普及反诈知识");
        
        return suggestions;
    }
}
