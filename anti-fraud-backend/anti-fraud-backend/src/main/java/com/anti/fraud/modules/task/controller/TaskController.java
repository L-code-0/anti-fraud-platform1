package com.anti.fraud.modules.task.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.task.dto.TaskCreateDTO;
import com.anti.fraud.modules.task.service.TaskService;
import com.anti.fraud.modules.task.vo.TaskVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "班级任务", description = "班级任务管理接口")
@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "创建任务", security = @SecurityRequirement(name = "Bearer"))
    @PostMapping
    public Result<Long> createTask(@RequestBody TaskCreateDTO dto) {
        Long taskId = taskService.createTask(dto);
        return Result.success(taskId, "创建成功");
    }

    @Operation(summary = "我发布的任务列表（教师）", security = @SecurityRequirement(name = "Bearer"))
    @GetMapping("/my")
    public Result<Page<TaskVO>> getMyTasks(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(taskService.getMyTasks(page, size));
    }

    @Operation(summary = "学生的任务列表", security = @SecurityRequirement(name = "Bearer"))
    @GetMapping("/student")
    public Result<Page<TaskVO>> getStudentTasks(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(taskService.getStudentTasks(page, size));
    }

    @Operation(summary = "任务详情", security = @SecurityRequirement(name = "Bearer"))
    @GetMapping("/{id}")
    public Result<TaskVO> getTaskDetail(@PathVariable Long id) {
        return Result.success(taskService.getTaskDetail(id));
    }

    @Operation(summary = "完成任务（学生）", security = @SecurityRequirement(name = "Bearer"))
    @PostMapping("/{id}/complete")
    public Result<Void> completeTask(
            @PathVariable Long id,
            @RequestParam(required = false) Integer score) {
        taskService.completeTask(id, score);
        return Result.successMsg("完成成功");
    }

    @Operation(summary = "任务完成情况", security = @SecurityRequirement(name = "Bearer"))
    @GetMapping("/{id}/completions")
    public Result<Page<?>> getTaskCompletions(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(taskService.getTaskCompletions(id, page, size));
    }

    @Operation(summary = "删除任务", security = @SecurityRequirement(name = "Bearer"))
    @DeleteMapping("/{id}")
    public Result<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return Result.successMsg("删除成功");
    }
}
