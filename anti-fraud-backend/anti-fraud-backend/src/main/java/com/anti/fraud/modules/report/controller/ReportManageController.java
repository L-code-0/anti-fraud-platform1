package com.anti.fraud.modules.report.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.common.enums.UserRole;
import com.anti.fraud.modules.report.dto.ReportHandleDTO;
import com.anti.fraud.modules.report.dto.WarningCreateDTO;
import com.anti.fraud.modules.report.service.ReportManageService;
import com.anti.fraud.modules.report.vo.ReportVO;
import com.anti.fraud.modules.report.vo.WarningVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "举报预警管理-后台", description = "举报预警管理后台接口")
@RestController
@RequestMapping("/admin/report")
@RequiredArgsConstructor
public class ReportManageController {

    private final ReportManageService reportManageService;

    // ==================== 举报管理 ====================

    @Operation(summary = "分页获取举报列表", security = @SecurityRequirement(name = "Bearer"))
    @GetMapping("/list")
    @PreAuthorize("hasRole('" + UserRole.ROLE_ADMIN + "') or hasRole('" + UserRole.ROLE_SUPER_ADMIN + "')")
    public Result<Page<ReportVO>> getReportList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer riskLevel) {
        return Result.success(reportManageService.getReportPage(page, size, status, riskLevel));
    }

    @Operation(summary = "获取举报详情", security = @SecurityRequirement(name = "Bearer"))
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('" + UserRole.ROLE_ADMIN + "') or hasRole('" + UserRole.ROLE_SUPER_ADMIN + "')")
    public Result<ReportVO> getReportDetail(@PathVariable Long id) {
        return Result.success(reportManageService.getReportDetail(id));
    }

    @Operation(summary = "处理举报", security = @SecurityRequirement(name = "Bearer"))
    @PostMapping("/{id}/handle")
    @PreAuthorize("hasRole('" + UserRole.ROLE_ADMIN + "') or hasRole('" + UserRole.ROLE_SUPER_ADMIN + "')")
    public Result<String> handleReport(@PathVariable Long id, @RequestBody ReportHandleDTO handleDTO) {
        reportManageService.handleReport(id, handleDTO);
        return Result.successMsg("处理成功");
    }

    // ==================== 预警管理 ====================

    @Operation(summary = "分页获取预警列表", security = @SecurityRequirement(name = "Bearer"))
    @GetMapping("/warnings")
    @PreAuthorize("hasRole('" + UserRole.ROLE_ADMIN + "') or hasRole('" + UserRole.ROLE_EXPERT + "') or hasRole('" + UserRole.ROLE_SUPER_ADMIN + "')")
    public Result<Page<WarningVO>> getWarningList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer warningLevel,
            @RequestParam(required = false) Integer status) {
        return Result.success(reportManageService.getWarningPage(page, size, warningLevel, status));
    }

    @Operation(summary = "创建预警", security = @SecurityRequirement(name = "Bearer"))
    @PostMapping("/warnings")
    @PreAuthorize("hasRole('" + UserRole.ROLE_ADMIN + "') or hasRole('" + UserRole.ROLE_EXPERT + "') or hasRole('" + UserRole.ROLE_SUPER_ADMIN + "')")
    public Result<String> createWarning(@RequestBody WarningCreateDTO createDTO) {
        reportManageService.createWarning(createDTO);
        return Result.successMsg("创建成功");
    }

    @Operation(summary = "更新预警", security = @SecurityRequirement(name = "Bearer"))
    @PutMapping("/warnings/{id}")
    @PreAuthorize("hasRole('" + UserRole.ROLE_ADMIN + "') or hasRole('" + UserRole.ROLE_EXPERT + "') or hasRole('" + UserRole.ROLE_SUPER_ADMIN + "')")
    public Result<String> updateWarning(@PathVariable Long id, @RequestBody WarningCreateDTO createDTO) {
        reportManageService.updateWarning(id, createDTO);
        return Result.successMsg("更新成功");
    }

    @Operation(summary = "删除预警", security = @SecurityRequirement(name = "Bearer"))
    @DeleteMapping("/warnings/{id}")
    @PreAuthorize("hasRole('" + UserRole.ROLE_ADMIN + "') or hasRole('" + UserRole.ROLE_SUPER_ADMIN + "')")
    public Result<String> deleteWarning(@PathVariable Long id) {
        reportManageService.deleteWarning(id);
        return Result.successMsg("删除成功");
    }

    @Operation(summary = "发布预警", security = @SecurityRequirement(name = "Bearer"))
    @PutMapping("/warnings/{id}/publish")
    @PreAuthorize("hasRole('" + UserRole.ROLE_ADMIN + "') or hasRole('" + UserRole.ROLE_SUPER_ADMIN + "')")
    public Result<String> publishWarning(@PathVariable Long id) {
        reportManageService.publishWarning(id);
        return Result.successMsg("发布成功");
    }
}