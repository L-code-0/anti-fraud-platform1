package com.anti.fraud.modules.vr.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.vr.entity.VRSimulation;
import com.anti.fraud.modules.vr.entity.VRSimulationRecord;
import com.anti.fraud.modules.vr.service.VRSimulationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * VR演练控制器
 */
@RestController
@RequestMapping("/vr/simulation")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "VR沉浸式演练管理")
public class VRSimulationController {

    private final VRSimulationService vrSimulationService;

    @Operation(summary = "创建VR演练")
    @PostMapping("/create")
    public Result<Void> createVRSimulation(@ApiParam(value = "VR演练信息", required = true) @RequestBody VRSimulation vrSimulation) {
        try {
            boolean success = vrSimulationService.createVRSimulation(vrSimulation);
            if (success) {
                return Result.successMsg("创建VR演练成功");
            } else {
                return Result.fail("创建VR演练失败");
            }
        } catch (Exception e) {
            log.error("创建VR演练失败: {}", e.getMessage(), e);
            return Result.fail("创建VR演练失败");
        }
    }

    @Operation(summary = "更新VR演练")
    @PutMapping("/update")
    public Result<Void> updateVRSimulation(@ApiParam(value = "VR演练信息", required = true) @RequestBody VRSimulation vrSimulation) {
        try {
            boolean success = vrSimulationService.updateVRSimulation(vrSimulation);
            if (success) {
                return Result.successMsg("更新VR演练成功");
            } else {
                return Result.fail("更新VR演练失败");
            }
        } catch (Exception e) {
            log.error("更新VR演练失败: {}", e.getMessage(), e);
            return Result.fail("更新VR演练失败");
        }
    }

    @Operation(summary = "删除VR演练")
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteVRSimulation(@ApiParam(value = "VR演练ID", required = true) @PathVariable Long id) {
        try {
            boolean success = vrSimulationService.deleteVRSimulation(id);
            if (success) {
                return Result.successMsg("删除VR演练成功");
            } else {
                return Result.fail("删除VR演练失败");
            }
        } catch (Exception e) {
            log.error("删除VR演练失败: {}", e.getMessage(), e);
            return Result.fail("删除VR演练失败");
        }
    }

    @Operation(summary = "获取VR演练详情")
    @GetMapping("/detail/{id}")
    public Result<VRSimulation> getVRSimulationById(@ApiParam(value = "VR演练ID", required = true) @PathVariable Long id) {
        try {
            VRSimulation vrSimulation = vrSimulationService.getVRSimulationById(id);
            if (vrSimulation != null) {
                // 增加浏览量
                vrSimulationService.incrementViewCount(id);
                return Result.success(vrSimulation);
            } else {
                return Result.fail("VR演练不存在");
            }
        } catch (Exception e) {
            log.error("获取VR演练详情失败: {}", e.getMessage(), e);
            return Result.fail("获取VR演练详情失败");
        }
    }

    @Operation(summary = "分页查询VR演练")
    @PostMapping("/list")
    public Result<Map<String, Object>> getVRSimulationList(
            @ApiParam(value = "查询参数", required = false) @RequestBody(required = false) Map<String, Object> params,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            if (params == null) {
                params = new java.util.HashMap<>();
            }
            Map<String, Object> result = vrSimulationService.getVRSimulationList(params, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询VR演练列表失败: {}", e.getMessage(), e);
            return Result.fail("查询VR演练列表失败");
        }
    }

    @Operation(summary = "根据类型查询VR演练")
    @GetMapping("/by-type")
    public Result<Map<String, Object>> getVRSimulationsByType(
            @ApiParam(value = "演练类型: 1-电信诈骗, 2-网络诈骗, 3-金融诈骗, 4-其他", required = true) @RequestParam Integer type,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = vrSimulationService.getVRSimulationsByType(type, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("根据类型查询VR演练失败: {}", e.getMessage(), e);
            return Result.fail("根据类型查询VR演练失败");
        }
    }

    @Operation(summary = "根据难度查询VR演练")
    @GetMapping("/by-difficulty")
    public Result<Map<String, Object>> getVRSimulationsByDifficulty(
            @ApiParam(value = "难度等级: 1-简单, 2-中等, 3-困难", required = true) @RequestParam Integer difficulty,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = vrSimulationService.getVRSimulationsByDifficulty(difficulty, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("根据难度查询VR演练失败: {}", e.getMessage(), e);
            return Result.fail("根据难度查询VR演练失败");
        }
    }

    @Operation(summary = "根据场景查询VR演练")
    @GetMapping("/by-scenario")
    public Result<Map<String, Object>> getVRSimulationsByScenarioId(
            @ApiParam(value = "场景ID", required = true) @RequestParam Long scenarioId,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = vrSimulationService.getVRSimulationsByScenarioId(scenarioId, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("根据场景查询VR演练失败: {}", e.getMessage(), e);
            return Result.fail("根据场景查询VR演练失败");
        }
    }

    @Operation(summary = "统计VR演练信息")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getVRSimulationStats() {
        try {
            Map<String, Object> stats = vrSimulationService.getVRSimulationStats();
            return Result.success(stats);
        } catch (Exception e) {
            log.error("统计VR演练信息失败: {}", e.getMessage(), e);
            return Result.fail("统计VR演练信息失败");
        }
    }

    @Operation(summary = "开始VR演练")
    @PostMapping("/start")
    public Result<Long> startVRSimulation(
            @ApiParam(value = "演练ID", required = true) @RequestParam Long simulationId,
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "用户名", required = true) @RequestParam String username) {
        try {
            Long recordId = vrSimulationService.startVRSimulation(simulationId, userId, username);
            if (recordId != null) {
                return Result.success(recordId);
            } else {
                return Result.fail("开始VR演练失败");
            }
        } catch (Exception e) {
            log.error("开始VR演练失败: {}", e.getMessage(), e);
            return Result.fail("开始VR演练失败");
        }
    }

    @Operation(summary = "结束VR演练")
    @PostMapping("/end")
    public Result<Void> endVRSimulation(
            @ApiParam(value = "记录ID", required = true) @RequestParam Long recordId,
            @ApiParam(value = "得分", required = true) @RequestParam Integer score,
            @ApiParam(value = "正确率", required = true) @RequestParam Double correctRate,
            @ApiParam(value = "操作记录", required = true) @RequestParam String operationRecord,
            @ApiParam(value = "评估结果", required = true) @RequestParam String evaluationResult) {
        try {
            boolean success = vrSimulationService.endVRSimulation(recordId, score, correctRate, operationRecord, evaluationResult);
            if (success) {
                return Result.successMsg("结束VR演练成功");
            } else {
                return Result.fail("结束VR演练失败");
            }
        } catch (Exception e) {
            log.error("结束VR演练失败: {}", e.getMessage(), e);
            return Result.fail("结束VR演练失败");
        }
    }

    @Operation(summary = "获取VR演练记录")
    @GetMapping("/record/{id}")
    public Result<VRSimulationRecord> getVRSimulationRecordById(@ApiParam(value = "记录ID", required = true) @PathVariable Long id) {
        try {
            VRSimulationRecord record = vrSimulationService.getVRSimulationRecordById(id);
            if (record != null) {
                return Result.success(record);
            } else {
                return Result.fail("VR演练记录不存在");
            }
        } catch (Exception e) {
            log.error("获取VR演练记录失败: {}", e.getMessage(), e);
            return Result.fail("获取VR演练记录失败");
        }
    }

    @Operation(summary = "根据用户ID查询VR演练记录")
    @GetMapping("/records/by-user/{userId}")
    public Result<Map<String, Object>> getVRSimulationRecordsByUserId(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long userId,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = vrSimulationService.getVRSimulationRecordsByUserId(userId, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("根据用户ID查询VR演练记录失败: {}", e.getMessage(), e);
            return Result.fail("根据用户ID查询VR演练记录失败");
        }
    }

    @Operation(summary = "根据演练ID查询VR演练记录")
    @GetMapping("/records/by-simulation/{simulationId}")
    public Result<Map<String, Object>> getVRSimulationRecordsBySimulationId(
            @ApiParam(value = "演练ID", required = true) @PathVariable Long simulationId,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = vrSimulationService.getVRSimulationRecordsBySimulationId(simulationId, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("根据演练ID查询VR演练记录失败: {}", e.getMessage(), e);
            return Result.fail("根据演练ID查询VR演练记录失败");
        }
    }

    @Operation(summary = "获取用户VR演练统计信息")
    @GetMapping("/stats/user/{userId}")
    public Result<Map<String, Object>> getUserVRSimulationStats(@ApiParam(value = "用户ID", required = true) @PathVariable Long userId) {
        try {
            Map<String, Object> stats = vrSimulationService.getUserVRSimulationStats(userId);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取用户VR演练统计信息失败: {}", e.getMessage(), e);
            return Result.fail("获取用户VR演练统计信息失败");
        }
    }

    @Operation(summary = "获取演练VR演练统计信息")
    @GetMapping("/stats/simulation/{simulationId}")
    public Result<Map<String, Object>> getSimulationVRSimulationStats(@ApiParam(value = "演练ID", required = true) @PathVariable Long simulationId) {
        try {
            Map<String, Object> stats = vrSimulationService.getSimulationVRSimulationStats(simulationId);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取演练VR演练统计信息失败: {}", e.getMessage(), e);
            return Result.fail("获取演练VR演练统计信息失败");
        }
    }

    @Operation(summary = "获取VR演练趋势")
    @GetMapping("/trend")
    public Result<List<Map<String, Object>>> getVRSimulationTrend(
            @ApiParam(value = "开始日期", required = true) @RequestParam String startDate,
            @ApiParam(value = "结束日期", required = true) @RequestParam String endDate) {
        try {
            List<Map<String, Object>> trend = vrSimulationService.getVRSimulationTrend(startDate, endDate);
            return Result.success(trend);
        } catch (Exception e) {
            log.error("获取VR演练趋势失败: {}", e.getMessage(), e);
            return Result.fail("获取VR演练趋势失败");
        }
    }
}
