package com.anti.fraud.modules.report.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.report.entity.Warning;
import com.anti.fraud.modules.report.service.WarningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 预警控制器
 */
@RestController
@RequestMapping("/api/warning")
@Tag(name = "预警管理", description = "预警相关接口")
@RequiredArgsConstructor
public class WarningController {

    private final WarningService warningService;

    @PostMapping("/create")
    @Operation(summary = "创建预警")
    public Result<?> createWarning(@RequestBody Warning warning) {
        warningService.createWarning(warning);
        return Result.success("预警创建成功");
    }

    @GetMapping("/user/list")
    @Operation(summary = "获取用户预警列表")
    public Result<?> getUserWarnings(@RequestParam String userId) {
        return Result.success(warningService.getUserWarnings(userId));
    }
}
