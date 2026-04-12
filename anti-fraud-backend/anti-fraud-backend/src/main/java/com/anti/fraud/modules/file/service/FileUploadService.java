package com.anti.fraud.modules.file.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * 文件上传服务
 */
public interface FileUploadService {
    
    /**
     * 上传文件
     * @param file 上传的文件
     * @param module 模块名称
     * @return 上传结果，包含文件路径和访问URL
     * @throws IOException 上传失败时抛出异常
     */
    Map<String, String> uploadFile(MultipartFile file, String module) throws IOException;
    
    /**
     * 批量上传文件
     * @param files 上传的文件数组
     * @param module 模块名称
     * @return 上传结果列表，每个元素包含文件路径和访问URL
     * @throws IOException 上传失败时抛出异常
     */
    Map<String, String>[] uploadFiles(MultipartFile[] files, String module) throws IOException;
    
    /**
     * 删除文件
     * @param filePath 文件路径
     * @return 是否删除成功
     */
    boolean deleteFile(String filePath);
    
    /**
     * 获取文件访问URL
     * @param filePath 文件路径
     * @return 文件访问URL
     */
    String getFileUrl(String filePath);
}
