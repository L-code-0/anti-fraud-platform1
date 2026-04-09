package com.anti.fraud.modules.simulation.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.simulation.dto.SceneCreateDTO;
import com.anti.fraud.modules.simulation.service.SimulationManageService;
import com.anti.fraud.modules.simulation.vo.SceneVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "演练管理-后台", description = "演练管理后台接口")
@RestController
@RequestMapping("/admin/simulation")
@RequiredArgsConstructor
public class SimulationManageController {

    private final SimulationManageService simulationManageService;

    @Operation(summary = "分页获取场景列表", security = @SecurityRequirement(name = "Bearer"))
    @GetMapping("/scenes")
    @PreAuthorize("hasRole('3') or hasRole('4') or hasRole('5')")
    public Result<Page<SceneVO>> getSceneList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String sceneType,
            @RequestParam(required = false) Integer status) {
        return Result.success(simulationManageService.getScenePage(page, size, sceneType, status));
    }

    @Operation(summary = "创建场景", security = @SecurityRequirement(name = "Bearer"))
    @PostMapping("/scenes")
    @PreAuthorize("hasRole('3') or hasRole('4') or hasRole('5')")
    public Result<Void> createScene(@RequestBody SceneCreateDTO createDTO) {
        simulationManageService.createScene(createDTO);
        return Result.successMsg("创建成功");
    }

    @Operation(summary = "更新场景", security = @SecurityRequirement(name = "Bearer"))
    @PutMapping("/scenes/{id}")
    @PreAuthorize("hasRole('3') or hasRole('4') or hasRole('5')")
    public Result<Void> updateScene(@PathVariable Long id, @RequestBody SceneCreateDTO createDTO) {
        simulationManageService.updateScene(id, createDTO);
        return Result.successMsg("更新成功");
    }

    @Operation(summary = "删除场景", security = @SecurityRequirement(name = "Bearer"))
    @DeleteMapping("/scenes/{id}")
    @PreAuthorize("hasRole('3') or hasRole('5')")
    public Result<Void> deleteScene(@PathVariable Long id) {
        simulationManageService.deleteScene(id);
        return Result.successMsg("删除成功");
    }

    @Operation(summary = "上架/下架场景", security = @SecurityRequirement(name = "Bearer"))
    @PutMapping("/scenes/{id}/status")
    @PreAuthorize("hasRole('3') or hasRole('5')")
    public Result<Void> updateSceneStatus(@PathVariable Long id, @RequestParam Integer status) {
        simulationManageService.updateSceneStatus(id, status);
        return Result.successMsg("操作成功");
    }

    @Operation(summary = "设置推荐", security = @SecurityRequirement(name = "Bearer"))
    @PutMapping("/scenes/{id}/recommend")
    @PreAuthorize("hasRole('3') or hasRole('5')")
    public Result<Void> setRecommend(@PathVariable Long id, @RequestParam Boolean isRecommend) {
        simulationManageService.setRecommend(id, isRecommend);
        return Result.successMsg("设置成功");
    }
}