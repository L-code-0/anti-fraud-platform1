package com.anti.fraud.modules.simulation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "演练记录VO")
public class SimulationRecordVO {

    @Schema(description = "记录ID")
    private Long id;

    @Schema(description = "场景ID")
    private Long sceneId;

    @Schema(description = "场景名称")
    private String sceneName;

    @Schema(description = "场景类型")
    private String sceneType;

    @Schema(description = "难度等级")
    private Integer difficulty;

    @Schema(description = "得分")
    private BigDecimal score;

    @Schema(description = "演练时长(秒)")
    private Integer duration;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "是否通过")
    private Boolean isPass;
}
