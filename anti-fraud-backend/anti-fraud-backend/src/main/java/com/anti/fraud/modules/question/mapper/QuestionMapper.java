package com.anti.fraud.modules.question.mapper;

import com.anti.fraud.modules.question.entity.Question;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 题目Mapper
 */
@Mapper
public interface QuestionMapper extends BaseMapper<Question> {

    /**
     * 根据条件查询题目列表
     * @param params 查询参数
     * @param page 页码
     * @param size 每页大小
     * @return 题目列表
     */
    List<Question> selectByCondition(@Param("params") Map<String, Object> params, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据条件查询题目总数
     * @param params 查询参数
     * @return 题目总数
     */
    Integer selectCountByCondition(@Param("params") Map<String, Object> params);

    /**
     * 随机获取题目
     * @param params 筛选参数
     * @param limit 数量限制
     * @return 题目列表
     */
    List<Question> selectRandomQuestions(@Param("params") Map<String, Object> params, @Param("limit") Integer limit);

    /**
     * 按知识点统计题目数量
     * @return 知识点统计
     */
    List<Map<String, Object>> selectKnowledgePointStats();

    /**
     * 按题目类型统计题目数量
     * @return 题目类型统计
     */
    List<Map<String, Object>> selectTypeStats();

    /**
     * 按难度统计题目数量
     * @return 难度统计
     */
    List<Map<String, Object>> selectDifficultyStats();

    /**
     * 更新题目使用率
     * @param id 题目ID
     */
    void updateUsageCount(@Param("id") Long id);

    /**
     * 更新题目正确率
     * @param id 题目ID
     * @param correctRate 正确率
     */
    void updateCorrectRate(@Param("id") Long id, @Param("correctRate") Double correctRate);
}
