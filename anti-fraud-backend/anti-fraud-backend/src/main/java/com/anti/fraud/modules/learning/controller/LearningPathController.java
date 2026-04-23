package com.anti.fraud.modules.learning.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.learning.entity.LearningPath;
import com.anti.fraud.modules.learning.entity.LearningPathNode;
import com.anti.fraud.modules.learning.service.LearningPathService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 学习路径控制器
 */
@RestController
@RequestMapping("/learning/path")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "学习路径管理")
public class LearningPathController {

    private final LearningPathService learningPathService;

    @Operation(summary = "创建学习路径")
    @PostMapping("/create")
    public Result<Void> createLearningPath(@ApiParam(value = "学习路径信息", required = true) @RequestBody LearningPath learningPath) {
        try {
            boolean success = learningPathService.createLearningPath(learningPath);
            if (success) {
                return Result.successMsg("创建学习路径成功");
            } else {
                return Result.fail("创建学习路径失败");
            }
        } catch (Exception e) {
            log.error("创建学习路径失败: {}", e.getMessage(), e);
            return Result.fail("创建学习路径失败");
        }
    }

    @Operation(summary = "更新学习路径")
    @PutMapping("/update")
    public Result<Void> updateLearningPath(@ApiParam(value = "学习路径信息", required = true) @RequestBody LearningPath learningPath) {
        try {
            boolean success = learningPathService.updateLearningPath(learningPath);
            if (success) {
                return Result.successMsg("更新学习路径成功");
            } else {
                return Result.fail("更新学习路径失败");
            }
        } catch (Exception e) {
            log.error("更新学习路径失败: {}", e.getMessage(), e);
            return Result.fail("更新学习路径失败");
        }
    }

    @Operation(summary = "删除学习路径")
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteLearningPath(@ApiParam(value = "学习路径ID", required = true) @PathVariable Long id) {
        try {
            boolean success = learningPathService.deleteLearningPath(id);
            if (success) {
                return Result.successMsg("删除学习路径成功");
            } else {
                return Result.fail("删除学习路径失败");
            }
        } catch (Exception e) {
            log.error("删除学习路径失败: {}", e.getMessage(), e);
            return Result.fail("删除学习路径失败");
        }
    }

    @Operation(summary = "获取学习路径详情")
    @GetMapping("/detail/{id}")
    public Result<LearningPath> getLearningPathById(@ApiParam(value = "学习路径ID", required = true) @PathVariable Long id) {
        try {
            LearningPath learningPath = learningPathService.getLearningPathById(id);
            if (learningPath != null) {
                // 增加浏览量
                learningPathService.incrementViewCount(id);
                return Result.success(learningPath);
            } else {
                return Result.fail("学习路径不存在");
            }
        } catch (Exception e) {
            log.error("获取学习路径详情失败: {}", e.getMessage(), e);
            return Result.fail("获取学习路径详情失败");
        }
    }

    @Operation(summary = "分页查询学习路径")
    @PostMapping("/list")
    public Result<Map<String, Object>> getLearningPathList(
            @ApiParam(value = "查询参数", required = false) @RequestBody(required = false) Map<String, Object> params,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            if (params == null) {
                params = new java.util.HashMap<>();
            }
            Map<String, Object> result = learningPathService.getLearningPathList(params, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询学习路径列表失败: {}", e.getMessage(), e);
            return Result.fail("查询学习路径列表失败");
        }
    }

    @Operation(summary = "根据目标用户类型查询学习路径")
    @GetMapping("/by-target-user-type")
    public Result<Map<String, Object>> getLearningPathsByTargetUserType(
            @ApiParam(value = "目标用户类型: 1-新手, 2-进阶, 3-专家", required = true) @RequestParam Integer targetUserType,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = learningPathService.getLearningPathsByTargetUserType(targetUserType, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("根据目标用户类型查询学习路径失败: {}", e.getMessage(), e);
            return Result.fail("根据目标用户类型查询学习路径失败");
        }
    }

    @Operation(summary = "根据难度查询学习路径")
    @GetMapping("/by-difficulty")
    public Result<Map<String, Object>> getLearningPathsByDifficulty(
            @ApiParam(value = "难度等级: 1-简单, 2-中等, 3-困难", required = true) @RequestParam Integer difficulty,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = learningPathService.getLearningPathsByDifficulty(difficulty, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("根据难度查询学习路径失败: {}", e.getMessage(), e);
            return Result.fail("根据难度查询学习路径失败");
        }
    }

    @Operation(summary = "统计学习路径信息")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getLearningPathStats() {
        try {
            Map<String, Object> stats = learningPathService.getLearningPathStats();
            return Result.success(stats);
        } catch (Exception e) {
            log.error("统计学习路径信息失败: {}", e.getMessage(), e);
            return Result.fail("统计学习路径信息失败");
        }
    }

    @Operation(summary = "获取热门学习路径")
    @GetMapping("/hot")
    public Result<List<LearningPath>> getHotLearningPaths(
            @ApiParam(value = "数量限制", required = true) @RequestParam Integer limit) {
        try {
            List<LearningPath> learningPaths = learningPathService.getHotLearningPaths(limit);
            return Result.success(learningPaths);
        } catch (Exception e) {
            log.error("获取热门学习路径失败: {}", e.getMessage(), e);
            return Result.fail("获取热门学习路径失败");
        }
    }

    @Operation(summary = "获取推荐学习路径")
    @GetMapping("/recommended")
    public Result<List<LearningPath>> getRecommendedLearningPaths(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "数量限制", required = true) @RequestParam Integer limit) {
        try {
            List<LearningPath> learningPaths = learningPathService.getRecommendedLearningPaths(userId, limit);
            return Result.success(learningPaths);
        } catch (Exception e) {
            log.error("获取推荐学习路径失败: {}", e.getMessage(), e);
            return Result.fail("获取推荐学习路径失败");
        }
    }

    @Operation(summary = "添加学习路径节点")
    @PostMapping("/node/add")
    public Result<Void> addLearningPathNode(@ApiParam(value = "学习路径节点信息", required = true) @RequestBody LearningPathNode node) {
        try {
            boolean success = learningPathService.addLearningPathNode(node);
            if (success) {
                return Result.successMsg("添加学习路径节点成功");
            } else {
                return Result.fail("添加学习路径节点失败");
            }
        } catch (Exception e) {
            log.error("添加学习路径节点失败: {}", e.getMessage(), e);
            return Result.fail("添加学习路径节点失败");
        }
    }

    @Operation(summary = "更新学习路径节点")
    @PutMapping("/node/update")
    public Result<Void> updateLearningPathNode(@ApiParam(value = "学习路径节点信息", required = true) @RequestBody LearningPathNode node) {
        try {
            boolean success = learningPathService.updateLearningPathNode(node);
            if (success) {
                return Result.successMsg("更新学习路径节点成功");
            } else {
                return Result.fail("更新学习路径节点失败");
            }
        } catch (Exception e) {
            log.error("更新学习路径节点失败: {}", e.getMessage(), e);
            return Result.fail("更新学习路径节点失败");
        }
    }

    @Operation(summary = "删除学习路径节点")
    @DeleteMapping("/node/delete/{id}")
    public Result<Void> deleteLearningPathNode(@ApiParam(value = "节点ID", required = true) @PathVariable Long id) {
        try {
            boolean success = learningPathService.deleteLearningPathNode(id);
            if (success) {
                return Result.successMsg("删除学习路径节点成功");
            } else {
                return Result.fail("删除学习路径节点失败");
            }
        } catch (Exception e) {
            log.error("删除学习路径节点失败: {}", e.getMessage(), e);
            return Result.fail("删除学习路径节点失败");
        }
    }

    @Operation(summary = "获取学习路径节点详情")
    @GetMapping("/node/detail/{id}")
    public Result<LearningPathNode> getLearningPathNodeById(@ApiParam(value = "节点ID", required = true) @PathVariable Long id) {
        try {
            LearningPathNode node = learningPathService.getLearningPathNodeById(id);
            if (node != null) {
                return Result.success(node);
            } else {
                return Result.fail("学习路径节点不存在");
            }
        } catch (Exception e) {
            log.error("获取学习路径节点详情失败: {}", e.getMessage(), e);
            return Result.fail("获取学习路径节点详情失败");
        }
    }

    @Operation(summary = "根据学习路径ID获取节点列表")
    @GetMapping("/nodes/by-path/{pathId}")
    public Result<List<LearningPathNode>> getLearningPathNodesByPathId(
            @ApiParam(value = "学习路径ID", required = true) @PathVariable Long pathId) {
        try {
            List<LearningPathNode> nodes = learningPathService.getLearningPathNodesByPathId(pathId);
            return Result.success(nodes);
        } catch (Exception e) {
            log.error("根据学习路径ID获取节点列表失败: {}", e.getMessage(), e);
            return Result.fail("根据学习路径ID获取节点列表失败");
        }
    }

    @Operation(summary = "计算学习路径的总时长")
    @GetMapping("/calculate-duration/{pathId}")
    public Result<Integer> calculateTotalDuration(@ApiParam(value = "学习路径ID", required = true) @PathVariable Long pathId) {
        try {
            Integer totalDuration = learningPathService.calculateTotalDuration(pathId);
            return Result.success(totalDuration);
        } catch (Exception e) {
            log.error("计算学习路径的总时长失败: {}", e.getMessage(), e);
            return Result.fail("计算学习路径的总时长失败");
        }
    }

    @Operation(summary = "获取学习路径的下一个节点")
    @GetMapping("/next-node")
    public Result<LearningPathNode> getNextNode(
            @ApiParam(value = "学习路径ID", required = true) @RequestParam Long pathId,
            @ApiParam(value = "当前节点顺序", required = true) @RequestParam Integer currentNodeOrder) {
        try {
            LearningPathNode node = learningPathService.getNextNode(pathId, currentNodeOrder);
            return Result.success(node);
        } catch (Exception e) {
            log.error("获取学习路径的下一个节点失败: {}", e.getMessage(), e);
            return Result.fail("获取学习路径的下一个节点失败");
        }
    }

    @Operation(summary = "获取学习路径的前一个节点")
    @GetMapping("/previous-node")
    public Result<LearningPathNode> getPreviousNode(
            @ApiParam(value = "学习路径ID", required = true) @RequestParam Long pathId,
            @ApiParam(value = "当前节点顺序", required = true) @RequestParam Integer currentNodeOrder) {
        try {
            LearningPathNode node = learningPathService.getPreviousNode(pathId, currentNodeOrder);
            return Result.success(node);
        } catch (Exception e) {
            log.error("获取学习路径的前一个节点失败: {}", e.getMessage(), e);
            return Result.fail("获取学习路径的前一个节点失败");
        }
    }

    @Operation(summary = "重新排序学习路径节点")
    @PutMapping("/reorder-nodes/{pathId}")
    public Result<Void> reorderNodes(@ApiParam(value = "学习路径ID", required = true) @PathVariable Long pathId) {
        try {
            boolean success = learningPathService.reorderNodes(pathId);
            if (success) {
                return Result.successMsg("重新排序学习路径节点成功");
            } else {
                return Result.fail("重新排序学习路径节点失败");
            }
        } catch (Exception e) {
            log.error("重新排序学习路径节点失败: {}", e.getMessage(), e);
            return Result.fail("重新排序学习路径节点失败");
        }
    }

    @Operation(summary = "生成个性化学习路径")
    @PostMapping("/generate-personalized")
    public Result<LearningPath> generatePersonalizedLearningPath(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "目标用户类型: 1-新手, 2-进阶, 3-专家", required = true) @RequestParam Integer targetUserType,
            @ApiParam(value = "难度等级: 1-简单, 2-中等, 3-困难", required = true) @RequestParam Integer difficulty) {
        try {
            LearningPath learningPath = learningPathService.generatePersonalizedLearningPath(userId, targetUserType, difficulty);
            if (learningPath != null) {
                return Result.success(learningPath);
            } else {
                return Result.fail("生成个性化学习路径失败");
            }
        } catch (Exception e) {
            log.error("生成个性化学习路径失败: {}", e.getMessage(), e);
            return Result.fail("生成个性化学习路径失败");
        }
    }

    @Operation(summary = "推荐学习路径")
    @GetMapping("/recommend")
    public Result<List<LearningPath>> recommendLearningPaths(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "数量限制", required = true) @RequestParam Integer limit) {
        try {
            List<LearningPath> learningPaths = learningPathService.recommendLearningPaths(userId, limit);
            return Result.success(learningPaths);
        } catch (Exception e) {
            log.error("推荐学习路径失败: {}", e.getMessage(), e);
            return Result.fail("推荐学习路径失败");
        }
    }
}
