package com.anti.fraud.modules.export.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 用户统计导出VO
 */
@Data
public class UserStatisticsExportVO {
    
    @ExcelProperty("用户名")
    private String username;
    
    @ExcelProperty("真实姓名")
    private String realName;
    
    @ExcelProperty("院系")
    private String department;
    
    @ExcelProperty("积分")
    private Integer points;
    
    @ExcelProperty("等级")
    private Integer level;
    
    @ExcelProperty("学习时长(分钟)")
    private Integer studyDuration;
    
    @ExcelProperty("完成测验数")
    private Integer testCount;
    
    @ExcelProperty("完成演练数")
    private Integer simulationCount;
    
    @ExcelProperty("注册时间")
    private String createTime;
}
