package com.anti.fraud.modules.checkin.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.checkin.service.CheckinService;
import com.anti.fraud.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

/**
 * 打卡控制器
 */
@RestController
@RequestMapping("/checkin")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "打卡管理")
public class CheckinController {

    private final CheckinService checkinService;

    @Operation(summary = "执行打卡")
    @PostMapping("/do-checkin")
    public Result<Map<String, Object>> doCheckin(
            @ApiParam(value = "打卡类型: 1-每日打卡, 2-学习打卡", required = true) @RequestParam Integer checkinType,
            @ApiParam(value = "打卡地点", required = false) @RequestParam(required = false) String location,
            HttpServletRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        String username = SecurityUtils.getCurrentUsername();
        String ipAddress = getClientIp(request);

        try {
            Map<String, Object> result = checkinService.doCheckin(userId, username, checkinType, location, ipAddress);
            if ((boolean) result.get("success")) {
                return Result.success((String) result.get("message"), result);
            } else {
                return Result.fail((String) result.get("message"));
            }
        } catch (Exception e) {
            log.error("打卡失败: {}", e.getMessage(), e);
            return Result.fail("打卡失败，请稍后重试");
        }
    }

    @Operation(summary = "检查当天是否已经打卡")
    @GetMapping("/check-today")
    public Result<Map<String, Boolean>> checkTodayCheckin(
            @ApiParam(value = "打卡类型: 1-每日打卡, 2-学习打卡", required = true) @RequestParam Integer checkinType) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            boolean hasCheckedIn = checkinService.hasCheckedInToday(userId, checkinType);
            Map<String, Boolean> result = Map.of("hasCheckedIn", hasCheckedIn);
            return Result.success(result);
        } catch (Exception e) {
            log.error("检查打卡状态失败: {}", e.getMessage(), e);
            return Result.fail("检查打卡状态失败");
        }
    }

    @Operation(summary = "获取连续打卡天数")
    @GetMapping("/consecutive-days")
    public Result<Map<String, Integer>> getConsecutiveDays(
            @ApiParam(value = "打卡类型: 1-每日打卡, 2-学习打卡", required = true) @RequestParam Integer checkinType) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            int consecutiveDays = checkinService.getConsecutiveDays(userId, checkinType);
            Map<String, Integer> result = Map.of("consecutiveDays", consecutiveDays);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取连续打卡天数失败: {}", e.getMessage(), e);
            return Result.fail("获取连续打卡天数失败");
        }
    }

    @Operation(summary = "获取打卡记录列表")
    @GetMapping("/records")
    public Result<List<?>> getCheckinRecords(
            @ApiParam(value = "打卡类型: 1-每日打卡, 2-学习打卡", required = false) @RequestParam(required = false) Integer checkinType,
            @ApiParam(value = "开始日期", required = false) @RequestParam(required = false) String startDateStr,
            @ApiParam(value = "结束日期", required = false) @RequestParam(required = false) String endDateStr,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            LocalDate startDate = startDateStr != null ? LocalDate.parse(startDateStr) : null;
            LocalDate endDate = endDateStr != null ? LocalDate.parse(endDateStr) : null;
            List<?> records = checkinService.getCheckinRecords(userId, checkinType, startDate, endDate, page, size);
            return Result.success(records);
        } catch (Exception e) {
            log.error("获取打卡记录失败: {}", e.getMessage(), e);
            return Result.fail("获取打卡记录失败");
        }
    }

    @Operation(summary = "获取打卡统计")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getCheckinStats(
            @ApiParam(value = "打卡类型: 1-每日打卡, 2-学习打卡", required = false) @RequestParam(required = false) Integer checkinType,
            @ApiParam(value = "开始日期", required = false) @RequestParam(required = false) String startDateStr,
            @ApiParam(value = "结束日期", required = false) @RequestParam(required = false) String endDateStr) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            LocalDate startDate = startDateStr != null ? LocalDate.parse(startDateStr) : null;
            LocalDate endDate = endDateStr != null ? LocalDate.parse(endDateStr) : null;
            Map<String, Object> stats = checkinService.getCheckinStats(userId, checkinType, startDate, endDate);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取打卡统计失败: {}", e.getMessage(), e);
            return Result.fail("获取打卡统计失败");
        }
    }

    @Operation(summary = "获取当月打卡记录")
    @GetMapping("/month-records")
    public Result<List<?>> getMonthCheckinRecords(
            @ApiParam(value = "打卡类型: 1-每日打卡, 2-学习打卡", required = false) @RequestParam(required = false) Integer checkinType,
            @ApiParam(value = "年份", required = false) @RequestParam(required = false) Integer year,
            @ApiParam(value = "月份", required = false) @RequestParam(required = false) Integer month) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            YearMonth now = YearMonth.now();
            int targetYear = year != null ? year : now.getYear();
            int targetMonth = month != null ? month : now.getMonthValue();
            List<?> records = checkinService.getMonthCheckinRecords(userId, checkinType, targetYear, targetMonth);
            return Result.success(records);
        } catch (Exception e) {
            log.error("获取当月打卡记录失败: {}", e.getMessage(), e);
            return Result.fail("获取当月打卡记录失败");
        }
    }

    @Operation(summary = "获取当日打卡用户数")
    @GetMapping("/today-count")
    public Result<Map<String, Integer>> getTodayCheckinCount(
            @ApiParam(value = "打卡类型: 1-每日打卡, 2-学习打卡", required = true) @RequestParam Integer checkinType) {
        try {
            int count = checkinService.getTodayCheckinCount(checkinType);
            Map<String, Integer> result = Map.of("count", count);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取当日打卡用户数失败: {}", e.getMessage(), e);
            return Result.fail("获取当日打卡用户数失败");
        }
    }

    @Operation(summary = "获取打卡排行榜")
    @GetMapping("/rank")
    public Result<List<Map<String, Object>>> getCheckinRank(
            @ApiParam(value = "打卡类型: 1-每日打卡, 2-学习打卡", required = true) @RequestParam Integer checkinType,
            @ApiParam(value = "开始日期", required = false) @RequestParam(required = false) String startDateStr,
            @ApiParam(value = "结束日期", required = false) @RequestParam(required = false) String endDateStr,
            @ApiParam(value = "限制数量", required = false) @RequestParam(required = false) Integer limit) {
        try {
            LocalDate startDate = startDateStr != null ? LocalDate.parse(startDateStr) : LocalDate.now().minusDays(30);
            LocalDate endDate = endDateStr != null ? LocalDate.parse(endDateStr) : LocalDate.now();
            int targetLimit = limit != null ? limit : 10;
            List<Map<String, Object>> rank = checkinService.getCheckinRank(checkinType, startDate, endDate, targetLimit);
            return Result.success(rank);
        } catch (Exception e) {
            log.error("获取打卡排行榜失败: {}", e.getMessage(), e);
            return Result.fail("获取打卡排行榜失败");
        }
    }

    // 获取客户端IP
    private String getClientIp(HttpServletRequest request) {
        String[] headerNames = {
                "X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP",
                "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"
        };

        String ip = null;
        for (String header : headerNames) {
            ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                break;
            }
        }

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }
}
