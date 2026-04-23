package com.anti.fraud.modules.task.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.task.entity.TaskInfo;
import com.anti.fraud.modules.task.service.TaskService;
import com.anti.fraud.modules.task.vo.TaskVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 任务控制器
 */
@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "任务服务")
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "获取用户任务列表")
    @GetMapping("/user-tasks")
    public Result<List<TaskVO>> getUserTasks(
            @ApiParam(value = "任务类型：1-每日任务，2-成就任务，3-赛季任务") @RequestParam(required = false) Integer taskType) {
        try {
            List<TaskVO> tasks = taskService.getUserTasks(taskType);
            return Result.success(tasks);
        } catch (Exception e) {
            log.error("获取用户任务列表失败: {}", e.getMessage(), e);
            return Result.fail("获取任务列表失败");
        }
    }

    @Operation(summary = "获取所有任务列表")
    @GetMapping("/all-tasks")
    public Result<List<TaskInfo>> getAllTasks(
            @ApiParam(value = "任务类型：1-每日任务，2-成就任务，3-赛季任务") @RequestParam(required = false) Integer taskType) {
        try {
            List<TaskInfo> tasks = taskService.getAllTasks(taskType);
            return Result.success(tasks);
        } catch (Exception e) {
            log.error("获取所有任务列表失败: {}", e.getMessage(), e);
            return Result.fail("获取任务列表失败");
        }
    }

    @Operation(summary = "更新任务进度")
    @PostMapping("/update-progress")
    public Result<Boolean> updateTaskProgress(
            @ApiParam(value = "任务ID", required = true) @RequestParam Long taskId,
            @ApiParam(value = "进度增量", required = true) @RequestParam Integer progress) {
        try {
            boolean result = taskService.updateTaskProgress(taskId, progress);
            return Result.success(result);
        } catch (Exception e) {
            log.error("更新任务进度失败: {}", e.getMessage(), e);
            return Result.fail("更新任务进度失败");
        }
    }

    @Operation(summary = "领取任务奖励")
    @PostMapping("/claim-reward")
    public Result<Boolean> claimTaskReward(
            @ApiParam(value = "任务ID", required = true) @RequestParam Long taskId,
            @ApiParam(value = "任务周期", required = true) @RequestParam String taskCycle) {
        try {
            boolean result = taskService.claimTaskReward(taskId, taskCycle);
            return Result.success(result);
        } catch (Exception e) {
            log.error("领取任务奖励失败: {}", e.getMessage(), e);
            return Result.fail("领取任务奖励失败");
        }
    }

    @Operation(summary = "获取未领取奖励的任务数量")
    @GetMapping("/unclaimed-count")
    public Result<Integer> getUnclaimedTaskCount() {
        try {
            int count = taskService.getUnclaimedTaskCount();
            return Result.success(count);
        } catch (Exception e) {
            log.error("获取未领取奖励任务数量失败: {}", e.getMessage(), e);
            return Result.fail("获取未领取奖励任务数量失败");
        }
    }

    @Operation(summary = "创建任务")
    @PostMapping("/create")
    public Result<Boolean> createTask(@RequestBody TaskInfo taskInfo) {
        try {
            boolean result = taskService.createTask(taskInfo);
            return Result.success(result);
        } catch (Exception e) {
            log.error("创建任务失败: {}", e.getMessage(), e);
            return Result.fail("创建任务失败");
        }
    }

    @Operation(summary = "更新任务")
    @PutMapping("/update")
    public Result<Boolean> updateTask(@RequestBody TaskInfo taskInfo) {
        try {
            boolean result = taskService.updateTask(taskInfo);
            return Result.success(result);
        } catch (Exception e) {
            log.error("更新任务失败: {}", e.getMessage(), e);
            return Result.fail("更新任务失败");
        }
    }

    @Operation(summary = "删除任务")
    @DeleteMapping("/delete/{taskId}")
    public Result<Boolean> deleteTask(@ApiParam(value = "任务ID", required = true) @PathVariable Long taskId) {
        try {
            boolean result = taskService.deleteTask(taskId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("删除任务失败: {}", e.getMessage(), e);
            return Result.fail("删除任务失败");
        }
    }

    @Operation(summary = "获取任务详情")
    @GetMapping("/detail/{taskId}")
    public Result<TaskInfo> getTaskById(@ApiParam(value = "任务ID", required = true) @PathVariable Long taskId) {
        try {
            TaskInfo task = taskService.getTaskById(taskId);
            return Result.success(task);
        } catch (Exception e) {
            log.error("获取任务详情失败: {}", e.getMessage(), e);
            return Result.fail("获取任务详情失败");
        }
    }

    @Operation(summary = "重置每日任务")
    @PostMapping("/reset-daily")
    public Result<Void> resetDailyTasks() {
        try {
            taskService.resetDailyTasks();
            return Result.successMsg("重置每日任务成功");
        } catch (Exception e) {
            log.error("重置每日任务失败: {}", e.getMessage(), e);
            return Result.fail("重置每日任务失败");
        }
    }
}
