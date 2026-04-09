package com.anti.fraud.modules.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "趋势数据VO")
public class TrendDataVO {

    @Schema(description = "日期/名称")
    private String label;

    @Schema(description = "数值")
    private Long value;

    public TrendDataVO() {}

    public TrendDataVO(String label, Long value) {
        this.label = label;
        this.value = value;
    }
}