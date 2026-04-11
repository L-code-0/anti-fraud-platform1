package com.anti.fraud.modules.test.service.impl;

import com.anti.fraud.modules.test.entity.UserAbility;
import com.anti.fraud.modules.test.mapper.UserAbilityMapper;
import com.anti.fraud.modules.test.service.TestService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class TestServiceImpl extends ServiceImpl<UserAbilityMapper, UserAbility> implements TestService {

    @Autowired
    private UserAbilityMapper userAbilityMapper;

    @Override
    public UserAbility initUserAbility(Long userId) {
        UserAbility ability = new UserAbility();
        ability.setUserId(userId);
        ability.setOverallAbility(50);
        ability.setKnowledgeMastery(50);
        ability.setTestPerformance(50);
        ability.setAdaptiveLevel(1);
        ability.setLastTestTime(LocalDateTime.now());
        userAbilityMapper.insert(ability);
        return ability;
    }

    @Override
    public UserAbility getUserAbility(Long userId) {
        QueryWrapper<UserAbility> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        UserAbility ability = userAbilityMapper.selectOne(queryWrapper);
        if (ability == null) {
            return initUserAbility(userId);
        }
        return ability;
    }

    @Override
    public Map<String, Object> adaptiveTest(Long userId, Integer questionCount) {
        UserAbility ability = getUserAbility(userId);
        
        // 根据用户能力水平生成自适应测试
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("abilityLevel", ability.getAdaptiveLevel());
        result.put("questionCount", questionCount);
        
        // 模拟生成题目
        List<Map<String, Object>> questions = new ArrayList<>();
        for (int i = 0; i < questionCount; i++) {
            Map<String, Object> question = new HashMap<>();
            question.put("id", i + 1);
            question.put("content", "测试题目 " + (i + 1));
            question.put("type", 1);
            question.put("difficulty", ability.getAdaptiveLevel());
            questions.add(question);
        }
        
        result.put("questions", questions);
        result.put("paperId", System.currentTimeMillis());
        
        return result;
    }

    @Override
    public Map<String, Object> submitTest(Long userId, Long paperId, Map<String, Object> answers) {
        UserAbility ability = getUserAbility(userId);
        
        // 模拟评分和能力评估
        int score = new Random().nextInt(50) + 50;
        int newAbility = ability.getOverallAbility() + (score - 70) / 2;
        newAbility = Math.max(0, Math.min(100, newAbility));
        
        // 更新用户能力
        ability.setOverallAbility(newAbility);
        ability.setLastTestTime(LocalDateTime.now());
        ability.setAdaptiveLevel(Math.min(5, Math.max(1, newAbility / 20 + 1)));
        userAbilityMapper.updateById(ability);
        
        Map<String, Object> result = new HashMap<>();
        result.put("score", score);
        result.put("pass", score >= 60);
        result.put("newAbility", newAbility);
        result.put("testId", System.currentTimeMillis());
        
        return result;
    }

    @Override
    public Map<String, Object> generateTestReport(Long userId, Long testId) {
        UserAbility ability = getUserAbility(userId);
        
        Map<String, Object> report = new HashMap<>();
        report.put("testId", testId);
        report.put("userId", userId);
        report.put("abilityLevel", ability.getAdaptiveLevel());
        report.put("overallAbility", ability.getOverallAbility());
        
        // 模拟测试结果分析
        Map<String, Object> analysis = new HashMap<>();
        analysis.put("correctRate", new Random().nextDouble() * 0.5 + 0.5);
        analysis.put("averageTime", new Random().nextInt(60) + 30);
        analysis.put("ranking", new Random().nextInt(100) + 1);
        
        // 知识点掌握情况
        List<Map<String, Object>> knowledgeMastery = new ArrayList<>();
        knowledgeMastery.add(Map.of("category", "电信诈骗", "mastery", new Random().nextInt(50) + 50));
        knowledgeMastery.add(Map.of("category", "网络诈骗", "mastery", new Random().nextInt(50) + 50));
        knowledgeMastery.add(Map.of("category", "校园贷", "mastery", new Random().nextInt(50) + 50));
        
        analysis.put("knowledgeMastery", knowledgeMastery);
        report.put("analysis", analysis);
        
        // 改进建议
        List<String> suggestions = new ArrayList<>();
        suggestions.add("加强电信诈骗防范知识的学习");
        suggestions.add("提高网络安全意识");
        suggestions.add("定期参加模拟测试");
        report.put("suggestions", suggestions);
        
        return report;
    }

    @Override
    public Map<String, Object> analyzeWeaknesses(Long userId) {
        Map<String, Object> result = new HashMap<>();
        
        // 模拟分析结果
        List<Map<String, Object>> weaknesses = new ArrayList<>();
        weaknesses.add(Map.of("category", "电信诈骗", "weaknessLevel", 3, "description", "对电信诈骗的识别能力较弱"));
        weaknesses.add(Map.of("category", "网络诈骗", "weaknessLevel", 2, "description", "对网络诈骗的防范意识需要加强"));
        
        result.put("weaknesses", weaknesses);
        result.put("totalWeaknessCount", weaknesses.size());
        
        return result;
    }

    @Override
    public Map<String, Object> recommendLearningContent(Long userId) {
        Map<String, Object> result = new HashMap<>();
        
        // 模拟推荐内容
        List<Map<String, Object>> recommendations = new ArrayList<>();
        recommendations.add(Map.of("id", 1, "title", "电信诈骗防范指南", "type", "article", "priority", 1));
        recommendations.add(Map.of("id", 2, "title", "网络诈骗案例分析", "type", "video", "priority", 2));
        recommendations.add(Map.of("id", 3, "title", "校园贷风险防范", "type", "article", "priority", 3));
        
        result.put("recommendations", recommendations);
        result.put("totalRecommendations", recommendations.size());
        
        return result;
    }
}
