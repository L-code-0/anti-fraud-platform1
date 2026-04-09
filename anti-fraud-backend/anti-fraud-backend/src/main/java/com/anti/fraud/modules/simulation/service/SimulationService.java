package com.anti.fraud.modules.simulation.service;

import com.anti.fraud.modules.simulation.dto.SimulationSubmitDTO;
import com.anti.fraud.modules.simulation.vo.SceneDetailVO;
import com.anti.fraud.modules.simulation.vo.SceneVO;
import com.anti.fraud.modules.simulation.vo.SimulationRecordVO;
import com.anti.fraud.modules.simulation.vo.UserSimulationStatsVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface SimulationService {

    List<SceneVO> getRecommendScenes();

    Page<SceneVO> getScenePage(Integer page, Integer size, String sceneType);

    SceneDetailVO getSceneDetail(Long id);

    Long startSimulation(Long sceneId);

    Integer submitSimulation(SimulationSubmitDTO submitDTO);

    List<SceneVO> getMySimulationRecords();

    List<SimulationRecordVO> getMySimulationRecordDetails();

    UserSimulationStatsVO getUserSimulationStats();
}
