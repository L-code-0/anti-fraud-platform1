package com.anti.fraud.modules.qa.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.anti.fraud.modules.qa.dto.AnswerDTO;
import com.anti.fraud.modules.qa.dto.QuestionDTO;
import com.anti.fraud.modules.qa.entity.FollowUpQuestion;
import com.anti.fraud.modules.qa.vo.QuestionVO;
import java.util.List;
import java.util.Map;

public interface QAService {
    /**
     * 提问
     */
    Long askQuestion(QuestionDTO dto);

    /**
     * 获取问题列表
     */
    Page<QuestionVO> getQuestions(Integer page, Integer size, Integer status);

    /**
     * 获取问题详情
     */
    QuestionVO getQuestionDetail(Long id);

    /**
     * 回答问题（专家）
     */
    Long answerQuestion(AnswerDTO dto);

    /**
     * 采纳回答
     */
    void acceptAnswer(Long answerId);

    /**
     * 点赞回答
     */
    void likeAnswer(Long answerId);

    /**
     * 删除问题
     */
    void deleteQuestion(Long id);

    /**
     * 追问
     */
    Long followUpQuestion(Long questionId, Long answerId, String content);

    /**
     * 回答追问
     */
    void answerFollowUp(Long followUpId, String content);

    /**
     * 获取追问列表
     */
    List<FollowUpQuestion> getFollowUpQuestions(Long questionId);

    /**
     * 获取用户的追问列表
     */
    List<FollowUpQuestion> getUserFollowUpQuestions(Long userId);

    /**
     * 获取用户的采纳率
     */
    Map<String, Object> getUserAcceptanceRate(Long userId);

    /**
     * 获取专家的采纳率排行榜
     */
    Page<Map<String, Object>> getExpertAcceptanceRateRank(Integer page, Integer size);
}
