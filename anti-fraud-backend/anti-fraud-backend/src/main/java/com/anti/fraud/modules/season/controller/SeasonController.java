package com.anti.fraud.modules.season.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.common.utils.SecurityUtils;
import com.anti.fraud.modules.season.entity.SeasonInfo;
import com.anti.fraud.modules.season.entity.UserSeasonData;
import com.anti.fraud.modules.season.service.SeasonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 赛季控制器
 */
@RestController
@RequestMapping("/season")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "赛季服务")
public class SeasonController {

    private final SeasonService seasonService;

    @Operation(summary = "获取当前赛季信息")
    @GetMapping("/current")
    public Result<SeasonInfo> getCurrentSeason() {
        try {
            SeasonInfo season = seasonService.getCurrentSeason();
            return Result.success(season);
        } catch (Exception e) {
            log.error("获取当前赛季失败: {}", e.getMessage(), e);
            return Result.fail("获取当前赛季失败");
        }
    }

    @Operation(summary = "获取历史赛季列表")
    @GetMapping("/history")
    public Result<List<SeasonInfo>> getHistorySeasons(
            @ApiParam(value = "数量限制") @RequestParam(required = false, defaultValue = "10") Integer limit) {
        try {
            List<SeasonInfo> seasons = seasonService.getHistorySeasons(limit);
            return Result.success(seasons);
        } catch (Exception e) {
            log.error("获取历史赛季失败: {}", e.getMessage(), e);
            return Result.fail("获取历史赛季失败");
        }
    }

    @Operation(summary = "获取用户当前赛季数据")
    @GetMapping("/user-current")
    public Result<UserSeasonData> getUserCurrentSeasonData() {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.fail("请先登录");
            }

            UserSeasonData userSeasonData = seasonService.getUserCurrentSeasonData(userId);
            return Result.success(userSeasonData);
        } catch (Exception e) {
            log.error("获取用户当前赛季数据失败: {}", e.getMessage(), e);
            return Result.fail("获取用户当前赛季数据失败");
        }
    }

    @Operation(summary = "获取用户历史赛季数据")
    @GetMapping("/user-history")
    public Result<List<UserSeasonData>> getUserHistorySeasonData(
            @ApiParam(value = "数量限制") @RequestParam(required = false, defaultValue = "10") Integer limit) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.fail("请先登录");
            }

            List<UserSeasonData> userSeasonDataList = seasonService.getUserHistorySeasonData(userId, limit);
            return Result.success(userSeasonDataList);
        } catch (Exception e) {
            log.error("获取用户历史赛季数据失败: {}", e.getMessage(), e);
            return Result.fail("获取用户历史赛季数据失败");
        }
    }

    @Operation(summary = "更新用户赛季积分")
    @PostMapping("/update-points")
    public Result<Boolean> updateUserSeasonPoints(
            @ApiParam(value = "积分增量", required = true) @RequestParam Integer points) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.fail("请先登录");
            }

            boolean result = seasonService.updateUserSeasonPoints(userId, points);
            return Result.success(result);
        } catch (Exception e) {
            log.error("更新用户赛季积分失败: {}", e.getMessage(), e);
            return Result.fail("更新用户赛季积分失败");
        }
    }

    @Operation(summary = "获取赛季排行榜")
    @GetMapping("/ranking/{seasonId}")
    public Result<List<Map<String, Object>>> getSeasonRanking(
            @ApiParam(value = "赛季ID", required = true) @PathVariable Long seasonId,
            @ApiParam(value = "数量限制") @RequestParam(required = false, defaultValue = "10") Integer limit) {
        try {
            List<Map<String, Object>> ranking = seasonService.getSeasonRanking(seasonId, limit);
            return Result.success(ranking);
        } catch (Exception e) {
            log.error("获取赛季排行榜失败: {}", e.getMessage(), e);
            return Result.fail("获取赛季排行榜失败");
        }
    }

    @Operation(summary = "检查赛季状态")
    @PostMapping("/check-status")
    public Result<Boolean> checkSeasonStatus() {
        try {
            boolean result = seasonService.checkSeasonStatus();
            return Result.success(result);
        } catch (Exception e) {
            log.error("检查赛季状态失败: {}", e.getMessage(), e);
            return Result.fail("检查赛季状态失败");
        }
    }

    @Operation(summary = "创建赛季")
    @PostMapping("/create")
    public Result<SeasonInfo> createSeason(@RequestBody SeasonInfo seasonInfo) {
        try {
            SeasonInfo newSeason = seasonService.createSeason(seasonInfo);
            return Result.success(newSeason);
        } catch (Exception e) {
            log.error("创建赛季失败: {}", e.getMessage(), e);
            return Result.fail("创建赛季失败");
        }
    }

    @Operation(summary = "更新赛季")
    @PutMapping("/update")
    public Result<Boolean> updateSeason(@RequestBody SeasonInfo seasonInfo) {
        try {
            boolean result = seasonService.updateSeason(seasonInfo);
            return Result.success(result);
        } catch (Exception e) {
            log.error("更新赛季失败: {}", e.getMessage(), e);
            return Result.fail("更新赛季失败");
        }
    }

    @Operation(summary = "删除赛季")
    @DeleteMapping("/delete/{seasonId}")
    public Result<Boolean> deleteSeason(
            @ApiParam(value = "赛季ID", required = true) @PathVariable Long seasonId) {
        try {
            boolean result = seasonService.deleteSeason(seasonId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("删除赛季失败: {}", e.getMessage(), e);
            return Result.fail("删除赛季失败");
        }
    }

    @Operation(summary = "获取赛季详情")
    @GetMapping("/detail/{seasonId}")
    public Result<SeasonInfo> getSeasonById(
            @ApiParam(value = "赛季ID", required = true) @PathVariable Long seasonId) {
        try {
            SeasonInfo season = seasonService.getSeasonById(seasonId);
            return Result.success(season);
        } catch (Exception e) {
            log.error("获取赛季详情失败: {}", e.getMessage(), e);
            return Result.fail("获取赛季详情失败");
        }
    }

    @Operation(summary = "结算赛季")
    @PostMapping("/settle/{seasonId}")
    public Result<Boolean> settleSeason(
            @ApiParam(value = "赛季ID", required = true) @PathVariable Long seasonId) {
        try {
            boolean result = seasonService.settleSeason(seasonId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("结算赛季失败: {}", e.getMessage(), e);
            return Result.fail("结算赛季失败");
        }
    }

    @Operation(summary = "重置赛季排行榜")
    @PostMapping("/reset-ranking/{seasonId}")
    public Result<Boolean> resetSeasonRanking(
            @ApiParam(value = "赛季ID", required = true) @PathVariable Long seasonId) {
        try {
            boolean result = seasonService.resetSeasonRanking(seasonId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("重置赛季排行榜失败: {}", e.getMessage(), e);
            return Result.fail("重置赛季排行榜失败");
        }
    }
}
