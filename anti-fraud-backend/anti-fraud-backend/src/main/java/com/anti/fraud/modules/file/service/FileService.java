package com.anti.fraud.modules.file.service;

import com.anti.fraud.modules.file.entity.FileInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件管理服务接口
 */
public interface FileService {
    
    /**
     * 上传文件
     * @param file 上传的文件
     * @param category 文件分类
     * @param bizType 业务类型
     * @param bizId 业务ID
     * @return 文件信息
     */
    FileInfo uploadFile(MultipartFile file, String category, String bizType, Long bizId);
    
    /**
     * 上传图片（支持裁剪和压缩）
     * @param file 上传的文件
     * @param category 文件分类
     * @param bizType 业务类型
     * @param bizId 业务ID
     * @return 文件信息
     */
    FileInfo uploadImage(MultipartFile file, String category, String bizType, Long bizId);
    
    /**
     * 分页查询文件列表
     * @param page 页码
     * @param size 每页数量
     * @param category 文件分类
     * @param bizType 业务类型
     * @param keyword 关键词
     * @return 文件分页列表
     */
    IPage<FileInfo> getFilePage(Integer page, Integer size, String category, String bizType, String keyword);
    
    /**
     * 获取文件详情
     * @param id 文件ID
     * @return 文件信息
     */
    FileInfo getFileById(Long id);
    
    /**
     * 获取文件URL
     * @param id 文件ID
     * @return 文件访问URL
     */
    String getFileUrl(Long id);
    
    /**
     * 删除文件
     * @param id 文件ID
     */
    void deleteFile(Long id);
    
    /**
     * 批量删除文件
     * @param ids 文件ID列表
     */
    void deleteFiles(List<Long> ids);
    
    /**
     * 下载文件
     * @param id 文件ID
     * @return 文件字节数组
     */
    byte[] downloadFile(Long id);
    
    /**
     * 获取用户上传的文件列表
     * @param page 页码
     * @param size 每页数量
     * @param userId 用户ID
     * @return 文件分页列表
     */
    IPage<FileInfo> getUserFiles(Integer page, Integer size, Long userId);
    
    /**
     * 获取业务关联的文件列表
     * @param bizType 业务类型
     * @param bizId 业务ID
     * @return 文件列表
     */
    List<FileInfo> getBizFiles(String bizType, Long bizId);
    
    /**
     * 更新文件状态
     * @param id 文件ID
     * @param status 状态
     */
    void updateFileStatus(Long id, Integer status);
}
