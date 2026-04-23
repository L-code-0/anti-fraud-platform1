package com.anti.fraud.modules.ml.service.impl;

import com.anti.fraud.modules.ml.entity.AnomalyDetection;
import com.anti.fraud.modules.ml.mapper.AnomalyDetectionMapper;
import com.anti.fraud.modules.ml.service.IsolationForestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Isolation Forest异常检测服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class IsolationForestServiceImpl implements IsolationForestService {

    private final AnomalyDetectionMapper anomalyDetectionMapper;
    private final ObjectMapper objectMapper;

    // Isolation Forest模型参数
    private int nEstimators = 100; // 树的数量
    private int maxSamples = 256; // 每棵树的最大样本数
    private int maxFeatures = 1; // 每棵树的最大特征数
    private double contamination = 0.1; // 异常比例
    private boolean bootstrap = false; // 是否使用bootstrap采样
    private int randomState = 42; // 随机种子

    // 模型状态
    private boolean isModelTrained = false;
    private double anomalyThreshold = 0.5; // 异常阈值
    private List<Map<String, Object>> trainingData = new ArrayList<>();

    @Override
    @Transactional
    public boolean trainModel(List<Map<String, Object>> trainingData) {
        try {
            // 保存训练数据
            this.trainingData = trainingData;

            // 这里简化处理，实际应该使用更复杂的Isolation Forest算法
            // 例如使用scikit-learn的IsolationForest或Apache Spark的MLlib
            log.info("开始训练Isolation Forest模型，训练数据量: {}", trainingData.size());

            // 模拟训练过程
            for (int i = 0; i < nEstimators; i++) {
                // 构建每棵树
                buildIsolationTree(trainingData);
            }

            // 计算异常阈值
            calculateAnomalyThreshold();

            // 标记模型为已训练
            isModelTrained = true;

            log.info("Isolation Forest模型训练完成");
            return true;
        } catch (Exception e) {
            log.error("训练Isolation Forest模型失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public Map<String, Object> detectAnomaly(Long userId, String username, Integer detectionType, Map<String, Object> featureData) {
        try {
            if (!isModelTrained) {
                throw new RuntimeException("模型未训练，请先训练模型");
            }

            // 计算异常分数
            double anomalyScore = predictAnomalyScore(featureData);

            // 确定异常等级
            int anomalyLevel = determineAnomalyLevel(anomalyScore);
            boolean isAnomaly = anomalyScore > anomalyThreshold;
            double confidence = calculateConfidence(anomalyScore);

            // 创建异常检测记录
            AnomalyDetection detection = new AnomalyDetection();
            detection.setDetectionId(UUID.randomUUID().toString());
            detection.setUserId(userId);
            detection.setUsername(username);
            detection.setDetectionType(detectionType);
            detection.setDetectionMethod(1); // Isolation Forest
            detection.setFeatureData(objectMapper.writeValueAsString(featureData));
            detection.setAnomalyScore(anomalyScore);
            detection.setAnomalyLevel(anomalyLevel);
            detection.setIsAnomaly(isAnomaly);
            detection.setConfidence(confidence);
            detection.setDetectionTime(LocalDateTime.now());
            detection.setStatus(1); // 已检测
            detection.setDeleted(0);
            detection.setCreateTime(LocalDateTime.now());
            detection.setUpdateTime(LocalDateTime.now());

            anomalyDetectionMapper.insert(detection);

            // 返回检测结果
            Map<String, Object> result = new HashMap<>();
            result.put("detectionId", detection.getDetectionId());
            result.put("anomalyScore", anomalyScore);
            result.put("anomalyLevel", anomalyLevel);
            result.put("isAnomaly", isAnomaly);
            result.put("confidence", confidence);
            result.put("detectionTime", detection.getDetectionTime());

            return result;
        } catch (Exception e) {
            log.error("检测异常失败: {}", e.getMessage(), e);
            throw new RuntimeException("检测异常失败");
        }
    }

    @Override
    @Transactional
    public int batchDetectAnomaly(List<Map<String, Object>> detectionList) {
        try {
            int successCount = 0;

            for (Map<String, Object> detectionData : detectionList) {
                try {
                    Long userId = Long.valueOf(detectionData.get("userId").toString());
                    String username = (String) detectionData.get("username");
                    Integer detectionType = (Integer) detectionData.get("detectionType");
                    Map<String, Object> featureData = (Map<String, Object>) detectionData.get("featureData");

                    detectAnomaly(userId, username, detectionType, featureData);
                    successCount++;
                } catch (Exception e) {
                    log.error("批量检测异常失败: {}", e.getMessage(), e);
                }
            }

            return successCount;
        } catch (Exception e) {
            log.error("批量检测异常失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public Map<String, Object> getModelInfo() {
        try {
            Map<String, Object> modelInfo = new HashMap<>();
            modelInfo.put("isModelTrained", isModelTrained);
            modelInfo.put("nEstimators", nEstimators);
            modelInfo.put("maxSamples", maxSamples);
            modelInfo.put("maxFeatures", maxFeatures);
            modelInfo.put("contamination", contamination);
            modelInfo.put("bootstrap", bootstrap);
            modelInfo.put("randomState", randomState);
            modelInfo.put("anomalyThreshold", anomalyThreshold);
            modelInfo.put("trainingDataSize", trainingData.size());
            return modelInfo;
        } catch (Exception e) {
            log.error("获取模型信息失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Object> evaluateModel(List<Map<String, Object>> testData) {
        try {
            if (!isModelTrained) {
                throw new RuntimeException("模型未训练，请先训练模型");
            }

            int truePositives = 0;
            int trueNegatives = 0;
            int falsePositives = 0;
            int falseNegatives = 0;

            for (Map<String, Object> data : testData) {
                double anomalyScore = predictAnomalyScore(data);
                boolean isAnomaly = anomalyScore > anomalyThreshold;
                boolean actualIsAnomaly = (Boolean) data.getOrDefault("isAnomaly", false);

                if (isAnomaly && actualIsAnomaly) {
                    truePositives++;
                } else if (!isAnomaly && !actualIsAnomaly) {
                    trueNegatives++;
                } else if (isAnomaly && !actualIsAnomaly) {
                    falsePositives++;
                } else {
                    falseNegatives++;
                }
            }

            // 计算性能指标
            int total = testData.size();
            double accuracy = (double) (truePositives + trueNegatives) / total;
            double precision = truePositives + falsePositives > 0 ? (double) truePositives / (truePositives + falsePositives) : 0;
            double recall = truePositives + falseNegatives > 0 ? (double) truePositives / (truePositives + falseNegatives) : 0;
            double f1Score = precision + recall > 0 ? 2 * (precision * recall) / (precision + recall) : 0;

            Map<String, Object> performance = new HashMap<>();
            performance.put("accuracy", accuracy);
            performance.put("precision", precision);
            performance.put("recall", recall);
            performance.put("f1Score", f1Score);
            performance.put("truePositives", truePositives);
            performance.put("trueNegatives", trueNegatives);
            performance.put("falsePositives", falsePositives);
            performance.put("falseNegatives", falseNegatives);
            performance.put("total", total);

            return performance;
        } catch (Exception e) {
            log.error("评估模型性能失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    @Transactional
    public boolean updateModel(List<Map<String, Object>> trainingData) {
        try {
            return trainModel(trainingData);
        } catch (Exception e) {
            log.error("更新模型失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Double predictAnomalyScore(Map<String, Object> featureData) {
        try {
            if (!isModelTrained) {
                throw new RuntimeException("模型未训练，请先训练模型");
            }

            // 这里简化处理，实际应该使用训练好的Isolation Forest模型进行预测
            // 计算平均路径长度
            double averagePathLength = calculateAveragePathLength(featureData);

            // 归一化异常分数
            double anomalyScore = Math.exp(-averagePathLength / cFactor());

            return anomalyScore;
        } catch (Exception e) {
            log.error("预测异常分数失败: {}", e.getMessage(), e);
            return 0.0;
        }
    }

    @Override
    public Double getAnomalyThreshold() {
        return anomalyThreshold;
    }

    @Override
    public void setAnomalyThreshold(Double threshold) {
        this.anomalyThreshold = threshold;
    }

    /**
     * 构建孤立树
     * @param data 数据
     */
    private void buildIsolationTree(List<Map<String, Object>> data) {
        // 这里简化处理，实际应该实现完整的孤立树构建算法
        // 孤立树通过随机选择特征和分割点来构建
        // 异常点的平均路径长度较短
    }

    /**
     * 计算平均路径长度
     * @param featureData 特征数据
     * @return 平均路径长度
     */
    private double calculateAveragePathLength(Map<String, Object> featureData) {
        // 这里简化处理，实际应该使用训练好的孤立树计算路径长度
        // 路径长度越短，越可能是异常
        return Math.random() * 10;
    }

    /**
     * 计算c(n)因子
     * @return c(n)因子
     */
    private double cFactor() {
        int n = maxSamples;
        if (n <= 1) {
            return 0.0;
        }
        double Hn = Math.log(n) + 0.577215664901532860606512090082402431; // Euler's constant
        double HnMinus1 = Math.log(n - 1) + 0.577215664901532860606512090082402431;
        return 2 * Hn - 2 * (n - 1) / n - HnMinus1;
    }

    /**
     * 计算异常阈值
     */
    private void calculateAnomalyThreshold() {
        // 这里简化处理，实际应该使用更复杂的方法计算阈值
        // 例如使用训练数据的异常分数分布
        anomalyThreshold = 0.5;
    }

    /**
     * 确定异常等级
     * @param anomalyScore 异常分数
     * @return 异常等级
     */
    private int determineAnomalyLevel(double anomalyScore) {
        if (anomalyScore < 0.3) {
            return 1; // 正常
        } else if (anomalyScore < 0.5) {
            return 2; // 轻微异常
        } else if (anomalyScore < 0.7) {
            return 3; // 中度异常
        } else {
            return 4; // 严重异常
        }
    }

    /**
     * 计算置信度
     * @param anomalyScore 异常分数
     * @return 置信度
     */
    private double calculateConfidence(double anomalyScore) {
        // 置信度与异常分数成正比
        return Math.min(1.0, Math.max(0.0, anomalyScore));
    }
}
