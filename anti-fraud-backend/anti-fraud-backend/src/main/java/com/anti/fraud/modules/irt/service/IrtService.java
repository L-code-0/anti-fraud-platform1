package com.anti.fraud.modules.irt.service;

import com.anti.fraud.modules.irt.entity.IrtModel;
import com.anti.fraud.modules.irt.entity.QuestionParam;
import com.anti.fraud.modules.irt.entity.AbilityEstimation;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * IRT模型服务接口
 */
public interface IrtService {

    /**
     * 创建IRT模型
     * @param irtModel IRT模型信息
     * @return 模型ID
     */
    String createIrtModel(IrtModel irtModel);

    /**
     * 更新IRT模型
     * @param irtModel IRT模型信息
     * @return 是否成功
     */
    boolean updateIrtModel(IrtModel irtModel);

    /**
     * 删除IRT模型
     * @param modelId 模型ID
     * @return 是否成功
     */
    boolean deleteIrtModel(String modelId);

    /**
     * 获取IRT模型详情
     * @param modelId 模型ID
     * @return IRT模型详情
     */
    IrtModel getIrtModelByModelId(String modelId);

    /**
     * 分页查询IRT模型列表
     * @param modelType 模型类型
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @return IRT模型列表和总数
     */
    Map<String, Object> getIrtModelList(Integer modelType, Integer status, int page, int size);

    /**
     * 训练IRT模型
     * @param modelId 模型ID
     * @param trainingData 训练数据
     * @return 是否成功
     */
    boolean trainIrtModel(String modelId, List<Map<String, Object>> trainingData);

    /**
     * 创建题目参数
     * @param questionParam 题目参数信息
     * @return 参数ID
     */
    String createQuestionParam(QuestionParam questionParam);

    /**
     * 更新题目参数
     * @param questionParam 题目参数信息
     * @return 是否成功
     */
    boolean updateQuestionParam(QuestionParam questionParam);

    /**
     * 删除题目参数
     * @param paramId 参数ID
     * @return 是否成功
     */
    boolean deleteQuestionParam(String paramId);

    /**
     * 获取题目参数详情
     * @param paramId 参数ID
     * @return 题目参数详情
     */
    QuestionParam getQuestionParamByParamId(String paramId);

    /**
     * 根据题目ID获取题目参数
     * @param questionId 题目ID
     * @return 题目参数
     */
    QuestionParam getQuestionParamByQuestionId(String questionId);

    /**
     * 分页查询题目参数列表
     * @param modelId 模型ID
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @return 题目参数列表和总数
     */
    Map<String, Object> getQuestionParamList(String modelId, Integer status, int page, int size);

    /**
     * 标定题目参数
     * @param modelId 模型ID
     * @param questionIds 题目ID列表
     * @param responseData 响应数据
     * @return 标定成功的题目数量
     */
    int calibrateQuestionParams(String modelId, List<String> questionIds, List<Map<String, Object>> responseData);

    /**
     * 创建能力估计
     * @param abilityEstimation 能力估计信息
     * @return 估计ID
     */
    String createAbilityEstimation(AbilityEstimation abilityEstimation);

    /**
     * 更新能力估计
     * @param abilityEstimation 能力估计信息
     * @return 是否成功
     */
    boolean updateAbilityEstimation(AbilityEstimation abilityEstimation);

    /**
     * 删除能力估计
     * @param estimationId 估计ID
     * @return 是否成功
     */
    boolean deleteAbilityEstimation(String estimationId);

    /**
     * 获取能力估计详情
     * @param estimationId 估计ID
     * @return 能力估计详情
     */
    AbilityEstimation getAbilityEstimationByEstimationId(String estimationId);

    /**
     * 根据用户ID获取能力估计列表
     * @param userId 用户ID
     * @param testStatus 测试状态
     * @param page 页码
     * @param size 每页大小
     * @return 能力估计列表和总数
     */
    Map<String, Object> getAbilityEstimationListByUserId(Long userId, Integer testStatus, int page, int size);

    /**
     * 估计用户能力
     * @param estimationId 估计ID
     * @param responses 答题响应
     * @return 能力估计结果
     */
    Map<String, Object> estimateAbility(String estimationId, List<Map<String, Object>> responses);

    /**
     * 智能选题
     * @param modelId 模型ID
     * @param ability 能力值
     * @param answeredQuestionIds 已答题ID列表
     * @param count 选题数量
     * @return 题目ID列表
     */
    List<String> selectQuestions(String modelId, Double ability, List<String> answeredQuestionIds, int count);

    /**
     * 检查CAT测试是否应该终止
     * @param estimationId 估计ID
     * @param abilityStd 能力估计标准差
     * @param answeredCount 已答题数量
     * @return 是否应该终止
     */
    boolean shouldTerminateCAT(String estimationId, Double abilityStd, int answeredCount);

    /**
     * 获取IRT模型统计信息
     * @return 统计信息
     */
    Map<String, Object> getIrtStatistics();

    /**
     * 批量创建题目参数
     * @param questionParams 题目参数列表
     * @return 成功创建的数量
     */
    int batchCreateQuestionParams(List<QuestionParam> questionParams);

    /**
     * 批量标定题目参数
     * @param modelId 模型ID
     * @param questionParams 题目参数列表
     * @param responseData 响应数据
     * @return 标定成功的数量
     */
    int batchCalibrateQuestionParams(String modelId, List<QuestionParam> questionParams, List<Map<String, Object>> responseData);
}
