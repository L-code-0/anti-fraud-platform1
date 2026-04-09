package com.anti.fraud.modules.simulation.dto;

import com.anti.fraud.modules.simulation.vo.DialogScript;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "场景创建DTO")
public class SceneCreateDTO {

    @Schema(description = "场景名称")
    private String sceneName;

    @Schema(description = "场景类型")
    private String sceneType;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "背景描述")
    private String background;

    @Schema(description = "对话脚本")
    private List<DialogScript> script;

    @Schema(description = "难度等级")
    private Integer difficulty;

    @Schema(description = "封面图")
    private String coverImage;

    @Schema(description = "是否推荐")
    private Boolean isRecommend;
}
