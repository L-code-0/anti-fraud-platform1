package com.anti.fraud.modules.offline.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.offline.entity.OfflineDownload;
import com.anti.fraud.modules.offline.service.OfflineDownloadService;
import com.anti.fraud.utils.SecurityUtils;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/offline")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "离线下载")
public class OfflineDownloadController {

    private final OfflineDownloadService offlineDownloadService;

    @Operation(summary = "开始离线下载")
    @PostMapping("/download")
    public Result<OfflineDownload> startDownload(@RequestBody Map<String, Object> params) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        Long contentId = Long.parseLong(params.get("contentId").toString());
        String contentType = params.get("contentType").toString();

        try {
            OfflineDownload download = offlineDownloadService.startDownload(userId, contentId, contentType);
            return Result.success("开始下载成功", download);
        } catch (Exception e) {
            log.error("开始离线下载失败: {}", e.getMessage(), e);
            return Result.fail("开始下载失败");
        }
    }

    @Operation(summary = "获取下载任务状态")
    @GetMapping("/download/{id}")
    public Result<OfflineDownload> getDownloadStatus(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            OfflineDownload download = offlineDownloadService.getDownloadStatus(id, userId);
            if (download == null) {
                return Result.fail("下载任务不存在");
            }
            return Result.success("获取下载任务状态成功", download);
        } catch (Exception e) {
            log.error("获取下载任务状态失败: {}", e.getMessage(), e);
            return Result.fail("获取下载任务状态失败");
        }
    }

    @Operation(summary = "获取下载任务列表")
    @GetMapping("/download/list")
    public Result<List<OfflineDownload>> getDownloadList(@RequestParam(required = false) Integer status) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            List<OfflineDownload> downloads = offlineDownloadService.getDownloadList(userId, status);
            return Result.success("获取下载任务列表成功", downloads);
        } catch (Exception e) {
            log.error("获取下载任务列表失败: {}", e.getMessage(), e);
            return Result.fail("获取下载任务列表失败");
        }
    }

    @Operation(summary = "取消下载任务")
    @PostMapping("/download/{id}/cancel")
    public Result<Void> cancelDownload(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            boolean success = offlineDownloadService.cancelDownload(id, userId);
            if (success) {
                return Result.successMsg("取消下载成功");
            } else {
                return Result.fail("取消下载失败");
            }
        } catch (Exception e) {
            log.error("取消下载任务失败: {}", e.getMessage(), e);
            return Result.fail("取消下载失败");
        }
    }

    @Operation(summary = "删除下载任务")
    @DeleteMapping("/download/{id}")
    public Result<Void> deleteDownload(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            boolean success = offlineDownloadService.deleteDownload(id, userId);
            if (success) {
                return Result.successMsg("删除下载任务成功");
            } else {
                return Result.fail("删除下载任务失败");
            }
        } catch (Exception e) {
            log.error("删除下载任务失败: {}", e.getMessage(), e);
            return Result.fail("删除下载任务失败");
        }
    }

    @Operation(summary = "下载离线文件")
    @GetMapping("/download/{id}/file")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Map<String, Object> fileInfo = offlineDownloadService.getDownloadFile(id, userId);
            String filePath = fileInfo.get("filePath").toString();
            String fileName = fileInfo.get("fileName").toString();

            File file = new File(filePath);
            FileSystemResource resource = new FileSystemResource(file);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
            headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()));

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        } catch (Exception e) {
            log.error("下载离线文件失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "清理过期下载任务")
    @PostMapping("/clean-expired")
    public Result<Integer> cleanExpiredDownloads(@RequestParam(defaultValue = "30") Integer days) {
        try {
            int count = offlineDownloadService.cleanExpiredDownloads(days);
            return Result.success("清理过期下载任务成功", count);
        } catch (Exception e) {
            log.error("清理过期下载任务失败: {}", e.getMessage(), e);
            return Result.fail("清理过期下载任务失败");
        }
    }
}
