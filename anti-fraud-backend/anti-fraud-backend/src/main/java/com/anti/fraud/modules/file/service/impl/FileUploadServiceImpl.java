package com.anti.fraud.modules.file.service.impl;

import com.anti.fraud.modules.file.service.FileUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传服务实现
 */
@Service
@Slf4j
public class FileUploadServiceImpl implements FileUploadService {
    
    @Value("${file.upload.path}")
    private String uploadPath;
    
    @Value("${file.upload.prefix}")
    private String uploadPrefix;
    
    @Value("${file.upload.max-size}")
    private long maxSize;
    
    @Value("${file.upload.allowed-types}")
    private String allowedTypes;
    
    @Override
    public Map<String, String> uploadFile(MultipartFile file, String module) throws IOException {
        // 验证文件大小
        if (file.getSize() > maxSize) {
            throw new IOException("File size exceeds the limit: " + maxSize / 1024 / 1024 + "MB");
        }
        
        // 验证文件类型
        String originalFilename = file.getOriginalFilename();
        String fileType = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if (!allowedTypes.contains(fileType)) {
            throw new IOException("File type not allowed: " + fileType);
        }
        
        // 生成文件路径
        String datePath = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        String fileName = UUID.randomUUID().toString() + "." + fileType;
        String relativePath = module + "/" + datePath + "/" + fileName;
        String fullPath = uploadPath + "/" + relativePath;
        
        // 创建目录
        File directory = new File(fullPath).getParentFile();
        if (!directory.exists()) {
            directory.mkdirs();
        }
        
        // 保存文件
        Path path = Paths.get(fullPath);
        Files.write(path, file.getBytes());
        
        // 生成访问URL
        String fileUrl = uploadPrefix + "/" + relativePath;
        
        // 返回结果
        Map<String, String> result = new HashMap<>();
        result.put("filePath", relativePath);
        result.put("fileUrl", fileUrl);
        result.put("fileName", originalFilename);
        
        log.info("File uploaded: {} -> {}", originalFilename, fileUrl);
        return result;
    }
    
    @Override
    public Map<String, String>[] uploadFiles(MultipartFile[] files, String module) throws IOException {
        Map<String, String>[] results = new Map[files.length];
        for (int i = 0; i < files.length; i++) {
            results[i] = uploadFile(files[i], module);
        }
        return results;
    }
    
    @Override
    public boolean deleteFile(String filePath) {
        try {
            String fullPath = uploadPath + "/" + filePath;
            Path path = Paths.get(fullPath);
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            log.error("Delete file failed: {}", filePath, e);
            return false;
        }
    }
    
    @Override
    public String getFileUrl(String filePath) {
        return uploadPrefix + "/" + filePath;
    }
}
