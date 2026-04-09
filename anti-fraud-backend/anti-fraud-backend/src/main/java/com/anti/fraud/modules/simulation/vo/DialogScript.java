package com.anti.fraud.modules.simulation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "对话脚本")
public class DialogScript {

    @Schema(description = "角色：user/system")
    private String role;

    @Schema(description = "对话内容")
    private String content;

    @Schema(description = "选项列表")
    private String[] options;

    @Schema(description = "正确选项索引")
    private Integer correctIndex;

    @Schema(description = "是否为关键决策点")
    private boolean critical;

    // 为了兼容性，添加默认值
    public boolean isCritical() {
        return critical;
    }
}
