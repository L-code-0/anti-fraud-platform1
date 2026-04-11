package com.anti.fraud.modules.file.dto;

import lombok.Data;



/**
 * 文件上传DTO
 */
@Data
public class FileUploadDTO {
    
    /**
     * 文件分类（image/video/document/other）
     */
    private String category;
    
    /**
     * 关联业务类型
     */
    private String bizType;
    
    /**
     * 关联业务ID
     */
    private Long bizId;
}
