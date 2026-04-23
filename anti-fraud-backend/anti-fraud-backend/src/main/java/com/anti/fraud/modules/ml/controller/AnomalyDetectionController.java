package com.anti.fraud.modules.ml.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.ml.entity.AnomalyDetection;
import com.anti.fraud.modules.ml.mapper.AnomalyDetectionMapper;
import com.anti.fraud.modules.ml.service.IsolationForestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 异常检测服务控制器
 */
@RestController
@RequestMapping("/ml/anomaly")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "异常检测服务")
public class AnomalyDetectionController {

    private final IsolationForestService isolationForestService;
    private final AnomalyDetectionMapper anomalyDetectionMapper;

    @Operation(summary = "训练Isolation Forest模型")
    @PostMapping("/train")
    public Result<Void> trainModel(@ApiParam(value = "训练数据", required = true) @RequestBody List<Map<String, Object>> trainingData) {
        try {
            boolean success = isolationForestService.trainModel(trainingData);
            if (success) {
                return Result.successMsg("训练Isolation Forest模型成功");
            } else {
                return Result.fail("训练Isolation Forest模型失败");
            }
        } catch (Exception e) {
            log.error("训练Isolation Forest模型失败: {}", e.getMessage(), e);
            return Result.fail("训练Isolation Forest模型失败");
        }
    }

    @Operation(summary = "检测异常")
    @PostMapping("/detect")
    public Result<Map<String, Object>> detectAnomaly(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "用户名", required = true) @RequestParam String username,
            @ApiParam(value = "检测类型: 1-行为异常，2-设备异常，3-社交异常，4-综合异常", required = true) @RequestParam Integer detectionType,
            @ApiParam(value = "特征数据", required = true) @RequestBody Map<String, Object> featureData) {
        try {
            Map<String, Object> result = isolationForestService.detectAnomaly(userId, username, detectionType, featureData);
            return Result.success(result);
        } catch (Exception e) {
            log.error("检测异常失败: {}", e.getMessage(), e);
            return Result.fail("检测异常失败");
        }
    }

    @Operation(summary = "批量检测异常")
    @PostMapping("/batch-detect")
    public Result<Integer> batchDetectAnomaly(@ApiParam(value = "检测列表", required = true) @RequestBody List<Map<String, Object>> detectionList) {
        try {
            int count = isolationForestService.batchDetectAnomaly(detectionList);
            return Result.success(count);
        } catch (Exception e) {
            log.error("批量检测异常失败: {}", e.getMessage(), e);
            return Result.fail("批量检测异常失败");
        }
    }

    @Operation(summary = "获取模型信息")
    @GetMapping("/model-info")
    public Result<Map<String, Object>> getModelInfo() {
        try {
            Map<String, Object> modelInfo = isolationForestService.getModelInfo();
            return Result.success(modelInfo);
        } catch (Exception e) {
            log.error("获取模型信息失败: {}", e.getMessage(), e);
            return Result.fail("获取模型信息失败");
        }
    }

    @Operation(summary = "评估模型性能")
    @PostMapping("/evaluate")
    public Result<Map<String, Object>> evaluateModel(@ApiParam(value = "测试数据", required = true) @RequestBody List<Map<String, Object>> testData) {
        try {
            Map<String, Object> performance = isolationForestService.evaluateModel(testData);
            return Result.success(performance);
        } catch (Exception e) {
            log.error("评估模型性能失败: {}", e.getMessage(), e);
            return Result.fail("评估模型性能失败");
        }
    }

    @Operation(summary = "更新模型")
    @PostMapping("/update")
    public Result<Void> updateModel(@ApiParam(value = "训练数据", required = true) @RequestBody List<Map<String, Object>> trainingData) {
        try {
            boolean success = isolationForestService.updateModel(trainingData);
            if (success) {
                return Result.successMsg("更新模型成功");
            } else {
                return Result.fail("更新模型失败");
            }
        } catch (Exception e) {
            log.error("更新模型失败: {}", e.getMessage(), e);
            return Result.fail("更新模型失败");
        }
    }

    @Operation(summary = "预测异常分数")
    @PostMapping("/predict")
    public Result<Double> predictAnomalyScore(@ApiParam(value = "特征数据", required = true) @RequestBody Map<String, Object> featureData) {
        try {
            Double anomalyScore = isolationForestService.predictAnomalyScore(featureData);
            return Result.success(anomalyScore);
        } catch (Exception e) {
            log.error("预测异常分数失败: {}", e.getMessage(), e);
            return Result.fail("预测异常分数失败");
        }
    }

    @Operation(summary = "获取异常阈值")
    @GetMapping("/threshold")
    public Result<Double> getAnomalyThreshold() {
        try {
            Double threshold = isolationForestService.getAnomalyThreshold();
            return Result.success(threshold);
        } catch (Exception e) {
            log.error("获取异常阈值失败: {}", e.getMessage(), e);
            return Result.fail("获取异常阈值失败");
        }
    }

    @Operation(summary = "设置异常阈值")
    @PostMapping("/threshold")
    public Result<Void> setAnomalyThreshold(@ApiParam(value = "异常阈值", required = true) @RequestParam Double threshold) {
        try {
            isolationForestService.setAnomalyThreshold(threshold);
            return Result.successMsg("设置异常阈值成功");
        } catch (Exception e) {
            log.error("设置异常阈值失败: {}", e.getMessage(), e);
            return Result.fail("设置异常阈值失败");
        }
    }

    @Operation(summary = "获取异常检测详情")
    @GetMapping("/detail/{detectionId}")
    public Result<AnomalyDetection> getAnomalyDetectionByDetectionId(@ApiParam(value = "检测ID", required = true) @PathVariable String detectionId) {
        try {
            AnomalyDetection detection = anomalyDetectionMapper.selectByDetectionId(detectionId);
            if (detection != null) {
                return Result.success(detection);
            } else {
                return Result.fail("异常检测不存在");
            }
        } catch (Exception e) {
            log.error("获取异常检测详情失败: {}", e.getMessage(), e);
            return Result.fail("获取异常检测详情失败");
        }
    }

    @Operation(summary = "分页查询异常检测列表")
    @GetMapping("/list")
    public Result<Map<String, Object>> getAnomalyDetectionList(
            @ApiParam(value = "用户ID", required = false) @RequestParam(required = false) Long userId,
            @ApiParam(value = "检测类型: 1-行为异常，2-设备异常，3-社交异常，4-综合异常", required = false) @RequestParam(required = false) Integer detectionType,
            @ApiParam(value = "异常等级: 1-正常，2-轻微异常，3-中度异常，4-严重异常", required = false) @RequestParam(required = false) Integer anomalyLevel,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            int offset = (page - 1) * size;
            List<AnomalyDetection> detections = anomalyDetectionMapper.selectByUserId(userId, detectionType, anomalyLevel, offset, size);
            // 计算总数
            int total = anomalyDetectionMapper.countByUserId(userId, detectionType, anomalyLevel);

            Map<String, Object> result = new HashMap<>();
            result.put("list", detections);
            result.put("total", total);
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询异常检测列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return Result.fail("查询异常检测列表失败");
        }
    }

    @Operation(summary = "根据时间范围查询异常检测列表")
    @GetMapping("/list-by-time-range")
    public Result<List<AnomalyDetection>> getAnomalyDetectionListByTimeRange(
            @ApiParam(value = "开始时间", required = true) @RequestParam LocalDateTime startTime,
            @ApiParam(value = "结束时间", required = true) @RequestParam LocalDateTime endTime,
            @ApiParam(value = "用户ID", required = false) @RequestParam(required = false) Long userId,
            @ApiParam(value = "检测类型: 1-行为异常，2-设备异常，3-社交异常，4-综合异常", required = false) @RequestParam(required = false) Integer detectionType,
            @ApiParam(value = "异常等级: 1-正常，2-轻微异常，3-中度异常，4-严重异常", required = false) @RequestParam(required = false) Integer anomalyLevel) {
        try {
            List<AnomalyDetection> detections = anomalyDetectionMapper.selectByTimeRange(startTime, endTime, userId, detectionType, anomalyLevel);
            return Result.success(detections);
        } catch (Exception e) {
            log.error("根据时间范围查询异常检测列表失败: {}", e.getMessage(), e);
            return Result.fail("根据时间范围查询异常检测列表失败");
        }
    }
}
