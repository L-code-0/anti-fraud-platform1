package com.anti.fraud.modules.risk.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.risk.entity.UserRiskProfile;
import com.anti.fraud.modules.risk.service.UserRiskProfileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 用户风险画像服务控制器
 */
@RestController
@RequestMapping("/risk/profile")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "用户风险画像服务")
public class UserRiskProfileController {

    private final UserRiskProfileService userRiskProfileService;

    @Operation(summary = "创建用户风险画像")
    @PostMapping("/create")
    public Result<Void> createUserRiskProfile(@ApiParam(value = "用户风险画像", required = true) @RequestBody UserRiskProfile userRiskProfile) {
        try {
            boolean success = userRiskProfileService.createUserRiskProfile(userRiskProfile);
            if (success) {
                return Result.successMsg("创建用户风险画像成功");
            } else {
                return Result.fail("创建用户风险画像失败");
            }
        } catch (Exception e) {
            log.error("创建用户风险画像失败: {}", e.getMessage(), e);
            return Result.fail("创建用户风险画像失败");
        }
    }

    @Operation(summary = "更新用户风险画像")
    @PostMapping("/update")
    public Result<Void> updateUserRiskProfile(@ApiParam(value = "用户风险画像", required = true) @RequestBody UserRiskProfile userRiskProfile) {
        try {
            boolean success = userRiskProfileService.updateUserRiskProfile(userRiskProfile);
            if (success) {
                return Result.successMsg("更新用户风险画像成功");
            } else {
                return Result.fail("更新用户风险画像失败");
            }
        } catch (Exception e) {
            log.error("更新用户风险画像失败: {}", e.getMessage(), e);
            return Result.fail("更新用户风险画像失败");
        }
    }

    @Operation(summary = "删除用户风险画像")
    @DeleteMapping("/delete/{userId}")
    public Result<Void> deleteUserRiskProfile(@ApiParam(value = "用户ID", required = true) @PathVariable Long userId) {
        try {
            boolean success = userRiskProfileService.deleteUserRiskProfile(userId);
            if (success) {
                return Result.successMsg("删除用户风险画像成功");
            } else {
                return Result.fail("删除用户风险画像失败");
            }
        } catch (Exception e) {
            log.error("删除用户风险画像失败: {}", e.getMessage(), e);
            return Result.fail("删除用户风险画像失败");
        }
    }

    @Operation(summary = "获取用户风险画像")
    @GetMapping("/detail/{userId}")
    public Result<UserRiskProfile> getUserRiskProfile(@ApiParam(value = "用户ID", required = true) @PathVariable Long userId) {
        try {
            UserRiskProfile profile = userRiskProfileService.getUserRiskProfile(userId);
            if (profile != null) {
                return Result.success(profile);
            } else {
                return Result.fail("用户风险画像不存在");
            }
        } catch (Exception e) {
            log.error("获取用户风险画像失败: {}", e.getMessage(), e);
            return Result.fail("获取用户风险画像失败");
        }
    }

    @Operation(summary = "分页查询用户风险画像列表")
    @GetMapping("/list")
    public Result<Map<String, Object>> getUserRiskProfileList(
            @ApiParam(value = "用户ID", required = false) @RequestParam(required = false) Long userId,
            @ApiParam(value = "风险等级: 1-低风险，2-中风险，3-高风险，4-极高风险", required = false) @RequestParam(required = false) Integer riskLevel,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = userRiskProfileService.getUserRiskProfileList(userId, riskLevel, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询用户风险画像列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return Result.fail("查询用户风险画像列表失败");
        }
    }

    @Operation(summary = "根据风险等级查询用户风险画像列表")
    @GetMapping("/list-by-risk-level")
    public Result<Map<String, Object>> getUserRiskProfileListByRiskLevel(
            @ApiParam(value = "风险等级: 1-低风险，2-中风险，3-高风险，4-极高风险", required = true) @RequestParam Integer riskLevel,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = userRiskProfileService.getUserRiskProfileListByRiskLevel(riskLevel, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("根据风险等级查询用户风险画像列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return Result.fail("根据风险等级查询用户风险画像列表失败");
        }
    }

    @Operation(summary = "根据时间范围查询用户风险画像列表")
    @GetMapping("/list-by-time-range")
    public Result<List<UserRiskProfile>> getUserRiskProfileListByTimeRange(
            @ApiParam(value = "开始时间", required = true) @RequestParam LocalDateTime startTime,
            @ApiParam(value = "结束时间", required = true) @RequestParam LocalDateTime endTime,
            @ApiParam(value = "用户ID", required = false) @RequestParam(required = false) Long userId,
            @ApiParam(value = "风险等级: 1-低风险，2-中风险，3-高风险，4-极高风险", required = false) @RequestParam(required = false) Integer riskLevel) {
        try {
            List<UserRiskProfile> profiles = userRiskProfileService.getUserRiskProfileListByTimeRange(startTime, endTime, userId, riskLevel);
            return Result.success(profiles);
        } catch (Exception e) {
            log.error("根据时间范围查询用户风险画像列表失败: {}", e.getMessage(), e);
            return Result.fail("根据时间范围查询用户风险画像列表失败");
        }
    }

    @Operation(summary = "生成用户风险画像")
    @PostMapping("/generate")
    public Result<UserRiskProfile> generateUserRiskProfile(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "用户名", required = true) @RequestParam String username) {
        try {
            UserRiskProfile profile = userRiskProfileService.generateUserRiskProfile(userId, username);
            return Result.success(profile);
        } catch (Exception e) {
            log.error("生成用户风险画像失败: {}", e.getMessage(), e);
            return Result.fail("生成用户风险画像失败");
        }
    }

    @Operation(summary = "获取用户风险画像可视化数据")
    @GetMapping("/visualization/{userId}")
    public Result<Map<String, Object>> getUserRiskProfileVisualization(@ApiParam(value = "用户ID", required = true) @PathVariable Long userId) {
        try {
            Map<String, Object> visualization = userRiskProfileService.getUserRiskProfileVisualization(userId);
            return Result.success(visualization);
        } catch (Exception e) {
            log.error("获取用户风险画像可视化数据失败: {}", e.getMessage(), e);
            return Result.fail("获取用户风险画像可视化数据失败");
        }
    }

    @Operation(summary = "获取用户风险趋势")
    @GetMapping("/trend/{userId}")
    public Result<List<Map<String, Object>>> getUserRiskTrend(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long userId,
            @ApiParam(value = "天数", required = true) @RequestParam Integer days) {
        try {
            List<Map<String, Object>> trend = userRiskProfileService.getUserRiskTrend(userId, days);
            return Result.success(trend);
        } catch (Exception e) {
            log.error("获取用户风险趋势失败: {}", e.getMessage(), e);
            return Result.fail("获取用户风险趋势失败");
        }
    }

    @Operation(summary = "获取高风险用户列表")
    @GetMapping("/high-risk-users")
    public Result<List<UserRiskProfile>> getHighRiskUsers(@ApiParam(value = "数量限制", required = true) @RequestParam Integer limit) {
        try {
            List<UserRiskProfile> profiles = userRiskProfileService.getHighRiskUsers(limit);
            return Result.success(profiles);
        } catch (Exception e) {
            log.error("获取高风险用户列表失败: {}", e.getMessage(), e);
            return Result.fail("获取高风险用户列表失败");
        }
    }

    @Operation(summary = "获取用户风险画像统计信息")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getUserRiskProfileStatistics() {
        try {
            Map<String, Object> statistics = userRiskProfileService.getUserRiskProfileStatistics();
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取用户风险画像统计信息失败: {}", e.getMessage(), e);
            return Result.fail("获取用户风险画像统计信息失败");
        }
    }

    @Operation(summary = "更新用户风险画像状态")
    @PostMapping("/update-status/{userId}")
    public Result<Void> updateUserRiskProfileStatus(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long userId,
            @ApiParam(value = "状态: 1-已评估，2-评估中，3-评估失败", required = true) @RequestParam Integer status) {
        try {
            boolean success = userRiskProfileService.updateUserRiskProfileStatus(userId, status);
            if (success) {
                return Result.successMsg("更新用户风险画像状态成功");
            } else {
                return Result.fail("更新用户风险画像状态失败");
            }
        } catch (Exception e) {
            log.error("更新用户风险画像状态失败: {}", e.getMessage(), e);
            return Result.fail("更新用户风险画像状态失败");
        }
    }

    @Operation(summary = "批量生成用户风险画像")
    @PostMapping("/batch-generate")
    public Result<Integer> batchGenerateUserRiskProfiles(@ApiParam(value = "用户ID列表", required = true) @RequestBody List<Long> userIds) {
        try {
            int count = userRiskProfileService.batchGenerateUserRiskProfiles(userIds);
            return Result.success(count);
        } catch (Exception e) {
            log.error("批量生成用户风险画像失败: {}", e.getMessage(), e);
            return Result.fail("批量生成用户风险画像失败");
        }
    }

    @Operation(summary = "获取用户风险因素分析")
    @GetMapping("/factor-analysis/{userId}")
    public Result<Map<String, Object>> getUserRiskFactorAnalysis(@ApiParam(value = "用户ID", required = true) @PathVariable Long userId) {
        try {
            Map<String, Object> analysis = userRiskProfileService.getUserRiskFactorAnalysis(userId);
            return Result.success(analysis);
        } catch (Exception e) {
            log.error("获取用户风险因素分析失败: {}", e.getMessage(), e);
            return Result.fail("获取用户风险因素分析失败");
        }
    }

    @Operation(summary = "获取用户风险建议")
    @GetMapping("/recommendations/{userId}")
    public Result<List<String>> getUserRiskRecommendations(@ApiParam(value = "用户ID", required = true) @PathVariable Long userId) {
        try {
            List<String> recommendations = userRiskProfileService.getUserRiskRecommendations(userId);
            return Result.success(recommendations);
        } catch (Exception e) {
            log.error("获取用户风险建议失败: {}", e.getMessage(), e);
            return Result.fail("获取用户风险建议失败");
        }
    }

    @Operation(summary = "获取用户风险标签")
    @GetMapping("/labels/{userId}")
    public Result<List<String>> getUserRiskLabels(@ApiParam(value = "用户ID", required = true) @PathVariable Long userId) {
        try {
            List<String> labels = userRiskProfileService.getUserRiskLabels(userId);
            return Result.success(labels);
        } catch (Exception e) {
            log.error("获取用户风险标签失败: {}", e.getMessage(), e);
            return Result.fail("获取用户风险标签失败");
        }
    }
}
