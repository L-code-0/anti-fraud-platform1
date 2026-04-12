package com.anti.fraud.modules.importdata.service.impl;

import com.anti.fraud.modules.importdata.service.ImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据导入服务实现
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ImportServiceImpl implements ImportService {
    
    @Override
    public Map<String, Object> importUsers(MultipartFile file) {
        return importData(file, "用户数据");
    }
    
    @Override
    public Map<String, Object> importKnowledge(MultipartFile file) {
        return importData(file, "知识内容");
    }
    
    @Override
    public Map<String, Object> importQuestions(MultipartFile file) {
        return importData(file, "题库数据");
    }
    
    @Override
    public Map<String, Object> importReports(MultipartFile file) {
        return importData(file, "举报记录");
    }
    
    @Override
    public Map<String, Object> importActivities(MultipartFile file) {
        return importData(file, "活动数据");
    }
    
    /**
     * 通用导入方法
     * @param file 上传的文件
     * @param type 数据类型
     * @return 导入结果
     */
    private Map<String, Object> importData(MultipartFile file, String type) {
        Map<String, Object> result = new HashMap<>();
        int successCount = 0;
        int errorCount = 0;
        
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            
            // 跳过表头，从第二行开始读取数据
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                try {
                    // 这里根据具体的数据类型进行处理
                    // 实际项目中需要根据Excel的列结构进行数据解析和存储
                    // 这里只是模拟导入过程
                    successCount++;
                } catch (Exception e) {
                    log.error("导入{}第{}行失败: {}", type, i + 1, e.getMessage());
                    errorCount++;
                }
            }
            
            workbook.close();
        } catch (Exception e) {
            log.error("导入{}失败: {}", type, e.getMessage());
            result.put("success", false);
            result.put("message", "文件解析失败: " + e.getMessage());
            return result;
        }
        
        result.put("success", true);
        result.put("message", "导入完成");
        result.put("totalCount", successCount + errorCount);
        result.put("successCount", successCount);
        result.put("errorCount", errorCount);
        
        return result;
    }
}
