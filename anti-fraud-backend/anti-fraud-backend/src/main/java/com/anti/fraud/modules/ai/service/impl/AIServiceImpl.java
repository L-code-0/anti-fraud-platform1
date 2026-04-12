package com.anti.fraud.modules.ai.service.impl;

import com.anti.fraud.modules.ai.entity.AIQuestion;
import com.anti.fraud.modules.ai.mapper.AIQuestionMapper;
import com.anti.fraud.modules.ai.service.AIService;
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
public class AIServiceImpl implements AIService {

    private final AIQuestionMapper aiQuestionMapper;

    @Override
    public Map<String, Object> askQuestion(Long userId, String question, String category) {
        log.info("AI问答请求: userId={}, question={}, category={}", userId, question, category);

        try {
            // 模拟AI回答（实际项目中应该调用真实的AI服务）
            String answer = generateAnswer(question, category);

            // 保存问答记录
            AIQuestion aiQuestion = new AIQuestion();
            aiQuestion.setUserId(userId);
            aiQuestion.setUserName("用户" + userId);
            aiQuestion.setQuestion(question);
            aiQuestion.setAnswer(answer);
            aiQuestion.setCategory(category);
            aiQuestion.setIsPublic(0);
            aiQuestion.setStatus(1);
            aiQuestionMapper.insert(aiQuestion);

            Map<String, Object> result = new HashMap<>();
            result.put("answer", answer);
            result.put("questionId", aiQuestion.getId());
            result.put("category", category);
            result.put("timestamp", new Date());

            log.info("AI问答成功: questionId={}, answer={}", aiQuestion.getId(), answer);
            return result;
        } catch (Exception e) {
            log.error("AI问答失败: {}", e.getMessage(), e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("answer", "抱歉，我暂时无法回答这个问题，请稍后再试。");
            errorResult.put("error", e.getMessage());
            return errorResult;
        }
    }

    @Override
    public List<AIQuestion> getQuestionHistory(Long userId, int page, int size) {
        try {
            LambdaQueryWrapper<AIQuestion> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AIQuestion::getUserId, userId)
                    .orderByDesc(AIQuestion::getCreateTime);

            IPage<AIQuestion> pageInfo = new Page<>(page, size);
            aiQuestionMapper.selectPage(pageInfo, queryWrapper);

            return pageInfo.getRecords();
        } catch (Exception e) {
            log.error("获取问答历史失败: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<AIQuestion> getHotQuestions(int limit) {
        try {
            LambdaQueryWrapper<AIQuestion> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AIQuestion::getIsPublic, 1)
                    .eq(AIQuestion::getStatus, 1)
                    .orderByDesc(AIQuestion::getCreateTime)
                    .last("LIMIT " + limit);

            return aiQuestionMapper.selectList(queryWrapper);
        } catch (Exception e) {
            log.error("获取热门问题失败: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public boolean markAsPublic(Long id, Long userId) {
        try {
            AIQuestion aiQuestion = aiQuestionMapper.selectById(id);
            if (aiQuestion != null && aiQuestion.getUserId().equals(userId)) {
                aiQuestion.setIsPublic(1);
                aiQuestionMapper.updateById(aiQuestion);
                log.info("问题标记为公开: id={}, userId={}", id, userId);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("标记问题为公开失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean markAsPrivate(Long id, Long userId) {
        try {
            AIQuestion aiQuestion = aiQuestionMapper.selectById(id);
            if (aiQuestion != null && aiQuestion.getUserId().equals(userId)) {
                aiQuestion.setIsPublic(0);
                aiQuestionMapper.updateById(aiQuestion);
                log.info("问题标记为私有: id={}, userId={}", id, userId);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("标记问题为私有失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean deleteQuestion(Long id, Long userId) {
        try {
            AIQuestion aiQuestion = aiQuestionMapper.selectById(id);
            if (aiQuestion != null && aiQuestion.getUserId().equals(userId)) {
                aiQuestionMapper.deleteById(id);
                log.info("删除问题: id={}, userId={}", id, userId);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("删除问题失败: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 模拟AI回答
     */
    private String generateAnswer(String question, String category) {
        // 简单的问题匹配逻辑
        question = question.toLowerCase();

        if (question.contains("诈骗") || question.contains("骗")) {
            if (question.contains("电话")) {
                return "电话诈骗是一种常见的诈骗方式，骗子通常会冒充公检法、银行等机构，以各种理由要求您转账。防范措施：不要轻易相信陌生电话，不要向陌生人转账，如有疑问可拨打官方客服电话核实。";
            } else if (question.contains("短信")) {
                return "短信诈骗通常以中奖、欠费、积分兑换等为由，诱导您点击链接或回复信息。防范措施：不要点击陌生链接，不要回复陌生短信，不要泄露个人信息。";
            } else if (question.contains("网络")) {
                return "网络诈骗包括网络购物诈骗、网络兼职诈骗、网络贷款诈骗等。防范措施：选择正规购物平台，不要相信高回报兼职，贷款需通过正规渠道。";
            } else {
                return "诈骗是指以非法占有为目的，用虚构事实或者隐瞒真相的方法，骗取款额较大的公私财物的行为。常见的诈骗类型包括：电话诈骗、短信诈骗、网络诈骗、电信诈骗等。防范诈骗的关键是提高警惕，不要轻易相信陌生人，不要随意透露个人信息，不要向陌生人转账。";
            }
        } else if (question.contains("防范") || question.contains("预防")) {
            return "防范诈骗的主要措施包括：1. 提高警惕，不要轻易相信陌生人；2. 保护个人信息，不要随意透露身份证号、银行卡号、密码等敏感信息；3. 不要点击陌生链接，不要回复陌生短信；4. 不要向陌生人转账，如有疑问可拨打官方客服电话核实；5. 安装反诈APP，及时了解最新诈骗手法。";
        } else if (question.contains("报警") || question.contains("处理")) {
            return "如果您遭遇了诈骗，应立即采取以下措施：1. 保留相关证据，如聊天记录、转账记录、电话号码等；2. 立即拨打110报警；3. 联系银行冻结账户，尝试追回损失；4. 向反诈中心报案，获取专业帮助。";
        } else {
            return "您好，我是防骗AI助手，很高兴为您服务。请问您想了解哪方面的防骗知识？例如：电话诈骗、短信诈骗、网络诈骗、防范措施等。";
        }
    }
}
