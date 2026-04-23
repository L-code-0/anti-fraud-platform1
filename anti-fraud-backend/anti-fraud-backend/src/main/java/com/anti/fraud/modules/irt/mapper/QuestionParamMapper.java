package com.anti.fraud.modules.irt.mapper;

import com.anti.fraud.modules.irt.entity.QuestionParam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 题目参数Mapper
 */
@Mapper
public interface QuestionParamMapper extends BaseMapper<QuestionParam> {

    /**
     * 根据参数ID查询题目参数
     * @param paramId 参数ID
     * @return 题目参数
     */
    QuestionParam selectByParamId(@Param("paramId") String paramId);

    /**
     * 根据题目ID查询题目参数
     * @param questionId 题目ID
     * @return 题目参数
     */
    QuestionParam selectByQuestionId(@Param("questionId") String questionId);

    /**
     * 根据模型ID查询题目参数列表
     * @param modelId 模型ID
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @return 题目参数列表
     */
    List<QuestionParam> selectByModelId(@Param("modelId") String modelId, @Param("status") Integer status, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据状态查询题目参数列表
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @return 题目参数列表
     */
    List<QuestionParam> selectByStatus(@Param("status") Integer status, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 更新题目参数状态
     * @param id 题目参数ID
     * @param status 状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 更新题目参数值
     * @param id 题目参数ID
     * @param difficulty 难度参数
     * @param discrimination 区分度参数
     * @param guessing 猜测参数
     * @param estimationAccuracy 估计精度
     * @param sampleSize 样本量
     * @param calibrationTime 标定时间
     * @return 影响行数
     */
    int updateQuestionParams(@Param("id") Long id, @Param("difficulty") Double difficulty, @Param("discrimination") Double discrimination, @Param("guessing") Double guessing, @Param("estimationAccuracy") Double estimationAccuracy, @Param("sampleSize") Integer sampleSize, @Param("calibrationTime") LocalDateTime calibrationTime);

    /**
     * 统计题目参数数量
     * @param modelId 模型ID
     * @param status 状态
     * @return 题目参数数量
     */
    Integer countByModelId(@Param("modelId") String modelId, @Param("status") Integer status);

    /**
     * 获取最新的题目参数
     * @param limit 数量限制
     * @param modelId 模型ID
     * @param status 状态
     * @return 题目参数列表
     */
    List<QuestionParam> selectRecentQuestionParams(@Param("limit") Integer limit, @Param("modelId") String modelId, @Param("status") Integer status);

    /**
     * 批量插入题目参数
     * @param questionParams 题目参数列表
     * @return 插入成功数量
     */
    int batchInsert(@Param("questionParams") List<QuestionParam> questionParams);

    /**
     * 批量更新题目参数状态
     * @param ids 题目参数ID列表
     * @param status 状态
     * @return 影响行数
     */
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") Integer status);
}
