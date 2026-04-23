package com.anti.fraud.modules.ai.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.ai.service.AiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * AI服务控制器
 */
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "AI服务")
public class AiController {

    private final AiService aiService;

    @Operation(summary = "文本生成")
    @PostMapping("/generate/text")
    public Result<String> generateText(
            @ApiParam(value = "提示词", required = true) @RequestParam String prompt,
            @ApiParam(value = "模型名称", required = false) @RequestParam(required = false) String model,
            @ApiParam(value = "温度参数", required = false) @RequestParam(required = false) Double temperature,
            @ApiParam(value = "最大token数", required = false) @RequestParam(required = false) Integer maxTokens) {
        try {
            String result = aiService.generateText(prompt, model, temperature, maxTokens);
            return Result.success(result);
        } catch (Exception e) {
            log.error("生成文本失败: {}", e.getMessage(), e);
            return Result.fail("生成文本失败");
        }
    }

    @Operation(summary = "对话生成")
    @PostMapping("/generate/chat")
    public Result<String> generateChat(
            @ApiParam(value = "对话历史", required = true) @RequestBody List<Map<String, String>> messages,
            @ApiParam(value = "模型名称", required = false) @RequestParam(required = false) String model,
            @ApiParam(value = "温度参数", required = false) @RequestParam(required = false) Double temperature,
            @ApiParam(value = "最大token数", required = false) @RequestParam(required = false) Integer maxTokens) {
        try {
            String result = aiService.generateChat(messages, model, temperature, maxTokens);
            return Result.success(result);
        } catch (Exception e) {
            log.error("生成对话失败: {}", e.getMessage(), e);
            return Result.fail("生成对话失败");
        }
    }

    @Operation(summary = "智能问答")
    @PostMapping("/answer")
    public Result<String> answerQuestion(
            @ApiParam(value = "问题", required = true) @RequestParam String question,
            @ApiParam(value = "上下文", required = false) @RequestParam(required = false) String context,
            @ApiParam(value = "模型名称", required = false) @RequestParam(required = false) String model) {
        try {
            String result = aiService.answerQuestion(question, context, model);
            return Result.success(result);
        } catch (Exception e) {
            log.error("回答问题失败: {}", e.getMessage(), e);
            return Result.fail("回答问题失败");
        }
    }

    @Operation(summary = "批量生成文本")
    @PostMapping("/batch/generate")
    public Result<List<String>> batchGenerateText(
            @ApiParam(value = "提示词列表", required = true) @RequestBody List<String> prompts,
            @ApiParam(value = "模型名称", required = false) @RequestParam(required = false) String model,
            @ApiParam(value = "温度参数", required = false) @RequestParam(required = false) Double temperature,
            @ApiParam(value = "最大token数", required = false) @RequestParam(required = false) Integer maxTokens) {
        try {
            List<String> results = aiService.batchGenerateText(prompts, model, temperature, maxTokens);
            return Result.success(results);
        } catch (Exception e) {
            log.error("批量生成文本失败: {}", e.getMessage(), e);
            return Result.fail("批量生成文本失败");
        }
    }

    @Operation(summary = "获取模型列表")
    @GetMapping("/models")
    public Result<List<String>> getModels() {
        try {
            List<String> models = aiService.getModels();
            return Result.success(models);
        } catch (Exception e) {
            log.error("获取模型列表失败: {}", e.getMessage(), e);
            return Result.fail("获取模型列表失败");
        }
    }

    @Operation(summary = "健康检查")
    @GetMapping("/health")
    public Result<Boolean> healthCheck(
            @ApiParam(value = "模型名称", required = false) @RequestParam(required = false) String model) {
        try {
            boolean health = aiService.healthCheck(model);
            return Result.success(health);
        } catch (Exception e) {
            log.error("健康检查失败: {}", e.getMessage(), e);
            return Result.fail("健康检查失败");
        }
    }

    @Operation(summary = "设置默认模型")
    @PutMapping("/default-model")
    public Result<Void> setDefaultModel(
            @ApiParam(value = "模型名称", required = true) @RequestParam String model) {
        try {
            aiService.setDefaultModel(model);
            return Result.successMsg("设置默认模型成功");
        } catch (Exception e) {
            log.error("设置默认模型失败: {}", e.getMessage(), e);
            return Result.fail("设置默认模型失败");
        }
    }

    @Operation(summary = "获取默认模型")
    @GetMapping("/default-model")
    public Result<String> getDefaultModel() {
        try {
            String model = aiService.getDefaultModel();
            return Result.success(model);
        } catch (Exception e) {
            log.error("获取默认模型失败: {}", e.getMessage(), e);
            return Result.fail("获取默认模型失败");
        }
    }
}
