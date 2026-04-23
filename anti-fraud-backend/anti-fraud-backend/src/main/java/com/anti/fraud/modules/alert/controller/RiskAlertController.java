package com.anti.fraud.modules.alert.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.alert.entity.RiskAlert;
import com.anti.fraud.modules.alert.service.RiskAlertService;
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
 * 风险预警服务控制器
 */
@RestController
@RequestMapping("/alert/risk")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "风险预警服务")
public class RiskAlertController {

    private final RiskAlertService riskAlertService;

    @Operation(summary = "创建预警")
    @PostMapping("/create")
    public Result<String> createRiskAlert(@ApiParam(value = "预警信息", required = true) @RequestBody RiskAlert riskAlert) {
        try {
            String alertId = riskAlertService.createRiskAlert(riskAlert);
            return Result.success(alertId);
        } catch (Exception e) {
            log.error("创建预警失败: {}", e.getMessage(), e);
            return Result.fail("创建预警失败");
        }
    }

    @Operation(summary = "更新预警")
    @PostMapping("/update")
    public Result<Void> updateRiskAlert(@ApiParam(value = "预警信息", required = true) @RequestBody RiskAlert riskAlert) {
        try {
            boolean success = riskAlertService.updateRiskAlert(riskAlert);
            if (success) {
                return Result.successMsg("更新预警成功");
            } else {
                return Result.fail("更新预警失败");
            }
        } catch (Exception e) {
            log.error("更新预警失败: {}", e.getMessage(), e);
            return Result.fail("更新预警失败");
        }
    }

    @Operation(summary = "删除预警")
    @DeleteMapping("/delete/{alertId}")
    public Result<Void> deleteRiskAlert(@ApiParam(value = "预警ID", required = true) @PathVariable String alertId) {
        try {
            boolean success = riskAlertService.deleteRiskAlert(alertId);
            if (success) {
                return Result.successMsg("删除预警成功");
            } else {
                return Result.fail("删除预警失败");
            }
        } catch (Exception e) {
            log.error("删除预警失败: {}", e.getMessage(), e);
            return Result.fail("删除预警失败");
        }
    }

    @Operation(summary = "获取预警详情")
    @GetMapping("/detail/{alertId}")
    public Result<RiskAlert> getRiskAlertByAlertId(@ApiParam(value = "预警ID", required = true) @PathVariable String alertId) {
        try {
            RiskAlert alert = riskAlertService.getRiskAlertByAlertId(alertId);
            if (alert != null) {
                return Result.success(alert);
            } else {
                return Result.fail("预警不存在");
            }
        } catch (Exception e) {
            log.error("获取预警详情失败: {}", e.getMessage(), e);
            return Result.fail("获取预警详情失败");
        }
    }

    @Operation(summary = "分页查询预警列表")
    @GetMapping("/list")
    public Result<Map<String, Object>> getRiskAlertList(
            @ApiParam(value = "用户ID", required = false) @RequestParam(required = false) Long userId,
            @ApiParam(value = "预警类型: 1-行为风险预警，2-设备风险预警，3-社交风险预警，4-综合风险预警", required = false) @RequestParam(required = false) Integer alertType,
            @ApiParam(value = "风险等级: 1-低风险，2-中风险，3-高风险，4-极高风险", required = false) @RequestParam(required = false) Integer riskLevel,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = riskAlertService.getRiskAlertList(userId, alertType, riskLevel, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询预警列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return Result.fail("查询预警列表失败");
        }
    }

    @Operation(summary = "根据时间范围查询预警列表")
    @GetMapping("/list-by-time-range")
    public Result<List<RiskAlert>> getRiskAlertListByTimeRange(
            @ApiParam(value = "开始时间", required = true) @RequestParam LocalDateTime startTime,
            @ApiParam(value = "结束时间", required = true) @RequestParam LocalDateTime endTime,
            @ApiParam(value = "用户ID", required = false) @RequestParam(required = false) Long userId,
            @ApiParam(value = "预警类型: 1-行为风险预警，2-设备风险预警，3-社交风险预警，4-综合风险预警", required = false) @RequestParam(required = false) Integer alertType,
            @ApiParam(value = "风险等级: 1-低风险，2-中风险，3-高风险，4-极高风险", required = false) @RequestParam(required = false) Integer riskLevel) {
        try {
            List<RiskAlert> alerts = riskAlertService.getRiskAlertListByTimeRange(startTime, endTime, userId, alertType, riskLevel);
            return Result.success(alerts);
        } catch (Exception e) {
            log.error("根据时间范围查询预警列表失败: {}", e.getMessage(), e);
            return Result.fail("根据时间范围查询预警列表失败");
        }
    }

    @Operation(summary = "发送预警")
    @PostMapping("/send/{alertId}")
    public Result<Void> sendAlert(@ApiParam(value = "预警ID", required = true) @PathVariable String alertId) {
        try {
            boolean success = riskAlertService.sendAlert(alertId);
            if (success) {
                return Result.successMsg("发送预警成功");
            } else {
                return Result.fail("发送预警失败");
            }
        } catch (Exception e) {
            log.error("发送预警失败: {}", e.getMessage(), e);
            return Result.fail("发送预警失败");
        }
    }

    @Operation(summary = "WebSocket推送预警")
    @PostMapping("/send-websocket/{alertId}")
    public Result<Void> sendWebSocketAlert(@ApiParam(value = "预警ID", required = true) @PathVariable String alertId) {
        try {
            boolean success = riskAlertService.sendWebSocketAlert(alertId);
            if (success) {
                return Result.successMsg("WebSocket推送预警成功");
            } else {
                return Result.fail("WebSocket推送预警失败");
            }
        } catch (Exception e) {
            log.error("WebSocket推送预警失败: {}", e.getMessage(), e);
            return Result.fail("WebSocket推送预警失败");
        }
    }

    @Operation(summary = "短信发送预警")
    @PostMapping("/send-sms/{alertId}")
    public Result<Void> sendSmsAlert(@ApiParam(value = "预警ID", required = true) @PathVariable String alertId) {
        try {
            boolean success = riskAlertService.sendSmsAlert(alertId);
            if (success) {
                return Result.successMsg("短信发送预警成功");
            } else {
                return Result.fail("短信发送预警失败");
            }
        } catch (Exception e) {
            log.error("短信发送预警失败: {}", e.getMessage(), e);
            return Result.fail("短信发送预警失败");
        }
    }

    @Operation(summary = "邮件发送预警")
    @PostMapping("/send-email/{alertId}")
    public Result<Void> sendEmailAlert(@ApiParam(value = "预警ID", required = true) @PathVariable String alertId) {
        try {
            boolean success = riskAlertService.sendEmailAlert(alertId);
            if (success) {
                return Result.successMsg("邮件发送预警成功");
            } else {
                return Result.fail("邮件发送预警失败");
            }
        } catch (Exception e) {
            log.error("邮件发送预警失败: {}", e.getMessage(), e);
            return Result.fail("邮件发送预警失败");
        }
    }

    @Operation(summary = "创建并发送预警")
    @PostMapping("/create-and-send")
    public Result<String> createAndSendAlert(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "用户名", required = true) @RequestParam String username,
            @ApiParam(value = "预警类型: 1-行为风险预警，2-设备风险预警，3-社交风险预警，4-综合风险预警", required = true) @RequestParam Integer alertType,
            @ApiParam(value = "风险等级: 1-低风险，2-中风险，3-高风险，4-极高风险", required = true) @RequestParam Integer riskLevel,
            @ApiParam(value = "预警标题", required = true) @RequestParam String alertTitle,
            @ApiParam(value = "预警内容", required = true) @RequestParam String alertContent,
            @ApiParam(value = "预警详情", required = false) @RequestParam(required = false) String alertDetails,
            @ApiParam(value = "通知方式: 1-WebSocket，2-短信，3-邮件，4-全部", required = true) @RequestParam Integer notificationMethod,
            @ApiParam(value = "手机号", required = false) @RequestParam(required = false) String phoneNumber,
            @ApiParam(value = "邮箱", required = false) @RequestParam(required = false) String email) {
        try {
            String alertId = riskAlertService.createAndSendAlert(userId, username, alertType, riskLevel, alertTitle, alertContent, alertDetails, notificationMethod, phoneNumber, email);
            return Result.success(alertId);
        } catch (Exception e) {
            log.error("创建并发送预警失败: {}", e.getMessage(), e);
            return Result.fail("创建并发送预警失败");
        }
    }

    @Operation(summary = "批量创建预警")
    @PostMapping("/batch-create")
    public Result<Integer> batchCreateAlerts(@ApiParam(value = "预警列表", required = true) @RequestBody List<RiskAlert> alerts) {
        try {
            int count = riskAlertService.batchCreateAlerts(alerts);
            return Result.success(count);
        } catch (Exception e) {
            log.error("批量创建预警失败: {}", e.getMessage(), e);
            return Result.fail("批量创建预警失败");
        }
    }

    @Operation(summary = "获取预警统计信息")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getRiskAlertStatistics() {
        try {
            Map<String, Object> statistics = riskAlertService.getRiskAlertStatistics();
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取预警统计信息失败: {}", e.getMessage(), e);
            return Result.fail("获取预警统计信息失败");
        }
    }

    @Operation(summary = "更新预警状态")
    @PostMapping("/update-status/{alertId}")
    public Result<Void> updateAlertStatus(
            @ApiParam(value = "预警ID", required = true) @PathVariable String alertId,
            @ApiParam(value = "状态: 1-已处理，2-处理中，3-处理失败", required = true) @RequestParam Integer status) {
        try {
            boolean success = riskAlertService.updateAlertStatus(alertId, status);
            if (success) {
                return Result.successMsg("更新预警状态成功");
            } else {
                return Result.fail("更新预警状态失败");
            }
        } catch (Exception e) {
            log.error("更新预警状态失败: {}", e.getMessage(), e);
            return Result.fail("更新预警状态失败");
        }
    }
}
