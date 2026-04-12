package com.anti.fraud.modules.vr.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.vr.entity.VrScene;
import com.anti.fraud.modules.vr.entity.VrDrillRecord;
import com.anti.fraud.modules.vr.service.VrService;
import com.anti.fraud.utils.SecurityUtils;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/vr")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "VR/沉浸式演练")
public class VrController {

    private final VrService vrService;

    @Operation(summary = "创建VR场景")
    @PostMapping("/scene/create")
    public Result<Void> createVrScene(@RequestBody VrScene scene) {
        scene.setCreatedBy(SecurityUtils.getCurrentUserName());
        scene.setUpdatedBy(SecurityUtils.getCurrentUserName());

        try {
            boolean success = vrService.createVrScene(scene);
            if (success) {
                return Result.successMsg("创建VR场景成功");
            } else {
                return Result.fail("创建VR场景失败");
            }
        } catch (Exception e) {
            log.error("创建VR场景失败: {}", e.getMessage(), e);
            return Result.fail("创建VR场景失败");
        }
    }

    @Operation(summary = "更新VR场景")
    @PostMapping("/scene/update")
    public Result<Void> updateVrScene(@RequestBody VrScene scene) {
        scene.setUpdatedBy(SecurityUtils.getCurrentUserName());

        try {
            boolean success = vrService.updateVrScene(scene);
            if (success) {
                return Result.successMsg("更新VR场景成功");
            } else {
                return Result.fail("更新VR场景失败");
            }
        } catch (Exception e) {
            log.error("更新VR场景失败: {}", e.getMessage(), e);
            return Result.fail("更新VR场景失败");
        }
    }

    @Operation(summary = "删除VR场景")
    @PostMapping("/scene/delete/{id}")
    public Result<Void> deleteVrScene(@PathVariable Long id) {
        try {
            boolean success = vrService.deleteVrScene(id);
            if (success) {
                return Result.successMsg("删除VR场景成功");
            } else {
                return Result.fail("删除VR场景失败");
            }
        } catch (Exception e) {
            log.error("删除VR场景失败: {}", e.getMessage(), e);
            return Result.fail("删除VR场景失败");
        }
    }

    @Operation(summary = "获取VR场景详情")
    @GetMapping("/scene/detail/{id}")
    public Result<VrScene> getVrSceneById(@PathVariable Long id) {
        try {
            VrScene scene = vrService.getVrSceneById(id);
            if (scene != null) {
                return Result.success("获取VR场景详情成功", scene);
            } else {
                return Result.fail("VR场景不存在");
            }
        } catch (Exception e) {
            log.error("获取VR场景详情失败: {}", e.getMessage(), e);
            return Result.fail("获取VR场景详情失败");
        }
    }

    @Operation(summary = "获取VR场景列表")
    @GetMapping("/scene/list")
    public Result<List<VrScene>> getVrSceneList(
            @RequestParam(required = false) String sceneType,
            @RequestParam(required = false) Integer difficulty,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<VrScene> scenes = vrService.getVrSceneList(sceneType, difficulty, page, size);
            return Result.success("获取VR场景列表成功", scenes);
        } catch (Exception e) {
            log.error("获取VR场景列表失败: {}", e.getMessage(), e);
            return Result.fail("获取VR场景列表失败");
        }
    }

    @Operation(summary = "开始VR演练")
    @PostMapping("/drill/start")
    public Result<Map<String, Object>> startVrDrill(@RequestBody Map<String, Object> data) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        Long sceneId = Long.valueOf(data.get("sceneId").toString());
        String deviceInfo = (String) data.get("deviceInfo");

        try {
            Map<String, Object> result = vrService.startVrDrill(sceneId, userId, SecurityUtils.getCurrentUserName(), deviceInfo);
            if ((boolean) result.get("success")) {
                return Result.success("开始VR演练成功", result);
            } else {
                return Result.fail((String) result.get("message"));
            }
        } catch (Exception e) {
            log.error("开始VR演练失败: {}", e.getMessage(), e);
            return Result.fail("开始VR演练失败");
        }
    }

    @Operation(summary = "提交VR演练结果")
    @PostMapping("/drill/submit")
    public Result<Map<String, Object>> submitVrDrillResult(@RequestBody VrDrillRecord record) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        record.setUserId(userId);
        record.setUserName(SecurityUtils.getCurrentUserName());

        try {
            Map<String, Object> result = vrService.submitVrDrillResult(record);
            if ((boolean) result.get("success")) {
                return Result.success("提交VR演练结果成功", result);
            } else {
                return Result.fail((String) result.get("message"));
            }
        } catch (Exception e) {
            log.error("提交VR演练结果失败: {}", e.getMessage(), e);
            return Result.fail("提交VR演练结果失败");
        }
    }

    @Operation(summary = "获取用户VR演练历史")
    @GetMapping("/drill/history")
    public Result<List<VrDrillRecord>> getUserVrDrillHistory(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            List<VrDrillRecord> history = vrService.getUserVrDrillHistory(userId, page, size);
            return Result.success("获取用户VR演练历史成功", history);
        } catch (Exception e) {
            log.error("获取用户VR演练历史失败: {}", e.getMessage(), e);
            return Result.fail("获取用户VR演练历史失败");
        }
    }

    @Operation(summary = "获取VR场景统计信息")
    @GetMapping("/scene/stats/{sceneId}")
    public Result<Map<String, Object>> getVrSceneStats(@PathVariable Long sceneId) {
        try {
            Map<String, Object> stats = vrService.getVrSceneStats(sceneId);
            return Result.success("获取VR场景统计信息成功", stats);
        } catch (Exception e) {
            log.error("获取VR场景统计信息失败: {}", e.getMessage(), e);
            return Result.fail("获取VR场景统计信息失败");
        }
    }

    @Operation(summary = "获取用户VR演练统计信息")
    @GetMapping("/drill/stats")
    public Result<Map<String, Object>> getUserVrDrillStats() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            Map<String, Object> stats = vrService.getUserVrDrillStats(userId);
            return Result.success("获取用户VR演练统计信息成功", stats);
        } catch (Exception e) {
            log.error("获取用户VR演练统计信息失败: {}", e.getMessage(), e);
            return Result.fail("获取用户VR演练统计信息失败");
        }
    }

    @Operation(summary = "搜索VR场景")
    @GetMapping("/scene/search")
    public Result<List<VrScene>> searchVrScenes(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<VrScene> scenes = vrService.searchVrScenes(keyword, page, size);
            return Result.success("搜索VR场景成功", scenes);
        } catch (Exception e) {
            log.error("搜索VR场景失败: {}", e.getMessage(), e);
            return Result.fail("搜索VR场景失败");
        }
    }
}
