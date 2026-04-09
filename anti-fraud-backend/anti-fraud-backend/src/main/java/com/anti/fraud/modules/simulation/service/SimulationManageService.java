package com.anti.fraud.modules.simulation.service;

import com.anti.fraud.modules.simulation.dto.SceneCreateDTO;
import com.anti.fraud.modules.simulation.vo.SceneVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface SimulationManageService {

    Page<SceneVO> getScenePage(Integer page, Integer size, String sceneType, Integer status);

    void createScene(SceneCreateDTO createDTO);

    void updateScene(Long id, SceneCreateDTO createDTO);

    void deleteScene(Long id);

    void updateSceneStatus(Long id, Integer status);

    void setRecommend(Long id, Boolean isRecommend);
}