package com.anti.fraud.modules.risk.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.risk.entity.RiskProfile;
import com.anti.fraud.modules.risk.service.RiskProfileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 风险画像控制器
 */
@RestController
@RequestMapping("/risk/profile")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "风险画像管理")
public class RiskProfileController {

    private final RiskProfileService riskProfileService;

    @Operation(summary = "创建风险画像")
    @PostMapping("/create")
    public Result<Void> createRiskProfile(@ApiParam(value = "风险画像信息", required = true) @RequestBody RiskProfile riskProfile) {
        try {
            boolean success = riskProfileService.createRiskProfile(riskProfile);
            if (success) {
                return Result.successMsg("创建风险画像成功");
            } else {
                return Result.fail("创建风险画像失败");
            }
        } catch (Exception e) {
            log.error("创建风险画像失败: {}", e.getMessage(), e);
            return Result.fail("创建风险画像失败");
        }
    }

    @Operation(summary = "更新风险画像")
    @PutMapping("/update")
    public Result<Void> updateRiskProfile(@ApiParam(value = "风险画像信息", required = true) @RequestBody RiskProfile riskProfile) {
        try {
            boolean success = riskProfileService.updateRiskProfile(riskProfile);
            if (success) {
                return Result.successMsg("更新风险画像成功");
            } else {
                return Result.fail("更新风险画像失败");
            }
        } catch (Exception e) {
            log.error("更新风险画像失败: {}", e.getMessage(), e);
            return Result.fail("更新风险画像失败");
        }
    }

    @Operation(summary = "删除风险画像")
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteRiskProfile(@ApiParam(value = "风险画像ID", required = true) @PathVariable Long id) {
        try {
            boolean success = riskProfileService.deleteRiskProfile(id);
            if (success) {
                return Result.successMsg("删除风险画像成功");
            } else {
                return Result.fail("删除风险画像失败");
            }
        } catch (Exception e) {
            log.error("删除风险画像失败: {}", e.getMessage(), e);
            return Result.fail("删除风险画像失败");
        }
    }

    @Operation(summary = "获取风险画像详情")
    @GetMapping("/detail/{id}")
    public Result<RiskProfile> getRiskProfileById(@ApiParam(value = "风险画像ID", required = true) @PathVariable Long id) {
        try {
            RiskProfile riskProfile = riskProfileService.getRiskProfileById(id);
            if (riskProfile != null) {
                return Result.success(riskProfile);
            } else {
                return Result.fail("风险画像不存在");
            }
        } catch (Exception e) {
            log.error("获取风险画像详情失败: {}", e.getMessage(), e);
            return Result.fail("获取风险画像详情失败");
        }
    }

    @Operation(summary = "根据用户ID获取风险画像")
    @GetMapping("/by-user/{userId}")
    public Result<RiskProfile> getRiskProfileByUserId(@ApiParam(value = "用户ID", required = true) @PathVariable Long userId) {
        try {
            RiskProfile riskProfile = riskProfileService.getRiskProfileByUserId(userId);
            if (riskProfile != null) {
                return Result.success(riskProfile);
            } else {
                return Result.fail("风险画像不存在");
            }
        } catch (Exception e) {
            log.error("根据用户ID获取风险画像失败: {}", e.getMessage(), e);
            return Result.fail("根据用户ID获取风险画像失败");
        }
    }

    @Operation(summary = "分页查询风险画像")
    @PostMapping("/list")
    public Result<Map<String, Object>> getRiskProfileList(
            @ApiParam(value = "查询参数", required = false) @RequestBody(required = false) Map<String, Object> params,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            if (params == null) {
                params = new java.util.HashMap<>();
            }
            Map<String, Object> result = riskProfileService.getRiskProfileList(params, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询风险画像列表失败: {}", e.getMessage(), e);
            return Result.fail("查询风险画像列表失败");
        }
    }

    @Operation(summary = "根据风险等级查询风险画像")
    @GetMapping("/by-level")
    public Result<Map<String, Object>> getRiskProfilesByLevel(
            @ApiParam(value = "风险等级: 1-低风险, 2-中风险, 3-高风险", required = true) @RequestParam Integer riskLevel,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = riskProfileService.getRiskProfilesByLevel(riskLevel, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("根据风险等级查询风险画像失败: {}", e.getMessage(), e);
            return Result.fail("根据风险等级查询风险画像失败");
        }
    }

    @Operation(summary = "根据风险评分范围查询风险画像")
    @GetMapping("/by-score-range")
    public Result<Map<String, Object>> getRiskProfilesByScoreRange(
            @ApiParam(value = "最低评分", required = true) @RequestParam Integer minScore,
            @ApiParam(value = "最高评分", required = true) @RequestParam Integer maxScore,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = riskProfileService.getRiskProfilesByScoreRange(minScore, maxScore, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("根据风险评分范围查询风险画像失败: {}", e.getMessage(), e);
            return Result.fail("根据风险评分范围查询风险画像失败");
        }
    }

    @Operation(summary = "更新风险评分和等级")
    @PutMapping("/update-score/{id}")
    public Result<Void> updateRiskScoreAndLevel(
            @ApiParam(value = "风险画像ID", required = true) @PathVariable Long id,
            @ApiParam(value = "风险评分", required = true) @RequestParam Integer riskScore,
            @ApiParam(value = "风险等级", required = true) @RequestParam Integer riskLevel) {
        try {
            boolean success = riskProfileService.updateRiskScoreAndLevel(id, riskScore, riskLevel);
            if (success) {
                return Result.successMsg("更新风险评分和等级成功");
            } else {
                return Result.fail("更新风险评分和等级失败");
            }
        } catch (Exception e) {
            log.error("更新风险评分和等级失败: {}", e.getMessage(), e);
            return Result.fail("更新风险评分和等级失败");
        }
    }

    @Operation(summary = "生成风险画像")
    @PostMapping("/generate")
    public Result<RiskProfile> generateRiskProfile(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "用户名", required = true) @RequestParam String username) {
        try {
            RiskProfile riskProfile = riskProfileService.generateRiskProfile(userId, username);
            if (riskProfile != null) {
                return Result.success(riskProfile);
            } else {
                return Result.fail("生成风险画像失败");
            }
        } catch (Exception e) {
            log.error("生成风险画像失败: {}", e.getMessage(), e);
            return Result.fail("生成风险画像失败");
        }
    }

    @Operation(summary = "批量生成风险画像")
    @PostMapping("/batch-generate")
    public Result<List<RiskProfile>> batchGenerateRiskProfiles(
            @ApiParam(value = "用户ID列表", required = true) @RequestBody List<Long> userIds) {
        try {
            List<RiskProfile> riskProfiles = riskProfileService.batchGenerateRiskProfiles(userIds);
            return Result.success(riskProfiles);
        } catch (Exception e) {
            log.error("批量生成风险画像失败: {}", e.getMessage(), e);
            return Result.fail("批量生成风险画像失败");
        }
    }

    @Operation(summary = "统计风险画像信息")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getRiskProfileStats() {
        try {
            Map<String, Object> stats = riskProfileService.getRiskProfileStats();
            return Result.success(stats);
        } catch (Exception e) {
            log.error("统计风险画像信息失败: {}", e.getMessage(), e);
            return Result.fail("统计风险画像信息失败");
        }
    }

    @Operation(summary = "获取风险评分最高的用户")
    @GetMapping("/top-risk-users")
    public Result<List<RiskProfile>> getTopRiskUsers(
            @ApiParam(value = "数量限制", required = true) @RequestParam Integer limit) {
        try {
            List<RiskProfile> riskProfiles = riskProfileService.getTopRiskUsers(limit);
            return Result.success(riskProfiles);
        } catch (Exception e) {
            log.error("获取风险评分最高的用户失败: {}", e.getMessage(), e);
            return Result.fail("获取风险评分最高的用户失败");
        }
    }

    @Operation(summary = "获取风险评分分布")
    @GetMapping("/score-distribution")
    public Result<Map<String, Object>> getScoreDistribution() {
        try {
            Map<String, Object> distribution = riskProfileService.getScoreDistribution();
            return Result.success(distribution);
        } catch (Exception e) {
            log.error("获取风险评分分布失败: {}", e.getMessage(), e);
            return Result.fail("获取风险评分分布失败");
        }
    }
}
