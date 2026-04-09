package com.anti.fraud.modules.activity.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.activity.dto.ActivityCreateDTO;
import com.anti.fraud.modules.activity.service.ActivityService;
import com.anti.fraud.modules.activity.vo.ActivityVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "活动管理", description = "活动管理相关接口")
@RestController
@RequestMapping("/activity")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @Operation(summary = "分页获取活动列表")
    @GetMapping("/list")
    public Result<Page<ActivityVO>> getActivityList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer type) {
        return Result.success(activityService.getActivityPage(page, size, status, type));
    }

    @Operation(summary = "获取活动详情")
    @GetMapping("/{id}")
    public Result<ActivityVO> getActivityDetail(@PathVariable Long id) {
        return Result.success(activityService.getActivityDetail(id));
    }

    @Operation(summary = "创建活动", security = @SecurityRequirement(name = "Bearer"))
    @PostMapping
    @PreAuthorize("hasRole('3') or hasRole('5')")
    public Result<Void> createActivity(@RequestBody ActivityCreateDTO createDTO) {
        activityService.createActivity(createDTO);
        return Result.successMsg("创建成功");
    }

    @Operation(summary = "更新活动", security = @SecurityRequirement(name = "Bearer"))
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('3') or hasRole('5')")
    public Result<Void> updateActivity(@PathVariable Long id, @RequestBody ActivityCreateDTO createDTO) {
        activityService.updateActivity(id, createDTO);
        return Result.successMsg("更新成功");
    }

    @Operation(summary = "删除活动", security = @SecurityRequirement(name = "Bearer"))
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('3') or hasRole('5')")
    public Result<Void> deleteActivity(@PathVariable Long id) {
        activityService.deleteActivity(id);
        return Result.successMsg("删除成功");
    }

    @Operation(summary = "报名活动", security = @SecurityRequirement(name = "Bearer"))
    @PostMapping("/{id}/register")
    public Result<Void> registerActivity(@PathVariable Long id) {
        activityService.registerActivity(id);
        return Result.successMsg("报名成功");
    }

    @Operation(summary = "取消报名", security = @SecurityRequirement(name = "Bearer"))
    @DeleteMapping("/{id}/register")
    public Result<Void> cancelRegistration(@PathVariable Long id) {
        activityService.cancelRegistration(id);
        return Result.successMsg("取消报名成功");
    }

    @Operation(summary = "获取我报名的活动", security = @SecurityRequirement(name = "Bearer"))
    @GetMapping("/my-activities")
    public Result<Page<ActivityVO>> getMyActivities(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(activityService.getMyActivities(page, size));
    }

    @Operation(summary = "获取进行中的活动")
    @GetMapping("/ongoing")
    public Result<List<ActivityVO>> getOngoingActivities() {
        return Result.success(activityService.getOngoingActivities());
    }
}
