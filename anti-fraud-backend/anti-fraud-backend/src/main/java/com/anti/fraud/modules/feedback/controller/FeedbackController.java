package com.anti.fraud.modules.feedback.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.feedback.entity.Feedback;
import com.anti.fraud.modules.feedback.service.FeedbackService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户评价服务控制器
 */
@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "用户评价服务")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @Operation(summary = "创建评价")
    @PostMapping("/create")
    public Result<String> createFeedback(@ApiParam(value = "评价信息", required = true) @RequestBody Feedback feedback) {
        try {
            String feedbackId = feedbackService.createFeedback(feedback);
            return Result.success(feedbackId);
        } catch (Exception e) {
            log.error("创建评价失败: {}", e.getMessage(), e);
            return Result.fail("创建评价失败");
        }
    }

    @Operation(summary = "更新评价")
    @PutMapping("/update")
    public Result<Void> updateFeedback(@ApiParam(value = "评价信息", required = true) @RequestBody Feedback feedback) {
        try {
            boolean success = feedbackService.updateFeedback(feedback);
            if (success) {
                return Result.successMsg("更新评价成功");
            } else {
                return Result.fail("更新评价失败");
            }
        } catch (Exception e) {
            log.error("更新评价失败: {}", e.getMessage(), e);
            return Result.fail("更新评价失败");
        }
    }

    @Operation(summary = "删除评价")
    @DeleteMapping("/delete/{feedbackId}")
    public Result<Void> deleteFeedback(@ApiParam(value = "评价ID", required = true) @PathVariable String feedbackId) {
        try {
            boolean success = feedbackService.deleteFeedback(feedbackId);
            if (success) {
                return Result.successMsg("删除评价成功");
            } else {
                return Result.fail("删除评价失败");
            }
        } catch (Exception e) {
            log.error("删除评价失败: {}", e.getMessage(), e);
            return Result.fail("删除评价失败");
        }
    }

    @Operation(summary = "获取评价详情")
    @GetMapping("/detail/{feedbackId}")
    public Result<Feedback> getFeedbackByFeedbackId(@ApiParam(value = "评价ID", required = true) @PathVariable String feedbackId) {
        try {
            Feedback feedback = feedbackService.getFeedbackByFeedbackId(feedbackId);
            if (feedback != null) {
                return Result.success(feedback);
            } else {
                return Result.fail("评价不存在");
            }
        } catch (Exception e) {
            log.error("获取评价详情失败: {}", e.getMessage(), e);
            return Result.fail("获取评价详情失败");
        }
    }

    @Operation(summary = "根据消息ID获取评价")
    @GetMapping("/by-message/{messageId}")
    public Result<Feedback> getFeedbackByMessageId(@ApiParam(value = "消息ID", required = true) @PathVariable String messageId) {
        try {
            Feedback feedback = feedbackService.getFeedbackByMessageId(messageId);
            if (feedback != null) {
                return Result.success(feedback);
            } else {
                return Result.fail("评价不存在");
            }
        } catch (Exception e) {
            log.error("根据消息ID获取评价失败: {}", e.getMessage(), e);
            return Result.fail("根据消息ID获取评价失败");
        }
    }

    @Operation(summary = "分页查询评价列表")
    @GetMapping("/list")
    public Result<Map<String, Object>> getFeedbackList(
            @ApiParam(value = "评价类型: 1-满意，2-不满意，3-一般", required = false) @RequestParam(required = false) Integer type,
            @ApiParam(value = "状态: 1-已提交，2-已处理，3-已忽略", required = false) @RequestParam(required = false) Integer status,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = feedbackService.getFeedbackList(type, status, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询评价列表失败: {}", e.getMessage(), e);
            return Result.fail("查询评价列表失败");
        }
    }

    @Operation(summary = "根据用户ID查询评价列表")
    @GetMapping("/list-by-user")
    public Result<Map<String, Object>> getFeedbackListByUserId(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "状态: 1-已提交，2-已处理，3-已忽略", required = false) @RequestParam(required = false) Integer status,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = feedbackService.getFeedbackListByUserId(userId, status, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("根据用户ID查询评价列表失败: {}", e.getMessage(), e);
            return Result.fail("根据用户ID查询评价列表失败");
        }
    }

    @Operation(summary = "根据会话ID查询评价列表")
    @GetMapping("/list-by-session")
    public Result<Map<String, Object>> getFeedbackListBySessionId(
            @ApiParam(value = "会话ID", required = true) @RequestParam String sessionId,
            @ApiParam(value = "状态: 1-已提交，2-已处理，3-已忽略", required = false) @RequestParam(required = false) Integer status,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = feedbackService.getFeedbackListBySessionId(sessionId, status, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("根据会话ID查询评价列表失败: {}", e.getMessage(), e);
            return Result.fail("根据会话ID查询评价列表失败");
        }
    }

    @Operation(summary = "更新评价状态")
    @PutMapping("/update-status/{feedbackId}")
    public Result<Void> updateFeedbackStatus(
            @ApiParam(value = "评价ID", required = true) @PathVariable String feedbackId,
            @ApiParam(value = "状态: 1-已提交，2-已处理，3-已忽略", required = true) @RequestParam Integer status) {
        try {
            boolean success = feedbackService.updateFeedbackStatus(feedbackId, status);
            if (success) {
                return Result.successMsg("更新评价状态成功");
            } else {
                return Result.fail("更新评价状态失败");
            }
        } catch (Exception e) {
            log.error("更新评价状态失败: {}", e.getMessage(), e);
            return Result.fail("更新评价状态失败");
        }
    }

    @Operation(summary = "处理评价")
    @PutMapping("/handle/{feedbackId}")
    public Result<Void> handleFeedback(
            @ApiParam(value = "评价ID", required = true) @PathVariable String feedbackId,
            @ApiParam(value = "处理人", required = true) @RequestParam String handler,
            @ApiParam(value = "处理结果", required = true) @RequestParam String handleResult) {
        try {
            boolean success = feedbackService.handleFeedback(feedbackId, handler, handleResult);
            if (success) {
                return Result.successMsg("处理评价成功");
            } else {
                return Result.fail("处理评价失败");
            }
        } catch (Exception e) {
            log.error("处理评价失败: {}", e.getMessage(), e);
            return Result.fail("处理评价失败");
        }
    }

    @Operation(summary = "统计评价数量")
    @GetMapping("/count")
    public Result<Integer> countFeedback(
            @ApiParam(value = "评价类型: 1-满意，2-不满意，3-一般", required = false) @RequestParam(required = false) Integer type,
            @ApiParam(value = "状态: 1-已提交，2-已处理，3-已忽略", required = false) @RequestParam(required = false) Integer status) {
        try {
            Integer count = feedbackService.countFeedback(type, status);
            return Result.success(count);
        } catch (Exception e) {
            log.error("统计评价数量失败: {}", e.getMessage(), e);
            return Result.fail("统计评价数量失败");
        }
    }

    @Operation(summary = "统计用户评价数量")
    @GetMapping("/count-by-user/{userId}")
    public Result<Integer> countFeedbackByUserId(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long userId,
            @ApiParam(value = "状态: 1-已提交，2-已处理，3-已忽略", required = false) @RequestParam(required = false) Integer status) {
        try {
            Integer count = feedbackService.countFeedbackByUserId(userId, status);
            return Result.success(count);
        } catch (Exception e) {
            log.error("统计用户评价数量失败: {}", e.getMessage(), e);
            return Result.fail("统计用户评价数量失败");
        }
    }

    @Operation(summary = "统计会话评价数量")
    @GetMapping("/count-by-session/{sessionId}")
    public Result<Integer> countFeedbackBySessionId(
            @ApiParam(value = "会话ID", required = true) @PathVariable String sessionId,
            @ApiParam(value = "状态: 1-已提交，2-已处理，3-已忽略", required = false) @RequestParam(required = false) Integer status) {
        try {
            Integer count = feedbackService.countFeedbackBySessionId(sessionId, status);
            return Result.success(count);
        } catch (Exception e) {
            log.error("统计会话评价数量失败: {}", e.getMessage(), e);
            return Result.fail("统计会话评价数量失败");
        }
    }

    @Operation(summary = "计算平均评分")
    @GetMapping("/average-score")
    public Result<Double> calculateAverageScore(
            @ApiParam(value = "评价类型: 1-满意，2-不满意，3-一般", required = false) @RequestParam(required = false) Integer type,
            @ApiParam(value = "状态: 1-已提交，2-已处理，3-已忽略", required = false) @RequestParam(required = false) Integer status) {
        try {
            Double averageScore = feedbackService.calculateAverageScore(type, status);
            return Result.success(averageScore);
        } catch (Exception e) {
            log.error("计算平均评分失败: {}", e.getMessage(), e);
            return Result.fail("计算平均评分失败");
        }
    }

    @Operation(summary = "获取最近的评价")
    @GetMapping("/recent")
    public Result<List<Feedback>> getRecentFeedback(
            @ApiParam(value = "数量限制", required = true) @RequestParam Integer limit,
            @ApiParam(value = "状态: 1-已提交，2-已处理，3-已忽略", required = false) @RequestParam(required = false) Integer status) {
        try {
            List<Feedback> feedbacks = feedbackService.getRecentFeedback(limit, status);
            return Result.success(feedbacks);
        } catch (Exception e) {
            log.error("获取最近的评价失败: {}", e.getMessage(), e);
            return Result.fail("获取最近的评价失败");
        }
    }

    @Operation(summary = "批量创建评价")
    @PostMapping("/batch-create")
    public Result<Integer> batchCreateFeedback(@ApiParam(value = "评价列表", required = true) @RequestBody List<Feedback> feedbacks) {
        try {
            int count = feedbackService.batchCreateFeedback(feedbacks);
            return Result.success(count);
        } catch (Exception e) {
            log.error("批量创建评价失败: {}", e.getMessage(), e);
            return Result.fail("批量创建评价失败");
        }
    }

    @Operation(summary = "获取评价统计信息")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getFeedbackStatistics() {
        try {
            Map<String, Object> statistics = feedbackService.getFeedbackStatistics();
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取评价统计信息失败: {}", e.getMessage(), e);
            return Result.fail("获取评价统计信息失败");
        }
    }
}
