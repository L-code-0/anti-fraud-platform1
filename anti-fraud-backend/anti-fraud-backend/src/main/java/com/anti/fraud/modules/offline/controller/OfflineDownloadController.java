package com.anti.fraud.modules.offline.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.offline.entity.OfflineDownload;
import com.anti.fraud.modules.offline.service.OfflineDownloadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 离线下载控制器
 */
@RestController
@RequestMapping("/offline/download")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "离线下载管理")
public class OfflineDownloadController {

    private final OfflineDownloadService offlineDownloadService;

    @Operation(summary = "开始下载")
    @PostMapping("/start")
    public Result<Void> startDownload(@ApiParam(value = "下载信息", required = true) @RequestBody OfflineDownload offlineDownload) {
        try {
            boolean success = offlineDownloadService.startDownload(offlineDownload);
            if (success) {
                return Result.successMsg("开始下载成功");
            } else {
                return Result.fail("开始下载失败");
            }
        } catch (Exception e) {
            log.error("开始下载失败: {}", e.getMessage(), e);
            return Result.fail("开始下载失败");
        }
    }

    @Operation(summary = "暂停下载")
    @PutMapping("/pause/{id}")
    public Result<Void> pauseDownload(@ApiParam(value = "下载记录ID", required = true) @PathVariable Long id) {
        try {
            boolean success = offlineDownloadService.pauseDownload(id);
            if (success) {
                return Result.successMsg("暂停下载成功");
            } else {
                return Result.fail("暂停下载失败");
            }
        } catch (Exception e) {
            log.error("暂停下载失败: {}", e.getMessage(), e);
            return Result.fail("暂停下载失败");
        }
    }

    @Operation(summary = "恢复下载")
    @PutMapping("/resume/{id}")
    public Result<Void> resumeDownload(@ApiParam(value = "下载记录ID", required = true) @PathVariable Long id) {
        try {
            boolean success = offlineDownloadService.resumeDownload(id);
            if (success) {
                return Result.successMsg("恢复下载成功");
            } else {
                return Result.fail("恢复下载失败");
            }
        } catch (Exception e) {
            log.error("恢复下载失败: {}", e.getMessage(), e);
            return Result.fail("恢复下载失败");
        }
    }

    @Operation(summary = "取消下载")
    @PutMapping("/cancel/{id}")
    public Result<Void> cancelDownload(@ApiParam(value = "下载记录ID", required = true) @PathVariable Long id) {
        try {
            boolean success = offlineDownloadService.cancelDownload(id);
            if (success) {
                return Result.successMsg("取消下载成功");
            } else {
                return Result.fail("取消下载失败");
            }
        } catch (Exception e) {
            log.error("取消下载失败: {}", e.getMessage(), e);
            return Result.fail("取消下载失败");
        }
    }

    @Operation(summary = "删除下载记录")
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteDownload(@ApiParam(value = "下载记录ID", required = true) @PathVariable Long id) {
        try {
            boolean success = offlineDownloadService.deleteDownload(id);
            if (success) {
                return Result.successMsg("删除下载记录成功");
            } else {
                return Result.fail("删除下载记录失败");
            }
        } catch (Exception e) {
            log.error("删除下载记录失败: {}", e.getMessage(), e);
            return Result.fail("删除下载记录失败");
        }
    }

    @Operation(summary = "获取下载详情")
    @GetMapping("/detail/{id}")
    public Result<OfflineDownload> getDownloadById(@ApiParam(value = "下载记录ID", required = true) @PathVariable Long id) {
        try {
            OfflineDownload download = offlineDownloadService.getDownloadById(id);
            if (download != null) {
                return Result.success(download);
            } else {
                return Result.fail("下载记录不存在");
            }
        } catch (Exception e) {
            log.error("获取下载详情失败: {}", e.getMessage(), e);
            return Result.fail("获取下载详情失败");
        }
    }

    @Operation(summary = "分页查询下载记录")
    @GetMapping("/list")
    public Result<Map<String, Object>> getDownloadList(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "状态: 1-待下载, 2-下载中, 3-已完成, 4-下载失败", required = false) @RequestParam(required = false) Integer status,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = offlineDownloadService.getDownloadList(userId, status, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询下载记录列表失败: {}", e.getMessage(), e);
            return Result.fail("查询下载记录列表失败");
        }
    }

    @Operation(summary = "按状态统计下载记录")
    @GetMapping("/stats/status")
    public Result<List<Map<String, Object>>> getStatusStats(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId) {
        try {
            List<Map<String, Object>> stats = offlineDownloadService.getStatusStats(userId);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("按状态统计下载记录失败: {}", e.getMessage(), e);
            return Result.fail("按状态统计下载记录失败");
        }
    }

    @Operation(summary = "按资源类型统计下载记录")
    @GetMapping("/stats/resource-type")
    public Result<List<Map<String, Object>>> getResourceTypeStats(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId) {
        try {
            List<Map<String, Object>> stats = offlineDownloadService.getResourceTypeStats(userId);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("按资源类型统计下载记录失败: {}", e.getMessage(), e);
            return Result.fail("按资源类型统计下载记录失败");
        }
    }

    @Operation(summary = "更新下载进度")
    @PutMapping("/update-progress/{id}")
    public Result<Void> updateProgress(
            @ApiParam(value = "下载记录ID", required = true) @PathVariable Long id,
            @ApiParam(value = "进度(0-100)", required = true) @RequestParam Integer progress,
            @ApiParam(value = "已下载大小(字节)", required = true) @RequestParam Long downloadedSize) {
        try {
            boolean success = offlineDownloadService.updateProgress(id, progress, downloadedSize);
            if (success) {
                return Result.successMsg("更新下载进度成功");
            } else {
                return Result.fail("更新下载进度失败");
            }
        } catch (Exception e) {
            log.error("更新下载进度失败: {}", e.getMessage(), e);
            return Result.fail("更新下载进度失败");
        }
    }

    @Operation(summary = "完成下载")
    @PutMapping("/complete/{id}")
    public Result<Void> completeDownload(@ApiParam(value = "下载记录ID", required = true) @PathVariable Long id) {
        try {
            boolean success = offlineDownloadService.completeDownload(id);
            if (success) {
                return Result.successMsg("完成下载成功");
            } else {
                return Result.fail("完成下载失败");
            }
        } catch (Exception e) {
            log.error("完成下载失败: {}", e.getMessage(), e);
            return Result.fail("完成下载失败");
        }
    }

    @Operation(summary = "标记下载失败")
    @PutMapping("/mark-failed/{id}")
    public Result<Void> markDownloadFailed(
            @ApiParam(value = "下载记录ID", required = true) @PathVariable Long id,
            @ApiParam(value = "失败原因", required = true) @RequestParam String failureReason) {
        try {
            boolean success = offlineDownloadService.markDownloadFailed(id, failureReason);
            if (success) {
                return Result.successMsg("标记下载失败成功");
            } else {
                return Result.fail("标记下载失败失败");
            }
        } catch (Exception e) {
            log.error("标记下载失败失败: {}", e.getMessage(), e);
            return Result.fail("标记下载失败失败");
        }
    }

    @Operation(summary = "获取待下载的记录")
    @GetMapping("/pending")
    public Result<List<OfflineDownload>> getPendingDownloads(
            @ApiParam(value = "数量限制", required = true) @RequestParam Integer limit) {
        try {
            List<OfflineDownload> downloads = offlineDownloadService.getPendingDownloads(limit);
            return Result.success(downloads);
        } catch (Exception e) {
            log.error("获取待下载的记录失败: {}", e.getMessage(), e);
            return Result.fail("获取待下载的记录失败");
        }
    }

    @Operation(summary = "获取下载中的记录")
    @GetMapping("/downloading")
    public Result<List<OfflineDownload>> getDownloadingRecords(
            @ApiParam(value = "数量限制", required = true) @RequestParam Integer limit) {
        try {
            List<OfflineDownload> downloads = offlineDownloadService.getDownloadingRecords(limit);
            return Result.success(downloads);
        } catch (Exception e) {
            log.error("获取下载中的记录失败: {}", e.getMessage(), e);
            return Result.fail("获取下载中的记录失败");
        }
    }

    @Operation(summary = "统计用户下载总量")
    @GetMapping("/stats/total-size")
    public Result<Long> getTotalDownloadSize(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId) {
        try {
            Long totalSize = offlineDownloadService.getTotalDownloadSize(userId);
            return Result.success(totalSize);
        } catch (Exception e) {
            log.error("统计用户下载总量失败: {}", e.getMessage(), e);
            return Result.fail("统计用户下载总量失败");
        }
    }

    @Operation(summary = "统计用户已完成的下载数量")
    @GetMapping("/stats/completed-count")
    public Result<Integer> getCompletedCount(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId) {
        try {
            Integer count = offlineDownloadService.getCompletedCount(userId);
            return Result.success(count);
        } catch (Exception e) {
            log.error("统计用户已完成的下载数量失败: {}", e.getMessage(), e);
            return Result.fail("统计用户已完成的下载数量失败");
        }
    }

    @Operation(summary = "清理过期的下载记录")
    @DeleteMapping("/clean-expired")
    public Result<Integer> cleanExpiredDownloads(
            @ApiParam(value = "天数", required = true) @RequestParam Integer days) {
        try {
            int count = offlineDownloadService.cleanExpiredDownloads(days);
            return Result.success(count);
        } catch (Exception e) {
            log.error("清理过期的下载记录失败: {}", e.getMessage(), e);
            return Result.fail("清理过期的下载记录失败");
        }
    }

    @Operation(summary = "批量开始下载")
    @PostMapping("/batch-start")
    public Result<Integer> batchStartDownload(
            @ApiParam(value = "下载记录列表", required = true) @RequestBody List<OfflineDownload> downloads) {
        try {
            int successCount = offlineDownloadService.batchStartDownload(downloads);
            return Result.success(successCount);
        } catch (Exception e) {
            log.error("批量开始下载失败: {}", e.getMessage(), e);
            return Result.fail("批量开始下载失败");
        }
    }

    @Operation(summary = "检查资源是否已下载")
    @GetMapping("/check-downloaded")
    public Result<Boolean> isResourceDownloaded(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "资源ID", required = true) @RequestParam Long resourceId) {
        try {
            boolean downloaded = offlineDownloadService.isResourceDownloaded(userId, resourceId);
            return Result.success(downloaded);
        } catch (Exception e) {
            log.error("检查资源是否已下载失败: {}", e.getMessage(), e);
            return Result.fail("检查资源是否已下载失败");
        }
    }

    @Operation(summary = "获取已下载的资源列表")
    @GetMapping("/downloaded-resources")
    public Result<Map<String, Object>> getDownloadedResources(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "资源类型: 1-知识文章, 2-视频, 3-音频, 4-文档, 5-其他", required = false) @RequestParam(required = false) Integer resourceType,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = offlineDownloadService.getDownloadedResources(userId, resourceType, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取已下载的资源列表失败: {}", e.getMessage(), e);
            return Result.fail("获取已下载的资源列表失败");
        }
    }
}
