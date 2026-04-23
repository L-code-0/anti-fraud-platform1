package com.anti.fraud.modules.ml.service;

import com.anti.fraud.modules.ml.entity.AnomalyDetection;
import java.util.List;
import java.util.Map;

/**
 * Isolation Forest异常检测服务接口
 */
public interface IsolationForestService {

    /**
     * 训练Isolation Forest模型
     * @param trainingData 训练数据
     * @return 是否成功
     */
    boolean trainModel(List<Map<String, Object>> trainingData);

    /**
     * 检测异常
     * @param userId 用户ID
     * @param username 用户名
     * @param detectionType 检测类型
     * @param featureData 特征数据
     * @return 异常检测结果
     */
    Map<String, Object> detectAnomaly(Long userId, String username, Integer detectionType, Map<String, Object> featureData);

    /**
     * 批量检测异常
     * @param detectionList 检测列表
     * @return 检测成功的数量
     */
    int batchDetectAnomaly(List<Map<String, Object>> detectionList);

    /**
     * 获取模型信息
     * @return 模型信息
     */
    Map<String, Object> getModelInfo();

    /**
     * 评估模型性能
     * @param testData 测试数据
     * @return 模型性能指标
     */
    Map<String, Object> evaluateModel(List<Map<String, Object>> testData);

    /**
     * 更新模型
     * @param trainingData 训练数据
     * @return 是否成功
     */
    boolean updateModel(List<Map<String, Object>> trainingData);

    /**
     * 预测异常分数
     * @param featureData 特征数据
     * @return 异常分数
     */
    Double predictAnomalyScore(Map<String, Object> featureData);

    /**
     * 获取异常阈值
     * @return 异常阈值
     */
    Double getAnomalyThreshold();

    /**
     * 设置异常阈值
     * @param threshold 异常阈值
     */
    void setAnomalyThreshold(Double threshold);
}
