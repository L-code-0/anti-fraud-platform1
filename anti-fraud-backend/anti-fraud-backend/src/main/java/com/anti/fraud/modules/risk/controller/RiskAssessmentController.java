package com.anti.fraud.modules.risk.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.risk.entity.RiskAssessment;
import com.anti.fraud.modules.risk.entity.BehaviorRisk;
import com.anti.fraud.modules.risk.entity.DeviceRisk;
import com.anti.fraud.modules.risk.entity.SocialRisk;
import com.anti.fraud.modules.risk.service.RiskAssessmentService;
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
 * 风险评估服务控制器
 */
@RestController
@RequestMapping("/risk")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "风险评估服务")
public class RiskAssessmentController {

    private final RiskAssessmentService riskAssessmentService;

    @Operation(summary = "创建风险评估")
    @PostMapping("/assessment/create")
    public Result<String> createRiskAssessment(@ApiParam(value = "风险评估信息", required = true) @RequestBody RiskAssessment riskAssessment) {
        try {
            String assessmentId = riskAssessmentService.createRiskAssessment(riskAssessment);
            return Result.success(assessmentId);
        } catch (Exception e) {
            log.error("创建风险评估失败: {}", e.getMessage(), e);
            return Result.fail("创建风险评估失败");
        }
    }

    @Operation(summary = "更新风险评估")
    @PutMapping("/assessment/update")
    public Result<Void> updateRiskAssessment(@ApiParam(value = "风险评估信息", required = true) @RequestBody RiskAssessment riskAssessment) {
        try {
            boolean success = riskAssessmentService.updateRiskAssessment(riskAssessment);
            if (success) {
                return Result.successMsg("更新风险评估成功");
            } else {
                return Result.fail("更新风险评估失败");
            }
        } catch (Exception e) {
            log.error("更新风险评估失败: {}", e.getMessage(), e);
            return Result.fail("更新风险评估失败");
        }
    }

    @Operation(summary = "删除风险评估")
    @DeleteMapping("/assessment/delete/{assessmentId}")
    public Result<Void> deleteRiskAssessment(@ApiParam(value = "评估ID", required = true) @PathVariable String assessmentId) {
        try {
            boolean success = riskAssessmentService.deleteRiskAssessment(assessmentId);
            if (success) {
                return Result.successMsg("删除风险评估成功");
            } else {
                return Result.fail("删除风险评估失败");
            }
        } catch (Exception e) {
            log.error("删除风险评估失败: {}", e.getMessage(), e);
            return Result.fail("删除风险评估失败");
        }
    }

    @Operation(summary = "获取风险评估详情")
    @GetMapping("/assessment/detail/{assessmentId}")
    public Result<RiskAssessment> getRiskAssessmentByAssessmentId(@ApiParam(value = "评估ID", required = true) @PathVariable String assessmentId) {
        try {
            RiskAssessment assessment = riskAssessmentService.getRiskAssessmentByAssessmentId(assessmentId);
            if (assessment != null) {
                return Result.success(assessment);
            } else {
                return Result.fail("风险评估不存在");
            }
        } catch (Exception e) {
            log.error("获取风险评估详情失败: {}", e.getMessage(), e);
            return Result.fail("获取风险评估详情失败");
        }
    }

    @Operation(summary = "分页查询风险评估列表")
    @GetMapping("/assessment/list")
    public Result<Map<String, Object>> getRiskAssessmentList(
            @ApiParam(value = "用户ID", required = false) @RequestParam(required = false) Long userId,
            @ApiParam(value = "风险等级: 1-低风险，2-中风险，3-高风险，4-极高风险", required = false) @RequestParam(required = false) Integer riskLevel,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = riskAssessmentService.getRiskAssessmentList(userId, riskLevel, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询风险评估列表失败: {}", e.getMessage(), e);
            return Result.fail("查询风险评估列表失败");
        }
    }

    @Operation(summary = "根据时间范围查询风险评估列表")
    @GetMapping("/assessment/list-by-time-range")
    public Result<List<RiskAssessment>> getRiskAssessmentListByTimeRange(
            @ApiParam(value = "开始时间", required = true) @RequestParam LocalDateTime startTime,
            @ApiParam(value = "结束时间", required = true) @RequestParam LocalDateTime endTime,
            @ApiParam(value = "风险等级: 1-低风险，2-中风险，3-高风险，4-极高风险", required = false) @RequestParam(required = false) Integer riskLevel) {
        try {
            List<RiskAssessment> assessments = riskAssessmentService.getRiskAssessmentListByTimeRange(startTime, endTime, riskLevel);
            return Result.success(assessments);
        } catch (Exception e) {
            log.error("根据时间范围查询风险评估列表失败: {}", e.getMessage(), e);
            return Result.fail("根据时间范围查询风险评估列表失败");
        }
    }

    @Operation(summary = "多维度风险评估")
    @PostMapping("/assessment/multi-dimensional")
    public Result<Map<String, Object>> multiDimensionalRiskAssessment(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "用户名", required = true) @RequestParam String username) {
        try {
            Map<String, Object> result = riskAssessmentService.multiDimensionalRiskAssessment(userId, username);
            return Result.success(result);
        } catch (Exception e) {
            log.error("多维度风险评估失败: {}", e.getMessage(), e);
            return Result.fail("多维度风险评估失败");
        }
    }

    @Operation(summary = "创建行为风险")
    @PostMapping("/behavior/create")
    public Result<String> createBehaviorRisk(@ApiParam(value = "行为风险信息", required = true) @RequestBody BehaviorRisk behaviorRisk) {
        try {
            String riskId = riskAssessmentService.createBehaviorRisk(behaviorRisk);
            return Result.success(riskId);
        } catch (Exception e) {
            log.error("创建行为风险失败: {}", e.getMessage(), e);
            return Result.fail("创建行为风险失败");
        }
    }

    @Operation(summary = "更新行为风险")
    @PutMapping("/behavior/update")
    public Result<Void> updateBehaviorRisk(@ApiParam(value = "行为风险信息", required = true) @RequestBody BehaviorRisk behaviorRisk) {
        try {
            boolean success = riskAssessmentService.updateBehaviorRisk(behaviorRisk);
            if (success) {
                return Result.successMsg("更新行为风险成功");
            } else {
                return Result.fail("更新行为风险失败");
            }
        } catch (Exception e) {
            log.error("更新行为风险失败: {}", e.getMessage(), e);
            return Result.fail("更新行为风险失败");
        }
    }

    @Operation(summary = "删除行为风险")
    @DeleteMapping("/behavior/delete/{riskId}")
    public Result<Void> deleteBehaviorRisk(@ApiParam(value = "风险ID", required = true) @PathVariable String riskId) {
        try {
            boolean success = riskAssessmentService.deleteBehaviorRisk(riskId);
            if (success) {
                return Result.successMsg("删除行为风险成功");
            } else {
                return Result.fail("删除行为风险失败");
            }
        } catch (Exception e) {
            log.error("删除行为风险失败: {}", e.getMessage(), e);
            return Result.fail("删除行为风险失败");
        }
    }

    @Operation(summary = "获取行为风险详情")
    @GetMapping("/behavior/detail/{riskId}")
    public Result<BehaviorRisk> getBehaviorRiskByRiskId(@ApiParam(value = "风险ID", required = true) @PathVariable String riskId) {
        try {
            BehaviorRisk risk = riskAssessmentService.getBehaviorRiskByRiskId(riskId);
            if (risk != null) {
                return Result.success(risk);
            } else {
                return Result.fail("行为风险不存在");
            }
        } catch (Exception e) {
            log.error("获取行为风险详情失败: {}", e.getMessage(), e);
            return Result.fail("获取行为风险详情失败");
        }
    }

    @Operation(summary = "分页查询行为风险列表")
    @GetMapping("/behavior/list")
    public Result<Map<String, Object>> getBehaviorRiskList(
            @ApiParam(value = "用户ID", required = false) @RequestParam(required = false) Long userId,
            @ApiParam(value = "行为类型: 1-登录行为，2-交易行为，3-搜索行为，4-浏览行为，5-其他", required = false) @RequestParam(required = false) Integer behaviorType,
            @ApiParam(value = "风险等级: 1-低风险，2-中风险，3-高风险，4-极高风险", required = false) @RequestParam(required = false) Integer riskLevel,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = riskAssessmentService.getBehaviorRiskList(userId, behaviorType, riskLevel, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询行为风险列表失败: {}", e.getMessage(), e);
            return Result.fail("查询行为风险列表失败");
        }
    }

    @Operation(summary = "分析行为风险")
    @PostMapping("/behavior/analyze")
    public Result<Map<String, Object>> analyzeBehaviorRisk(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "用户名", required = true) @RequestParam String username,
            @ApiParam(value = "行为类型: 1-登录行为，2-交易行为，3-搜索行为，4-浏览行为，5-其他", required = true) @RequestParam Integer behaviorType,
            @ApiParam(value = "行为内容", required = true) @RequestParam String behaviorContent) {
        try {
            Map<String, Object> result = riskAssessmentService.analyzeBehaviorRisk(userId, username, behaviorType, behaviorContent);
            return Result.success(result);
        } catch (Exception e) {
            log.error("分析行为风险失败: {}", e.getMessage(), e);
            return Result.fail("分析行为风险失败");
        }
    }

    @Operation(summary = "创建设备风险")
    @PostMapping("/device/create")
    public Result<String> createDeviceRisk(@ApiParam(value = "设备风险信息", required = true) @RequestBody DeviceRisk deviceRisk) {
        try {
            String riskId = riskAssessmentService.createDeviceRisk(deviceRisk);
            return Result.success(riskId);
        } catch (Exception e) {
            log.error("创建设备风险失败: {}", e.getMessage(), e);
            return Result.fail("创建设备风险失败");
        }
    }

    @Operation(summary = "更新设备风险")
    @PutMapping("/device/update")
    public Result<Void> updateDeviceRisk(@ApiParam(value = "设备风险信息", required = true) @RequestBody DeviceRisk deviceRisk) {
        try {
            boolean success = riskAssessmentService.updateDeviceRisk(deviceRisk);
            if (success) {
                return Result.successMsg("更新设备风险成功");
            } else {
                return Result.fail("更新设备风险失败");
            }
        } catch (Exception e) {
            log.error("更新设备风险失败: {}", e.getMessage(), e);
            return Result.fail("更新设备风险失败");
        }
    }

    @Operation(summary = "删除设备风险")
    @DeleteMapping("/device/delete/{riskId}")
    public Result<Void> deleteDeviceRisk(@ApiParam(value = "风险ID", required = true) @PathVariable String riskId) {
        try {
            boolean success = riskAssessmentService.deleteDeviceRisk(riskId);
            if (success) {
                return Result.successMsg("删除设备风险成功");
            } else {
                return Result.fail("删除设备风险失败");
            }
        } catch (Exception e) {
            log.error("删除设备风险失败: {}", e.getMessage(), e);
            return Result.fail("删除设备风险失败");
        }
    }

    @Operation(summary = "获取设备风险详情")
    @GetMapping("/device/detail/{riskId}")
    public Result<DeviceRisk> getDeviceRiskByRiskId(@ApiParam(value = "风险ID", required = true) @PathVariable String riskId) {
        try {
            DeviceRisk risk = riskAssessmentService.getDeviceRiskByRiskId(riskId);
            if (risk != null) {
                return Result.success(risk);
            } else {
                return Result.fail("设备风险不存在");
            }
        } catch (Exception e) {
            log.error("获取设备风险详情失败: {}", e.getMessage(), e);
            return Result.fail("获取设备风险详情失败");
        }
    }

    @Operation(summary = "分页查询设备风险列表")
    @GetMapping("/device/list")
    public Result<Map<String, Object>> getDeviceRiskList(
            @ApiParam(value = "用户ID", required = false) @RequestParam(required = false) Long userId,
            @ApiParam(value = "设备类型: 1-手机，2-平板，3-电脑，4-其他", required = false) @RequestParam(required = false) Integer deviceType,
            @ApiParam(value = "风险等级: 1-低风险，2-中风险，3-高风险，4-极高风险", required = false) @RequestParam(required = false) Integer riskLevel,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = riskAssessmentService.getDeviceRiskList(userId, deviceType, riskLevel, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询设备风险列表失败: {}", e.getMessage(), e);
            return Result.fail("查询设备风险列表失败");
        }
    }

    @Operation(summary = "分析设备风险")
    @PostMapping("/device/analyze")
    public Result<Map<String, Object>> analyzeDeviceRisk(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "用户名", required = true) @RequestParam String username,
            @ApiParam(value = "设备ID", required = true) @RequestParam String deviceId,
            @ApiParam(value = "设备信息", required = true) @RequestParam String deviceInfo,
            @ApiParam(value = "IP地址", required = true) @RequestParam String ipAddress,
            @ApiParam(value = "地理位置", required = true) @RequestParam String location) {
        try {
            Map<String, Object> result = riskAssessmentService.analyzeDeviceRisk(userId, username, deviceId, deviceInfo, ipAddress, location);
            return Result.success(result);
        } catch (Exception e) {
            log.error("分析设备风险失败: {}", e.getMessage(), e);
            return Result.fail("分析设备风险失败");
        }
    }

    @Operation(summary = "创建社交风险")
    @PostMapping("/social/create")
    public Result<String> createSocialRisk(@ApiParam(value = "社交风险信息", required = true) @RequestBody SocialRisk socialRisk) {
        try {
            String riskId = riskAssessmentService.createSocialRisk(socialRisk);
            return Result.success(riskId);
        } catch (Exception e) {
            log.error("创建社交风险失败: {}", e.getMessage(), e);
            return Result.fail("创建社交风险失败");
        }
    }

    @Operation(summary = "更新社交风险")
    @PutMapping("/social/update")
    public Result<Void> updateSocialRisk(@ApiParam(value = "社交风险信息", required = true) @RequestBody SocialRisk socialRisk) {
        try {
            boolean success = riskAssessmentService.updateSocialRisk(socialRisk);
            if (success) {
                return Result.successMsg("更新社交风险成功");
            } else {
                return Result.fail("更新社交风险失败");
            }
        } catch (Exception e) {
            log.error("更新社交风险失败: {}", e.getMessage(), e);
            return Result.fail("更新社交风险失败");
        }
    }

    @Operation(summary = "删除社交风险")
    @DeleteMapping("/social/delete/{riskId}")
    public Result<Void> deleteSocialRisk(@ApiParam(value = "风险ID", required = true) @PathVariable String riskId) {
        try {
            boolean success = riskAssessmentService.deleteSocialRisk(riskId);
            if (success) {
                return Result.successMsg("删除社交风险成功");
            } else {
                return Result.fail("删除社交风险失败");
            }
        } catch (Exception e) {
            log.error("删除社交风险失败: {}", e.getMessage(), e);
            return Result.fail("删除社交风险失败");
        }
    }

    @Operation(summary = "获取社交风险详情")
    @GetMapping("/social/detail/{riskId}")
    public Result<SocialRisk> getSocialRiskByRiskId(@ApiParam(value = "风险ID", required = true) @PathVariable String riskId) {
        try {
            SocialRisk risk = riskAssessmentService.getSocialRiskByRiskId(riskId);
            if (risk != null) {
                return Result.success(risk);
            } else {
                return Result.fail("社交风险不存在");
            }
        } catch (Exception e) {
            log.error("获取社交风险详情失败: {}", e.getMessage(), e);
            return Result.fail("获取社交风险详情失败");
        }
    }

    @Operation(summary = "分页查询社交风险列表")
    @GetMapping("/social/list")
    public Result<Map<String, Object>> getSocialRiskList(
            @ApiParam(value = "用户ID", required = false) @RequestParam(required = false) Long userId,
            @ApiParam(value = "社交类型: 1-好友关系，2-群组关系，3-关注关系，4-其他", required = false) @RequestParam(required = false) Integer socialType,
            @ApiParam(value = "风险等级: 1-低风险，2-中风险，3-高风险，4-极高风险", required = false) @RequestParam(required = false) Integer riskLevel,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = riskAssessmentService.getSocialRiskList(userId, socialType, riskLevel, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询社交风险列表失败: {}", e.getMessage(), e);
            return Result.fail("查询社交风险列表失败");
        }
    }

    @Operation(summary = "分析社交风险")
    @PostMapping("/social/analyze")
    public Result<Map<String, Object>> analyzeSocialRisk(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "用户名", required = true) @RequestParam String username,
            @ApiParam(value = "社交对象ID", required = true) @RequestParam String targetUserId,
            @ApiParam(value = "社交对象名称", required = true) @RequestParam String targetUsername,
            @ApiParam(value = "社交类型: 1-好友关系，2-群组关系，3-关注关系，4-其他", required = true) @RequestParam Integer socialType,
            @ApiParam(value = "社交内容", required = true) @RequestParam String socialContent) {
        try {
            Map<String, Object> result = riskAssessmentService.analyzeSocialRisk(userId, username, targetUserId, targetUsername, socialType, socialContent);
            return Result.success(result);
        } catch (Exception e) {
            log.error("分析社交风险失败: {}", e.getMessage(), e);
            return Result.fail("分析社交风险失败");
        }
    }

    @Operation(summary = "更新时间衰减因子")
    @PutMapping("/time-decay/update")
    public Result<Integer> updateTimeDecayFactor(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "天数", required = true) @RequestParam Integer days) {
        try {
            int count = riskAssessmentService.updateTimeDecayFactor(userId, days);
            return Result.success(count);
        } catch (Exception e) {
            log.error("更新时间衰减因子失败: {}", e.getMessage(), e);
            return Result.fail("更新时间衰减因子失败");
        }
    }

    @Operation(summary = "获取风险评估统计信息")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getRiskAssessmentStatistics() {
        try {
            Map<String, Object> statistics = riskAssessmentService.getRiskAssessmentStatistics();
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取风险评估统计信息失败: {}", e.getMessage(), e);
            return Result.fail("获取风险评估统计信息失败");
        }
    }

    @Operation(summary = "批量创建行为风险")
    @PostMapping("/behavior/batch-create")
    public Result<Integer> batchCreateBehaviorRisks(@ApiParam(value = "行为风险列表", required = true) @RequestBody List<BehaviorRisk> behaviorRisks) {
        try {
            int count = riskAssessmentService.batchCreateBehaviorRisks(behaviorRisks);
            return Result.success(count);
        } catch (Exception e) {
            log.error("批量创建行为风险失败: {}", e.getMessage(), e);
            return Result.fail("批量创建行为风险失败");
        }
    }

    @Operation(summary = "批量创建设备风险")
    @PostMapping("/device/batch-create")
    public Result<Integer> batchCreateDeviceRisks(@ApiParam(value = "设备风险列表", required = true) @RequestBody List<DeviceRisk> deviceRisks) {
        try {
            int count = riskAssessmentService.batchCreateDeviceRisks(deviceRisks);
            return Result.success(count);
        } catch (Exception e) {
            log.error("批量创建设备风险失败: {}", e.getMessage(), e);
            return Result.fail("批量创建设备风险失败");
        }
    }

    @Operation(summary = "批量创建社交风险")
    @PostMapping("/social/batch-create")
    public Result<Integer> batchCreateSocialRisks(@ApiParam(value = "社交风险列表", required = true) @RequestBody List<SocialRisk> socialRisks) {
        try {
            int count = riskAssessmentService.batchCreateSocialRisks(socialRisks);
            return Result.success(count);
        } catch (Exception e) {
            log.error("批量创建社交风险失败: {}", e.getMessage(), e);
            return Result.fail("批量创建社交风险失败");
        }
    }
}
