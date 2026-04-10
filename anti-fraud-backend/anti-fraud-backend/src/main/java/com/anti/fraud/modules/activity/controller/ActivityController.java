package com.anti.fraud.modules.activity.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.common.enums.UserRole;
import com.anti.fraud.modules.activity.dto.ActivityCreateDTO;
import com.anti.fraud.modules.activity.service.ActivityService;
import com.anti.fraud.modules.activity.vo.ActivityVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 活动管理控制器
 * <p>
 * 提供活动相关的RESTful API接口，包括活动的CRUD操作、报名管理等功能。
 * 所有管理操作需要管理员或超级管理员权限，普通用户可以进行活动查询和报名操作。
 * </p>
 *
 * @author Anti-Fraud Platform Team
 * @version 1.0
 * @since 2024-01-01
 */
@Tag(name = "活动管理", description = "活动管理相关接口")
@RestController
@RequestMapping("/activity")
@RequiredArgsConstructor
@Slf4j
public class ActivityController {

    private final ActivityService activityService;

    /**
     * 分页获取活动列表
     * <p>
     * 支持按状态和类型筛选，可用于后台管理和前台展示。
     * </p>
     *
     * @param page   页码，默认1
     * @param size   每页数量，默认10
     * @param status 活动状态筛选（可选）：1-报名中，2-进行中，3-已结束
     * @param type   活动类型筛选（可选）
     * @return 分页后的活动列表
     */
    @Operation(summary = "分页获取活动列表", description = "支持按状态和类型筛选的分页查询")
    @GetMapping("/list")
    public Result<Page<ActivityVO>> getActivityList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "活动状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "活动类型") @RequestParam(required = false) Integer type) {
        log.debug("查询活动列表: page={}, size={}, status={}, type={}", page, size, status, type);
        return Result.success(activityService.getActivityPage(page, size, status, type));
    }

    /**
     * 获取活动详情
     * <p>
     * 根据活动ID获取活动详细信息，包括当前用户是否已报名等状态。
     * </p>
     *
     * @param id 活动ID
     * @return 活动详情
     */
    @Operation(summary = "获取活动详情", description = "根据ID获取活动详细信息")
    @GetMapping("/{id}")
    public Result<ActivityVO> getActivityDetail(
            @Parameter(description = "活动ID") @PathVariable Long id) {
        log.debug("查询活动详情: id={}", id);
        return Result.success(activityService.getActivityDetail(id));
    }

    /**
     * 创建新活动
     * <p>
     * 仅管理员和超级管理员可创建活动。系统会自动根据开始和结束时间设置活动状态。
     * </p>
     *
     * @param createDTO 活动创建信息
     * @return 操作结果
     */
    @Operation(summary = "创建活动", description = "创建新活动，仅管理员可操作")
    @PostMapping
    @PreAuthorize("hasRole('" + UserRole.ROLE_ADMIN + "') or hasRole('" + UserRole.ROLE_SUPER_ADMIN + "')")
    public Result<Void> createActivity(
            @Parameter(description = "活动创建信息") @RequestBody ActivityCreateDTO createDTO) {
        log.info("创建活动: name={}", createDTO.getActivityName());
        activityService.createActivity(createDTO);
        return Result.successMsg("创建成功");
    }

    /**
     * 更新活动信息
     * <p>
     * 仅管理员和超级管理员可更新活动。更新后系统会重新计算活动状态。
     * </p>
     *
     * @param id       活动ID
     * @param createDTO 活动更新信息
     * @return 操作结果
     */
    @Operation(summary = "更新活动", description = "更新活动信息，仅管理员可操作")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('" + UserRole.ROLE_ADMIN + "') or hasRole('" + UserRole.ROLE_SUPER_ADMIN + "')")
    public Result<Void> updateActivity(
            @Parameter(description = "活动ID") @PathVariable Long id,
            @Parameter(description = "活动更新信息") @RequestBody ActivityCreateDTO createDTO) {
        log.info("更新活动: id={}", id);
        activityService.updateActivity(id, createDTO);
        return Result.successMsg("更新成功");
    }

    /**
     * 删除活动
     * <p>
     * 仅管理员和超级管理员可删除活动。删除操作会同时删除相关的报名记录。
     * </p>
     *
     * @param id 活动ID
     * @return 操作结果
     */
    @Operation(summary = "删除活动", description = "删除活动，仅管理员可操作")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('" + UserRole.ROLE_ADMIN + "') or hasRole('" + UserRole.ROLE_SUPER_ADMIN + "')")
    public Result<Void> deleteActivity(
            @Parameter(description = "活动ID") @PathVariable Long id) {
        log.info("删除活动: id={}", id);
        activityService.deleteActivity(id);
        return Result.successMsg("删除成功");
    }

    /**
     * 报名参加活动
     * <p>
     * 用户可以报名参加处于报名阶段的活动。系统会检查报名人数限制和重复报名。
     * </p>
     *
     * @param id 活动ID
     * @return 操作结果
     */
    @Operation(summary = "报名活动", description = "用户报名参加活动")
    @PostMapping("/{id}/register")
    public Result<Void> registerActivity(
            @Parameter(description = "活动ID") @PathVariable Long id) {
        log.info("报名活动: activityId={}", id);
        activityService.registerActivity(id);
        return Result.successMsg("报名成功");
    }

    /**
     * 取消报名
     * <p>
     * 用户可以取消已报名的活动。取消后参与人数会相应减少。
     * </p>
     *
     * @param id 活动ID
     * @return 操作结果
     */
    @Operation(summary = "取消报名", description = "取消已报名的活动")
    @DeleteMapping("/{id}/register")
    public Result<Void> cancelRegistration(
            @Parameter(description = "活动ID") @PathVariable Long id) {
        log.info("取消报名: activityId={}", id);
        activityService.cancelRegistration(id);
        return Result.successMsg("取消报名成功");
    }

    /**
     * 获取我报名的活动列表
     * <p>
     * 获取当前用户已报名的所有活动，支持分页查询。
     * </p>
     *
     * @param page 页码，默认1
     * @param size 每页数量，默认10
     * @return 用户报名的活动分页列表
     */
    @Operation(summary = "获取我报名的活动", description = "获取当前用户已报名的活动列表")
    @GetMapping("/my-activities")
    public Result<Page<ActivityVO>> getMyActivities(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(activityService.getMyActivities(page, size));
    }

    /**
     * 获取进行中的活动
     * <p>
     * 获取当前处于报名中或进行中的活动列表，最多返回10条记录，按开始时间排序。
     * </p>
     *
     * @return 进行中的活动列表
     */
    @Operation(summary = "获取进行中的活动", description = "获取当前可报名或进行中的活动")
    @GetMapping("/ongoing")
    public Result<List<ActivityVO>> getOngoingActivities() {
        return Result.success(activityService.getOngoingActivities());
    }
}
