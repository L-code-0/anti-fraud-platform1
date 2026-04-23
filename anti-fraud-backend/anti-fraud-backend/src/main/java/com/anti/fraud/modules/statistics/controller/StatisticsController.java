package com.anti.fraud.modules.statistics.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.statistics.entity.StatisticsData;
import com.anti.fraud.modules.statistics.service.StatisticsService;
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
 * 统计数据服务控制器
 */
@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "统计数据服务")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @Operation(summary = "创建统计数据")
    @PostMapping("/create")
    public Result<String> createStatisticsData(@ApiParam(value = "统计数据信息", required = true) @RequestBody StatisticsData statisticsData) {
        try {
            String statisticsId = statisticsService.createStatisticsData(statisticsData);
            return Result.success(statisticsId);
        } catch (Exception e) {
            log.error("创建统计数据失败: {}", e.getMessage(), e);
            return Result.fail("创建统计数据失败");
        }
    }

    @Operation(summary = "更新统计数据")
    @PutMapping("/update")
    public Result<Void> updateStatisticsData(@ApiParam(value = "统计数据信息", required = true) @RequestBody StatisticsData statisticsData) {
        try {
            boolean success = statisticsService.updateStatisticsData(statisticsData);
            if (success) {
                return Result.successMsg("更新统计数据成功");
            } else {
                return Result.fail("更新统计数据失败");
            }
        } catch (Exception e) {
            log.error("更新统计数据失败: {}", e.getMessage(), e);
            return Result.fail("更新统计数据失败");
        }
    }

    @Operation(summary = "删除统计数据")
    @DeleteMapping("/delete/{statisticsId}")
    public Result<Void> deleteStatisticsData(@ApiParam(value = "统计ID", required = true) @PathVariable String statisticsId) {
        try {
            boolean success = statisticsService.deleteStatisticsData(statisticsId);
            if (success) {
                return Result.successMsg("删除统计数据成功");
            } else {
                return Result.fail("删除统计数据失败");
            }
        } catch (Exception e) {
            log.error("删除统计数据失败: {}", e.getMessage(), e);
            return Result.fail("删除统计数据失败");
        }
    }

    @Operation(summary = "获取统计数据详情")
    @GetMapping("/detail/{statisticsId}")
    public Result<StatisticsData> getStatisticsDataByStatisticsId(@ApiParam(value = "统计ID", required = true) @PathVariable String statisticsId) {
        try {
            StatisticsData data = statisticsService.getStatisticsDataByStatisticsId(statisticsId);
            if (data != null) {
                return Result.success(data);
            } else {
                return Result.fail("统计数据不存在");
            }
        } catch (Exception e) {
            log.error("获取统计数据详情失败: {}", e.getMessage(), e);
            return Result.fail("获取统计数据详情失败");
        }
    }

    @Operation(summary = "分页查询统计数据列表")
    @GetMapping("/list")
    public Result<Map<String, Object>> getStatisticsDataList(
            @ApiParam(value = "统计类型: 1-用户活跃度，2-演练参与度，3-诈骗类型分布，4-知识掌握度，5-其他", required = false) @RequestParam(required = false) Integer type,
            @ApiParam(value = "统计维度: 1-日，2-周，3-月，4-年，5-自定义", required = false) @RequestParam(required = false) Integer dimension,
            @ApiParam(value = "状态: 1-已统计，2-统计中，3-统计失败", required = false) @RequestParam(required = false) Integer status,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = statisticsService.getStatisticsDataList(type, dimension, status, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询统计数据列表失败: {}", e.getMessage(), e);
            return Result.fail("查询统计数据列表失败");
        }
    }

    @Operation(summary = "根据时间范围查询统计数据")
    @GetMapping("/by-time-range")
    public Result<List<StatisticsData>> getStatisticsDataByTimeRange(
            @ApiParam(value = "开始时间", required = true) @RequestParam LocalDateTime startTime,
            @ApiParam(value = "结束时间", required = true) @RequestParam LocalDateTime endTime,
            @ApiParam(value = "统计类型: 1-用户活跃度，2-演练参与度，3-诈骗类型分布，4-知识掌握度，5-其他", required = false) @RequestParam(required = false) Integer type,
            @ApiParam(value = "状态: 1-已统计，2-统计中，3-统计失败", required = false) @RequestParam(required = false) Integer status) {
        try {
            List<StatisticsData> dataList = statisticsService.getStatisticsDataByTimeRange(startTime, endTime, type, status);
            return Result.success(dataList);
        } catch (Exception e) {
            log.error("根据时间范围查询统计数据失败: {}", e.getMessage(), e);
            return Result.fail("根据时间范围查询统计数据失败");
        }
    }

    @Operation(summary = "更新统计状态")
    @PutMapping("/update-status/{statisticsId}")
    public Result<Void> updateStatisticsDataStatus(
            @ApiParam(value = "统计ID", required = true) @PathVariable String statisticsId,
            @ApiParam(value = "状态: 1-已统计，2-统计中，3-统计失败", required = true) @RequestParam Integer status) {
        try {
            boolean success = statisticsService.updateStatisticsDataStatus(statisticsId, status);
            if (success) {
                return Result.successMsg("更新统计状态成功");
            } else {
                return Result.fail("更新统计状态失败");
            }
        } catch (Exception e) {
            log.error("更新统计状态失败: {}", e.getMessage(), e);
            return Result.fail("更新统计状态失败");
        }
    }

    @Operation(summary = "更新统计结果")
    @PutMapping("/update-result/{statisticsId}")
    public Result<Void> updateStatisticsDataResult(
            @ApiParam(value = "统计ID", required = true) @PathVariable String statisticsId,
            @ApiParam(value = "统计值", required = true) @RequestParam Double value,
            @ApiParam(value = "统计数据（JSON格式）", required = true) @RequestParam String data) {
        try {
            boolean success = statisticsService.updateStatisticsDataResult(statisticsId, value, data);
            if (success) {
                return Result.successMsg("更新统计结果成功");
            } else {
                return Result.fail("更新统计结果失败");
            }
        } catch (Exception e) {
            log.error("更新统计结果失败: {}", e.getMessage(), e);
            return Result.fail("更新统计结果失败");
        }
    }

    @Operation(summary = "统计统计数据数量")
    @GetMapping("/count")
    public Result<Integer> countStatisticsData(
            @ApiParam(value = "统计类型: 1-用户活跃度，2-演练参与度，3-诈骗类型分布，4-知识掌握度，5-其他", required = false) @RequestParam(required = false) Integer type,
            @ApiParam(value = "统计维度: 1-日，2-周，3-月，4-年，5-自定义", required = false) @RequestParam(required = false) Integer dimension,
            @ApiParam(value = "状态: 1-已统计，2-统计中，3-统计失败", required = false) @RequestParam(required = false) Integer status) {
        try {
            Integer count = statisticsService.countStatisticsData(type, dimension, status);
            return Result.success(count);
        } catch (Exception e) {
            log.error("统计统计数据数量失败: {}", e.getMessage(), e);
            return Result.fail("统计统计数据数量失败");
        }
    }

    @Operation(summary = "计算统计值总和")
    @GetMapping("/total-value")
    public Result<Double> calculateTotalValue(
            @ApiParam(value = "统计类型: 1-用户活跃度，2-演练参与度，3-诈骗类型分布，4-知识掌握度，5-其他", required = false) @RequestParam(required = false) Integer type,
            @ApiParam(value = "统计维度: 1-日，2-周，3-月，4-年，5-自定义", required = false) @RequestParam(required = false) Integer dimension,
            @ApiParam(value = "状态: 1-已统计，2-统计中，3-统计失败", required = false) @RequestParam(required = false) Integer status) {
        try {
            Double totalValue = statisticsService.calculateTotalValue(type, dimension, status);
            return Result.success(totalValue);
        } catch (Exception e) {
            log.error("计算统计值总和失败: {}", e.getMessage(), e);
            return Result.fail("计算统计值总和失败");
        }
    }

    @Operation(summary = "计算统计值平均值")
    @GetMapping("/average-value")
    public Result<Double> calculateAverageValue(
            @ApiParam(value = "统计类型: 1-用户活跃度，2-演练参与度，3-诈骗类型分布，4-知识掌握度，5-其他", required = false) @RequestParam(required = false) Integer type,
            @ApiParam(value = "统计维度: 1-日，2-周，3-月，4-年，5-自定义", required = false) @RequestParam(required = false) Integer dimension,
            @ApiParam(value = "状态: 1-已统计，2-统计中，3-统计失败", required = false) @RequestParam(required = false) Integer status) {
        try {
            Double averageValue = statisticsService.calculateAverageValue(type, dimension, status);
            return Result.success(averageValue);
        } catch (Exception e) {
            log.error("计算统计值平均值失败: {}", e.getMessage(), e);
            return Result.fail("计算统计值平均值失败");
        }
    }

    @Operation(summary = "获取最近的统计数据")
    @GetMapping("/recent")
    public Result<List<StatisticsData>> getRecentStatisticsData(
            @ApiParam(value = "统计类型: 1-用户活跃度，2-演练参与度，3-诈骗类型分布，4-知识掌握度，5-其他", required = false) @RequestParam(required = false) Integer type,
            @ApiParam(value = "统计维度: 1-日，2-周，3-月，4-年，5-自定义", required = false) @RequestParam(required = false) Integer dimension,
            @ApiParam(value = "数量限制", required = true) @RequestParam Integer limit,
            @ApiParam(value = "状态: 1-已统计，2-统计中，3-统计失败", required = false) @RequestParam(required = false) Integer status) {
        try {
            List<StatisticsData> dataList = statisticsService.getRecentStatisticsData(type, dimension, limit, status);
            return Result.success(dataList);
        } catch (Exception e) {
            log.error("获取最近的统计数据失败: {}", e.getMessage(), e);
            return Result.fail("获取最近的统计数据失败");
        }
    }

    @Operation(summary = "批量创建统计数据")
    @PostMapping("/batch-create")
    public Result<Integer> batchCreateStatisticsData(@ApiParam(value = "统计数据列表", required = true) @RequestBody List<StatisticsData> statisticsDataList) {
        try {
            int count = statisticsService.batchCreateStatisticsData(statisticsDataList);
            return Result.success(count);
        } catch (Exception e) {
            log.error("批量创建统计数据失败: {}", e.getMessage(), e);
            return Result.fail("批量创建统计数据失败");
        }
    }

    @Operation(summary = "获取统计数据统计信息")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatisticsDataStatistics() {
        try {
            Map<String, Object> statistics = statisticsService.getStatisticsDataStatistics();
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取统计数据统计信息失败: {}", e.getMessage(), e);
            return Result.fail("获取统计数据统计信息失败");
        }
    }

    @Operation(summary = "执行统计任务")
    @PostMapping("/execute-task")
    public Result<String> executeStatisticsTask(
            @ApiParam(value = "统计类型: 1-用户活跃度，2-演练参与度，3-诈骗类型分布，4-知识掌握度，5-其他", required = true) @RequestParam Integer type,
            @ApiParam(value = "统计维度: 1-日，2-周，3-月，4-年，5-自定义", required = true) @RequestParam Integer dimension,
            @ApiParam(value = "统计周期开始时间", required = true) @RequestParam LocalDateTime periodStartTime,
            @ApiParam(value = "统计周期结束时间", required = true) @RequestParam LocalDateTime periodEndTime) {
        try {
            String statisticsId = statisticsService.executeStatisticsTask(type, dimension, periodStartTime, periodEndTime);
            return Result.success(statisticsId);
        } catch (Exception e) {
            log.error("执行统计任务失败: {}", e.getMessage(), e);
            return Result.fail("执行统计任务失败");
        }
    }

    @Operation(summary = "统计用户活跃度")
    @PostMapping("/user-activity")
    public Result<StatisticsData> statisticsUserActivity(
            @ApiParam(value = "统计周期开始时间", required = true) @RequestParam LocalDateTime periodStartTime,
            @ApiParam(value = "统计周期结束时间", required = true) @RequestParam LocalDateTime periodEndTime) {
        try {
            StatisticsData data = statisticsService.statisticsUserActivity(periodStartTime, periodEndTime);
            if (data != null) {
                return Result.success(data);
            } else {
                return Result.fail("统计用户活跃度失败");
            }
        } catch (Exception e) {
            log.error("统计用户活跃度失败: {}", e.getMessage(), e);
            return Result.fail("统计用户活跃度失败");
        }
    }

    @Operation(summary = "统计演练参与度")
    @PostMapping("/exercise-participation")
    public Result<StatisticsData> statisticsExerciseParticipation(
            @ApiParam(value = "统计周期开始时间", required = true) @RequestParam LocalDateTime periodStartTime,
            @ApiParam(value = "统计周期结束时间", required = true) @RequestParam LocalDateTime periodEndTime) {
        try {
            StatisticsData data = statisticsService.statisticsExerciseParticipation(periodStartTime, periodEndTime);
            if (data != null) {
                return Result.success(data);
            } else {
                return Result.fail("统计演练参与度失败");
            }
        } catch (Exception e) {
            log.error("统计演练参与度失败: {}", e.getMessage(), e);
            return Result.fail("统计演练参与度失败");
        }
    }

    @Operation(summary = "统计诈骗类型分布")
    @PostMapping("/fraud-type-distribution")
    public Result<StatisticsData> statisticsFraudTypeDistribution(
            @ApiParam(value = "统计周期开始时间", required = true) @RequestParam LocalDateTime periodStartTime,
            @ApiParam(value = "统计周期结束时间", required = true) @RequestParam LocalDateTime periodEndTime) {
        try {
            StatisticsData data = statisticsService.statisticsFraudTypeDistribution(periodStartTime, periodEndTime);
            if (data != null) {
                return Result.success(data);
            } else {
                return Result.fail("统计诈骗类型分布失败");
            }
        } catch (Exception e) {
            log.error("统计诈骗类型分布失败: {}", e.getMessage(), e);
            return Result.fail("统计诈骗类型分布失败");
        }
    }

    @Operation(summary = "统计知识掌握度")
    @PostMapping("/knowledge-mastery")
    public Result<StatisticsData> statisticsKnowledgeMastery(
            @ApiParam(value = "统计周期开始时间", required = true) @RequestParam LocalDateTime periodStartTime,
            @ApiParam(value = "统计周期结束时间", required = true) @RequestParam LocalDateTime periodEndTime) {
        try {
            StatisticsData data = statisticsService.statisticsKnowledgeMastery(periodStartTime, periodEndTime);
            if (data != null) {
                return Result.success(data);
            } else {
                return Result.fail("统计知识掌握度失败");
            }
        } catch (Exception e) {
            log.error("统计知识掌握度失败: {}", e.getMessage(), e);
            return Result.fail("统计知识掌握度失败");
        }
    }
}
