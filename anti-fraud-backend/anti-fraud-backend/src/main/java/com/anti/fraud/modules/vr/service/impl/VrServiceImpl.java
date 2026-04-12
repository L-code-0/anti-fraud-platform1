package com.anti.fraud.modules.vr.service.impl;

import com.anti.fraud.modules.vr.entity.VrScene;
import com.anti.fraud.modules.vr.entity.VrDrillRecord;
import com.anti.fraud.modules.vr.mapper.VrSceneMapper;
import com.anti.fraud.modules.vr.mapper.VrDrillRecordMapper;
import com.anti.fraud.modules.vr.service.VrService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class VrServiceImpl implements VrService {

    private final VrSceneMapper vrSceneMapper;
    private final VrDrillRecordMapper vrDrillRecordMapper;

    @Override
    public boolean createVrScene(VrScene scene) {
        try {
            scene.setStatus(1); // 1: 可用
            vrSceneMapper.insert(scene);
            log.info("创建VR场景成功: id={}, name={}", scene.getId(), scene.getName());
            return true;
        } catch (Exception e) {
            log.error("创建VR场景失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean updateVrScene(VrScene scene) {
        try {
            vrSceneMapper.updateById(scene);
            log.info("更新VR场景成功: id={}", scene.getId());
            return true;
        } catch (Exception e) {
            log.error("更新VR场景失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean deleteVrScene(Long id) {
        try {
            vrSceneMapper.deleteById(id);
            log.info("删除VR场景成功: id={}", id);
            return true;
        } catch (Exception e) {
            log.error("删除VR场景失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public VrScene getVrSceneById(Long id) {
        try {
            return vrSceneMapper.selectById(id);
        } catch (Exception e) {
            log.error("获取VR场景详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<VrScene> getVrSceneList(String sceneType, Integer difficulty, int page, int size) {
        try {
            LambdaQueryWrapper<VrScene> queryWrapper = new LambdaQueryWrapper<>();
            if (sceneType != null) {
                queryWrapper.eq(VrScene::getSceneType, sceneType);
            }
            if (difficulty != null) {
                queryWrapper.eq(VrScene::getDifficulty, difficulty);
            }
            queryWrapper.eq(VrScene::getStatus, 1) // 只查询可用的场景
                    .orderByDesc(VrScene::getCreateTime);

            IPage<VrScene> pageInfo = new Page<>(page, size);
            vrSceneMapper.selectPage(pageInfo, queryWrapper);

            return pageInfo.getRecords();
        } catch (Exception e) {
            log.error("获取VR场景列表失败: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public Map<String, Object> startVrDrill(Long sceneId, Long userId, String userName, String deviceInfo) {
        try {
            // 检查场景是否存在
            VrScene scene = vrSceneMapper.selectById(sceneId);
            if (scene == null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "VR场景不存在");
                return result;
            }
            
            // 检查场景状态
            if (scene.getStatus() != 1) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "VR场景不可用");
                return result;
            }
            
            // 模拟开始VR演练
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "开始VR演练成功");
            result.put("sceneId", sceneId);
            result.put("sceneName", scene.getName());
            result.put("sceneType", scene.getSceneType());
            result.put("modelUrl", scene.getModelUrl());
            result.put("difficulty", scene.getDifficulty());
            result.put("duration", scene.getDuration());
            result.put("startTime", LocalDateTime.now());
            
            log.info("开始VR演练成功: sceneId={}, userId={}", sceneId, userId);
            return result;
        } catch (Exception e) {
            log.error("开始VR演练失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "开始VR演练失败");
            return result;
        }
    }

    @Override
    public Map<String, Object> submitVrDrillResult(VrDrillRecord record) {
        try {
            // 保存演练记录
            record.setStatus("completed");
            vrDrillRecordMapper.insert(record);
            
            // 模拟提交结果
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "提交VR演练结果成功");
            result.put("recordId", record.getId());
            result.put("score", record.getScore());
            result.put("feedback", record.getFeedback());
            result.put("submitTime", LocalDateTime.now());
            
            log.info("提交VR演练结果成功: recordId={}, userId={}, sceneId={}", record.getId(), record.getUserId(), record.getSceneId());
            return result;
        } catch (Exception e) {
            log.error("提交VR演练结果失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "提交VR演练结果失败");
            return result;
        }
    }

    @Override
    public List<VrDrillRecord> getUserVrDrillHistory(Long userId, int page, int size) {
        try {
            LambdaQueryWrapper<VrDrillRecord> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(VrDrillRecord::getUserId, userId)
                    .orderByDesc(VrDrillRecord::getCreateTime);

            IPage<VrDrillRecord> pageInfo = new Page<>(page, size);
            vrDrillRecordMapper.selectPage(pageInfo, queryWrapper);

            return pageInfo.getRecords();
        } catch (Exception e) {
            log.error("获取用户VR演练历史失败: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public Map<String, Object> getVrSceneStats(Long sceneId) {
        try {
            VrScene scene = vrSceneMapper.selectById(sceneId);
            if (scene == null) {
                return new HashMap<>();
            }
            
            // 模拟统计数据
            Map<String, Object> stats = new HashMap<>();
            stats.put("sceneName", scene.getName());
            stats.put("totalDrills", 100);
            stats.put("averageScore", 85.5);
            stats.put("highestScore", 100);
            stats.put("lowestScore", 60);
            stats.put("completionRate", 95.0);
            stats.put("userCount", 50);
            
            log.info("获取VR场景统计信息成功: sceneId={}", sceneId);
            return stats;
        } catch (Exception e) {
            log.error("获取VR场景统计信息失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Object> getUserVrDrillStats(Long userId) {
        try {
            // 模拟统计数据
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalDrills", 10);
            stats.put("averageScore", 82.5);
            stats.put("highestScore", 95);
            stats.put("lowestScore", 70);
            stats.put("totalDuration", 600); // 总时长（分钟）
            stats.put("completedScenes", 5);
            stats.put("sceneTypes", Map.of(
                    "电信诈骗", 3,
                    "网络诈骗", 2,
                    "金融诈骗", 2,
                    "虚假信息", 3
            ));
            
            log.info("获取用户VR演练统计信息成功: userId={}", userId);
            return stats;
        } catch (Exception e) {
            log.error("获取用户VR演练统计信息失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    public List<VrScene> searchVrScenes(String keyword, int page, int size) {
        try {
            LambdaQueryWrapper<VrScene> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.like(VrScene::getName, keyword)
                    .or()
                    .like(VrScene::getDescription, keyword)
                    .eq(VrScene::getStatus, 1)
                    .orderByDesc(VrScene::getCreateTime);

            IPage<VrScene> pageInfo = new Page<>(page, size);
            vrSceneMapper.selectPage(pageInfo, queryWrapper);

            return pageInfo.getRecords();
        } catch (Exception e) {
            log.error("搜索VR场景失败: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}
