package com.anti.fraud.modules.multimedia.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.multimedia.entity.MultimediaResource;
import com.anti.fraud.modules.multimedia.service.MultimediaResourceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 多媒体资源控制器
 */
@RestController
@RequestMapping("/multimedia")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "多媒体资源管理")
public class MultimediaResourceController {

    private final MultimediaResourceService multimediaResourceService;

    @Operation(summary = "新增多媒体资源")
    @PostMapping("/add")
    public Result<Void> addMultimediaResource(@ApiParam(value = "多媒体资源信息", required = true) @RequestBody MultimediaResource multimediaResource) {
        try {
            boolean success = multimediaResourceService.addMultimediaResource(multimediaResource);
            if (success) {
                return Result.successMsg("新增多媒体资源成功");
            } else {
                return Result.fail("新增多媒体资源失败");
            }
        } catch (Exception e) {
            log.error("新增多媒体资源失败: {}", e.getMessage(), e);
            return Result.fail("新增多媒体资源失败");
        }
    }

    @Operation(summary = "更新多媒体资源")
    @PutMapping("/update")
    public Result<Void> updateMultimediaResource(@ApiParam(value = "多媒体资源信息", required = true) @RequestBody MultimediaResource multimediaResource) {
        try {
            boolean success = multimediaResourceService.updateMultimediaResource(multimediaResource);
            if (success) {
                return Result.successMsg("更新多媒体资源成功");
            } else {
                return Result.fail("更新多媒体资源失败");
            }
        } catch (Exception e) {
            log.error("更新多媒体资源失败: {}", e.getMessage(), e);
            return Result.fail("更新多媒体资源失败");
        }
    }

    @Operation(summary = "删除多媒体资源")
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteMultimediaResource(@ApiParam(value = "多媒体资源ID", required = true) @PathVariable Long id) {
        try {
            boolean success = multimediaResourceService.deleteMultimediaResource(id);
            if (success) {
                return Result.successMsg("删除多媒体资源成功");
            } else {
                return Result.fail("删除多媒体资源失败");
            }
        } catch (Exception e) {
            log.error("删除多媒体资源失败: {}", e.getMessage(), e);
            return Result.fail("删除多媒体资源失败");
        }
    }

    @Operation(summary = "获取多媒体资源详情")
    @GetMapping("/detail/{id}")
    public Result<MultimediaResource> getMultimediaResourceById(@ApiParam(value = "多媒体资源ID", required = true) @PathVariable Long id) {
        try {
            MultimediaResource multimediaResource = multimediaResourceService.getMultimediaResourceById(id);
            if (multimediaResource != null) {
                // 增加浏览量
                multimediaResourceService.increaseViewCount(id);
                return Result.success(multimediaResource);
            } else {
                return Result.fail("多媒体资源不存在");
            }
        } catch (Exception e) {
            log.error("获取多媒体资源详情失败: {}", e.getMessage(), e);
            return Result.fail("获取多媒体资源详情失败");
        }
    }

    @Operation(summary = "分页查询多媒体资源")
    @PostMapping("/list")
    public Result<Map<String, Object>> getMultimediaResourceList(
            @ApiParam(value = "查询参数", required = false) @RequestBody(required = false) Map<String, Object> params,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            if (params == null) {
                params = new java.util.HashMap<>();
            }
            Map<String, Object> result = multimediaResourceService.getMultimediaResourceList(params, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询多媒体资源列表失败: {}", e.getMessage(), e);
            return Result.fail("查询多媒体资源列表失败");
        }
    }

    @Operation(summary = "根据资源类型查询多媒体资源")
    @GetMapping("/by-type")
    public Result<Map<String, Object>> getMultimediaResourcesByType(
            @ApiParam(value = "资源类型: 1-视频, 2-音频, 3-图片, 4-文档", required = true) @RequestParam Integer type,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = multimediaResourceService.getMultimediaResourcesByType(type, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("根据资源类型查询多媒体资源失败: {}", e.getMessage(), e);
            return Result.fail("根据资源类型查询多媒体资源失败");
        }
    }

    @Operation(summary = "根据知识点查询多媒体资源")
    @GetMapping("/by-knowledge-point")
    public Result<Map<String, Object>> getMultimediaResourcesByKnowledgePoint(
            @ApiParam(value = "知识点", required = true) @RequestParam String knowledgePoint,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = multimediaResourceService.getMultimediaResourcesByKnowledgePoint(knowledgePoint, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("根据知识点查询多媒体资源失败: {}", e.getMessage(), e);
            return Result.fail("根据知识点查询多媒体资源失败");
        }
    }

    @Operation(summary = "增加点赞数")
    @PostMapping("/like/{id}")
    public Result<Void> increaseLikeCount(@ApiParam(value = "多媒体资源ID", required = true) @PathVariable Long id) {
        try {
            boolean success = multimediaResourceService.increaseLikeCount(id);
            if (success) {
                return Result.successMsg("增加点赞数成功");
            } else {
                return Result.fail("增加点赞数失败");
            }
        } catch (Exception e) {
            log.error("增加点赞数失败: {}", e.getMessage(), e);
            return Result.fail("增加点赞数失败");
        }
    }

    @Operation(summary = "增加分享数")
    @PostMapping("/share/{id}")
    public Result<Void> increaseShareCount(@ApiParam(value = "多媒体资源ID", required = true) @PathVariable Long id) {
        try {
            boolean success = multimediaResourceService.increaseShareCount(id);
            if (success) {
                return Result.successMsg("增加分享数成功");
            } else {
                return Result.fail("增加分享数失败");
            }
        } catch (Exception e) {
            log.error("增加分享数失败: {}", e.getMessage(), e);
            return Result.fail("增加分享数失败");
        }
    }

    @Operation(summary = "获取热门多媒体资源")
    @GetMapping("/hot")
    public Result<List<MultimediaResource>> getHotResources(
            @ApiParam(value = "数量限制", required = true) @RequestParam Integer limit) {
        try {
            List<MultimediaResource> multimediaResources = multimediaResourceService.getHotResources(limit);
            return Result.success(multimediaResources);
        } catch (Exception e) {
            log.error("获取热门多媒体资源失败: {}", e.getMessage(), e);
            return Result.fail("获取热门多媒体资源失败");
        }
    }

    @Operation(summary = "获取最新多媒体资源")
    @GetMapping("/latest")
    public Result<List<MultimediaResource>> getLatestResources(
            @ApiParam(value = "数量限制", required = true) @RequestParam Integer limit) {
        try {
            List<MultimediaResource> multimediaResources = multimediaResourceService.getLatestResources(limit);
            return Result.success(multimediaResources);
        } catch (Exception e) {
            log.error("获取最新多媒体资源失败: {}", e.getMessage(), e);
            return Result.fail("获取最新多媒体资源失败");
        }
    }

    @Operation(summary = "统计多媒体资源信息")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getMultimediaResourceStats() {
        try {
            Map<String, Object> stats = multimediaResourceService.getMultimediaResourceStats();
            return Result.success(stats);
        } catch (Exception e) {
            log.error("统计多媒体资源信息失败: {}", e.getMessage(), e);
            return Result.fail("统计多媒体资源信息失败");
        }
    }

    @Operation(summary = "搜索多媒体资源")
    @GetMapping("/search")
    public Result<Map<String, Object>> searchMultimediaResources(
            @ApiParam(value = "关键词", required = true) @RequestParam String keyword,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = multimediaResourceService.searchMultimediaResources(keyword, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("搜索多媒体资源失败: {}", e.getMessage(), e);
            return Result.fail("搜索多媒体资源失败");
        }
    }

    @Operation(summary = "批量上传多媒体资源")
    @PostMapping("/batch-upload")
    public Result<List<MultimediaResource>> batchUploadMultimediaResources(
            @ApiParam(value = "多媒体资源列表", required = true) @RequestBody List<MultimediaResource> resources) {
        try {
            List<MultimediaResource> uploadedResources = multimediaResourceService.batchUploadMultimediaResources(resources);
            return Result.success(uploadedResources);
        } catch (Exception e) {
            log.error("批量上传多媒体资源失败: {}", e.getMessage(), e);
            return Result.fail("批量上传多媒体资源失败");
        }
    }
}
