package com.anti.fraud.modules.feedback.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.feedback.service.RealTimeFeedbackService;
import com.anti.fraud.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 实时处理反馈控制器
 */
@RestController
@RequestMapping("/feedback/realtime")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "实时处理反馈")
public class RealTimeFeedbackController {

    private final RealTimeFeedbackService realTimeFeedbackService;

    @Operation(summary = "发送实时反馈")
    @PostMapping("/send")
    public Result<Boolean> sendFeedback(
            @ApiParam(value = "反馈类型", required = true) @RequestParam String feedbackType,
            @ApiParam(value = "反馈内容", required = true) @RequestParam String content,
            @ApiParam(value = "附加数据", required = false) @RequestBody(required = false) Map<String, Object> data) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            boolean success = realTimeFeedbackService.sendFeedback(userId, feedbackType, content, data);
            if (success) {
                return Result.success("发送实时反馈成功", success);
            } else {
                return Result.fail("发送实时反馈失败，用户可能不在线");
            }
        } catch (Exception e) {
            log.error("发送实时反馈失败: {}", e.getMessage(), e);
            return Result.fail("发送实时反馈失败");
        }
    }

    @Operation(summary = "发送系统通知")
    @PostMapping("/notification")
    public Result<Void> sendSystemNotification(
            @ApiParam(value = "通知标题", required = true) @RequestParam String title,
            @ApiParam(value = "通知内容", required = true) @RequestParam String content,
            @ApiParam(value = "附加数据", required = false) @RequestBody(required = false) Map<String, Object> data) {
        try {
            realTimeFeedbackService.sendSystemNotification(title, content, data);
            return Result.successMsg("发送系统通知成功");
        } catch (Exception e) {
            log.error("发送系统通知失败: {}", e.getMessage(), e);
            return Result.fail("发送系统通知失败");
        }
    }

    @Operation(summary = "发送考试进度反馈")
    @PostMapping("/exam-progress")
    public Result<Boolean> sendExamProgress(
            @ApiParam(value = "考试ID", required = true) @RequestParam Long examId,
            @ApiParam(value = "进度（0-100）", required = true) @RequestParam int progress,
            @ApiParam(value = "当前题目", required = true) @RequestParam int currentQuestion,
            @ApiParam(value = "总题目数", required = true) @RequestParam int totalQuestions) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            boolean success = realTimeFeedbackService.sendExamProgress(userId, examId, progress, currentQuestion, totalQuestions);
            if (success) {
                return Result.success("发送考试进度反馈成功", success);
            } else {
                return Result.fail("发送考试进度反馈失败，用户可能不在线");
            }
        } catch (Exception e) {
            log.error("发送考试进度反馈失败: {}", e.getMessage(), e);
            return Result.fail("发送考试进度反馈失败");
        }
    }

    @Operation(summary = "发送任务处理状态反馈")
    @PostMapping("/task-status")
    public Result<Boolean> sendTaskStatus(
            @ApiParam(value = "任务ID", required = true) @RequestParam String taskId,
            @ApiParam(value = "任务状态", required = true) @RequestParam String status,
            @ApiParam(value = "处理进度（0-100）", required = true) @RequestParam int progress,
            @ApiParam(value = "状态消息", required = true) @RequestParam String message) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            boolean success = realTimeFeedbackService.sendTaskStatus(userId, taskId, status, progress, message);
            if (success) {
                return Result.success("发送任务状态反馈成功", success);
            } else {
                return Result.fail("发送任务状态反馈失败，用户可能不在线");
            }
        } catch (Exception e) {
            log.error("发送任务状态反馈失败: {}", e.getMessage(), e);
            return Result.fail("发送任务状态反馈失败");
        }
    }

    @Operation(summary = "发送风险预警")
    @PostMapping("/risk-alert")
    public Result<Boolean> sendRiskAlert(
            @ApiParam(value = "风险等级", required = true) @RequestParam String riskLevel,
            @ApiParam(value = "风险类型", required = true) @RequestParam String riskType,
            @ApiParam(value = "预警消息", required = true) @RequestParam String message,
            @ApiParam(value = "附加数据", required = false) @RequestBody(required = false) Map<String, Object> data) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            boolean success = realTimeFeedbackService.sendRiskAlert(userId, riskLevel, riskType, message, data);
            if (success) {
                return Result.success("发送风险预警成功", success);
            } else {
                return Result.fail("发送风险预警失败，用户可能不在线");
            }
        } catch (Exception e) {
            log.error("发送风险预警失败: {}", e.getMessage(), e);
            return Result.fail("发送风险预警失败");
        }
    }
}
