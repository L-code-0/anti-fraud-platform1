package com.anti.fraud.modules.question.service.impl;

import com.anti.fraud.modules.question.entity.Question;
import com.anti.fraud.modules.question.mapper.QuestionMapper;
import com.anti.fraud.modules.question.service.QuestionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 题目服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    private final QuestionMapper questionMapper;

    @Override
    @Transactional
    public boolean addQuestion(Question question) {
        try {
            question.setUsageCount(0);
            question.setCorrectRate(0.0);
            question.setStatus(1);
            question.setDeleted(0);
            question.setCreateTime(LocalDateTime.now());
            question.setUpdateTime(LocalDateTime.now());
            return save(question);
        } catch (Exception e) {
            log.error("新增题目失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateQuestion(Question question) {
        try {
            question.setUpdateTime(LocalDateTime.now());
            return updateById(question);
        } catch (Exception e) {
            log.error("更新题目失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteQuestion(Long id) {
        try {
            Question question = getById(id);
            if (question != null) {
                question.setDeleted(1);
                question.setUpdateTime(LocalDateTime.now());
                return updateById(question);
            }
            return false;
        } catch (Exception e) {
            log.error("删除题目失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Question getQuestionById(Long id) {
        try {
            return getById(id);
        } catch (Exception e) {
            log.error("获取题目详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getQuestionList(Map<String, Object> params, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<Question> questions = questionMapper.selectByCondition(params, offset, size);
            int total = questionMapper.selectCountByCondition(params);

            Map<String, Object> result = new HashMap<>();
            result.put("list", questions);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("查询题目列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public List<Question> generateQuestions(Map<String, Object> params, int count) {
        try {
            return questionMapper.selectRandomQuestions(params, count);
        } catch (Exception e) {
            log.error("随机生成题目失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public Question generateQuestionByAI(Map<String, Object> params) {
        try {
            // 模拟AI出题逻辑
            // 实际项目中，这里可以调用AI模型生成题目
            log.info("AI智能出题: params={}", params);

            Question question = new Question();
            question.setContent(generateQuestionContent(params));
            question.setType(getQuestionType(params));
            question.setDifficulty(getQuestionDifficulty(params));
            question.setKnowledgePoint(getKnowledgePoint(params));
            question.setOptions(generateOptions(question.getType()));
            question.setCorrectAnswer(generateCorrectAnswer(question.getType()));
            question.setExplanation(generateExplanation(question.getContent(), question.getCorrectAnswer()));
            question.setSource("AI生成");
            question.setTags(generateTags(question.getKnowledgePoint()));
            question.setUsageCount(0);
            question.setCorrectRate(0.0);
            question.setStatus(1);
            question.setDeleted(0);
            question.setCreateTime(LocalDateTime.now());
            question.setUpdateTime(LocalDateTime.now());

            // 保存生成的题目
            save(question);
            log.info("AI智能出题成功: id={}, content={}", question.getId(), question.getContent());
            return question;
        } catch (Exception e) {
            log.error("AI智能出题失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<Question> batchGenerateQuestionsByAI(Map<String, Object> params, int count) {
        try {
            List<Question> questions = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                Question question = generateQuestionByAI(params);
                if (question != null) {
                    questions.add(question);
                }
            }
            log.info("批量AI智能出题成功: 生成{}道题目", questions.size());
            return questions;
        } catch (Exception e) {
            log.error("批量AI智能出题失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public Map<String, Object> getQuestionStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("knowledgePointStats", questionMapper.selectKnowledgePointStats());
            stats.put("typeStats", questionMapper.selectTypeStats());
            stats.put("difficultyStats", questionMapper.selectDifficultyStats());
            return stats;
        } catch (Exception e) {
            log.error("统计题目信息失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    @Transactional
    public boolean updateUsageCount(Long id) {
        try {
            questionMapper.updateUsageCount(id);
            return true;
        } catch (Exception e) {
            log.error("更新题目使用率失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateCorrectRate(Long id, Double correctRate) {
        try {
            questionMapper.updateCorrectRate(id, correctRate);
            return true;
        } catch (Exception e) {
            log.error("更新题目正确率失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Map<String, Object> verifyAnswer(Long questionId, String answer) {
        try {
            Question question = getById(questionId);
            if (question == null) {
                Map<String, Object> result = new HashMap<>();
                result.put("correct", false);
                result.put("message", "题目不存在");
                return result;
            }

            boolean correct = question.getCorrectAnswer().equals(answer);
            Map<String, Object> result = new HashMap<>();
            result.put("correct", correct);
            result.put("message", correct ? "回答正确" : "回答错误");
            result.put("explanation", question.getExplanation());
            result.put("correctAnswer", question.getCorrectAnswer());

            // 更新题目使用率
            updateUsageCount(questionId);

            return result;
        } catch (Exception e) {
            log.error("验证答案失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("correct", false);
            result.put("message", "验证答案失败");
            return result;
        }
    }

    // 生成题目内容
    private String generateQuestionContent(Map<String, Object> params) {
        String knowledgePoint = (String) params.get("knowledgePoint");
        if (knowledgePoint == null) {
            knowledgePoint = "诈骗防范";
        }

        String[] questionTemplates = {
                "以下关于{}的说法，正确的是：",
                "下列哪种行为属于{}的常见手段？",
                "遇到{}时，正确的做法是：",
                "{}的主要特征不包括：",
                "如何识别{}？"
        };

        Random random = new Random();
        String template = questionTemplates[random.nextInt(questionTemplates.length)];
        return String.format(template, knowledgePoint);
    }

    // 获取题目类型
    private Integer getQuestionType(Map<String, Object> params) {
        Integer type = (Integer) params.get("type");
        if (type != null) {
            return type;
        }
        // 随机生成题目类型
        Random random = new Random();
        return random.nextInt(3) + 1; // 1-单选题，2-多选题，3-判断题
    }

    // 获取题目难度
    private Integer getQuestionDifficulty(Map<String, Object> params) {
        Integer difficulty = (Integer) params.get("difficulty");
        if (difficulty != null) {
            return difficulty;
        }
        // 随机生成难度
        Random random = new Random();
        return random.nextInt(3) + 1; // 1-简单，2-中等，3-困难
    }

    // 获取知识点
    private String getKnowledgePoint(Map<String, Object> params) {
        String knowledgePoint = (String) params.get("knowledgePoint");
        if (knowledgePoint != null) {
            return knowledgePoint;
        }
        // 随机生成知识点
        String[] knowledgePoints = {
                "电信诈骗",
                "网络诈骗",
                "金融诈骗",
                "冒充公检法诈骗",
                "刷单诈骗",
                "中奖诈骗",
                "投资理财诈骗",
                "婚恋诈骗"
        };
        Random random = new Random();
        return knowledgePoints[random.nextInt(knowledgePoints.length)];
    }

    // 生成选项
    private String generateOptions(Integer type) {
        String[] options = {
                "A. 选项1",
                "B. 选项2",
                "C. 选项3",
                "D. 选项4"
        };

        if (type == 3) { // 判断题
            return "A. 正确,B. 错误";
        } else if (type == 1) { // 单选题
            return String.join(",", options);
        } else { // 多选题
            return String.join(",", options);
        }
    }

    // 生成正确答案
    private String generateCorrectAnswer(Integer type) {
        if (type == 3) { // 判断题
            return Math.random() > 0.5 ? "A" : "B";
        } else if (type == 1) { // 单选题
            String[] answers = {"A", "B", "C", "D"};
            return answers[new Random().nextInt(4)];
        } else { // 多选题
            // 生成2-4个正确答案
            StringBuilder answer = new StringBuilder();
            String[] options = {"A", "B", "C", "D"};
            int count = new Random().nextInt(3) + 2; // 2-4个正确答案
            boolean[] selected = new boolean[4];
            for (int i = 0; i < count; i++) {
                int index = new Random().nextInt(4);
                if (!selected[index]) {
                    selected[index] = true;
                    answer.append(options[index]);
                }
            }
            return answer.toString();
        }
    }

    // 生成解析
    private String generateExplanation(String content, String correctAnswer) {
        return "本题主要考察对相关知识的理解和掌握。正确答案是" + correctAnswer + "，因为...";
    }

    // 生成标签
    private String generateTags(String knowledgePoint) {
        return knowledgePoint + ",防诈骗,安全知识";
    }
}
