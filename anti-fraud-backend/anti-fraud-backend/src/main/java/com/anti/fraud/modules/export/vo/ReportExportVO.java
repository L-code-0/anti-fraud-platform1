package com.anti.fraud.modules.export.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 举报记录导出VO
 */
@Data
public class ReportExportVO {
    
    @ExcelProperty("举报类型")
    private String reportType;
    
    @ExcelProperty("诈骗类型")
    private String fraudType;
    
    @ExcelProperty("举报人")
    private String reporterName;
    
    @ExcelProperty("被举报内容")
    private String content;
    
    @ExcelProperty("举报时间")
    private String createTime;
    
    @ExcelProperty("处理状态")
    private String status;
    
    @ExcelProperty("处理人")
    private String handlerName;
    
    @ExcelProperty("处理时间")
    private String handleTime;
    
    @ExcelProperty("处理结果")
    private String handleResult;
}
