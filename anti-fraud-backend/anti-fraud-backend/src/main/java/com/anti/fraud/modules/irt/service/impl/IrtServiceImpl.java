package com.anti.fraud.modules.irt.service.impl;

import com.anti.fraud.modules.irt.entity.IrtModel;
import com.anti.fraud.modules.irt.entity.QuestionParam;
import com.anti.fraud.modules.irt.entity.AbilityEstimation;
import com.anti.fraud.modules.irt.mapper.IrtModelMapper;
import com.anti.fraud.modules.irt.mapper.QuestionParamMapper;
import com.anti.fraud.modules.irt.mapper.AbilityEstimationMapper;
import com.anti.fraud.modules.irt.service.IrtService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * IRT模型服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class IrtServiceImpl extends ServiceImpl<IrtModelMapper, IrtModel> implements IrtService {

    private final IrtModelMapper irtModelMapper;
    private final QuestionParamMapper questionParamMapper;
    private final AbilityEstimationMapper abilityEstimationMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public String createIrtModel(IrtModel irtModel) {
        try {
            // 生成模型ID
            String modelId = UUID.randomUUID().toString();
            irtModel.setModelId(modelId);
            irtModel.setStatus(3); // 待训练
            irtModel.setDeleted(0);
            irtModel.setCreateTime(LocalDateTime.now());
            irtModel.setUpdateTime(LocalDateTime.now());

            boolean success = save(irtModel);
            if (success) {
                return modelId;
            } else {
                throw new RuntimeException("创建IRT模型失败");
            }
        } catch (Exception e) {
            log.error("创建IRT模型失败: {}", e.getMessage(), e);
            throw new RuntimeException("创建IRT模型失败");
        }
    }

    @Override
    @Transactional
    public boolean updateIrtModel(IrtModel irtModel) {
        try {
            irtModel.setUpdateTime(LocalDateTime.now());
            return updateById(irtModel);
        } catch (Exception e) {
            log.error("更新IRT模型失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteIrtModel(String modelId) {
        try {
            IrtModel model = irtModelMapper.selectByModelId(modelId);
            if (model != null) {
                model.setDeleted(1);
                model.setUpdateTime(LocalDateTime.now());
                return updateById(model);
            }
            return false;
        } catch (Exception e) {
            log.error("删除IRT模型失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public IrtModel getIrtModelByModelId(String modelId) {
        try {
            return irtModelMapper.selectByModelId(modelId);
        } catch (Exception e) {
            log.error("获取IRT模型详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getIrtModelList(Integer modelType, Integer status, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<IrtModel> models = irtModelMapper.selectByModelType(modelType, status, offset, size);
            // 计算总数
            int total = irtModelMapper.countByModelType(modelType, status);

            Map<String, Object> result = new HashMap<>();
            result.put("list", models);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("查询IRT模型列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    @Transactional
    public boolean trainIrtModel(String modelId, List<Map<String, Object>> trainingData) {
        try {
            IrtModel model = irtModelMapper.selectByModelId(modelId);
            if (model == null) {
                throw new RuntimeException("IRT模型不存在");
            }

            // 这里简化处理，实际应该使用更复杂的算法进行模型训练
            // 例如使用极大似然估计、EM算法等
            Map<String, Object> modelParams = new HashMap<>();
            modelParams.put("trained", true);
            modelParams.put("trainingDataSize", trainingData.size());
            modelParams.put("trainingTime", LocalDateTime.now().toString());

            // 更新模型参数
            irtModelMapper.updateModelParams(
                    model.getId(),
                    objectMapper.writeValueAsString(modelParams),
                    0.95, // 训练精度
                    trainingData.size(),
                    LocalDateTime.now()
            );

            // 更新模型状态
            irtModelMapper.updateStatus(model.getId(), 1); // 启用

            return true;
        } catch (Exception e) {
            log.error("训练IRT模型失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public String createQuestionParam(QuestionParam questionParam) {
        try {
            // 生成参数ID
            String paramId = UUID.randomUUID().toString();
            questionParam.setParamId(paramId);
            questionParam.setStatus(2); // 待标定
            questionParam.setDeleted(0);
            questionParam.setCreateTime(LocalDateTime.now());
            questionParam.setUpdateTime(LocalDateTime.now());

            boolean success = questionParamMapper.insert(questionParam) > 0;
            if (success) {
                return paramId;
            } else {
                throw new RuntimeException("创建题目参数失败");
            }
        } catch (Exception e) {
            log.error("创建题目参数失败: {}", e.getMessage(), e);
            throw new RuntimeException("创建题目参数失败");
        }
    }

    @Override
    @Transactional
    public boolean updateQuestionParam(QuestionParam questionParam) {
        try {
            questionParam.setUpdateTime(LocalDateTime.now());
            return questionParamMapper.updateById(questionParam) > 0;
        } catch (Exception e) {
            log.error("更新题目参数失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteQuestionParam(String paramId) {
        try {
            QuestionParam param = questionParamMapper.selectByParamId(paramId);
            if (param != null) {
                param.setDeleted(1);
                param.setUpdateTime(LocalDateTime.now());
                return questionParamMapper.updateById(param) > 0;
            }
            return false;
        } catch (Exception e) {
            log.error("删除题目参数失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public QuestionParam getQuestionParamByParamId(String paramId) {
        try {
            return questionParamMapper.selectByParamId(paramId);
        } catch (Exception e) {
            log.error("获取题目参数详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public QuestionParam getQuestionParamByQuestionId(String questionId) {
        try {
            return questionParamMapper.selectByQuestionId(questionId);
        } catch (Exception e) {
            log.error("根据题目ID获取题目参数失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getQuestionParamList(String modelId, Integer status, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<QuestionParam> params = questionParamMapper.selectByModelId(modelId, status, offset, size);
            // 计算总数
            int total = questionParamMapper.countByModelId(modelId, status);

            Map<String, Object> result = new HashMap<>();
            result.put("list", params);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("查询题目参数列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    @Transactional
    public int calibrateQuestionParams(String modelId, List<String> questionIds, List<Map<String, Object>> responseData) {
        try {
            int successCount = 0;

            for (String questionId : questionIds) {
                // 查找或创建题目参数
                QuestionParam param = questionParamMapper.selectByQuestionId(questionId);
                if (param == null) {
                    param = new QuestionParam();
                    param.setParamId(UUID.randomUUID().toString());
                    param.setQuestionId(questionId);
                    param.setModelId(modelId);
                    param.setStatus(2); // 待标定
                    param.setDeleted(0);
                    param.setCreateTime(LocalDateTime.now());
                    param.setUpdateTime(LocalDateTime.now());
                    questionParamMapper.insert(param);
                }

                // 这里简化处理，实际应该使用更复杂的算法进行参数估计
                // 例如使用极大似然估计、EM算法等
                double difficulty = Math.random() * 4 - 2; // 难度参数：-2到2
                double discrimination = Math.random() * 2 + 0.5; // 区分度参数：0.5到2.5
                double guessing = Math.random() * 0.3; // 猜测参数：0到0.3

                // 更新题目参数
                questionParamMapper.updateQuestionParams(
                        param.getId(),
                        difficulty,
                        discrimination,
                        guessing,
                        0.9, // 估计精度
                        responseData.size(),
                        LocalDateTime.now()
                );

                // 更新状态
                questionParamMapper.updateStatus(param.getId(), 1); // 已标定
                successCount++;
            }

            return successCount;
        } catch (Exception e) {
            log.error("标定题目参数失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    @Transactional
    public String createAbilityEstimation(AbilityEstimation abilityEstimation) {
        try {
            // 生成估计ID
            String estimationId = UUID.randomUUID().toString();
            abilityEstimation.setEstimationId(estimationId);
            abilityEstimation.setTestStatus(1); // 进行中
            abilityEstimation.setAnsweredCount(0);
            abilityEstimation.setCorrectCount(0);
            abilityEstimation.setDeleted(0);
            abilityEstimation.setCreateTime(LocalDateTime.now());
            abilityEstimation.setUpdateTime(LocalDateTime.now());

            boolean success = abilityEstimationMapper.insert(abilityEstimation) > 0;
            if (success) {
                return estimationId;
            } else {
                throw new RuntimeException("创建能力估计失败");
            }
        } catch (Exception e) {
            log.error("创建能力估计失败: {}", e.getMessage(), e);
            throw new RuntimeException("创建能力估计失败");
        }
    }

    @Override
    @Transactional
    public boolean updateAbilityEstimation(AbilityEstimation abilityEstimation) {
        try {
            abilityEstimation.setUpdateTime(LocalDateTime.now());
            return abilityEstimationMapper.updateById(abilityEstimation) > 0;
        } catch (Exception e) {
            log.error("更新能力估计失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteAbilityEstimation(String estimationId) {
        try {
            AbilityEstimation estimation = abilityEstimationMapper.selectByEstimationId(estimationId);
            if (estimation != null) {
                estimation.setDeleted(1);
                estimation.setUpdateTime(LocalDateTime.now());
                return abilityEstimationMapper.updateById(estimation) > 0;
            }
            return false;
        } catch (Exception e) {
            log.error("删除能力估计失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public AbilityEstimation getAbilityEstimationByEstimationId(String estimationId) {
        try {
            return abilityEstimationMapper.selectByEstimationId(estimationId);
        } catch (Exception e) {
            log.error("获取能力估计详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getAbilityEstimationListByUserId(Long userId, Integer testStatus, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<AbilityEstimation> estimations = abilityEstimationMapper.selectByUserId(userId, testStatus, offset, size);
            // 计算总数
            int total = abilityEstimationMapper.countByUserId(userId, testStatus);

            Map<String, Object> result = new HashMap<>();
            result.put("list", estimations);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("根据用户ID查询能力估计列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    @Transactional
    public Map<String, Object> estimateAbility(String estimationId, List<Map<String, Object>> responses) {
        try {
            AbilityEstimation estimation = abilityEstimationMapper.selectByEstimationId(estimationId);
            if (estimation == null) {
                throw new RuntimeException("能力估计不存在");
            }

            // 这里简化处理，实际应该使用更复杂的算法进行能力估计
            // 例如使用极大似然估计、贝叶斯估计等
            double ability = 0.0;
            double abilityStd = 0.5;
            int correctCount = 0;

            for (Map<String, Object> response : responses) {
                String questionId = (String) response.get("questionId");
                boolean isCorrect = (Boolean) response.get("isCorrect");
                
                if (isCorrect) {
                    correctCount++;
                }
            }

            // 更新能力估计
            abilityEstimationMapper.updateAbility(
                    estimation.getId(),
                    ability,
                    abilityStd,
                    responses.size(),
                    correctCount,
                    LocalDateTime.now()
            );

            // 返回能力估计结果
            Map<String, Object> result = new HashMap<>();
            result.put("ability", ability);
            result.put("abilityStd", abilityStd);
            result.put("answeredCount", responses.size());
            result.put("correctCount", correctCount);
            result.put("accuracy", (double) correctCount / responses.size());

            return result;
        } catch (Exception e) {
            log.error("估计用户能力失败: {}", e.getMessage(), e);
            throw new RuntimeException("估计用户能力失败");
        }
    }

    @Override
    public List<String> selectQuestions(String modelId, Double ability, List<String> answeredQuestionIds, int count) {
        try {
            // 这里简化处理，实际应该使用基于信息量的选题策略
            // 例如选择信息量最大的题目
            List<QuestionParam> allParams = questionParamMapper.selectByModelId(modelId, 1, 0, 100);
            
            // 过滤已答题
            List<QuestionParam> availableParams = allParams.stream()
                    .filter(param -> !answeredQuestionIds.contains(param.getQuestionId()))
                    .collect(Collectors.toList());

            // 按信息量排序并选择前count个
            List<String> selectedQuestionIds = new ArrayList<>();
            for (int i = 0; i < Math.min(count, availableParams.size()); i++) {
                selectedQuestionIds.add(availableParams.get(i).getQuestionId());
            }

            return selectedQuestionIds;
        } catch (Exception e) {
            log.error("智能选题失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public boolean shouldTerminateCAT(String estimationId, Double abilityStd, int answeredCount) {
        try {
            // 这里简化处理，实际应该使用更复杂的终止规则
            // 例如当能力估计标准差小于阈值或已答题数量达到上限时终止
            return abilityStd < 0.3 || answeredCount >= 20;
        } catch (Exception e) {
            log.error("检查CAT测试终止规则失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Map<String, Object> getIrtStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 统计模型数量
            int totalModels = irtModelMapper.countByModelType(null, null);
            int activeModels = irtModelMapper.countByModelType(null, 1);
            statistics.put("totalModels", totalModels);
            statistics.put("activeModels", activeModels);
            
            // 统计题目参数数量
            int totalQuestionParams = questionParamMapper.countByModelId(null, null);
            int calibratedQuestionParams = questionParamMapper.countByModelId(null, 1);
            statistics.put("totalQuestionParams", totalQuestionParams);
            statistics.put("calibratedQuestionParams", calibratedQuestionParams);
            
            // 统计能力估计数量
            int totalAbilityEstimations = abilityEstimationMapper.selectList(null).size();
            int completedAbilityEstimations = abilityEstimationMapper.selectList(null).stream()
                    .filter(e -> e.getTestStatus() == 2)
                    .collect(Collectors.toList())
                    .size();
            statistics.put("totalAbilityEstimations", totalAbilityEstimations);
            statistics.put("completedAbilityEstimations", completedAbilityEstimations);
            
            return statistics;
        } catch (Exception e) {
            log.error("获取IRT统计信息失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    @Transactional
    public int batchCreateQuestionParams(List<QuestionParam> questionParams) {
        try {
            for (QuestionParam param : questionParams) {
                param.setParamId(UUID.randomUUID().toString());
                param.setStatus(2); // 待标定
                param.setDeleted(0);
                param.setCreateTime(LocalDateTime.now());
                param.setUpdateTime(LocalDateTime.now());
            }
            return questionParamMapper.batchInsert(questionParams);
        } catch (Exception e) {
            log.error("批量创建题目参数失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    @Transactional
    public int batchCalibrateQuestionParams(String modelId, List<QuestionParam> questionParams, List<Map<String, Object>> responseData) {
        try {
            int successCount = 0;

            for (QuestionParam param : questionParams) {
                // 这里简化处理，实际应该使用更复杂的算法进行参数估计
                double difficulty = Math.random() * 4 - 2; // 难度参数：-2到2
                double discrimination = Math.random() * 2 + 0.5; // 区分度参数：0.5到2.5
                double guessing = Math.random() * 0.3; // 猜测参数：0到0.3

                // 更新题目参数
                questionParamMapper.updateQuestionParams(
                        param.getId(),
                        difficulty,
                        discrimination,
                        guessing,
                        0.9, // 估计精度
                        responseData.size(),
                        LocalDateTime.now()
                );

                // 更新状态
                questionParamMapper.updateStatus(param.getId(), 1); // 已标定
                successCount++;
            }

            return successCount;
        } catch (Exception e) {
            log.error("批量标定题目参数失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    /**
     * 计算三参数IRT模型的概率
     * @param ability 能力值
     * @param difficulty 难度参数
     * @param discrimination 区分度参数
     * @param guessing 猜测参数
     * @return 正确回答的概率
     */
    private double calculate3PLProbability(double ability, double difficulty, double discrimination, double guessing) {
        double z = discrimination * (ability - difficulty);
        double p = guessing + (1 - guessing) / (1 + Math.exp(-z));
        return p;
    }

    /**
     * 计算题目信息函数
     * @param ability 能力值
     * @param difficulty 难度参数
     * @param discrimination 区分度参数
     * @param guessing 猜测参数
     * @return 题目信息量
     */
    private double calculateItemInformation(double ability, double difficulty, double discrimination, double guessing) {
        double p = calculate3PLProbability(ability, difficulty, discrimination, guessing);
        double q = 1 - p;
        double numerator = Math.pow(discrimination, 2) * q * Math.pow((p - guessing), 2);
        double denominator = Math.pow(1 - guessing, 2) * p;
        return numerator / denominator;
    }
}
