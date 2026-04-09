package com.anti.fraud.modules.works.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.works.dto.WorksSubmitDTO;
import com.anti.fraud.modules.works.service.WorksService;
import com.anti.fraud.modules.works.vo.WorksVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "作品管理", description = "作品提交和展示接口")
@RestController
@RequestMapping("/works")
@RequiredArgsConstructor
public class WorksController {

    private final WorksService worksService;

    @Operation(summary = "提交作品", security = @SecurityRequirement(name = "Bearer"))
    @PostMapping
    public Result<Long> submitWorks(@RequestBody WorksSubmitDTO dto) {
        Long worksId = worksService.submitWorks(dto);
        return Result.success(worksId, "提交成功，请等待审核");
    }

    @Operation(summary = "我的作品列表", security = @SecurityRequirement(name = "Bearer"))
    @GetMapping("/my")
    public Result<Page<WorksVO>> getMyWorks(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(worksService.getMyWorks(page, size));
    }

    @Operation(summary = "作品列表（公开）")
    @GetMapping("/list")
    public Result<Page<WorksVO>> getWorksList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String worksType,
            @RequestParam(required = false) Long activityId,
            @RequestParam(required = false) Boolean excellent) {
        return Result.success(worksService.getWorksList(page, size, worksType, activityId, excellent));
    }

    @Operation(summary = "作品详情")
    @GetMapping("/{id}")
    public Result<WorksVO> getWorksDetail(@PathVariable Long id) {
        return Result.success(worksService.getWorksDetail(id));
    }

    @Operation(summary = "审核作品（管理员）", security = @SecurityRequirement(name = "Bearer"))
    @PostMapping("/{id}/audit")
    public Result<Void> auditWorks(
            @PathVariable Long id,
            @RequestParam Integer status,
            @RequestParam(required = false) String remark) {
        worksService.auditWorks(id, status, remark);
        return Result.successMsg("审核完成");
    }

    @Operation(summary = "设置优秀作品", security = @SecurityRequirement(name = "Bearer"))
    @PostMapping("/{id}/excellent")
    public Result<Void> setExcellent(
            @PathVariable Long id,
            @RequestParam Boolean excellent,
            @RequestParam(required = false) Integer rank,
            @RequestParam(required = false) Integer points) {
        worksService.setExcellent(id, excellent, rank, points);
        return Result.successMsg("设置成功");
    }

    @Operation(summary = "删除作品", security = @SecurityRequirement(name = "Bearer"))
    @DeleteMapping("/{id}")
    public Result<Void> deleteWorks(@PathVariable Long id) {
        worksService.deleteWorks(id);
        return Result.successMsg("删除成功");
    }

    @Operation(summary = "点赞作品", security = @SecurityRequirement(name = "Bearer"))
    @PostMapping("/{id}/like")
    public Result<Void> likeWorks(@PathVariable Long id) {
        worksService.likeWorks(id);
        return Result.successMsg("点赞成功");
    }
}