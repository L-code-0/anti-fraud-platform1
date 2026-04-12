package com.anti.fraud.modules.exam.service.impl;

import com.anti.fraud.modules.exam.entity.Question;
import com.anti.fraud.modules.exam.mapper.QuestionMapper;
import com.anti.fraud.modules.exam.service.QuestionService;
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
public class QuestionServiceImpl implements QuestionService {

    private final QuestionMapper questionMapper;

    @Override
    public boolean createQuestion(Question question) {
        try {
            question.setUseCount(0);
            question.setCorrectRate(0);
            questionMapper.insert(question);
            log.info("创建题目成功: id={}, category={}, difficulty={}", 
                    question.getId(), question.getCategory(), question.getDifficulty());
            return true;
        } catch (Exception e) {
            log.error("创建题目失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean updateQuestion(Question question) {
        try {
            questionMapper.updateById(question);
            log.info("更新题目成功: id={}", question.getId());
            return true;
        } catch (Exception e) {
            log.error("更新题目失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean deleteQuestion(Long id) {
        try {
            questionMapper.deleteById(id);
            log.info("删除题目成功: id={}", id);
            return true;
        } catch (Exception e) {
            log.error("删除题目失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Question getQuestionById(Long id) {
        try {
            return questionMapper.selectById(id);
        } catch (Exception e) {
            log.error("获取题目详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<Question> getQuestionList(String category, String difficulty, int page, int size) {
        try {
            LambdaQueryWrapper<Question> queryWrapper = new LambdaQueryWrapper<>();
            if (category != null && !category.isEmpty()) {
                queryWrapper.eq(Question::getCategory, category);
            }
            if (difficulty != null && !difficulty.isEmpty()) {
                queryWrapper.eq(Question::getDifficulty, difficulty);
            }
            queryWrapper.orderByDesc(Question::getCreateTime);

            IPage<Question> pageInfo = new Page<>(page, size);
            questionMapper.selectPage(pageInfo, queryWrapper);

            return pageInfo.getRecords();
        } catch (Exception e) {
            log.error("获取题目列表失败: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Question> generateQuestions(String category, String difficulty, int count) {
        List<Question> questions = new ArrayList<>();
        try {
            for (int i = 0; i < count; i++) {
                Question question = generateQuestion(category, difficulty);
                if (question != null) {
                    questionMapper.insert(question);
                    questions.add(question);
                }
            }
            log.info("AI智能生成题目成功: category={}, difficulty={}, count={}", 
                    category, difficulty, count);
            return questions;
        } catch (Exception e) {
            log.error("AI智能生成题目失败: {}", e.getMessage(), e);
            return questions;
        }
    }

    @Override
    public Map<String, Object> batchGenerateQuestions(Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            String category = (String) params.get("category");
            String difficulty = (String) params.get("difficulty");
            int count = (int) params.get("count");

            List<Question> questions = generateQuestions(category, difficulty, count);
            result.put("success", true);
            result.put("count", questions.size());
            result.put("questions", questions);
            return result;
        } catch (Exception e) {
            log.error("批量生成题目失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", e.getMessage());
            return result;
        }
    }

    @Override
    public List<Question> recommendQuestions(Long userId, int count) {
        try {
            // 简单的推荐逻辑：随机选择题目
            LambdaQueryWrapper<Question> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.orderByAsc("RAND()");
            queryWrapper.last("LIMIT " + count);
            return questionMapper.selectList(queryWrapper);
        } catch (Exception e) {
            log.error("推荐题目失败: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public boolean updateQuestionUsage(Long id, boolean isCorrect) {
        try {
            Question question = questionMapper.selectById(id);
            if (question != null) {
                int useCount = question.getUseCount() + 1;
                int correctCount = question.getUseCount() * question.getCorrectRate() / 100;
                if (isCorrect) {
                    correctCount++;
                }
                int correctRate = useCount > 0 ? correctCount * 100 / useCount : 0;

                question.setUseCount(useCount);
                question.setCorrectRate(correctRate);
                questionMapper.updateById(question);
                log.info("更新题目使用情况: id={}, useCount={}, correctRate={}%", 
                        id, useCount, correctRate);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("更新题目使用情况失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Map<String, Object> getQuestionStats() {
        try {
            // 统计题目总数
            long totalCount = questionMapper.selectCount(null);

            // 按分类统计
            Map<String, Long> categoryStats = new HashMap<>();
            // 按难度统计
            Map<String, Long> difficultyStats = new HashMap<>();

            // 简单的统计逻辑（实际项目中应该使用分组查询）
            List<Question> questions = questionMapper.selectList(null);
            for (Question question : questions) {
                categoryStats.put(question.getCategory(), 
                        categoryStats.getOrDefault(question.getCategory(), 0L) + 1);
                difficultyStats.put(question.getDifficulty(), 
                        difficultyStats.getOrDefault(question.getDifficulty(), 0L) + 1);
            }

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalCount", totalCount);
            stats.put("categoryStats", categoryStats);
            stats.put("difficultyStats", difficultyStats);
            return stats;
        } catch (Exception e) {
            log.error("获取题目统计信息失败: {}", e.getMessage(), e);
            return Collections.emptyMap();
        }
    }

    /**
     * 生成单个题目
     */
    private Question generateQuestion(String category, String difficulty) {
        Question question = new Question();
        question.setCategory(category);
        question.setDifficulty(difficulty);
        question.setScore(getScoreByDifficulty(difficulty));
        question.setUseCount(0);
        question.setCorrectRate(0);
        question.setCreatedBy("AI");

        // 根据分类和难度生成题目
        if ("电信诈骗".equals(category)) {
            generateTelecomFraudQuestion(question, difficulty);
        } else if ("网络诈骗".equals(category)) {
            generateOnlineFraudQuestion(question, difficulty);
        } else if ("金融诈骗".equals(category)) {
            generateFinancialFraudQuestion(question, difficulty);
        } else {
            generateGeneralFraudQuestion(question, difficulty);
        }

        return question;
    }

    /**
     * 生成电信诈骗题目
     */
    private void generateTelecomFraudQuestion(Question question, String difficulty) {
        List<String> questions = Arrays.asList(
                "以下哪种是典型的电话诈骗手法？",
                "当接到自称公检法的电话，要求您转账到安全账户时，正确的做法是？",
                "接到陌生电话称您的银行卡被冻结，需要验证身份，您应该？"
        );

        List<List<String>> options = Arrays.asList(
                Arrays.asList("冒充公检法", "快递员派送", "朋友问候", "家庭聚餐"),
                Arrays.asList("立即转账", "挂断电话并报警", "按照要求操作", "与对方理论"),
                Arrays.asList("提供身份证号和密码", "挂断电话并联系银行", "按提示操作", "恐慌并立即转账")
        );

        List<String> answers = Arrays.asList("A", "B", "B");
        List<String> explanations = Arrays.asList(
                "冒充公检法是典型的电话诈骗手法，骗子会以各种理由要求受害人转账。",
                "公检法机关不会通过电话办案，也不会要求转账到安全账户，应挂断电话并报警。",
                "银行不会通过电话要求验证身份，应挂断电话并联系银行官方客服核实。"
        );

        int index = new Random().nextInt(questions.size());
        question.setQuestionText(questions.get(index));
        question.setOptionA(options.get(index).get(0));
        question.setOptionB(options.get(index).get(1));
        question.setOptionC(options.get(index).get(2));
        question.setOptionD(options.get(index).get(3));
        question.setCorrectAnswer(answers.get(index));
        question.setExplanation(explanations.get(index));
    }

    /**
     * 生成网络诈骗题目
     */
    private void generateOnlineFraudQuestion(Question question, String difficulty) {
        List<String> questions = Arrays.asList(
                "以下哪种是网络购物诈骗的常见手法？",
                "收到陌生链接，声称点击可领取红包，正确的做法是？",
                "网络兼职刷单诈骗的特点是？"
        );

        List<List<String>> options = Arrays.asList(
                Arrays.asList("低价促销", "正规商家", "货到付款", "虚假发货"),
                Arrays.asList("点击链接领取", "忽略并删除", "分享给朋友", "截图保存"),
                Arrays.asList("高回报低风险", "需要缴纳保证金", "工作轻松", "以上都是")
        );

        List<String> answers = Arrays.asList("D", "B", "D");
        List<String> explanations = Arrays.asList(
                "虚假发货是网络购物诈骗的常见手法，骗子收了钱却不发货。",
                "陌生链接可能含有病毒或钓鱼网站，应忽略并删除。",
                "网络兼职刷单诈骗通常以高回报低风险为诱饵，要求缴纳保证金，工作轻松等特点。"
        );

        int index = new Random().nextInt(questions.size());
        question.setQuestionText(questions.get(index));
        question.setOptionA(options.get(index).get(0));
        question.setOptionB(options.get(index).get(1));
        question.setOptionC(options.get(index).get(2));
        question.setOptionD(options.get(index).get(3));
        question.setCorrectAnswer(answers.get(index));
        question.setExplanation(explanations.get(index));
    }

    /**
     * 生成金融诈骗题目
     */
    private void generateFinancialFraudQuestion(Question question, String difficulty) {
        List<String> questions = Arrays.asList(
                "以下哪种是金融诈骗的常见手法？",
                "遇到高息理财产品，承诺无风险高回报，您应该？",
                "信用卡诈骗的常见手法是？"
        );

        List<List<String>> options = Arrays.asList(
                Arrays.asList("正规银行理财", "P2P平台投资", "非法集资", "股票投资"),
                Arrays.asList("立即投资", "谨慎核实", "全部投入", "推荐给朋友"),
                Arrays.asList("冒充银行客服", "盗刷信用卡", "虚假办卡", "以上都是")
        );

        List<String> answers = Arrays.asList("C", "B", "D");
        List<String> explanations = Arrays.asList(
                "非法集资是金融诈骗的常见手法，通过承诺高回报吸引投资者。",
                "高息理财产品往往存在风险，应谨慎核实其合法性。",
                "信用卡诈骗包括冒充银行客服、盗刷信用卡、虚假办卡等多种手法。"
        );

        int index = new Random().nextInt(questions.size());
        question.setQuestionText(questions.get(index));
        question.setOptionA(options.get(index).get(0));
        question.setOptionB(options.get(index).get(1));
        question.setOptionC(options.get(index).get(2));
        question.setOptionD(options.get(index).get(3));
        question.setCorrectAnswer(answers.get(index));
        question.setExplanation(explanations.get(index));
    }

    /**
     * 生成通用诈骗题目
     */
    private void generateGeneralFraudQuestion(Question question, String difficulty) {
        List<String> questions = Arrays.asList(
                "防范诈骗的基本措施是？",
                "遭遇诈骗后，正确的做法是？",
                "以下哪种信息不应轻易透露给陌生人？"
        );

        List<List<String>> options = Arrays.asList(
                Arrays.asList("轻信陌生人", "保护个人信息", "随意转账", "点击陌生链接"),
                Arrays.asList("自认倒霉", "立即报警", "沉默不语", "找骗子理论"),
                Arrays.asList("姓名", "身份证号", "电话号码", "以上都是")
        );

        List<String> answers = Arrays.asList("B", "B", "D");
        List<String> explanations = Arrays.asList(
                "保护个人信息是防范诈骗的基本措施，不要轻易透露个人敏感信息。",
                "遭遇诈骗后应立即报警，尽可能挽回损失。",
                "姓名、身份证号、电话号码等个人信息都不应轻易透露给陌生人。"
        );

        int index = new Random().nextInt(questions.size());
        question.setQuestionText(questions.get(index));
        question.setOptionA(options.get(index).get(0));
        question.setOptionB(options.get(index).get(1));
        question.setOptionC(options.get(index).get(2));
        question.setOptionD(options.get(index).get(3));
        question.setCorrectAnswer(answers.get(index));
        question.setExplanation(explanations.get(index));
    }

    /**
     * 根据难度获取分数
     */
    private int getScoreByDifficulty(String difficulty) {
        switch (difficulty) {
            case "简单":
                return 1;
            case "中等":
                return 2;
            case "困难":
                return 3;
            default:
                return 2;
        }
    }
}
