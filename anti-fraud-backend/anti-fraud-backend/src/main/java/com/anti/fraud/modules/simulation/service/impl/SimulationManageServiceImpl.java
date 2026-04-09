package com.anti.fraud.modules.simulation.service.impl;

import com.anti.fraud.common.exception.BusinessException;
import com.anti.fraud.modules.simulation.dto.SceneCreateDTO;
import com.anti.fraud.modules.simulation.entity.SimulationScene;
import com.anti.fraud.modules.simulation.mapper.SimulationSceneMapper;
import com.anti.fraud.modules.simulation.service.SimulationManageService;
import com.anti.fraud.modules.simulation.vo.SceneVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SimulationManageServiceImpl implements SimulationManageService {

    private final SimulationSceneMapper sceneMapper;
    private final ObjectMapper objectMapper;

    @Override
    public Page<SceneVO> getScenePage(Integer page, Integer size, String sceneType, Integer status) {
        Page<SimulationScene> scenePage = new Page<>(page, size);

        LambdaQueryWrapper<SimulationScene> wrapper = new LambdaQueryWrapper<>();

        if (sceneType != null && !sceneType.isEmpty()) {
            wrapper.eq(SimulationScene::getSceneType, sceneType);
        }

        if (status != null) {
            wrapper.eq(SimulationScene::getStatus, status);
        }

        wrapper.orderByDesc(SimulationScene::getCreateTime);

        sceneMapper.selectPage(scenePage, wrapper);

        // 手动转换避免类型不兼容
        Page<SceneVO> result = new Page<>(scenePage.getCurrent(), scenePage.getSize(), scenePage.getTotal());
        result.setRecords(scenePage.getRecords().stream().map(this::convertToSceneVO).collect(Collectors.toList()));
        return result;
    }

    @Override
    @Transactional
    public void createScene(SceneCreateDTO createDTO) {
        SimulationScene scene = new SimulationScene();
        scene.setSceneName(createDTO.getSceneName());
        scene.setSceneType(createDTO.getSceneType());
        scene.setCategoryId(createDTO.getCategoryId());
        scene.setBackground(createDTO.getBackground());
        scene.setDifficulty(createDTO.getDifficulty());
        scene.setCoverImage(createDTO.getCoverImage());
        scene.setPlayCount(0);
        scene.setAvgScore(BigDecimal.ZERO);
        scene.setIsRecommend(createDTO.getIsRecommend() != null && createDTO.getIsRecommend() ? 1 : 0);
        scene.setStatus(1);

        if (createDTO.getScript() != null) {
            try {
                scene.setScriptConfig(objectMapper.writeValueAsString(createDTO.getScript()));
            } catch (JsonProcessingException e) {
                scene.setScriptConfig("[]");
            }
        }

        sceneMapper.insert(scene);
    }

    @Override
    @Transactional
    public void updateScene(Long id, SceneCreateDTO createDTO) {
        SimulationScene scene = sceneMapper.selectById(id);
        if (scene == null) {
            throw new BusinessException("演练场景不存在");
        }

        scene.setSceneName(createDTO.getSceneName());
        scene.setSceneType(createDTO.getSceneType());
        scene.setCategoryId(createDTO.getCategoryId());
        scene.setBackground(createDTO.getBackground());
        scene.setDifficulty(createDTO.getDifficulty());
        scene.setCoverImage(createDTO.getCoverImage());

        if (createDTO.getScript() != null) {
            try {
                scene.setScriptConfig(objectMapper.writeValueAsString(createDTO.getScript()));
            } catch (JsonProcessingException e) {
                scene.setScriptConfig("[]");
            }
        }

        sceneMapper.updateById(scene);
    }

    @Override
    @Transactional
    public void deleteScene(Long id) {
        sceneMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void updateSceneStatus(Long id, Integer status) {
        SimulationScene scene = sceneMapper.selectById(id);
        if (scene == null) {
            throw new BusinessException("演练场景不存在");
        }

        scene.setStatus(status);
        sceneMapper.updateById(scene);
    }

    @Override
    @Transactional
    public void setRecommend(Long id, Boolean isRecommend) {
        SimulationScene scene = sceneMapper.selectById(id);
        if (scene == null) {
            throw new BusinessException("演练场景不存在");
        }

        scene.setIsRecommend(isRecommend ? 1 : 0);
        sceneMapper.updateById(scene);
    }

    private SceneVO convertToSceneVO(SimulationScene scene) {
        SceneVO vo = new SceneVO();
        vo.setId(scene.getId());
        vo.setSceneName(scene.getSceneName());
        vo.setSceneType(scene.getSceneType());
        vo.setDifficulty(scene.getDifficulty());
        vo.setCoverImage(scene.getCoverImage());
        vo.setPlayCount(scene.getPlayCount());
        vo.setAvgScore(scene.getAvgScore());
        vo.setIsRecommend(scene.getIsRecommend() == 1);
        return vo;
    }
}