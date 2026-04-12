package com.anti.fraud.modules.file.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.file.service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * 文件上传控制器
 */
@Tag(name = "文件上传", description = "文件上传相关操作")
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileUploadController {
    
    private final FileUploadService fileUploadService;
    
    @Operation(summary = "上传单个文件")
    @PostMapping("/upload")
    public Result<Map<String, String>> uploadFile(
            @Parameter(description = "上传的文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "模块名称") @RequestParam(value = "module", defaultValue = "common") String module
    ) {
        try {
            Map<String, String> result = fileUploadService.uploadFile(file, module);
            return Result.success(result);
        } catch (IOException e) {
            return Result.fail(e.getMessage());
        }
    }
    
    @Operation(summary = "批量上传文件")
    @PostMapping("/upload/batch")
    public Result<Map<String, String>[]> uploadFiles(
            @Parameter(description = "上传的文件数组") @RequestParam("files") MultipartFile[] files,
            @Parameter(description = "模块名称") @RequestParam(value = "module", defaultValue = "common") String module
    ) {
        try {
            Map<String, String>[] results = fileUploadService.uploadFiles(files, module);
            return Result.success(results);
        } catch (IOException e) {
            return Result.fail(e.getMessage());
        }
    }
    
    @Operation(summary = "删除文件")
    @DeleteMapping("/delete")
    public Result<Boolean> deleteFile(
            @Parameter(description = "文件路径") @RequestParam("filePath") String filePath
    ) {
        boolean result = fileUploadService.deleteFile(filePath);
        return Result.success(result);
    }
    
    @Operation(summary = "获取文件访问URL")
    @GetMapping("/url")
    public Result<String> getFileUrl(
            @Parameter(description = "文件路径") @RequestParam("filePath") String filePath
    ) {
        String fileUrl = fileUploadService.getFileUrl(filePath);
        return Result.success(fileUrl);
    }
}
