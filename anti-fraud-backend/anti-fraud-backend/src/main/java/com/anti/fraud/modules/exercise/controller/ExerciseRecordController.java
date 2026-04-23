package com.anti.fraud.modules.exercise.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.exercise.entity.ExerciseRecord;
import com.anti.fraud.modules.exercise.service.ExerciseRecordService;
import com.anti.fraud.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 演练记录控制器
 */
@RestController
@RequestMapping("/exercise")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "演练记录管理")
public class ExerciseRecordController {

    private final ExerciseRecordService exerciseRecordService;

    @Operation(summary = "新增演练记录")
    @PostMapping("/add")
    public Result<Void> addExerciseRecord(@ApiParam(value = "演练记录信息", required = true) @RequestBody ExerciseRecord exerciseRecord) {
        try {
            boolean success = exerciseRecordService.addExerciseRecord(exerciseRecord);
            if (success) {
                return Result.successMsg("新增演练记录成功");
            } else {
                return Result.fail("新增演练记录失败");
            }
        } catch (Exception e) {
            log.error("新增演练记录失败: {}", e.getMessage(), e);
            return Result.fail("新增演练记录失败");
        }
    }

    @Operation(summary = "更新演练记录")
    @PutMapping("/update")
    public Result<Void> updateExerciseRecord(@ApiParam(value = "演练记录信息", required = true) @RequestBody ExerciseRecord exerciseRecord) {
        try {
            boolean success = exerciseRecordService.updateExerciseRecord(exerciseRecord);
            if (success) {
                return Result.successMsg("更新演练记录成功");
            } else {
                return Result.fail("更新演练记录失败");
            }
        } catch (Exception e) {
            log.error("更新演练记录失败: {}", e.getMessage(), e);
            return Result.fail("更新演练记录失败");
        }
    }

    @Operation(summary = "删除演练记录")
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteExerciseRecord(@ApiParam(value = "演练记录ID", required = true) @PathVariable Long id) {
        try {
            boolean success = exerciseRecordService.deleteExerciseRecord(id);
            if (success) {
                return Result.successMsg("删除演练记录成功");
            } else {
                return Result.fail("删除演练记录失败");
            }
        } catch (Exception e) {
            log.error("删除演练记录失败: {}", e.getMessage(), e);
            return Result.fail("删除演练记录失败");
        }
    }

    @Operation(summary = "获取演练记录详情")
    @GetMapping("/detail/{id}")
    public Result<ExerciseRecord> getExerciseRecordById(@ApiParam(value = "演练记录ID", required = true) @PathVariable Long id) {
        try {
            ExerciseRecord exerciseRecord = exerciseRecordService.getExerciseRecordById(id);
            if (exerciseRecord != null) {
                return Result.success(exerciseRecord);
            } else {
                return Result.fail("演练记录不存在");
            }
        } catch (Exception e) {
            log.error("获取演练记录详情失败: {}", e.getMessage(), e);
            return Result.fail("获取演练记录详情失败");
        }
    }

    @Operation(summary = "分页查询演练记录")
    @PostMapping("/list")
    public Result<Map<String, Object>> getExerciseRecordList(
            @ApiParam(value = "查询参数", required = false) @RequestBody(required = false) Map<String, Object> params,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            if (params == null) {
                params = new java.util.HashMap<>();
            }
            Map<String, Object> result = exerciseRecordService.getExerciseRecordList(params, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询演练记录列表失败: {}", e.getMessage(), e);
            return Result.fail("查询演练记录列表失败");
        }
    }

    @Operation(summary = "根据用户ID查询演练记录")
    @GetMapping("/by-user")
    public Result<Map<String, Object>> getExerciseRecordsByUserId(
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.fail("请先登录");
            }
            Map<String, Object> result = exerciseRecordService.getExerciseRecordsByUserId(userId, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("根据用户ID查询演练记录失败: {}", e.getMessage(), e);
            return Result.fail("根据用户ID查询演练记录失败");
        }
    }

    @Operation(summary = "获取用户演练统计信息")
    @GetMapping("/user-stats")
    public Result<Map<String, Object>> getUserExerciseStats() {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.fail("请先登录");
            }
            Map<String, Object> stats = exerciseRecordService.getUserExerciseStats(userId);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取用户演练统计信息失败: {}", e.getMessage(), e);
            return Result.fail("获取用户演练统计信息失败");
        }
    }

    @Operation(summary = "获取用户最近的演练记录")
    @GetMapping("/latest")
    public Result<List<ExerciseRecord>> getLatestExerciseRecordsByUserId(
            @ApiParam(value = "数量限制", required = true) @RequestParam Integer limit) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.fail("请先登录");
            }
            List<ExerciseRecord> exerciseRecords = exerciseRecordService.getLatestExerciseRecordsByUserId(userId, limit);
            return Result.success(exerciseRecords);
        } catch (Exception e) {
            log.error("获取用户最近的演练记录失败: {}", e.getMessage(), e);
            return Result.fail("获取用户最近的演练记录失败");
        }
    }

    @Operation(summary = "获取用户最佳成绩")
    @GetMapping("/best-score")
    public Result<ExerciseRecord> getBestScoreByUserId(
            @ApiParam(value = "演练类型", required = true) @RequestParam Integer exerciseType) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.fail("请先登录");
            }
            ExerciseRecord exerciseRecord = exerciseRecordService.getBestScoreByUserId(userId, exerciseType);
            return Result.success(exerciseRecord);
        } catch (Exception e) {
            log.error("获取用户最佳成绩失败: {}", e.getMessage(), e);
            return Result.fail("获取用户最佳成绩失败");
        }
    }

    @Operation(summary = "统计演练记录信息")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getExerciseRecordStats() {
        try {
            Map<String, Object> stats = exerciseRecordService.getExerciseRecordStats();
            return Result.success(stats);
        } catch (Exception e) {
            log.error("统计演练记录信息失败: {}", e.getMessage(), e);
            return Result.fail("统计演练记录信息失败");
        }
    }

    @Operation(summary = "获取演练记录趋势")
    @GetMapping("/trend")
    public Result<List<Map<String, Object>>> getExerciseRecordTrend(
            @ApiParam(value = "天数", required = true) @RequestParam Integer days) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.fail("请先登录");
            }
            List<Map<String, Object>> trend = exerciseRecordService.getExerciseRecordTrend(userId, days);
            return Result.success(trend);
        } catch (Exception e) {
            log.error("获取演练记录趋势失败: {}", e.getMessage(), e);
            return Result.fail("获取演练记录趋势失败");
        }
    }

    @Operation(summary = "对比两次演练记录")
    @PostMapping("/compare")
    public Result<Map<String, Object>> compareExerciseRecords(
            @ApiParam(value = "第一次演练记录ID", required = true) @RequestParam Long recordId1,
            @ApiParam(value = "第二次演练记录ID", required = true) @RequestParam Long recordId2) {
        try {
            Map<String, Object> result = exerciseRecordService.compareExerciseRecords(recordId1, recordId2);
            if ((boolean) result.get("success")) {
                return Result.success(result);
            } else {
                return Result.fail((String) result.get("message"));
            }
        } catch (Exception e) {
            log.error("对比演练记录失败: {}", e.getMessage(), e);
            return Result.fail("对比演练记录失败");
        }
    }

    @Operation(summary = "分析用户演练表现")
    @GetMapping("/analyze")
    public Result<Map<String, Object>> analyzeUserExercisePerformance(
            @ApiParam(value = "天数", required = true) @RequestParam Integer days) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.fail("请先登录");
            }
            Map<String, Object> result = exerciseRecordService.analyzeUserExercisePerformance(userId, days);
            return Result.success(result);
        } catch (Exception e) {
            log.error("分析用户演练表现失败: {}", e.getMessage(), e);
            return Result.fail("分析用户演练表现失败");
        }
    }
}
