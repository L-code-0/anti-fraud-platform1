package com.anti.fraud.modules.file.service.impl;

import com.anti.fraud.common.exception.BusinessException;
import com.anti.fraud.common.utils.SecurityUtils;
import com.anti.fraud.modules.file.entity.FileInfo;
import com.anti.fraud.modules.file.mapper.FileInfoMapper;
import com.anti.fraud.modules.file.service.FileService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 文件管理服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    
    private final FileInfoMapper fileInfoMapper;
    
    @Value("${file.upload.path:/tmp/uploads}")
    private String uploadPath;
    
    @Value("${file.access.url:/api/file/download}")
    private String accessUrl;
    
    private static final Set<String> IMAGE_TYPES = Set.of("image/jpeg", "image/png", "image/gif", "image/webp");
    private static final Set<String> VIDEO_TYPES = Set.of("video/mp4", "video/webm", "video/ogg");
    private static final Set<String> DOCUMENT_TYPES = Set.of(
            "application/pdf", "application/msword", 
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "text/plain", "text/html", "text/markdown"
    );
    
    @Override
    @Transactional
    public FileInfo uploadFile(MultipartFile file, String category, String bizType, Long bizId) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }
        
        Long userId = SecurityUtils.getCurrentUserId();
        String userName = SecurityUtils.getCurrentUserName();
        
        // 获取文件信息
        String originalName = file.getOriginalFilename();
        String extension = getFileExtension(originalName);
        String contentType = file.getContentType();
        long fileSize = file.getSize();
        
        // 判断文件类型
        if (category == null) {
            category = determineCategory(contentType);
        }
        
        // 验证文件大小（最大100MB）
        if (fileSize > 100 * 1024 * 1024) {
            throw new BusinessException("文件大小不能超过100MB");
        }
        
        // 计算MD5
        String md5 = calculateMd5(file);
        
        // 检查是否已存在相同文件（去重）
        FileInfo existingFile = fileInfoMapper.selectByMd5(md5);
        if (existingFile != null) {
            log.info("文件已存在，复用: {}", existingFile.getFilePath());
            return existingFile;
        }
        
        // 生成存储路径
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String storedFileName = uuid + extension;
        String relativePath = category + "/" + datePath + "/" + storedFileName;
        String fullPath = uploadPath + "/" + relativePath;
        
        // 创建目录并保存文件
        try {
            Path path = Paths.get(fullPath);
            Files.createDirectories(path.getParent());
            file.transferTo(path.toFile());
        } catch (IOException e) {
            log.error("文件保存失败", e);
            throw new BusinessException("文件上传失败");
        }
        
        // 保存文件信息
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileName(storedFileName);
        fileInfo.setOriginalName(originalName);
        fileInfo.setFileSize(fileSize);
        fileInfo.setFileType(contentType);
        fileInfo.setExtension(extension);
        fileInfo.setFilePath(relativePath);
        fileInfo.setFileUrl(accessUrl + "/" + storedFileName);
        fileInfo.setCategory(category);
        fileInfo.setUploadUserId(userId);
        fileInfo.setUploadUserName(userName);
        fileInfo.setBizType(bizType);
        fileInfo.setBizId(bizId);
        fileInfo.setStorageType("local");
        fileInfo.setMd5(md5);
        fileInfo.setStatus(1);
        fileInfo.setDownloadCount(0);
        fileInfo.setCreateTime(LocalDate.now().atStartOfDay());
        fileInfo.setUpdateTime(LocalDate.now().atStartOfDay());
        
        fileInfoMapper.insert(fileInfo);
        
        log.info("文件上传成功: {}, 用户: {}", originalName, userName);
        return fileInfo;
    }
    
    @Override
    @Transactional
    public FileInfo uploadImage(MultipartFile file, String category, String bizType, Long bizId) {
        String contentType = file.getContentType();
        if (contentType == null || !IMAGE_TYPES.contains(contentType)) {
            throw new BusinessException("仅支持上传图片文件（jpg、png、gif、webp）");
        }
        
        // 验证图片大小（最大5MB）
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new BusinessException("图片大小不能超过5MB");
        }
        
        return uploadFile(file, "image", bizType, bizId);
    }
    
    @Override
    public IPage<FileInfo> getFilePage(Integer page, Integer size, String category, String bizType, String keyword) {
        Page<FileInfo> pageParam = new Page<>(page, size);
        return fileInfoMapper.selectFilePage(pageParam, category, bizType, keyword);
    }
    
    @Override
    public FileInfo getFileById(Long id) {
        FileInfo fileInfo = fileInfoMapper.selectById(id);
        if (fileInfo == null) {
            throw new BusinessException("文件不存在");
        }
        return fileInfo;
    }
    
    @Override
    public String getFileUrl(Long id) {
        FileInfo fileInfo = getFileById(id);
        return fileInfo.getFileUrl();
    }
    
    @Override
    @Transactional
    public void deleteFile(Long id) {
        FileInfo fileInfo = getFileById(id);
        
        // 检查权限
        Long userId = SecurityUtils.getCurrentUserId();
        if (!fileInfo.getUploadUserId().equals(userId)) {
            throw new BusinessException("无权删除他人上传的文件");
        }
        
        // 删除物理文件
        String fullPath = uploadPath + "/" + fileInfo.getFilePath();
        try {
            Files.deleteIfExists(Paths.get(fullPath));
        } catch (IOException e) {
            log.warn("物理文件删除失败: {}", fullPath);
        }
        
        // 删除数据库记录
        fileInfoMapper.deleteById(id);
        log.info("文件删除成功: {}", fileInfo.getOriginalName());
    }
    
    @Override
    @Transactional
    public void deleteFiles(List<Long> ids) {
        for (Long id : ids) {
            deleteFile(id);
        }
    }
    
    @Override
    public byte[] downloadFile(Long id) {
        FileInfo fileInfo = getFileById(id);
        
        // 增加下载次数
        fileInfo.setDownloadCount(fileInfo.getDownloadCount() + 1);
        fileInfoMapper.updateById(fileInfo);
        
        // 读取文件
        String fullPath = uploadPath + "/" + fileInfo.getFilePath();
        try {
            return Files.readAllBytes(Paths.get(fullPath));
        } catch (IOException e) {
            log.error("文件读取失败", e);
            throw new BusinessException("文件读取失败");
        }
    }
    
    @Override
    public IPage<FileInfo> getUserFiles(Integer page, Integer size, Long userId) {
        if (userId == null) {
            userId = SecurityUtils.getCurrentUserId();
        }
        Page<FileInfo> pageParam = new Page<>(page, size);
        return fileInfoMapper.selectByUserId(pageParam, userId);
    }
    
    @Override
    public List<FileInfo> getBizFiles(String bizType, Long bizId) {
        LambdaQueryWrapper<FileInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FileInfo::getBizType, bizType)
               .eq(FileInfo::getBizId, bizId)
               .eq(FileInfo::getStatus, 1)
               .orderByDesc(FileInfo::getCreateTime);
        return fileInfoMapper.selectList(wrapper);
    }
    
    @Override
    @Transactional
    public void updateFileStatus(Long id, Integer status) {
        FileInfo fileInfo = getFileById(id);
        fileInfo.setStatus(status);
        fileInfo.setUpdateTime(LocalDate.now().atStartOfDay());
        fileInfoMapper.updateById(fileInfo);
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int dotIndex = filename.lastIndexOf('.');
        return dotIndex > 0 ? filename.substring(dotIndex).toLowerCase() : "";
    }
    
    /**
     * 判断文件分类
     */
    private String determineCategory(String contentType) {
        if (contentType == null) {
            return "other";
        }
        if (IMAGE_TYPES.contains(contentType)) {
            return "image";
        }
        if (VIDEO_TYPES.contains(contentType)) {
            return "video";
        }
        if (DOCUMENT_TYPES.contains(contentType)) {
            return "document";
        }
        return "other";
    }
    
    /**
     * 计算文件MD5
     */
    private String calculateMd5(MultipartFile file) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(file.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | IOException e) {
            log.error("MD5计算失败", e);
            return UUID.randomUUID().toString();
        }
    }
}
