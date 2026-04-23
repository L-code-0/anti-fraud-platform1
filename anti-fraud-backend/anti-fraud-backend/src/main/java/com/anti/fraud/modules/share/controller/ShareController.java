package com.anti.fraud.modules.share.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.common.utils.SecurityUtils;
import com.anti.fraud.modules.share.service.ShareService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 社交分享控制器
 */
@RestController
@RequestMapping("/share")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "社交分享服务")
public class ShareController {

    private final ShareService shareService;

    @Operation(summary = "生成分享链接")
    @PostMapping("/generate-link")
    public Result<String> generateShareLink(
            @ApiParam(value = "分享类型：1-知识分享，2-勋章分享，3-排行榜分享，4-预警信息分享", required = true) @RequestParam Integer type,
            @ApiParam(value = "目标ID（如知识ID、勋章ID等）", required = true) @RequestParam Long targetId) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.fail("请先登录");
            }

            String shareLink = shareService.generateShareLink(userId, type, targetId);
            return Result.success(shareLink);
        } catch (Exception e) {
            log.error("生成分享链接失败: {}", e.getMessage(), e);
            return Result.fail("生成分享链接失败");
        }
    }

    @Operation(summary = "处理分享回调")
    @PostMapping("/callback")
    public Result<Map<String, Object>> handleShareCallback(
            @ApiParam(value = "分享码", required = true) @RequestParam String shareCode,
            @ApiParam(value = "分享平台", required = true) @RequestParam String platform) {
        try {
            Map<String, Object> result = shareService.handleShareCallback(shareCode, platform);
            return Result.success(result);
        } catch (Exception e) {
            log.error("处理分享回调失败: {}", e.getMessage(), e);
            return Result.fail("处理分享回调失败");
        }
    }

    @Operation(summary = "验证分享码")
    @GetMapping("/verify-code")
    public Result<Map<String, Object>> verifyShareCode(
            @ApiParam(value = "分享码", required = true) @RequestParam String shareCode) {
        try {
            Map<String, Object> result = shareService.verifyShareCode(shareCode);
            return Result.success(result);
        } catch (Exception e) {
            log.error("验证分享码失败: {}", e.getMessage(), e);
            return Result.fail("验证分享码失败");
        }
    }

    @Operation(summary = "领取分享奖励")
    @PostMapping("/claim-reward")
    public Result<Map<String, Object>> claimShareReward(
            @ApiParam(value = "分享码", required = true) @RequestParam String shareCode) {
        try {
            Map<String, Object> result = shareService.claimShareReward(shareCode);
            return Result.success(result);
        } catch (Exception e) {
            log.error("领取分享奖励失败: {}", e.getMessage(), e);
            return Result.fail("领取分享奖励失败");
        }
    }

    @Operation(summary = "获取用户分享记录")
    @GetMapping("/records")
    public Result<Map<String, Object>> getUserShareRecords(
            @ApiParam(value = "数量限制") @RequestParam(required = false, defaultValue = "10") Integer limit) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.fail("请先登录");
            }

            Map<String, Object> records = shareService.getUserShareRecords(userId, limit);
            return Result.success(records);
        } catch (Exception e) {
            log.error("获取用户分享记录失败: {}", e.getMessage(), e);
            return Result.fail("获取分享记录失败");
        }
    }

    @Operation(summary = "获取分享统计")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getShareStatistics() {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.fail("请先登录");
            }

            Map<String, Object> statistics = shareService.getShareStatistics(userId);
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取分享统计失败: {}", e.getMessage(), e);
            return Result.fail("获取分享统计失败");
        }
    }

    @Operation(summary = "批量生成分享链接")
    @PostMapping("/batch-generate")
    public Result<Map<String, String>> batchGenerateShareLinks(
            @ApiParam(value = "分享数据，key为分享类型，value为目标ID", required = true) @RequestBody Map<Integer, Long> shareData) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.fail("请先登录");
            }

            Map<String, String> shareLinks = shareService.batchGenerateShareLinks(userId, shareData);
            return Result.success(shareLinks);
        } catch (Exception e) {
            log.error("批量生成分享链接失败: {}", e.getMessage(), e);
            return Result.fail("批量生成分享链接失败");
        }
    }

    @Operation(summary = "生成分享海报")
    @PostMapping("/generate-poster")
    public Result<String> generateSharePoster(
            @ApiParam(value = "分享类型：1-知识分享，2-勋章分享，3-排行榜分享，4-预警信息分享", required = true) @RequestParam Integer type,
            @ApiParam(value = "目标ID（如知识ID、勋章ID等）", required = true) @RequestParam Long targetId) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.fail("请先登录");
            }

            String posterUrl = shareService.generateSharePoster(userId, type, targetId);
            return Result.success(posterUrl);
        } catch (Exception e) {
            log.error("生成分享海报失败: {}", e.getMessage(), e);
            return Result.fail("生成分享海报失败");
        }
    }

    @Operation(summary = "增加分享计数")
    @PostMapping("/increment-count")
    public Result<Boolean> incrementShareCount(
            @ApiParam(value = "目标ID", required = true) @RequestParam Long targetId,
            @ApiParam(value = "分享类型：1-知识分享，2-勋章分享，3-排行榜分享，4-预警信息分享", required = true) @RequestParam Integer type) {
        try {
            boolean result = shareService.incrementShareCount(targetId, type);
            return Result.success(result);
        } catch (Exception e) {
            log.error("增加分享计数失败: {}", e.getMessage(), e);
            return Result.fail("增加分享计数失败");
        }
    }

    @Operation(summary = "获取分享平台列表")
    @GetMapping("/platforms")
    public Result<Map<String, Object>> getSharePlatforms() {
        try {
            Map<String, Object> platforms = shareService.getSharePlatforms();
            return Result.success(platforms);
        } catch (Exception e) {
            log.error("获取分享平台列表失败: {}", e.getMessage(), e);
            return Result.fail("获取分享平台列表失败");
        }
    }
}
