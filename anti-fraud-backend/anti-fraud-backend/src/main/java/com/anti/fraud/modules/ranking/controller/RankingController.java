package com.anti.fraud.modules.ranking.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.common.utils.SecurityUtils;
import com.anti.fraud.modules.ranking.service.RankingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 排行榜控制器
 */
@RestController
@RequestMapping("/ranking")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "排行榜服务")
public class RankingController {

    private final RankingService rankingService;

    @Operation(summary = "获取排行榜前N名")
    @GetMapping("/top")
    public Result<List<Map<String, Object>>> getTopRankings(
            @ApiParam(value = "数量限制", required = true) @RequestParam Integer limit) {
        try {
            List<Map<String, Object>> rankings = rankingService.getTopRankings(limit);
            return Result.success(rankings);
        } catch (Exception e) {
            log.error("获取排行榜失败: {}", e.getMessage(), e);
            return Result.fail("获取排行榜失败");
        }
    }

    @Operation(summary = "获取用户排名")
    @GetMapping("/user-ranking")
    public Result<Map<String, Object>> getUserRanking() {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.fail("请先登录");
            }

            long rank = rankingService.getUserRanking(userId);
            int points = rankingService.getUserPoints(userId);

            Map<String, Object> result = new java.util.HashMap<>();
            result.put("rank", rank);
            result.put("points", points);
            result.put("userId", userId);

            return Result.success(result);
        } catch (Exception e) {
            log.error("获取用户排名失败: {}", e.getMessage(), e);
            return Result.fail("获取用户排名失败");
        }
    }

    @Operation(summary = "获取指定范围内的排名")
    @GetMapping("/range")
    public Result<List<Map<String, Object>>> getRankingsInRange(
            @ApiParam(value = "起始位置（从0开始）", required = true) @RequestParam Integer start,
            @ApiParam(value = "结束位置", required = true) @RequestParam Integer end) {
        try {
            List<Map<String, Object>> rankings = rankingService.getRankingsInRange(start, end);
            return Result.success(rankings);
        } catch (Exception e) {
            log.error("获取指定范围排行榜失败: {}", e.getMessage(), e);
            return Result.fail("获取指定范围排行榜失败");
        }
    }

    @Operation(summary = "获取用户周围的排名")
    @GetMapping("/surrounding")
    public Result<List<Map<String, Object>>> getUserSurroundingRankings(
            @ApiParam(value = "范围", required = true) @RequestParam Integer range) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.fail("请先登录");
            }

            List<Map<String, Object>> rankings = rankingService.getUserSurroundingRankings(userId, range);
            return Result.success(rankings);
        } catch (Exception e) {
            log.error("获取用户周围排名失败: {}", e.getMessage(), e);
            return Result.fail("获取用户周围排名失败");
        }
    }

    @Operation(summary = "获取排行榜总人数")
    @GetMapping("/size")
    public Result<Long> getRankingSize() {
        try {
            long size = rankingService.getRankingSize();
            return Result.success(size);
        } catch (Exception e) {
            log.error("获取排行榜总人数失败: {}", e.getMessage(), e);
            return Result.fail("获取排行榜总人数失败");
        }
    }

    @Operation(summary = "刷新排行榜")
    @GetMapping("/refresh")
    public Result<Void> refreshRanking() {
        try {
            rankingService.refreshRanking();
            return Result.successMsg("刷新排行榜成功");
        } catch (Exception e) {
            log.error("刷新排行榜失败: {}", e.getMessage(), e);
            return Result.fail("刷新排行榜失败");
        }
    }
}
