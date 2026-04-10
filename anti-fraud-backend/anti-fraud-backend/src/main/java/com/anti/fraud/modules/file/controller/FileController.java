package com.anti.fraud.modules.file.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.file.entity.FileInfo;
import com.anti.fraud.modules.file.service.FileService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件管理控制器
 */
@Tag(name = "文件管理", description = "文件上传、下载、删除等操作")
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {
    
    private final FileService fileService;
    
    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    public Result<FileInfo> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String bizType,
            @RequestParam(required = false) Long bizId
    ) {
        FileInfo fileInfo = fileService.uploadFile(file, category, bizType, bizId);
        return Result.success(fileInfo);
    }
    
    @Operation(summary = "上传图片")
    @PostMapping("/upload/image")
    public Result<FileInfo> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String bizType,
            @RequestParam(required = false) Long bizId
    ) {
        FileInfo fileInfo = fileService.uploadImage(file, category, bizType, bizId);
        return Result.success(fileInfo);
    }
    
    @Operation(summary = "分页查询文件列表")
    @GetMapping("/list")
    public Result<IPage<FileInfo>> getFilePage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String bizType,
            @RequestParam(required = false) String keyword
    ) {
        IPage<FileInfo> result = fileService.getFilePage(page, size, category, bizType, keyword);
        return Result.success(result);
    }
    
    @Operation(summary = "获取文件详情")
    @GetMapping("/{id}")
    public Result<FileInfo> getFileById(
            @Parameter(description = "文件ID") @PathVariable Long id
    ) {
        FileInfo fileInfo = fileService.getFileById(id);
        return Result.success(fileInfo);
    }
    
    @Operation(summary = "获取文件访问URL")
    @GetMapping("/{id}/url")
    public Result<String> getFileUrl(
            @Parameter(description = "文件ID") @PathVariable Long id
    ) {
        String url = fileService.getFileUrl(id);
        return Result.success(url);
    }
    
    @Operation(summary = "删除文件")
    @DeleteMapping("/{id}")
    public Result<Void> deleteFile(
            @Parameter(description = "文件ID") @PathVariable Long id
    ) {
        fileService.deleteFile(id);
        return Result.success();
    }
    
    @Operation(summary = "批量删除文件")
    @DeleteMapping("/batch")
    public Result<Void> deleteFiles(
            @RequestBody List<Long> ids
    ) {
        fileService.deleteFiles(ids);
        return Result.success();
    }
    
    @Operation(summary = "下载文件")
    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadFile(
            @Parameter(description = "文件ID") @PathVariable Long id
    ) {
        FileInfo fileInfo = fileService.getFileById(id);
        byte[] fileData = fileService.downloadFile(id);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(fileInfo.getFileType()));
        headers.setContentDispositionFormData("attachment", fileInfo.getOriginalName());
        headers.setContentLength(fileData.length);
        
        return new ResponseEntity<>(fileData, headers, HttpStatus.OK);
    }
    
    @Operation(summary = "获取我的文件列表")
    @GetMapping("/my")
    public Result<IPage<FileInfo>> getMyFiles(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        IPage<FileInfo> result = fileService.getUserFiles(page, size, null);
        return Result.success(result);
    }
    
    @Operation(summary = "获取业务关联文件")
    @GetMapping("/biz/{bizType}/{bizId}")
    public Result<List<FileInfo>> getBizFiles(
            @Parameter(description = "业务类型") @PathVariable String bizType,
            @Parameter(description = "业务ID") @PathVariable Long bizId
    ) {
        List<FileInfo> files = fileService.getBizFiles(bizType, bizId);
        return Result.success(files);
    }
}
