package com.anti.fraud.modules.simulation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "演练场景VO")
public class SceneVO {

    @Schema(description = "场景ID")
    private Long id;

    @Schema(description = "场景名称")
    private String sceneName;

    @Schema(description = "场景类型")
    private String sceneType;

    @Schema(description = "难度等级")
    private Integer difficulty;

    @Schema(description = "封面图")
    private String coverImage;

    @Schema(description = "演练次数")
    private Integer playCount;

    @Schema(description = "平均得分")
    private BigDecimal avgScore;

    @Schema(description = "是否推荐")
    private Boolean isRecommend;
}
