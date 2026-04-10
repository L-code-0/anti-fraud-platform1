package com.anti.fraud.modules.report.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.report.dto.ReportSubmitDTO;
import com.anti.fraud.modules.report.service.ReportService;
import com.anti.fraud.modules.report.vo.ReportVO;
import com.anti.fraud.modules.report.vo.WarningVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 举报预警控制器
 * <p>
 * 提供举报管理和预警信息相关的RESTful API接口。
 * 举报功能允许用户提交诈骗举报，预警功能展示系统发布的各类诈骗预警信息。
 * </p>
 *
 * @author Anti-Fraud Platform Team
 * @version 1.0
 * @since 2024-01-01
 */
@Tag(name = "举报预警", description = "举报预警相关接口")
@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
@Slf4j
public class ReportController {

    private final ReportService reportService;

    /**
     * 提交举报
     * <p>
     * 用户可以提交诈骗举报信息。系统会根据举报内容自动评估风险等级。
     * 举报信息会生成唯一的举报编号，用户可以凭此编号查询处理进度。
     * </p>
     *
     * @param submitDTO 举报提交信息
     * @return 操作结果
     */
    @Operation(summary = "提交举报", description = "提交诈骗举报信息，系统自动评估风险等级")
    @PostMapping("/submit")
    public Result<Void> submitReport(
            @Parameter(description = "举报信息") @RequestBody ReportSubmitDTO submitDTO) {
        log.info("用户提交举报: type={}, fraudType={}", submitDTO.getReportType(), submitDTO.getFraudType());
        reportService.submitReport(submitDTO);
        return Result.successMsg("举报提交成功，感谢您的参与");
    }

    /**
     * 获取我的举报记录
     * <p>
     * 分页查询当前用户的举报历史记录。
     * </p>
     *
     * @param page 页码，默认1
     * @param size 每页数量，默认10
     * @return 举报分页列表
     */
    @Operation(summary = "获取我的举报记录", description = "分页查询当前用户的举报历史")
    @GetMapping("/my-reports")
    public Result<Page<ReportVO>> getMyReports(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size) {
        log.debug("查询用户举报记录: page={}, size={}", page, size);
        return Result.success(reportService.getMyReports(page, size));
    }

    /**
     * 获取举报详情
     * <p>
     * 根据举报ID获取详细信息。用户只能查看自己提交的举报信息。
     * </p>
     *
     * @param id 举报ID
     * @return 举报详情
     */
    @Operation(summary = "获取举报详情", description = "获取指定举报的详细信息")
    @GetMapping("/{id}")
    public Result<ReportVO> getReportDetail(
            @Parameter(description = "举报ID") @PathVariable Long id) {
        log.debug("查询举报详情: id={}", id);
        return Result.success(reportService.getReportDetail(id));
    }

    /**
     * 获取预警列表
     * <p>
     * 获取所有生效的预警信息，按预警等级和发布时间排序，最多返回20条。
     * </p>
     *
     * @return 预警列表
     */
    @Operation(summary = "获取预警列表", description = "获取所有生效的诈骗预警信息")
    @GetMapping("/warnings")
    public Result<List<WarningVO>> getWarningList() {
        log.debug("查询预警列表");
        return Result.success(reportService.getWarningList());
    }

    /**
     * 获取预警详情
     * <p>
     * 根据预警ID获取详细信息，同时更新预警的浏览次数。
     * </p>
     *
     * @param id 预警ID
     * @return 预警详情
     */
    @Operation(summary = "获取预警详情", description = "获取指定预警的详细信息")
    @GetMapping("/warnings/{id}")
    public Result<WarningVO> getWarningDetail(
            @Parameter(description = "预警ID") @PathVariable Long id) {
        log.debug("查询预警详情: id={}", id);
        return Result.success(reportService.getWarningDetail(id));
    }

    /**
     * 获取最新预警
     * <p>
     * 获取最近发布的5条预警信息。
     * </p>
     *
     * @return 最新预警列表
     */
    @Operation(summary = "获取最新预警", description = "获取最近发布的预警信息")
    @GetMapping("/warnings/latest")
    public Result<List<WarningVO>> getLatestWarnings() {
        log.debug("查询最新预警");
        return Result.success(reportService.getLatestWarnings());
    }
}
