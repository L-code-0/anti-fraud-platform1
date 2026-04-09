package com.anti.fraud.modules.warning.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 预警查询 DTO
 */
@Data
@Schema(description = "预警查询条件")
public class WarningQueryDTO {

    @Schema(description = "页码", example = "1")
    private Integer page = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer size = 10;

    @Schema(description = "搜索关键词")
    private String keyword;

    @Schema(description = "风险等级：1-高风险 2-中风险 3-低风险，0-全部")
    private Integer riskLevel = 0;

    @Schema(description = "预警类型：1-电信诈骗 2-网络诈骗 3-校园贷 4-兼职诈骗，0-全部")
    private Integer warningType = 0;

    @Schema(description = "处理状态：0-待处理 1-已处理 2-误报")
    private Integer status;
}
