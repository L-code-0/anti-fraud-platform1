package com.anti.fraud.modules.irt.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.irt.entity.IrtModel;
import com.anti.fraud.modules.irt.entity.QuestionParam;
import com.anti.fraud.modules.irt.entity.AbilityEstimation;
import com.anti.fraud.modules.irt.service.IrtService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * IRT模型服务控制器
 */
@RestController
@RequestMapping("/irt")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "IRT模型服务")
public class IrtController {

    private final IrtService irtService;

    @Operation(summary = "创建IRT模型")
    @PostMapping("/model/create")
    public Result<String> createIrtModel(@ApiParam(value = "IRT模型信息", required = true) @RequestBody IrtModel irtModel) {
        try {
            String modelId = irtService.createIrtModel(irtModel);
            return Result.success(modelId);
        } catch (Exception e) {
            log.error("创建IRT模型失败: {}", e.getMessage(), e);
            return Result.fail("创建IRT模型失败");
        }
    }

    @Operation(summary = "更新IRT模型")
    @PutMapping("/model/update")
    public Result<Void> updateIrtModel(@ApiParam(value = "IRT模型信息", required = true) @RequestBody IrtModel irtModel) {
        try {
            boolean success = irtService.updateIrtModel(irtModel);
            if (success) {
                return Result.successMsg("更新IRT模型成功");
            } else {
                return Result.fail("更新IRT模型失败");
            }
        } catch (Exception e) {
            log.error("更新IRT模型失败: {}", e.getMessage(), e);
            return Result.fail("更新IRT模型失败");
        }
    }

    @Operation(summary = "删除IRT模型")
    @DeleteMapping("/model/delete/{modelId}")
    public Result<Void> deleteIrtModel(@ApiParam(value = "模型ID", required = true) @PathVariable String modelId) {
        try {
            boolean success = irtService.deleteIrtModel(modelId);
            if (success) {
                return Result.successMsg("删除IRT模型成功");
            } else {
                return Result.fail("删除IRT模型失败");
            }
        } catch (Exception e) {
            log.error("删除IRT模型失败: {}", e.getMessage(), e);
            return Result.fail("删除IRT模型失败");
        }
    }

    @Operation(summary = "获取IRT模型详情")
    @GetMapping("/model/detail/{modelId}")
    public Result<IrtModel> getIrtModelByModelId(@ApiParam(value = "模型ID", required = true) @PathVariable String modelId) {
        try {
            IrtModel model = irtService.getIrtModelByModelId(modelId);
            if (model != null) {
                return Result.success(model);
            } else {
                return Result.fail("IRT模型不存在");
            }
        } catch (Exception e) {
            log.error("获取IRT模型详情失败: {}", e.getMessage(), e);
            return Result.fail("获取IRT模型详情失败");
        }
    }

    @Operation(summary = "分页查询IRT模型列表")
    @GetMapping("/model/list")
    public Result<Map<String, Object>> getIrtModelList(
            @ApiParam(value = "模型类型: 1-1PL，2-2PL，3-3PL", required = false) @RequestParam(required = false) Integer modelType,
            @ApiParam(value = "状态: 1-启用，2-禁用，3-待训练", required = false) @RequestParam(required = false) Integer status,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = irtService.getIrtModelList(modelType, status, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询IRT模型列表失败: {}", e.getMessage(), e);
            return Result.fail("查询IRT模型列表失败");
        }
    }

    @Operation(summary = "训练IRT模型")
    @PostMapping("/model/train/{modelId}")
    public Result<Void> trainIrtModel(
            @ApiParam(value = "模型ID", required = true) @PathVariable String modelId,
            @ApiParam(value = "训练数据", required = true) @RequestBody List<Map<String, Object>> trainingData) {
        try {
            boolean success = irtService.trainIrtModel(modelId, trainingData);
            if (success) {
                return Result.successMsg("训练IRT模型成功");
            } else {
                return Result.fail("训练IRT模型失败");
            }
        } catch (Exception e) {
            log.error("训练IRT模型失败: {}", e.getMessage(), e);
            return Result.fail("训练IRT模型失败");
        }
    }

    @Operation(summary = "创建题目参数")
    @PostMapping("/question-param/create")
    public Result<String> createQuestionParam(@ApiParam(value = "题目参数信息", required = true) @RequestBody QuestionParam questionParam) {
        try {
            String paramId = irtService.createQuestionParam(questionParam);
            return Result.success(paramId);
        } catch (Exception e) {
            log.error("创建题目参数失败: {}", e.getMessage(), e);
            return Result.fail("创建题目参数失败");
        }
    }

    @Operation(summary = "更新题目参数")
    @PutMapping("/question-param/update")
    public Result<Void> updateQuestionParam(@ApiParam(value = "题目参数信息", required = true) @RequestBody QuestionParam questionParam) {
        try {
            boolean success = irtService.updateQuestionParam(questionParam);
            if (success) {
                return Result.successMsg("更新题目参数成功");
            } else {
                return Result.fail("更新题目参数失败");
            }
        } catch (Exception e) {
            log.error("更新题目参数失败: {}", e.getMessage(), e);
            return Result.fail("更新题目参数失败");
        }
    }

    @Operation(summary = "删除题目参数")
    @DeleteMapping("/question-param/delete/{paramId}")
    public Result<Void> deleteQuestionParam(@ApiParam(value = "参数ID", required = true) @PathVariable String paramId) {
        try {
            boolean success = irtService.deleteQuestionParam(paramId);
            if (success) {
                return Result.successMsg("删除题目参数成功");
            } else {
                return Result.fail("删除题目参数失败");
            }
        } catch (Exception e) {
            log.error("删除题目参数失败: {}", e.getMessage(), e);
            return Result.fail("删除题目参数失败");
        }
    }

    @Operation(summary = "获取题目参数详情")
    @GetMapping("/question-param/detail/{paramId}")
    public Result<QuestionParam> getQuestionParamByParamId(@ApiParam(value = "参数ID", required = true) @PathVariable String paramId) {
        try {
            QuestionParam param = irtService.getQuestionParamByParamId(paramId);
            if (param != null) {
                return Result.success(param);
            } else {
                return Result.fail("题目参数不存在");
            }
        } catch (Exception e) {
            log.error("获取题目参数详情失败: {}", e.getMessage(), e);
            return Result.fail("获取题目参数详情失败");
        }
    }

    @Operation(summary = "根据题目ID获取题目参数")
    @GetMapping("/question-param/by-question/{questionId}")
    public Result<QuestionParam> getQuestionParamByQuestionId(@ApiParam(value = "题目ID", required = true) @PathVariable String questionId) {
        try {
            QuestionParam param = irtService.getQuestionParamByQuestionId(questionId);
            if (param != null) {
                return Result.success(param);
            } else {
                return Result.fail("题目参数不存在");
            }
        } catch (Exception e) {
            log.error("根据题目ID获取题目参数失败: {}", e.getMessage(), e);
            return Result.fail("根据题目ID获取题目参数失败");
        }
    }

    @Operation(summary = "分页查询题目参数列表")
    @GetMapping("/question-param/list")
    public Result<Map<String, Object>> getQuestionParamList(
            @ApiParam(value = "模型ID", required = false) @RequestParam(required = false) String modelId,
            @ApiParam(value = "状态: 1-已标定，2-待标定，3-标定失败", required = false) @RequestParam(required = false) Integer status,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = irtService.getQuestionParamList(modelId, status, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询题目参数列表失败: {}", e.getMessage(), e);
            return Result.fail("查询题目参数列表失败");
        }
    }

    @Operation(summary = "标定题目参数")
    @PostMapping("/question-param/calibrate/{modelId}")
    public Result<Integer> calibrateQuestionParams(
            @ApiParam(value = "模型ID", required = true) @PathVariable String modelId,
            @ApiParam(value = "题目ID列表", required = true) @RequestParam List<String> questionIds,
            @ApiParam(value = "响应数据", required = true) @RequestBody List<Map<String, Object>> responseData) {
        try {
            int successCount = irtService.calibrateQuestionParams(modelId, questionIds, responseData);
            return Result.success(successCount);
        } catch (Exception e) {
            log.error("标定题目参数失败: {}", e.getMessage(), e);
            return Result.fail("标定题目参数失败");
        }
    }

    @Operation(summary = "创建能力估计")
    @PostMapping("/ability/create")
    public Result<String> createAbilityEstimation(@ApiParam(value = "能力估计信息", required = true) @RequestBody AbilityEstimation abilityEstimation) {
        try {
            String estimationId = irtService.createAbilityEstimation(abilityEstimation);
            return Result.success(estimationId);
        } catch (Exception e) {
            log.error("创建能力估计失败: {}", e.getMessage(), e);
            return Result.fail("创建能力估计失败");
        }
    }

    @Operation(summary = "更新能力估计")
    @PutMapping("/ability/update")
    public Result<Void> updateAbilityEstimation(@ApiParam(value = "能力估计信息", required = true) @RequestBody AbilityEstimation abilityEstimation) {
        try {
            boolean success = irtService.updateAbilityEstimation(abilityEstimation);
            if (success) {
                return Result.successMsg("更新能力估计成功");
            } else {
                return Result.fail("更新能力估计失败");
            }
        } catch (Exception e) {
            log.error("更新能力估计失败: {}", e.getMessage(), e);
            return Result.fail("更新能力估计失败");
        }
    }

    @Operation(summary = "删除能力估计")
    @DeleteMapping("/ability/delete/{estimationId}")
    public Result<Void> deleteAbilityEstimation(@ApiParam(value = "估计ID", required = true) @PathVariable String estimationId) {
        try {
            boolean success = irtService.deleteAbilityEstimation(estimationId);
            if (success) {
                return Result.successMsg("删除能力估计成功");
            } else {
                return Result.fail("删除能力估计失败");
            }
        } catch (Exception e) {
            log.error("删除能力估计失败: {}", e.getMessage(), e);
            return Result.fail("删除能力估计失败");
        }
    }

    @Operation(summary = "获取能力估计详情")
    @GetMapping("/ability/detail/{estimationId}")
    public Result<AbilityEstimation> getAbilityEstimationByEstimationId(@ApiParam(value = "估计ID", required = true) @PathVariable String estimationId) {
        try {
            AbilityEstimation estimation = irtService.getAbilityEstimationByEstimationId(estimationId);
            if (estimation != null) {
                return Result.success(estimation);
            } else {
                return Result.fail("能力估计不存在");
            }
        } catch (Exception e) {
            log.error("获取能力估计详情失败: {}", e.getMessage(), e);
            return Result.fail("获取能力估计详情失败");
        }
    }

    @Operation(summary = "根据用户ID获取能力估计列表")
    @GetMapping("/ability/list-by-user/{userId}")
    public Result<Map<String, Object>> getAbilityEstimationListByUserId(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long userId,
            @ApiParam(value = "测试状态: 1-进行中，2-已完成，3-已取消", required = false) @RequestParam(required = false) Integer testStatus,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = irtService.getAbilityEstimationListByUserId(userId, testStatus, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("根据用户ID查询能力估计列表失败: {}", e.getMessage(), e);
            return Result.fail("根据用户ID查询能力估计列表失败");
        }
    }

    @Operation(summary = "估计用户能力")
    @PostMapping("/ability/estimate/{estimationId}")
    public Result<Map<String, Object>> estimateAbility(
            @ApiParam(value = "估计ID", required = true) @PathVariable String estimationId,
            @ApiParam(value = "答题响应", required = true) @RequestBody List<Map<String, Object>> responses) {
        try {
            Map<String, Object> result = irtService.estimateAbility(estimationId, responses);
            return Result.success(result);
        } catch (Exception e) {
            log.error("估计用户能力失败: {}", e.getMessage(), e);
            return Result.fail("估计用户能力失败");
        }
    }

    @Operation(summary = "智能选题")
    @PostMapping("/question/select")
    public Result<List<String>> selectQuestions(
            @ApiParam(value = "模型ID", required = true) @RequestParam String modelId,
            @ApiParam(value = "能力值", required = true) @RequestParam Double ability,
            @ApiParam(value = "已答题ID列表", required = true) @RequestBody List<String> answeredQuestionIds,
            @ApiParam(value = "选题数量", required = true) @RequestParam Integer count) {
        try {
            List<String> questionIds = irtService.selectQuestions(modelId, ability, answeredQuestionIds, count);
            return Result.success(questionIds);
        } catch (Exception e) {
            log.error("智能选题失败: {}", e.getMessage(), e);
            return Result.fail("智能选题失败");
        }
    }

    @Operation(summary = "检查CAT测试是否应该终止")
    @PostMapping("/cat/should-terminate")
    public Result<Boolean> shouldTerminateCAT(
            @ApiParam(value = "估计ID", required = true) @RequestParam String estimationId,
            @ApiParam(value = "能力估计标准差", required = true) @RequestParam Double abilityStd,
            @ApiParam(value = "已答题数量", required = true) @RequestParam Integer answeredCount) {
        try {
            boolean shouldTerminate = irtService.shouldTerminateCAT(estimationId, abilityStd, answeredCount);
            return Result.success(shouldTerminate);
        } catch (Exception e) {
            log.error("检查CAT测试终止规则失败: {}", e.getMessage(), e);
            return Result.fail("检查CAT测试终止规则失败");
        }
    }

    @Operation(summary = "获取IRT模型统计信息")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getIrtStatistics() {
        try {
            Map<String, Object> statistics = irtService.getIrtStatistics();
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取IRT统计信息失败: {}", e.getMessage(), e);
            return Result.fail("获取IRT统计信息失败");
        }
    }

    @Operation(summary = "批量创建题目参数")
    @PostMapping("/question-param/batch-create")
    public Result<Integer> batchCreateQuestionParams(@ApiParam(value = "题目参数列表", required = true) @RequestBody List<QuestionParam> questionParams) {
        try {
            int count = irtService.batchCreateQuestionParams(questionParams);
            return Result.success(count);
        } catch (Exception e) {
            log.error("批量创建题目参数失败: {}", e.getMessage(), e);
            return Result.fail("批量创建题目参数失败");
        }
    }

    @Operation(summary = "批量标定题目参数")
    @PostMapping("/question-param/batch-calibrate/{modelId}")
    public Result<Integer> batchCalibrateQuestionParams(
            @ApiParam(value = "模型ID", required = true) @PathVariable String modelId,
            @ApiParam(value = "题目参数列表", required = true) @RequestBody List<QuestionParam> questionParams,
            @ApiParam(value = "响应数据", required = true) @RequestBody List<Map<String, Object>> responseData) {
        try {
            int successCount = irtService.batchCalibrateQuestionParams(modelId, questionParams, responseData);
            return Result.success(successCount);
        } catch (Exception e) {
            log.error("批量标定题目参数失败: {}", e.getMessage(), e);
            return Result.fail("批量标定题目参数失败");
        }
    }
}
