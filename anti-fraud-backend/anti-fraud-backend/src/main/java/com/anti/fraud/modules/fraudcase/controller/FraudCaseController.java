package com.anti.fraud.modules.fraudcase.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.fraudcase.entity.FraudCase;
import com.anti.fraud.modules.fraudcase.service.FraudCaseService;
import com.anti.fraud.utils.SecurityUtils;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/case")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "真实案例")
public class FraudCaseController {

    private final FraudCaseService fraudCaseService;

    @Operation(summary = "创建案例")
    @PostMapping("/create")
    public Result<Void> createCase(@RequestBody FraudCase fraudCase) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        fraudCase.setCreatedBy(SecurityUtils.getCurrentUserName());
        fraudCase.setUpdatedBy(SecurityUtils.getCurrentUserName());

        try {
            boolean success = fraudCaseService.createCase(fraudCase);
            if (success) {
                return Result.successMsg("创建案例成功");
            } else {
                return Result.fail("创建案例失败");
            }
        } catch (Exception e) {
            log.error("创建案例失败: {}", e.getMessage(), e);
            return Result.fail("创建案例失败");
        }
    }

    @Operation(summary = "更新案例")
    @PostMapping("/update")
    public Result<Void> updateCase(@RequestBody FraudCase fraudCase) {
        fraudCase.setUpdatedBy(SecurityUtils.getCurrentUserName());

        try {
            boolean success = fraudCaseService.updateCase(fraudCase);
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
    @PostMapping("/delete/{id}")
    public Result<Void> deleteCase(@PathVariable Long id) {
        try {
            boolean success = fraudCaseService.deleteCase(id);
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
    public Result<FraudCase> getCaseById(@PathVariable Long id) {
        try {
            // 增加浏览次数
            fraudCaseService.increaseViewCount(id);
            
            FraudCase fraudCase = fraudCaseService.getCaseById(id);
            if (fraudCase != null) {
                return Result.success("获取案例详情成功", fraudCase);
            } else {
                return Result.fail("案例不存在");
            }
        } catch (Exception e) {
            log.error("获取案例详情失败: {}", e.getMessage(), e);
            return Result.fail("获取案例详情失败");
        }
    }

    @Operation(summary = "获取案例列表")
    @GetMapping("/list")
    public Result<List<FraudCase>> getCaseList(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String caseType,
            @RequestParam(required = false) String difficulty,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<FraudCase> cases = fraudCaseService.getCaseList(category, caseType, difficulty, page, size);
            return Result.success("获取案例列表成功", cases);
        } catch (Exception e) {
            log.error("获取案例列表失败: {}", e.getMessage(), e);
            return Result.fail("获取案例列表失败");
        }
    }

    @Operation(summary = "搜索案例")
    @GetMapping("/search")
    public Result<List<FraudCase>> searchCases(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<FraudCase> cases = fraudCaseService.searchCases(keyword, page, size);
            return Result.success("搜索案例成功", cases);
        } catch (Exception e) {
            log.error("搜索案例失败: {}", e.getMessage(), e);
            return Result.fail("搜索案例失败");
        }
    }

    @Operation(summary = "增加案例点赞次数")
    @PostMapping("/like/{id}")
    public Result<Void> increaseLikeCount(@PathVariable Long id) {
        try {
            boolean success = fraudCaseService.increaseLikeCount(id);
            if (success) {
                return Result.successMsg("点赞成功");
            } else {
                return Result.fail("点赞失败");
            }
        } catch (Exception e) {
            log.error("点赞失败: {}", e.getMessage(), e);
            return Result.fail("点赞失败");
        }
    }

    @Operation(summary = "获取热门案例")
    @GetMapping("/hot")
    public Result<List<FraudCase>> getHotCases(@RequestParam(defaultValue = "5") int count) {
        try {
            List<FraudCase> cases = fraudCaseService.getHotCases(count);
            return Result.success("获取热门案例成功", cases);
        } catch (Exception e) {
            log.error("获取热门案例失败: {}", e.getMessage(), e);
            return Result.fail("获取热门案例失败");
        }
    }

    @Operation(summary = "获取推荐案例")
    @GetMapping("/recommended")
    public Result<List<FraudCase>> getRecommendedCases(@RequestParam(defaultValue = "5") int count) {
        Long userId = SecurityUtils.getCurrentUserId();
        try {
            List<FraudCase> cases = fraudCaseService.getRecommendedCases(userId, count);
            return Result.success("获取推荐案例成功", cases);
        } catch (Exception e) {
            log.error("获取推荐案例失败: {}", e.getMessage(), e);
            return Result.fail("获取推荐案例失败");
        }
    }

    @Operation(summary = "获取案例统计信息")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getCaseStats() {
        try {
            Map<String, Object> stats = fraudCaseService.getCaseStats();
            return Result.success("获取案例统计信息成功", stats);
        } catch (Exception e) {
            log.error("获取案例统计信息失败: {}", e.getMessage(), e);
            return Result.fail("获取案例统计信息失败");
        }
    }

    @Operation(summary = "导入案例")
    @PostMapping("/import")
    public Result<Map<String, Object>> importCases(@RequestBody List<FraudCase> cases) {
        try {
            Map<String, Object> result = fraudCaseService.importCases(cases);
            if ((boolean) result.get("success")) {
                return Result.success("导入案例成功", result);
            } else {
                return Result.fail((String) result.get("message"));
            }
        } catch (Exception e) {
            log.error("导入案例失败: {}", e.getMessage(), e);
            return Result.fail("导入案例失败");
        }
    }

    @Operation(summary = "导出案例")
    @PostMapping("/export")
    public Result<Map<String, Object>> exportCases(@RequestBody List<Long> ids) {
        try {
            Map<String, Object> result = fraudCaseService.exportCases(ids);
            if ((boolean) result.get("success")) {
                return Result.success("导出案例成功", result);
            } else {
                return Result.fail((String) result.get("message"));
            }
        } catch (Exception e) {
            log.error("导出案例失败: {}", e.getMessage(), e);
            return Result.fail("导出案例失败");
        }
    }
}
