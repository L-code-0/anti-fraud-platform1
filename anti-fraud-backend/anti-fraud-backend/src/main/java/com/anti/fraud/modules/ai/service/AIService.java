package com.anti.fraud.modules.ai.service;

import com.anti.fraud.modules.ai.entity.AIQuestion;
import java.util.List;
import java.util.Map;

public interface AIService {

    /**
     * 智能问答
     * @param userId 用户ID
     * @param question 问题
     * @param category 分类
     * @return 回答结果
     */
    Map<String, Object> askQuestion(Long userId, String question, String category);

    /**
     * 获取用户的问答历史
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 问答历史列表
     */
    List<AIQuestion> getQuestionHistory(Long userId, int page, int size);

    /**
     * 获取热门问题
     * @param limit 数量限制
     * @return 热门问题列表
     */
    List<AIQuestion> getHotQuestions(int limit);

    /**
     * 标记问题为公开
     * @param id 问题ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean markAsPublic(Long id, Long userId);

    /**
     * 标记问题为私有
     * @param id 问题ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean markAsPrivate(Long id, Long userId);

    /**
     * 删除问题
     * @param id 问题ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean deleteQuestion(Long id, Long userId);
}
