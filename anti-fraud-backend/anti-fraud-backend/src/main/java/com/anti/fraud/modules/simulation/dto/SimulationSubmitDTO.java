package com.anti.fraud.modules.simulation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "演练提交DTO")
public class SimulationSubmitDTO {

    @Schema(description = "场景ID")
    private Long sceneId;

    @Schema(description = "记录ID")
    private Long recordId;

    @Schema(description = "选项列表")
    private List<Integer> choices;

    @Schema(description = "演练时长(秒)")
    private Integer duration;
}