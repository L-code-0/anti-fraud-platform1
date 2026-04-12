package com.anti.fraud.modules.importdata.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 数据导入服务
 */
public interface ImportService {
    
    /**
     * 导入用户数据
     * @param file 上传的文件
     * @return 导入结果
     */
    Map<String, Object> importUsers(MultipartFile file);
    
    /**
     * 导入知识内容
     * @param file 上传的文件
     * @return 导入结果
     */
    Map<String, Object> importKnowledge(MultipartFile file);
    
    /**
     * 导入题库数据
     * @param file 上传的文件
     * @return 导入结果
     */
    Map<String, Object> importQuestions(MultipartFile file);
    
    /**
     * 导入举报记录
     * @param file 上传的文件
     * @return 导入结果
     */
    Map<String, Object> importReports(MultipartFile file);
    
    /**
     * 导入活动数据
     * @param file 上传的文件
     * @return 导入结果
     */
    Map<String, Object> importActivities(MultipartFile file);
}
