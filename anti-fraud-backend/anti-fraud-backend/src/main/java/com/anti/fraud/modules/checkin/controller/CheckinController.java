package com.anti.fraud.modules.checkin.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.checkin.entity.Checkin;
import com.anti.fraud.modules.checkin.service.CheckinService;
import com.anti.fraud.utils.SecurityUtils;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/checkin")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "学习打卡")
public class CheckinController {

    private final CheckinService checkinService;

    @Operation(summary = "每日打卡")
    @PostMapping("/do")
    public Result<Map<String, Object>> checkin(@RequestBody Map<String, String> params) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        String checkinType = params.get("checkinType");
        if (checkinType == null) {
            checkinType = "daily";
        }

        try {
            Map<String, Object> result = checkinService.checkin(userId, checkinType);
            if ((Boolean) result.get("success")) {
                return Result.success("打卡成功", result);
            } else {
                return Result.fail(result.get("message").toString());
            }
        } catch (Exception e) {
            log.error("打卡失败: {}", e.getMessage(), e);
            return Result.fail("打卡失败");
        }
    }

    @Operation(summary = "获取今日打卡状态")
    @GetMapping("/today")
    public Result<Checkin> getTodayCheckin() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            Checkin checkin = checkinService.getTodayCheckin(userId);
            return Result.success("获取今日打卡状态成功", checkin);
        } catch (Exception e) {
            log.error("获取今日打卡状态失败: {}", e.getMessage(), e);
            return Result.fail("获取今日打卡状态失败");
        }
    }

    @Operation(summary = "获取打卡历史")
    @GetMapping("/history")
    public Result<List<Checkin>> getCheckinHistory(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            List<Checkin> history = checkinService.getCheckinHistory(userId, start, end);
            return Result.success("获取打卡历史成功", history);
        } catch (Exception e) {
            log.error("获取打卡历史失败: {}", e.getMessage(), e);
            return Result.fail("获取打卡历史失败");
        }
    }

    @Operation(summary = "获取连续打卡天数")
    @GetMapping("/continuous-days")
    public Result<Integer> getContinuousDays() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            Integer days = checkinService.getContinuousDays(userId);
            return Result.success("获取连续打卡天数成功", days);
        } catch (Exception e) {
            log.error("获取连续打卡天数失败: {}", e.getMessage(), e);
            return Result.fail("获取连续打卡天数失败");
        }
    }

    @Operation(summary = "获取总打卡天数")
    @GetMapping("/total-days")
    public Result<Integer> getTotalCheckinDays() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            Integer days = checkinService.getTotalCheckinDays(userId);
            return Result.success("获取总打卡天数成功", days);
        } catch (Exception e) {
            log.error("获取总打卡天数失败: {}", e.getMessage(), e);
            return Result.fail("获取总打卡天数失败");
        }
    }

    @Operation(summary = "获取本月打卡记录")
    @GetMapping("/month")
    public Result<List<Checkin>> getMonthCheckin(
            @RequestParam(defaultValue = "2026") Integer year,
            @RequestParam(defaultValue = "4") Integer month) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            List<Checkin> checkins = checkinService.getMonthCheckin(userId, year, month);
            return Result.success("获取本月打卡记录成功", checkins);
        } catch (Exception e) {
            log.error("获取本月打卡记录失败: {}", e.getMessage(), e);
            return Result.fail("获取本月打卡记录失败");
        }
    }

    @Operation(summary = "领取连续打卡奖励")
    @PostMapping("/claim-reward")
    public Result<Map<String, Object>> claimReward(@RequestBody Map<String, Integer> params) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        Integer days = params.get("days");
        if (days == null || days <= 0) {
            return Result.fail("请指定连续打卡天数");
        }

        try {
            Map<String, Object> result = checkinService.claimReward(userId, days);
            if ((Boolean) result.get("success")) {
                return Result.success("领取奖励成功", result);
            } else {
                return Result.fail(result.get("message").toString());
            }
        } catch (Exception e) {
            log.error("领取奖励失败: {}", e.getMessage(), e);
            return Result.fail("领取奖励失败");
        }
    }
}
