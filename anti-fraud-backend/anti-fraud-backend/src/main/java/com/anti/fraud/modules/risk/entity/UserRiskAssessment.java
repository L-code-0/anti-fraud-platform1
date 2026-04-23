package com.anti.fraud.modules.risk.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户风险评估实体
 */
@Data
@TableName("user_risk_assessment")
public class UserRiskAssessment {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    private String username;
    private Integer riskScore; // 风险评分 0-100
    private String riskLevel; // 风险等级：低、中、高、极高
    private String riskFactors; // 风险因素
    private String assessmentResult; // 评估结果
    private String recommendations; // 建议措施
    private Integer status; // 状态：1-正常，2-异常
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
