package com.anti.fraud.modules.qa.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.anti.fraud.modules.qa.dto.AnswerDTO;
import com.anti.fraud.modules.qa.dto.QuestionDTO;
import com.anti.fraud.modules.qa.vo.QuestionVO;

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
}
