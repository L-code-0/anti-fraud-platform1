package com.anti.fraud.modules.exercise.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.exercise.entity.ExerciseEvent;
import com.anti.fraud.modules.exercise.service.ExerciseEventService;
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
 * 演练事件服务控制器
 */
@RestController
@RequestMapping("/exercise/event")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "演练事件服务")
public class ExerciseEventController {

    private final ExerciseEventService exerciseEventService;

    @Operation(summary = "创建演练事件")
    @PostMapping("/create")
    public Result<String> createExerciseEvent(@ApiParam(value = "演练事件信息", required = true) @RequestBody ExerciseEvent exerciseEvent) {
        try {
            String eventId = exerciseEventService.createExerciseEvent(exerciseEvent);
            return Result.success(eventId);
        } catch (Exception e) {
            log.error("创建演练事件失败: {}", e.getMessage(), e);
            return Result.fail("创建演练事件失败");
        }
    }

    @Operation(summary = "更新演练事件")
    @PutMapping("/update")
    public Result<Void> updateExerciseEvent(@ApiParam(value = "演练事件信息", required = true) @RequestBody ExerciseEvent exerciseEvent) {
        try {
            boolean success = exerciseEventService.updateExerciseEvent(exerciseEvent);
            if (success) {
                return Result.successMsg("更新演练事件成功");
            } else {
                return Result.fail("更新演练事件失败");
            }
        } catch (Exception e) {
            log.error("更新演练事件失败: {}", e.getMessage(), e);
            return Result.fail("更新演练事件失败");
        }
    }

    @Operation(summary = "删除演练事件")
    @DeleteMapping("/delete/{eventId}")
    public Result<Void> deleteExerciseEvent(@ApiParam(value = "事件ID", required = true) @PathVariable String eventId) {
        try {
            boolean success = exerciseEventService.deleteExerciseEvent(eventId);
            if (success) {
                return Result.successMsg("删除演练事件成功");
            } else {
                return Result.fail("删除演练事件失败");
            }
        } catch (Exception e) {
            log.error("删除演练事件失败: {}", e.getMessage(), e);
            return Result.fail("删除演练事件失败");
        }
    }

    @Operation(summary = "获取演练事件详情")
    @GetMapping("/detail/{eventId}")
    public Result<ExerciseEvent> getExerciseEventByEventId(@ApiParam(value = "事件ID", required = true) @PathVariable String eventId) {
        try {
            ExerciseEvent event = exerciseEventService.getExerciseEventByEventId(eventId);
            if (event != null) {
                return Result.success(event);
            } else {
                return Result.fail("演练事件不存在");
            }
        } catch (Exception e) {
            log.error("获取演练事件详情失败: {}", e.getMessage(), e);
            return Result.fail("获取演练事件详情失败");
        }
    }

    @Operation(summary = "分页查询演练事件列表")
    @GetMapping("/list")
    public Result<Map<String, Object>> getExerciseEventList(
            @ApiParam(value = "事件类型: 1-开始演练，2-结束演练，3-选择答案，4-查看解析，5-获得分数，6-其他", required = false) @RequestParam(required = false) Integer eventType,
            @ApiParam(value = "状态: 1-已处理，2-处理中，3-处理失败", required = false) @RequestParam(required = false) Integer status,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = exerciseEventService.getExerciseEventList(eventType, status, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询演练事件列表失败: {}", e.getMessage(), e);
            return Result.fail("查询演练事件列表失败");
        }
    }

    @Operation(summary = "根据演练ID查询事件列表")
    @GetMapping("/list-by-exercise")
    public Result<Map<String, Object>> getExerciseEventListByExerciseId(
            @ApiParam(value = "演练ID", required = true) @RequestParam String exerciseId,
            @ApiParam(value = "事件类型: 1-开始演练，2-结束演练，3-选择答案，4-查看解析，5-获得分数，6-其他", required = false) @RequestParam(required = false) Integer eventType,
            @ApiParam(value = "状态: 1-已处理，2-处理中，3-处理失败", required = false) @RequestParam(required = false) Integer status,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = exerciseEventService.getExerciseEventListByExerciseId(exerciseId, eventType, status, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("根据演练ID查询事件列表失败: {}", e.getMessage(), e);
            return Result.fail("根据演练ID查询事件列表失败");
        }
    }

    @Operation(summary = "根据用户ID查询事件列表")
    @GetMapping("/list-by-user")
    public Result<Map<String, Object>> getExerciseEventListByUserId(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "事件类型: 1-开始演练，2-结束演练，3-选择答案，4-查看解析，5-获得分数，6-其他", required = false) @RequestParam(required = false) Integer eventType,
            @ApiParam(value = "状态: 1-已处理，2-处理中，3-处理失败", required = false) @RequestParam(required = false) Integer status,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = exerciseEventService.getExerciseEventListByUserId(userId, eventType, status, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("根据用户ID查询事件列表失败: {}", e.getMessage(), e);
            return Result.fail("根据用户ID查询事件列表失败");
        }
    }

    @Operation(summary = "根据时间范围查询事件列表")
    @GetMapping("/list-by-time-range")
    public Result<List<ExerciseEvent>> getExerciseEventListByTimeRange(
            @ApiParam(value = "开始时间", required = true) @RequestParam LocalDateTime startTime,
            @ApiParam(value = "结束时间", required = true) @RequestParam LocalDateTime endTime,
            @ApiParam(value = "事件类型: 1-开始演练，2-结束演练，3-选择答案，4-查看解析，5-获得分数，6-其他", required = false) @RequestParam(required = false) Integer eventType,
            @ApiParam(value = "状态: 1-已处理，2-处理中，3-处理失败", required = false) @RequestParam(required = false) Integer status) {
        try {
            List<ExerciseEvent> events = exerciseEventService.getExerciseEventListByTimeRange(startTime, endTime, eventType, status);
            return Result.success(events);
        } catch (Exception e) {
            log.error("根据时间范围查询事件列表失败: {}", e.getMessage(), e);
            return Result.fail("根据时间范围查询事件列表失败");
        }
    }

    @Operation(summary = "更新事件状态")
    @PutMapping("/update-status/{eventId}")
    public Result<Void> updateExerciseEventStatus(
            @ApiParam(value = "事件ID", required = true) @PathVariable String eventId,
            @ApiParam(value = "状态: 1-已处理，2-处理中，3-处理失败", required = true) @RequestParam Integer status) {
        try {
            boolean success = exerciseEventService.updateExerciseEventStatus(eventId, status);
            if (success) {
                return Result.successMsg("更新事件状态成功");
            } else {
                return Result.fail("更新事件状态失败");
            }
        } catch (Exception e) {
            log.error("更新事件状态失败: {}", e.getMessage(), e);
            return Result.fail("更新事件状态失败");
        }
    }

    @Operation(summary = "统计事件数量")
    @GetMapping("/count")
    public Result<Integer> countExerciseEvent(
            @ApiParam(value = "事件类型: 1-开始演练，2-结束演练，3-选择答案，4-查看解析，5-获得分数，6-其他", required = false) @RequestParam(required = false) Integer eventType,
            @ApiParam(value = "状态: 1-已处理，2-处理中，3-处理失败", required = false) @RequestParam(required = false) Integer status) {
        try {
            Integer count = exerciseEventService.countExerciseEvent(eventType, status);
            return Result.success(count);
        } catch (Exception e) {
            log.error("统计事件数量失败: {}", e.getMessage(), e);
            return Result.fail("统计事件数量失败");
        }
    }

    @Operation(summary = "统计用户事件数量")
    @GetMapping("/count-by-user/{userId}")
    public Result<Integer> countExerciseEventByUserId(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long userId,
            @ApiParam(value = "事件类型: 1-开始演练，2-结束演练，3-选择答案，4-查看解析，5-获得分数，6-其他", required = false) @RequestParam(required = false) Integer eventType,
            @ApiParam(value = "状态: 1-已处理，2-处理中，3-处理失败", required = false) @RequestParam(required = false) Integer status) {
        try {
            Integer count = exerciseEventService.countExerciseEventByUserId(userId, eventType, status);
            return Result.success(count);
        } catch (Exception e) {
            log.error("统计用户事件数量失败: {}", e.getMessage(), e);
            return Result.fail("统计用户事件数量失败");
        }
    }

    @Operation(summary = "统计演练事件数量")
    @GetMapping("/count-by-exercise/{exerciseId}")
    public Result<Integer> countExerciseEventByExerciseId(
            @ApiParam(value = "演练ID", required = true) @PathVariable String exerciseId,
            @ApiParam(value = "事件类型: 1-开始演练，2-结束演练，3-选择答案，4-查看解析，5-获得分数，6-其他", required = false) @RequestParam(required = false) Integer eventType,
            @ApiParam(value = "状态: 1-已处理，2-处理中，3-处理失败", required = false) @RequestParam(required = false) Integer status) {
        try {
            Integer count = exerciseEventService.countExerciseEventByExerciseId(exerciseId, eventType, status);
            return Result.success(count);
        } catch (Exception e) {
            log.error("统计演练事件数量失败: {}", e.getMessage(), e);
            return Result.fail("统计演练事件数量失败");
        }
    }

    @Operation(summary = "获取最近的事件")
    @GetMapping("/recent")
    public Result<List<ExerciseEvent>> getRecentExerciseEvents(
            @ApiParam(value = "数量限制", required = true) @RequestParam Integer limit,
            @ApiParam(value = "事件类型: 1-开始演练，2-结束演练，3-选择答案，4-查看解析，5-获得分数，6-其他", required = false) @RequestParam(required = false) Integer eventType,
            @ApiParam(value = "状态: 1-已处理，2-处理中，3-处理失败", required = false) @RequestParam(required = false) Integer status) {
        try {
            List<ExerciseEvent> events = exerciseEventService.getRecentExerciseEvents(limit, eventType, status);
            return Result.success(events);
        } catch (Exception e) {
            log.error("获取最近的事件失败: {}", e.getMessage(), e);
            return Result.fail("获取最近的事件失败");
        }
    }

    @Operation(summary = "批量创建演练事件")
    @PostMapping("/batch-create")
    public Result<Integer> batchCreateExerciseEvent(@ApiParam(value = "事件列表", required = true) @RequestBody List<ExerciseEvent> events) {
        try {
            int count = exerciseEventService.batchCreateExerciseEvent(events);
            return Result.success(count);
        } catch (Exception e) {
            log.error("批量创建演练事件失败: {}", e.getMessage(), e);
            return Result.fail("批量创建演练事件失败");
        }
    }

    @Operation(summary = "获取事件统计信息")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getExerciseEventStatistics() {
        try {
            Map<String, Object> statistics = exerciseEventService.getExerciseEventStatistics();
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取事件统计信息失败: {}", e.getMessage(), e);
            return Result.fail("获取事件统计信息失败");
        }
    }

    @Operation(summary = "处理演练事件")
    @PostMapping("/process/{eventId}")
    public Result<Void> processExerciseEvent(@ApiParam(value = "事件ID", required = true) @PathVariable String eventId) {
        try {
            boolean success = exerciseEventService.processExerciseEvent(eventId);
            if (success) {
                return Result.successMsg("处理演练事件成功");
            } else {
                return Result.fail("处理演练事件失败");
            }
        } catch (Exception e) {
            log.error("处理演练事件失败: {}", e.getMessage(), e);
            return Result.fail("处理演练事件失败");
        }
    }

    @Operation(summary = "记录开始演练事件")
    @PostMapping("/record-start")
    public Result<String> recordStartExerciseEvent(
            @ApiParam(value = "演练ID", required = true) @RequestParam String exerciseId,
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "用户名", required = true) @RequestParam String username) {
        try {
            String eventId = exerciseEventService.recordStartExerciseEvent(exerciseId, userId, username);
            return Result.success(eventId);
        } catch (Exception e) {
            log.error("记录开始演练事件失败: {}", e.getMessage(), e);
            return Result.fail("记录开始演练事件失败");
        }
    }

    @Operation(summary = "记录结束演练事件")
    @PostMapping("/record-end")
    public Result<String> recordEndExerciseEvent(
            @ApiParam(value = "演练ID", required = true) @RequestParam String exerciseId,
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "用户名", required = true) @RequestParam String username,
            @ApiParam(value = "分数", required = true) @RequestParam Integer score,
            @ApiParam(value = "持续时间（秒）", required = true) @RequestParam Integer duration) {
        try {
            String eventId = exerciseEventService.recordEndExerciseEvent(exerciseId, userId, username, score, duration);
            return Result.success(eventId);
        } catch (Exception e) {
            log.error("记录结束演练事件失败: {}", e.getMessage(), e);
            return Result.fail("记录结束演练事件失败");
        }
    }

    @Operation(summary = "记录选择答案事件")
    @PostMapping("/record-select-answer")
    public Result<String> recordSelectAnswerEvent(
            @ApiParam(value = "演练ID", required = true) @RequestParam String exerciseId,
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "用户名", required = true) @RequestParam String username,
            @ApiParam(value = "题目ID", required = true) @RequestParam String questionId,
            @ApiParam(value = "选择的答案", required = true) @RequestParam String selectedAnswer,
            @ApiParam(value = "正确答案", required = true) @RequestParam String correctAnswer,
            @ApiParam(value = "是否正确", required = true) @RequestParam Boolean isCorrect) {
        try {
            String eventId = exerciseEventService.recordSelectAnswerEvent(exerciseId, userId, username, questionId, selectedAnswer, correctAnswer, isCorrect);
            return Result.success(eventId);
        } catch (Exception e) {
            log.error("记录选择答案事件失败: {}", e.getMessage(), e);
            return Result.fail("记录选择答案事件失败");
        }
    }

    @Operation(summary = "记录查看解析事件")
    @PostMapping("/record-view-explanation")
    public Result<String> recordViewExplanationEvent(
            @ApiParam(value = "演练ID", required = true) @RequestParam String exerciseId,
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "用户名", required = true) @RequestParam String username,
            @ApiParam(value = "题目ID", required = true) @RequestParam String questionId) {
        try {
            String eventId = exerciseEventService.recordViewExplanationEvent(exerciseId, userId, username, questionId);
            return Result.success(eventId);
        } catch (Exception e) {
            log.error("记录查看解析事件失败: {}", e.getMessage(), e);
            return Result.fail("记录查看解析事件失败");
        }
    }

    @Operation(summary = "记录获得分数事件")
    @PostMapping("/record-score")
    public Result<String> recordScoreEvent(
            @ApiParam(value = "演练ID", required = true) @RequestParam String exerciseId,
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "用户名", required = true) @RequestParam String username,
            @ApiParam(value = "分数", required = true) @RequestParam Integer score,
            @ApiParam(value = "分数类型: 1-题目得分，2-演练得分", required = true) @RequestParam Integer type) {
        try {
            String eventId = exerciseEventService.recordScoreEvent(exerciseId, userId, username, score, type);
            return Result.success(eventId);
        } catch (Exception e) {
            log.error("记录获得分数事件失败: {}", e.getMessage(), e);
            return Result.fail("记录获得分数事件失败");
        }
    }
}
