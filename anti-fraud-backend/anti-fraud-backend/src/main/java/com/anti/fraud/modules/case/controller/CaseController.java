package com.anti.fraud.modules.case.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.case.entity.Case;
import com.anti.fraud.modules.case.service.CaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 案例控制器
 */
@RestController
@RequestMapping("/case")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "案例管理")
public class CaseController {

    private final CaseService caseService;

    @Operation(summary = "新增案例")
    @PostMapping("/add")
    public Result<Void> addCase(@ApiParam(value = "案例信息", required = true) @RequestBody Case caseInfo) {
        try {
            boolean success = caseService.addCase(caseInfo);
            if (success) {
                return Result.successMsg("新增案例成功");
            } else {
                return Result.fail("新增案例失败");
            }
        } catch (Exception e) {
            log.error("新增案例失败: {}", e.getMessage(), e);
            return Result.fail("新增案例失败");
        }
    }

    @Operation(summary = "更新案例")
    @PutMapping("/update")
    public Result<Void> updateCase(@ApiParam(value = "案例信息", required = true) @RequestBody Case caseInfo) {
        try {
            boolean success = caseService.updateCase(caseInfo);
            if (success) {
                return Result.successMsg("更新案例成功");
            } else {
                return Result.fail("更新案例失败");
            }
        } catch (Exception e) {
            log.error("更新案例失败: {}", e.getMessage(), e);
            return Result.fail("更新案例失败");
        }
    }

    @Operation(summary = "删除案例")
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteCase(@ApiParam(value = "案例ID", required = true) @PathVariable Long id) {
        try {
            boolean success = caseService.deleteCase(id);
            if (success) {
                return Result.successMsg("删除案例成功");
            } else {
                return Result.fail("删除案例失败");
            }
        } catch (Exception e) {
            log.error("删除案例失败: {}", e.getMessage(), e);
            return Result.fail("删除案例失败");
        }
    }

    @Operation(summary = "获取案例详情")
    @GetMapping("/detail/{id}")
    public Result<Case> getCaseById(@ApiParam(value = "案例ID", required = true) @PathVariable Long id) {
        try {
            Case caseInfo = caseService.getCaseById(id);
            if (caseInfo != null) {
                // 增加浏览量
                caseService.increaseViewCount(id);
                return Result.success(caseInfo);
            } else {
                return Result.fail("案例不存在");
            }
        } catch (Exception e) {
            log.error("获取案例详情失败: {}", e.getMessage(), e);
            return Result.fail("获取案例详情失败");
        }
    }

    @Operation(summary = "分页查询案例")
    @PostMapping("/list")
    public Result<Map<String, Object>> getCaseList(
            @ApiParam(value = "查询参数", required = false) @RequestBody(required = false) Map<String, Object> params,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            if (params == null) {
                params = new java.util.HashMap<>();
            }
            Map<String, Object> result = caseService.getCaseList(params, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询案例列表失败: {}", e.getMessage(), e);
            return Result.fail("查询案例列表失败");
        }
    }

    @Operation(summary = "根据案例类型查询案例")
    @GetMapping("/by-type")
    public Result<Map<String, Object>> getCasesByType(
            @ApiParam(value = "案例类型: 1-电信诈骗, 2-网络诈骗, 3-金融诈骗, 4-冒充公检法诈骗, 5-刷单诈骗, 6-中奖诈骗, 7-投资理财诈骗, 8-婚恋诈骗", required = true) @RequestParam Integer type,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = caseService.getCasesByType(type, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("根据案例类型查询案例失败: {}", e.getMessage(), e);
            return Result.fail("根据案例类型查询案例失败");
        }
    }

    @Operation(summary = "增加点赞数")
    @PostMapping("/like/{id}")
    public Result<Void> increaseLikeCount(@ApiParam(value = "案例ID", required = true) @PathVariable Long id) {
        try {
            boolean success = caseService.increaseLikeCount(id);
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

    @Operation(summary = "增加评论数")
    @PostMapping("/comment/{id}")
    public Result<Void> increaseCommentCount(@ApiParam(value = "案例ID", required = true) @PathVariable Long id) {
        try {
            boolean success = caseService.increaseCommentCount(id);
            if (success) {
                return Result.successMsg("增加评论数成功");
            } else {
                return Result.fail("增加评论数失败");
            }
        } catch (Exception e) {
            log.error("增加评论数失败: {}", e.getMessage(), e);
            return Result.fail("增加评论数失败");
        }
    }

    @Operation(summary = "增加分享数")
    @PostMapping("/share/{id}")
    public Result<Void> increaseShareCount(@ApiParam(value = "案例ID", required = true) @PathVariable Long id) {
        try {
            boolean success = caseService.increaseShareCount(id);
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

    @Operation(summary = "获取热门案例")
    @GetMapping("/hot")
    public Result<List<Case>> getHotCases(
            @ApiParam(value = "数量限制", required = true) @RequestParam Integer limit) {
        try {
            List<Case> cases = caseService.getHotCases(limit);
            return Result.success(cases);
        } catch (Exception e) {
            log.error("获取热门案例失败: {}", e.getMessage(), e);
            return Result.fail("获取热门案例失败");
        }
    }

    @Operation(summary = "获取最新案例")
    @GetMapping("/latest")
    public Result<List<Case>> getLatestCases(
            @ApiParam(value = "数量限制", required = true) @RequestParam Integer limit) {
        try {
            List<Case> cases = caseService.getLatestCases(limit);
            return Result.success(cases);
        } catch (Exception e) {
            log.error("获取最新案例失败: {}", e.getMessage(), e);
            return Result.fail("获取最新案例失败");
        }
    }

    @Operation(summary = "统计案例信息")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getCaseStats() {
        try {
            Map<String, Object> stats = caseService.getCaseStats();
            return Result.success(stats);
        } catch (Exception e) {
            log.error("统计案例信息失败: {}", e.getMessage(), e);
            return Result.fail("统计案例信息失败");
        }
    }

    @Operation(summary = "搜索案例")
    @GetMapping("/search")
    public Result<Map<String, Object>> searchCases(
            @ApiParam(value = "关键词", required = true) @RequestParam String keyword,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = caseService.searchCases(keyword, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("搜索案例失败: {}", e.getMessage(), e);
            return Result.fail("搜索案例失败");
        }
    }

    @Operation(summary = "批量导入案例")
    @PostMapping("/batch-import")
    public Result<List<Case>> batchImportCases(
            @ApiParam(value = "案例列表", required = true) @RequestBody List<Case> cases) {
        try {
            List<Case> importedCases = caseService.batchImportCases(cases);
            return Result.success(importedCases);
        } catch (Exception e) {
            log.error("批量导入案例失败: {}", e.getMessage(), e);
            return Result.fail("批量导入案例失败");
        }
    }
}
