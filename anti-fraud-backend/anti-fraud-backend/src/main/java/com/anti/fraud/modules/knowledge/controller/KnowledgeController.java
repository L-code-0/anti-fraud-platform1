package com.anti.fraud.modules.knowledge.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.knowledge.entity.KnowledgeNode;
import com.anti.fraud.modules.knowledge.entity.KnowledgeEdge;
import com.anti.fraud.modules.knowledge.service.KnowledgeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 知识图谱服务控制器
 */
@RestController
@RequestMapping("/knowledge")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "知识图谱服务")
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    @Operation(summary = "创建节点")
    @PostMapping("/node/create")
    public Result<String> createNode(@ApiParam(value = "节点信息", required = true) @RequestBody KnowledgeNode knowledgeNode) {
        try {
            String nodeId = knowledgeService.createNode(knowledgeNode);
            return Result.success(nodeId);
        } catch (Exception e) {
            log.error("创建节点失败: {}", e.getMessage(), e);
            return Result.fail("创建节点失败");
        }
    }

    @Operation(summary = "更新节点")
    @PutMapping("/node/update")
    public Result<Void> updateNode(@ApiParam(value = "节点信息", required = true) @RequestBody KnowledgeNode knowledgeNode) {
        try {
            boolean success = knowledgeService.updateNode(knowledgeNode);
            if (success) {
                return Result.successMsg("更新节点成功");
            } else {
                return Result.fail("更新节点失败");
            }
        } catch (Exception e) {
            log.error("更新节点失败: {}", e.getMessage(), e);
            return Result.fail("更新节点失败");
        }
    }

    @Operation(summary = "删除节点")
    @DeleteMapping("/node/delete/{nodeId}")
    public Result<Void> deleteNode(@ApiParam(value = "节点ID", required = true) @PathVariable String nodeId) {
        try {
            boolean success = knowledgeService.deleteNode(nodeId);
            if (success) {
                return Result.successMsg("删除节点成功");
            } else {
                return Result.fail("删除节点失败");
            }
        } catch (Exception e) {
            log.error("删除节点失败: {}", e.getMessage(), e);
            return Result.fail("删除节点失败");
        }
    }

    @Operation(summary = "获取节点详情")
    @GetMapping("/node/detail/{nodeId}")
    public Result<KnowledgeNode> getNodeByNodeId(@ApiParam(value = "节点ID", required = true) @PathVariable String nodeId) {
        try {
            KnowledgeNode node = knowledgeService.getNodeByNodeId(nodeId);
            if (node != null) {
                return Result.success(node);
            } else {
                return Result.fail("节点不存在");
            }
        } catch (Exception e) {
            log.error("获取节点详情失败: {}", e.getMessage(), e);
            return Result.fail("获取节点详情失败");
        }
    }

    @Operation(summary = "分页查询节点列表")
    @GetMapping("/node/list")
    public Result<Map<String, Object>> getNodeList(
            @ApiParam(value = "节点类型: 1-诈骗类型，2-防范措施，3-案例，4-知识，5-其他", required = false) @RequestParam(required = false) Integer type,
            @ApiParam(value = "状态: 1-启用，2-禁用", required = false) @RequestParam(required = false) Integer status,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = knowledgeService.getNodeList(type, status, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询节点列表失败: {}", e.getMessage(), e);
            return Result.fail("查询节点列表失败");
        }
    }

    @Operation(summary = "模糊查询节点")
    @GetMapping("/node/search")
    public Result<Map<String, Object>> searchNodes(
            @ApiParam(value = "关键词", required = true) @RequestParam String keyword,
            @ApiParam(value = "状态: 1-启用，2-禁用", required = false) @RequestParam(required = false) Integer status,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = knowledgeService.searchNodes(keyword, status, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("模糊查询节点失败: {}", e.getMessage(), e);
            return Result.fail("模糊查询节点失败");
        }
    }

    @Operation(summary = "创建边")
    @PostMapping("/edge/create")
    public Result<String> createEdge(@ApiParam(value = "边信息", required = true) @RequestBody KnowledgeEdge knowledgeEdge) {
        try {
            String edgeId = knowledgeService.createEdge(knowledgeEdge);
            return Result.success(edgeId);
        } catch (Exception e) {
            log.error("创建边失败: {}", e.getMessage(), e);
            return Result.fail("创建边失败");
        }
    }

    @Operation(summary = "更新边")
    @PutMapping("/edge/update")
    public Result<Void> updateEdge(@ApiParam(value = "边信息", required = true) @RequestBody KnowledgeEdge knowledgeEdge) {
        try {
            boolean success = knowledgeService.updateEdge(knowledgeEdge);
            if (success) {
                return Result.successMsg("更新边成功");
            } else {
                return Result.fail("更新边失败");
            }
        } catch (Exception e) {
            log.error("更新边失败: {}", e.getMessage(), e);
            return Result.fail("更新边失败");
        }
    }

    @Operation(summary = "删除边")
    @DeleteMapping("/edge/delete/{edgeId}")
    public Result<Void> deleteEdge(@ApiParam(value = "边ID", required = true) @PathVariable String edgeId) {
        try {
            boolean success = knowledgeService.deleteEdge(edgeId);
            if (success) {
                return Result.successMsg("删除边成功");
            } else {
                return Result.fail("删除边失败");
            }
        } catch (Exception e) {
            log.error("删除边失败: {}", e.getMessage(), e);
            return Result.fail("删除边失败");
        }
    }

    @Operation(summary = "获取边详情")
    @GetMapping("/edge/detail/{edgeId}")
    public Result<KnowledgeEdge> getEdgeByEdgeId(@ApiParam(value = "边ID", required = true) @PathVariable String edgeId) {
        try {
            KnowledgeEdge edge = knowledgeService.getEdgeByEdgeId(edgeId);
            if (edge != null) {
                return Result.success(edge);
            } else {
                return Result.fail("边不存在");
            }
        } catch (Exception e) {
            log.error("获取边详情失败: {}", e.getMessage(), e);
            return Result.fail("获取边详情失败");
        }
    }

    @Operation(summary = "分页查询边列表")
    @GetMapping("/edge/list")
    public Result<Map<String, Object>> getEdgeList(
            @ApiParam(value = "边类型: 1-属于，2-包含，3-关联，4-推荐，5-其他", required = false) @RequestParam(required = false) Integer type,
            @ApiParam(value = "状态: 1-启用，2-禁用", required = false) @RequestParam(required = false) Integer status,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = knowledgeService.getEdgeList(type, status, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询边列表失败: {}", e.getMessage(), e);
            return Result.fail("查询边列表失败");
        }
    }

    @Operation(summary = "获取节点的入边")
    @GetMapping("/node/in-edges/{nodeId}")
    public Result<List<KnowledgeEdge>> getInEdges(
            @ApiParam(value = "节点ID", required = true) @PathVariable String nodeId,
            @ApiParam(value = "状态: 1-启用，2-禁用", required = false) @RequestParam(required = false) Integer status) {
        try {
            List<KnowledgeEdge> edges = knowledgeService.getInEdges(nodeId, status);
            return Result.success(edges);
        } catch (Exception e) {
            log.error("获取节点的入边失败: {}", e.getMessage(), e);
            return Result.fail("获取节点的入边失败");
        }
    }

    @Operation(summary = "获取节点的出边")
    @GetMapping("/node/out-edges/{nodeId}")
    public Result<List<KnowledgeEdge>> getOutEdges(
            @ApiParam(value = "节点ID", required = true) @PathVariable String nodeId,
            @ApiParam(value = "状态: 1-启用，2-禁用", required = false) @RequestParam(required = false) Integer status) {
        try {
            List<KnowledgeEdge> edges = knowledgeService.getOutEdges(nodeId, status);
            return Result.success(edges);
        } catch (Exception e) {
            log.error("获取节点的出边失败: {}", e.getMessage(), e);
            return Result.fail("获取节点的出边失败");
        }
    }

    @Operation(summary = "获取节点的邻居节点")
    @GetMapping("/node/neighbors/{nodeId}")
    public Result<List<KnowledgeNode>> getNeighbors(
            @ApiParam(value = "节点ID", required = true) @PathVariable String nodeId,
            @ApiParam(value = "状态: 1-启用，2-禁用", required = false) @RequestParam(required = false) Integer status) {
        try {
            List<KnowledgeNode> neighbors = knowledgeService.getNeighbors(nodeId, status);
            return Result.success(neighbors);
        } catch (Exception e) {
            log.error("获取节点的邻居节点失败: {}", e.getMessage(), e);
            return Result.fail("获取节点的邻居节点失败");
        }
    }

    @Operation(summary = "根据诈骗类型获取相关知识")
    @GetMapping("/related/knowledge")
    public Result<List<KnowledgeNode>> getRelatedKnowledgeByFraudType(
            @ApiParam(value = "诈骗类型ID", required = true) @RequestParam Long fraudTypeId,
            @ApiParam(value = "数量限制", required = true) @RequestParam Integer limit) {
        try {
            List<KnowledgeNode> relatedKnowledge = knowledgeService.getRelatedKnowledgeByFraudType(fraudTypeId, limit);
            return Result.success(relatedKnowledge);
        } catch (Exception e) {
            log.error("根据诈骗类型获取相关知识失败: {}", e.getMessage(), e);
            return Result.fail("根据诈骗类型获取相关知识失败");
        }
    }

    @Operation(summary = "建立诈骗类型与知识的映射")
    @PostMapping("/mapping/fraud-type-knowledge")
    public Result<Void> buildFraudTypeKnowledgeMapping(
            @ApiParam(value = "诈骗类型ID", required = true) @RequestParam Long fraudTypeId,
            @ApiParam(value = "知识ID列表", required = true) @RequestParam List<String> knowledgeIds) {
        try {
            boolean success = knowledgeService.buildFraudTypeKnowledgeMapping(fraudTypeId, knowledgeIds);
            if (success) {
                return Result.successMsg("建立诈骗类型与知识的映射成功");
            } else {
                return Result.fail("建立诈骗类型与知识的映射失败");
            }
        } catch (Exception e) {
            log.error("建立诈骗类型与知识的映射失败: {}", e.getMessage(), e);
            return Result.fail("建立诈骗类型与知识的映射失败");
        }
    }

    @Operation(summary = "获取热度最高的节点")
    @GetMapping("/node/hot")
    public Result<List<KnowledgeNode>> getHotNodes(
            @ApiParam(value = "节点类型: 1-诈骗类型，2-防范措施，3-案例，4-知识，5-其他", required = false) @RequestParam(required = false) Integer type,
            @ApiParam(value = "状态: 1-启用，2-禁用", required = false) @RequestParam(required = false) Integer status,
            @ApiParam(value = "数量限制", required = true) @RequestParam Integer limit) {
        try {
            List<KnowledgeNode> hotNodes = knowledgeService.getHotNodes(type, status, limit);
            return Result.success(hotNodes);
        } catch (Exception e) {
            log.error("获取热度最高的节点失败: {}", e.getMessage(), e);
            return Result.fail("获取热度最高的节点失败");
        }
    }

    @Operation(summary = "更新节点热度")
    @PutMapping("/node/update-hotness/{nodeId}")
    public Result<Void> updateNodeHotness(
            @ApiParam(value = "节点ID", required = true) @PathVariable String nodeId,
            @ApiParam(value = "热度", required = true) @RequestParam Integer hotness) {
        try {
            boolean success = knowledgeService.updateNodeHotness(nodeId, hotness);
            if (success) {
                return Result.successMsg("更新节点热度成功");
            } else {
                return Result.fail("更新节点热度失败");
            }
        } catch (Exception e) {
            log.error("更新节点热度失败: {}", e.getMessage(), e);
            return Result.fail("更新节点热度失败");
        }
    }

    @Operation(summary = "批量创建节点")
    @PostMapping("/node/batch-create")
    public Result<Integer> batchCreateNodes(@ApiParam(value = "节点列表", required = true) @RequestBody List<KnowledgeNode> nodes) {
        try {
            int count = knowledgeService.batchCreateNodes(nodes);
            return Result.success(count);
        } catch (Exception e) {
            log.error("批量创建节点失败: {}", e.getMessage(), e);
            return Result.fail("批量创建节点失败");
        }
    }

    @Operation(summary = "批量创建边")
    @PostMapping("/edge/batch-create")
    public Result<Integer> batchCreateEdges(@ApiParam(value = "边列表", required = true) @RequestBody List<KnowledgeEdge> edges) {
        try {
            int count = knowledgeService.batchCreateEdges(edges);
            return Result.success(count);
        } catch (Exception e) {
            log.error("批量创建边失败: {}", e.getMessage(), e);
            return Result.fail("批量创建边失败");
        }
    }
}
