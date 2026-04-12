package com.anti.fraud.modules.exam.service;

import com.anti.fraud.modules.exam.entity.Question;
import java.util.List;
import java.util.Map;

public interface QuestionService {

    /**
     * 创建题目
     * @param question 题目信息
     * @return 是否成功
     */
    boolean createQuestion(Question question);

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
     * 获取题目列表
     * @param category 分类
     * @param difficulty 难度
     * @param page 页码
     * @param size 每页大小
     * @return 题目列表
     */
    List<Question> getQuestionList(String category, String difficulty, int page, int size);

    /**
     * AI智能生成题目
     * @param category 分类
     * @param difficulty 难度
     * @param count 数量
     * @return 生成的题目列表
     */
    List<Question> generateQuestions(String category, String difficulty, int count);

    /**
     * 批量生成题目
     * @param params 生成参数
     * @return 生成结果
     */
    Map<String, Object> batchGenerateQuestions(Map<String, Object> params);

    /**
     * 推荐题目
     * @param userId 用户ID
     * @param count 数量
     * @return 推荐题目列表
     */
    List<Question> recommendQuestions(Long userId, int count);

    /**
     * 更新题目使用情况
     * @param id 题目ID
     * @param isCorrect 是否正确
     * @return 是否成功
     */
    boolean updateQuestionUsage(Long id, boolean isCorrect);

    /**
     * 获取题目统计信息
     * @return 统计信息
     */
    Map<String, Object> getQuestionStats();
}
