package com.anti.fraud.modules.simulation.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.simulation.dto.SimulationSubmitDTO;
import com.anti.fraud.modules.simulation.service.SimulationService;
import com.anti.fraud.modules.simulation.vo.SceneDetailVO;
import com.anti.fraud.modules.simulation.vo.SceneVO;
import com.anti.fraud.modules.simulation.vo.SimulationRecordVO;
import com.anti.fraud.modules.simulation.vo.UserSimulationStatsVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "演练模拟", description = "演练模拟相关接口")
@RestController
@RequestMapping("/simulation")
@RequiredArgsConstructor
public class SimulationController {

    private final SimulationService simulationService;

    @Operation(summary = "获取推荐场景", security = @SecurityRequirement(name = "Bearer"))
    @PreAuthorize("hasAnyRole('ROLE_STUDENT', 'ROLE_TEACHER', 'ROLE_ADMIN', 'ROLE_EXPERT', 'ROLE_SUPER_ADMIN')")
    @GetMapping("/recommend")
    public Result<List<SceneVO>> getRecommendScenes() {
        return Result.success(simulationService.getRecommendScenes());
    }

    @Operation(summary = "分页获取场景列表", security = @SecurityRequirement(name = "Bearer"))
    @PreAuthorize("hasAnyRole('ROLE_STUDENT', 'ROLE_TEACHER', 'ROLE_ADMIN', 'ROLE_EXPERT', 'ROLE_SUPER_ADMIN')")
    @GetMapping("/list")
    public Result<Page<SceneVO>> getScenePage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String sceneType) {
        return Result.success(simulationService.getScenePage(page, size, sceneType));
    }

    @Operation(summary = "获取场景详情", security = @SecurityRequirement(name = "Bearer"))
    @PreAuthorize("hasAnyRole('ROLE_STUDENT', 'ROLE_TEACHER', 'ROLE_ADMIN', 'ROLE_EXPERT', 'ROLE_SUPER_ADMIN')")
    @GetMapping("/{id}")
    public Result<SceneDetailVO> getSceneDetail(@PathVariable Long id) {
        return Result.success(simulationService.getSceneDetail(id));
    }

    @Operation(summary = "开始演练", security = @SecurityRequirement(name = "Bearer"))
    @PreAuthorize("hasAnyRole('ROLE_STUDENT', 'ROLE_TEACHER', 'ROLE_ADMIN', 'ROLE_EXPERT', 'ROLE_SUPER_ADMIN')")
    @PostMapping("/{sceneId}/start")
    public Result<Long> startSimulation(@PathVariable Long sceneId) {
        return Result.success(simulationService.startSimulation(sceneId));
    }

    @Operation(summary = "提交演练", security = @SecurityRequirement(name = "Bearer"))
    @PreAuthorize("hasAnyRole('ROLE_STUDENT', 'ROLE_TEACHER', 'ROLE_ADMIN', 'ROLE_EXPERT', 'ROLE_SUPER_ADMIN')")
    @PostMapping("/submit")
    public Result<Integer> submitSimulation(@RequestBody SimulationSubmitDTO submitDTO) {
        return Result.success(simulationService.submitSimulation(submitDTO));
    }

    @Operation(summary = "获取我的演练记录", security = @SecurityRequirement(name = "Bearer"))
    @PreAuthorize("hasAnyRole('ROLE_STUDENT', 'ROLE_TEACHER', 'ROLE_ADMIN', 'ROLE_EXPERT', 'ROLE_SUPER_ADMIN')")
    @GetMapping("/my-records")
    public Result<List<SceneVO>> getMySimulationRecords() {
        return Result.success(simulationService.getMySimulationRecords());
    }

    @Operation(summary = "获取我的演练记录详情", security = @SecurityRequirement(name = "Bearer"))
    @PreAuthorize("hasAnyRole('ROLE_STUDENT', 'ROLE_TEACHER', 'ROLE_ADMIN', 'ROLE_EXPERT', 'ROLE_SUPER_ADMIN')")
    @GetMapping("/my-records/details")
    public Result<List<SimulationRecordVO>> getMySimulationRecordDetails() {
        return Result.success(simulationService.getMySimulationRecordDetails());
    }

    @Operation(summary = "获取我的演练统计", security = @SecurityRequirement(name = "Bearer"))
    @PreAuthorize("hasAnyRole('ROLE_STUDENT', 'ROLE_TEACHER', 'ROLE_ADMIN', 'ROLE_EXPERT', 'ROLE_SUPER_ADMIN')")
    @GetMapping("/my-stats")
    public Result<UserSimulationStatsVO> getUserSimulationStats() {
        return Result.success(simulationService.getUserSimulationStats());
    }
}