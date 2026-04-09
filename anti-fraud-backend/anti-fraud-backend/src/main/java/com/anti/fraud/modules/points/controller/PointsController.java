package com.anti.fraud.modules.points.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.points.service.PointsService;
import com.anti.fraud.modules.points.vo.BadgeVO;
import com.anti.fraud.modules.points.vo.PointsRecordVO;
import com.anti.fraud.modules.points.vo.PointsStatsVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "积分勋章", description = "积分勋章相关接口")
@RestController
@RequestMapping("/points")
@RequiredArgsConstructor
public class PointsController {

    private final PointsService pointsService;

    @Operation(summary = "获取积分统计", security = @SecurityRequirement(name = "Bearer"))
    @GetMapping("/stats")
    public Result<PointsStatsVO> getPointsStats() {
        return Result.success(pointsService.getPointsStats());
    }

    @Operation(summary = "获取积分记录", security = @SecurityRequirement(name = "Bearer"))
    @GetMapping("/records")
    public Result<Page<PointsRecordVO>> getPointsRecords(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(pointsService.getPointsRecords(page, size));
    }

    @Operation(summary = "获取所有勋章")
    @GetMapping("/badges")
    public Result<List<BadgeVO>> getAllBadges() {
        return Result.success(pointsService.getAllBadges());
    }

    @Operation(summary = "获取我的勋章", security = @SecurityRequirement(name = "Bearer"))
    @GetMapping("/my-badges")
    public Result<List<BadgeVO>> getMyBadges() {
        return Result.success(pointsService.getMyBadges());
    }
}