package com.anti.fraud.modules.replay.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.replay.entity.CaseReplay;
import com.anti.fraud.modules.replay.service.CaseReplayService;
import com.anti.fraud.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 案例回放控制器
 */
@RestController
@RequestMapping("/replay")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "案例回放管理")
public class CaseReplayController {

    private final CaseReplayService caseReplayService;

    @Operation(summary = "新增案例回放")
    @PostMapping("/add")
    public Result<Void> addCaseReplay(@ApiParam(value = "案例回放信息", required = true) @RequestBody CaseReplay caseReplay) {
        try {
            boolean success = caseReplayService.addCaseReplay(caseReplay);
            if (success) {
                return Result.successMsg("新增案例回放成功");
            } else {
                return Result.fail("新增案例回放失败");
            }
        } catch (Exception e) {
            log.error("新增案例回放失败: {}", e.getMessage(), e);
            return Result.fail("新增案例回放失败");
        }
    }

    @Operation(summary = "更新案例回放")
    @PutMapping("/update")
    public Result<Void> updateCaseReplay(@ApiParam(value = "案例回放信息", required = true) @RequestBody CaseReplay caseReplay) {
        try {
            boolean success = caseReplayService.updateCaseReplay(caseReplay);
            if (success) {
                return Result.successMsg("更新案例回放成功");
            } else {
                return Result.fail("更新案例回放失败");
            }
        } catch (Exception e) {
            log.error("更新案例回放失败: {}", e.getMessage(), e);
            return Result.fail("更新案例回放失败");
        }
    }

    @Operation(summary = "删除案例回放")
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteCaseReplay(@ApiParam(value = "案例回放ID", required = true) @PathVariable Long id) {
        try {
            boolean success = caseReplayService.deleteCaseReplay(id);
            if (success) {
                return Result.successMsg("删除案例回放成功");
            } else {
                return Result.fail("删除案例回放失败");
            }
        } catch (Exception e) {
            log.error("删除案例回放失败: {}", e.getMessage(), e);
            return Result.fail("删除案例回放失败");
        }
    }

    @Operation(summary = "获取案例回放详情")
    @GetMapping("/detail/{id}")
    public Result<CaseReplay> getCaseReplayById(@ApiParam(value = "案例回放ID", required = true) @PathVariable Long id) {
        try {
            CaseReplay caseReplay = caseReplayService.getCaseReplayById(id);
            if (caseReplay != null) {
                return Result.success(caseReplay);
            } else {
                return Result.fail("案例回放不存在");
            }
        } catch (Exception e) {
            log.error("获取案例回放详情失败: {}", e.getMessage(), e);
            return Result.fail("获取案例回放详情失败");
        }
    }

    @Operation(summary = "分页查询案例回放")
    @PostMapping("/list")
    public Result<Map<String, Object>> getCaseReplayList(
            @ApiParam(value = "查询参数", required = false) @RequestBody(required = false) Map<String, Object> params,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            if (params == null) {
                params = new java.util.HashMap<>();
            }
            Map<String, Object> result = caseReplayService.getCaseReplayList(params, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询案例回放列表失败: {}", e.getMessage(), e);
            return Result.fail("查询案例回放列表失败");
        }
    }

    @Operation(summary = "根据案例ID查询案例回放")
    @GetMapping("/by-case/{caseId}")
    public Result<List<CaseReplay>> getCaseReplaysByCaseId(@ApiParam(value = "案例ID", required = true) @PathVariable Long caseId) {
        try {
            List<CaseReplay> caseReplays = caseReplayService.getCaseReplaysByCaseId(caseId);
            return Result.success(caseReplays);
        } catch (Exception e) {
            log.error("根据案例ID查询案例回放失败: {}", e.getMessage(), e);
            return Result.fail("根据案例ID查询案例回放失败");
        }
    }

    @Operation(summary = "增加浏览量")
    @PostMapping("/view/{id}")
    public Result<Void> increaseViewCount(@ApiParam(value = "案例回放ID", required = true) @PathVariable Long id) {
        try {
            boolean success = caseReplayService.increaseViewCount(id);
            if (success) {
                return Result.successMsg("增加浏览量成功");
            } else {
                return Result.fail("增加浏览量失败");
            }
        } catch (Exception e) {
            log.error("增加浏览量失败: {}", e.getMessage(), e);
            return Result.fail("增加浏览量失败");
        }
    }

    @Operation(summary = "增加点赞数")
    @PostMapping("/like/{id}")
    public Result<Void> increaseLikeCount(@ApiParam(value = "案例回放ID", required = true) @PathVariable Long id) {
        try {
            boolean success = caseReplayService.increaseLikeCount(id);
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
    public Result<Void> increaseShareCount(@ApiParam(value = "案例回放ID", required = true) @PathVariable Long id) {
        try {
            boolean success = caseReplayService.increaseShareCount(id);
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

    @Operation(summary = "获取热门案例回放")
    @GetMapping("/hot")
    public Result<List<CaseReplay>> getHotReplays(@ApiParam(value = "数量限制", required = true) @RequestParam Integer limit) {
        try {
            List<CaseReplay> caseReplays = caseReplayService.getHotReplays(limit);
            return Result.success(caseReplays);
        } catch (Exception e) {
            log.error("获取热门案例回放失败: {}", e.getMessage(), e);
            return Result.fail("获取热门案例回放失败");
        }
    }

    @Operation(summary = "获取最新案例回放")
    @GetMapping("/latest")
    public Result<List<CaseReplay>> getLatestReplays(@ApiParam(value = "数量限制", required = true) @RequestParam Integer limit) {
        try {
            List<CaseReplay> caseReplays = caseReplayService.getLatestReplays(limit);
            return Result.success(caseReplays);
        } catch (Exception e) {
            log.error("获取最新案例回放失败: {}", e.getMessage(), e);
            return Result.fail("获取最新案例回放失败");
        }
    }

    @Operation(summary = "统计案例回放信息")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getCaseReplayStats() {
        try {
            Map<String, Object> stats = caseReplayService.getCaseReplayStats();
            return Result.success(stats);
        } catch (Exception e) {
            log.error("统计案例回放信息失败: {}", e.getMessage(), e);
            return Result.fail("统计案例回放信息失败");
        }
    }

    @Operation(summary = "开始案例回放")
    @PostMapping("/start/{id}")
    public Result<Map<String, Object>> startReplay(@ApiParam(value = "案例回放ID", required = true) @PathVariable Long id) {
        try {
            Map<String, Object> result = caseReplayService.startReplay(id);
            if ((boolean) result.get("success")) {
                return Result.success(result);
            } else {
                return Result.fail((String) result.get("message"));
            }
        } catch (Exception e) {
            log.error("开始案例回放失败: {}", e.getMessage(), e);
            return Result.fail("开始案例回放失败");
        }
    }

    @Operation(summary = "获取回放进度")
    @GetMapping("/progress/{id}")
    public Result<Map<String, Object>> getReplayProgress(@ApiParam(value = "案例回放ID", required = true) @PathVariable Long id) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.fail("请先登录");
            }
            Map<String, Object> progress = caseReplayService.getReplayProgress(id, userId);
            return Result.success(progress);
        } catch (Exception e) {
            log.error("获取回放进度失败: {}", e.getMessage(), e);
            return Result.fail("获取回放进度失败");
        }
    }

    @Operation(summary = "更新回放进度")
    @PostMapping("/progress/{id}")
    public Result<Void> updateReplayProgress(
            @ApiParam(value = "案例回放ID", required = true) @PathVariable Long id,
            @ApiParam(value = "进度（秒）", required = true) @RequestParam Integer progress) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.fail("请先登录");
            }
            boolean success = caseReplayService.updateReplayProgress(id, userId, progress);
            if (success) {
                return Result.successMsg("更新回放进度成功");
            } else {
                return Result.fail("更新回放进度失败");
            }
        } catch (Exception e) {
            log.error("更新回放进度失败: {}", e.getMessage(), e);
            return Result.fail("更新回放进度失败");
        }
    }
}
