package com.anti.fraud.modules.importdata.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.importdata.service.ImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 数据导入控制器
 */
@Tag(name = "数据导入", description = "批量导入数据功能")
@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
public class ImportController {
    
    private final ImportService importService;
    
    @Operation(summary = "导入用户数据")
    @PostMapping("/users")
    public Result<Map<String, Object>> importUsers(
            @RequestParam("file") MultipartFile file
    ) {
        Map<String, Object> result = importService.importUsers(file);
        return Result.success(result);
    }
    
    @Operation(summary = "导入知识内容")
    @PostMapping("/knowledge")
    public Result<Map<String, Object>> importKnowledge(
            @RequestParam("file") MultipartFile file
    ) {
        Map<String, Object> result = importService.importKnowledge(file);
        return Result.success(result);
    }
    
    @Operation(summary = "导入题库数据")
    @PostMapping("/questions")
    public Result<Map<String, Object>> importQuestions(
            @RequestParam("file") MultipartFile file
    ) {
        Map<String, Object> result = importService.importQuestions(file);
        return Result.success(result);
    }
    
    @Operation(summary = "导入举报记录")
    @PostMapping("/reports")
    public Result<Map<String, Object>> importReports(
            @RequestParam("file") MultipartFile file
    ) {
        Map<String, Object> result = importService.importReports(file);
        return Result.success(result);
    }
    
    @Operation(summary = "导入活动数据")
    @PostMapping("/activities")
    public Result<Map<String, Object>> importActivities(
            @RequestParam("file") MultipartFile file
    ) {
        Map<String, Object> result = importService.importActivities(file);
        return Result.success(result);
    }
}
