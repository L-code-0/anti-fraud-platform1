package com.anti.fraud.modules.warning.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 预警详情 VO
 */
@Data
@Schema(description = "预警详情")
public class WarningAlertVO {

    @Schema(description = "预警ID")
    private Long id;

    @Schema(description = "风险等级：1-高风险 2-中风险 3-低风险")
    private Integer riskLevel;

    @Schema(description = "风险等级名称")
    private String riskLevelName;

    @Schema(description = "预警类型：1-电信诈骗 2-网络诈骗 3-校园贷 4-兼职诈骗")
    private Integer warningType;

    @Schema(description = "预警类型名称")
    private String warningTypeName;

    @Schema(description = "预警内容")
    private String content;

    @Schema(description = "风险分数 0-100")
    private Integer riskScore;

    @Schema(description = "风险分析")
    private String analysis;

    @Schema(description = "建议措施")
    private String suggestion;

    @Schema(description = "相关用户")
    private String relatedUser;

    @Schema(description = "相关知识列表")
    private List<String> relatedKnowledge;

    @Schema(description = "处理状态：0-待处理 1-已处理 2-误报")
    private Integer status;

    @Schema(description = "处理结果")
    private String processResult;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "处理时间")
    private LocalDateTime processedAt;

    @Schema(description = "处理人")
    private String processorName;
}
