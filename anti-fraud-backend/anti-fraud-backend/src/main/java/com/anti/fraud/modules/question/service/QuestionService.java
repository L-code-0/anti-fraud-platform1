package com.anti.fraud.modules.question.service;

import com.anti.fraud.modules.question.entity.Question;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 题目服务接口
 */
public interface QuestionService extends IService<Question> {

    /**
     * 新增题目
     * @param question 题目信息
     * @return 是否成功
     */
    boolean addQuestion(Question question);

    /**
     * 更新题目
     * @param question 题目信息
     * @return 是否成功
     */
    boolean updateQuestion(Question question);

    /**
     * 删除题目
     * @param id 题目ID
     * @return 是否成功
     */
    boolean deleteQuestion(Long id);

    /**
     * 获取题目详情
     * @param id 题目ID
     * @return 题目详情
     */
    Question getQuestionById(Long id);

    /**
     * 分页查询题目
     * @param params 查询参数
     * @param page 页码
     * @param size 每页大小
     * @return 题目列表和总数
     */
    Map<String, Object> getQuestionList(Map<String, Object> params, int page, int size);

    /**
     * 随机生成题目
     * @param params 筛选参数
     * @param count 题目数量
     * @return 题目列表
     */
    List<Question> generateQuestions(Map<String, Object> params, int count);

    /**
     * AI智能出题
     * @param params 出题参数
     * @return 生成的题目
     */
    Question generateQuestionByAI(Map<String, Object> params);

    /**
     * 批量AI智能出题
     * @param params 出题参数
     * @param count 题目数量
     * @return 生成的题目列表
     */
    List<Question> batchGenerateQuestionsByAI(Map<String, Object> params, int count);

    /**
     * 统计题目信息
     * @return 统计信息
     */
    Map<String, Object> getQuestionStats();

    /**
     * 更新题目使用率
     * @param id 题目ID
     * @return 是否成功
     */
    boolean updateUsageCount(Long id);

    /**
     * 更新题目正确率
     * @param id 题目ID
     * @param correctRate 正确率
     * @return 是否成功
     */
    boolean updateCorrectRate(Long id, Double correctRate);

    /**
     * 验证答案
     * @param questionId 题目ID
     * @param answer 用户答案
     * @return 验证结果
     */
    Map<String, Object> verifyAnswer(Long questionId, String answer);
}
