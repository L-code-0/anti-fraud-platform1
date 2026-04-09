package com.anti.fraud.modules.simulation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "场景详情VO")
public class SceneDetailVO {

    @Schema(description = "场景ID")
    private Long id;

    @Schema(description = "场景名称")
    private String sceneName;

    @Schema(description = "场景类型")
    private String sceneType;

    @Schema(description = "难度等级")
    private Integer difficulty;

    @Schema(description = "背景描述")
    private String background;

    @Schema(description = "封面图")
    private String coverImage;

    @Schema(description = "对话脚本")
    private List<DialogScript> script;

    @Schema(description = "演练次数")
    private Integer playCount;

    @Schema(description = "平均得分")
    private BigDecimal avgScore;
}
