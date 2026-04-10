package com.anti.fraud.modules.export.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 测验成绩导出VO
 */
@Data
public class TestScoreExportVO {
    
    @ExcelProperty("用户名")
    private String username;
    
    @ExcelProperty("真实姓名")
    private String realName;
    
    @ExcelProperty("试卷名称")
    private String paperName;
    
    @ExcelProperty("得分")
    private Integer score;
    
    @ExcelProperty("总分")
    private Integer totalScore;
    
    @ExcelProperty("正确率")
    private String correctRate;
    
    @ExcelProperty("用时(分钟)")
    private Integer duration;
    
    @ExcelProperty("提交时间")
    private String submitTime;
    
    @ExcelProperty("考试结果")
    private String result;
}
