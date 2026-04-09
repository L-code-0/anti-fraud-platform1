package com.anti.fraud.modules.simulation.service.impl;

import com.anti.fraud.common.exception.BusinessException;
import com.anti.fraud.common.utils.SecurityUtils;
import com.anti.fraud.modules.simulation.dto.SimulationSubmitDTO;
import com.anti.fraud.modules.simulation.entity.SimulationRecord;
import com.anti.fraud.modules.simulation.entity.SimulationScene;
import com.anti.fraud.modules.simulation.mapper.SimulationRecordMapper;
import com.anti.fraud.modules.simulation.mapper.SimulationSceneMapper;
import com.anti.fraud.modules.simulation.service.SimulationService;
import com.anti.fraud.modules.simulation.vo.SceneDetailVO;
import com.anti.fraud.modules.simulation.vo.SceneVO;
import com.anti.fraud.modules.simulation.vo.DialogScript;
import com.anti.fraud.modules.simulation.vo.SimulationRecordVO;
import com.anti.fraud.modules.simulation.vo.UserSimulationStatsVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SimulationServiceImpl implements SimulationService {

    private final SimulationSceneMapper sceneMapper;
    private final SimulationRecordMapper recordMapper;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final Timer simulationTimer;
    private final Counter simulationCounter;

    @Override
    public List<SceneVO> getRecommendScenes() {
        Long userId = SecurityUtils.getCurrentUserId();
        String cacheKey = "simulation:recommend:" + (userId != null ? userId : "anonymous");
        
        // 尝试从缓存获取
        Object cachedObject = redisTemplate.opsForValue().get(cacheKey);
        if (cachedObject instanceof List) {
            @SuppressWarnings("unchecked")
            List<SceneVO> result = (List<SceneVO>) cachedObject;
            return result;
        }
        
        List<SimulationScene> scenes;

        if (userId != null) {
            // 获取用户已演练的场景ID
            List<Long> completedSceneIds = recordMapper.selectList(
                    new LambdaQueryWrapper<SimulationRecord>()
                            .eq(SimulationRecord::getUserId, userId)
                            .eq(SimulationRecord::getIsCompleted, 1)
            ).stream()
                    .map(SimulationRecord::getSceneId)
                    .collect(Collectors.toList());

            // 如果用户有演练记录，推荐未演练的场景
            if (!completedSceneIds.isEmpty()) {
                scenes = sceneMapper.selectList(
                        new LambdaQueryWrapper<SimulationScene>()
                                .eq(SimulationScene::getStatus, 1)
                                .notIn(SimulationScene::getId, completedSceneIds)
                                .orderByDesc(SimulationScene::getIsRecommend)
                                .orderByAsc(SimulationScene::getDifficulty)
                                .last("LIMIT 10")
                );

                // 如果未演练的场景不足10个，补充推荐场景
                if (scenes.size() < 10) {
                    List<SimulationScene> recommendScenes = sceneMapper.selectList(
                            new LambdaQueryWrapper<SimulationScene>()
                                    .eq(SimulationScene::getStatus, 1)
                                    .eq(SimulationScene::getIsRecommend, 1)
                                    .notIn(SimulationScene::getId, completedSceneIds)
                                    .last("LIMIT " + (10 - scenes.size()))
                    );
                    scenes.addAll(recommendScenes);
                }
            } else {
                // 用户没有演练记录，推荐默认场景
                scenes = sceneMapper.selectList(
                        new LambdaQueryWrapper<SimulationScene>()
                                .eq(SimulationScene::getStatus, 1)
                                .eq(SimulationScene::getIsRecommend, 1)
                                .orderByAsc(SimulationScene::getDifficulty)
                                .last("LIMIT 10")
                );
            }
        } else {
            // 未登录用户，推荐默认场景
            scenes = sceneMapper.selectList(
                    new LambdaQueryWrapper<SimulationScene>()
                            .eq(SimulationScene::getStatus, 1)
                            .eq(SimulationScene::getIsRecommend, 1)
                            .orderByAsc(SimulationScene::getDifficulty)
                            .last("LIMIT 10")
            );
        }

        List<SceneVO> result = scenes.stream()
                .map(this::convertToSceneVO)
                .collect(Collectors.toList());
        
        // 缓存结果，有效期30分钟
        redisTemplate.opsForValue().set(cacheKey, result, 30, TimeUnit.MINUTES);
        
        return result;
    }

    @Override
    public Page<SceneVO> getScenePage(Integer page, Integer size, String sceneType) {
        Page<SimulationScene> scenePage = new Page<>(page, size);

        LambdaQueryWrapper<SimulationScene> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SimulationScene::getStatus, 1);

        if (sceneType != null && !sceneType.isEmpty()) {
            wrapper.eq(SimulationScene::getSceneType, sceneType);
        }

        wrapper.orderByDesc(SimulationScene::getIsRecommend)
                .orderByAsc(SimulationScene::getDifficulty);

        sceneMapper.selectPage(scenePage, wrapper);

        // 手动转换避免类型不兼容
        Page<SceneVO> result = new Page<>(scenePage.getCurrent(), scenePage.getSize(), scenePage.getTotal());
        result.setRecords(scenePage.getRecords().stream().map(this::convertToSceneVO).collect(Collectors.toList()));
        return result;
    }

    @Override
    @Transactional
    public SceneDetailVO getSceneDetail(Long id) {
        String cacheKey = "simulation:scene:" + id;
        
        // 尝试从缓存获取
        SceneDetailVO cachedScene = (SceneDetailVO) redisTemplate.opsForValue().get(cacheKey);
        if (cachedScene != null) {
            // 更新播放次数（不影响缓存）
            SimulationScene scene = sceneMapper.selectById(id);
            if (scene != null) {
                scene.setPlayCount(scene.getPlayCount() + 1);
                sceneMapper.updateById(scene);
                // 更新返回对象中的播放次数
                cachedScene.setPlayCount(scene.getPlayCount());
            }
            return cachedScene;
        }
        
        SimulationScene scene = sceneMapper.selectById(id);
        if (scene == null) {
            throw new BusinessException("演练场景不存在");
        }

        scene.setPlayCount(scene.getPlayCount() + 1);
        sceneMapper.updateById(scene);

        SceneDetailVO vo = new SceneDetailVO();
        vo.setId(scene.getId());
        vo.setSceneName(scene.getSceneName());
        vo.setSceneType(scene.getSceneType());
        vo.setDifficulty(scene.getDifficulty());
        vo.setBackground(scene.getBackground());
        vo.setCoverImage(scene.getCoverImage());
        vo.setPlayCount(scene.getPlayCount());
        vo.setAvgScore(scene.getAvgScore());

        try {
            List<DialogScript> script = objectMapper.readValue(
                    scene.getScriptConfig(),
                    new TypeReference<List<DialogScript>>() {}
            );
            vo.setScript(script);
        } catch (Exception e) {
            vo.setScript(new ArrayList<>());
        }

        // 缓存结果，有效期1小时
        redisTemplate.opsForValue().set(cacheKey, vo, 1, TimeUnit.HOURS);
        
        return vo;
    }

    @Override
    @Transactional
    public Long startSimulation(Long sceneId) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("请先登录");
        }

        SimulationScene scene = sceneMapper.selectById(sceneId);
        if (scene == null) {
            throw new BusinessException("演练场景不存在");
        }

        SimulationRecord record = new SimulationRecord();
        record.setUserId(userId);
        record.setSceneId(sceneId);
        record.setIsCompleted(0);
        record.setStartTime(LocalDateTime.now());

        recordMapper.insert(record);

        return record.getId();
    }

    @Override
    @Transactional
    public Integer submitSimulation(SimulationSubmitDTO submitDTO) {
        return simulationTimer.record(() -> {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                throw new BusinessException("请先登录");
            }

            SimulationRecord record = recordMapper.selectById(submitDTO.getRecordId());
            if (record == null || !record.getUserId().equals(userId)) {
                throw new BusinessException("演练记录不存在");
            }

            if (record.getIsCompleted() == 1) {
                throw new BusinessException("该演练已提交");
            }

            SimulationScene scene = sceneMapper.selectById(record.getSceneId());

            int correctCount = 0;
            int totalQuestions = 0;
            int criticalCorrectCount = 0;
            int criticalTotalCount = 0;
            BigDecimal baseScore = BigDecimal.ZERO;
            BigDecimal timeBonus = BigDecimal.ZERO;
            BigDecimal difficultyBonus = BigDecimal.ZERO;
            BigDecimal completionBonus = BigDecimal.ZERO;

            try {
                List<DialogScript> script = objectMapper.readValue(
                        scene.getScriptConfig(),
                        new TypeReference<List<DialogScript>>() {}
                );

                totalQuestions = Math.min(script.size(), submitDTO.getChoices().size());

                for (int i = 0; i < totalQuestions; i++) {
                    DialogScript dialog = script.get(i);
                    if (dialog.getCorrectIndex() != null) {
                        if (dialog.getCorrectIndex().equals(submitDTO.getChoices().get(i))) {
                            correctCount++;
                            // 检查是否为关键决策点
                            if (dialog.isCritical()) {
                                criticalCorrectCount++;
                            }
                        }
                        // 统计关键决策点总数
                        if (dialog.isCritical()) {
                            criticalTotalCount++;
                        }
                    }
                }

                // 基础分数：正确答案比例
                if (totalQuestions > 0) {
                    baseScore = BigDecimal.valueOf(correctCount)
                            .divide(BigDecimal.valueOf(totalQuestions), 2, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(70)); // 基础分数占70%
                }

                // 时间奖励：根据完成时间给予奖励（最多10分）
                int expectedTime = totalQuestions * 30; // 假设每题30秒
                if (submitDTO.getDuration() < expectedTime) {
                    // 提前完成给予奖励
                    double timeRatio = 1.0 - (double) submitDTO.getDuration() / expectedTime;
                    timeBonus = BigDecimal.valueOf(timeRatio * 10).min(BigDecimal.TEN);
                } else if (submitDTO.getDuration() > expectedTime * 1.5) {
                    // 超时严重扣分
                    double timeRatio = (double) (submitDTO.getDuration() - expectedTime * 1.5) / expectedTime;
                    timeBonus = BigDecimal.valueOf(-timeRatio * 5).max(BigDecimal.valueOf(-5));
                }

                // 难度奖励：根据场景难度给予奖励（最多10分）
                int difficulty = scene.getDifficulty();
                switch (difficulty) {
                    case 3: // 困难
                        difficultyBonus = BigDecimal.valueOf(10);
                        break;
                    case 2: // 中等
                        difficultyBonus = BigDecimal.valueOf(5);
                        break;
                    case 1: // 简单
                        difficultyBonus = BigDecimal.valueOf(2);
                        break;
                    default:
                        difficultyBonus = BigDecimal.ZERO;
                }

                // 关键决策点奖励：关键决策点正确率（最多10分）
                if (criticalTotalCount > 0) {
                    BigDecimal criticalRatio = BigDecimal.valueOf(criticalCorrectCount)
                            .divide(BigDecimal.valueOf(criticalTotalCount), 2, RoundingMode.HALF_UP);
                    completionBonus = criticalRatio.multiply(BigDecimal.TEN);
                }

                // 完成度奖励：如果完成所有问题，额外奖励（最多5分）
                if (totalQuestions == script.size()) {
                    completionBonus = completionBonus.add(BigDecimal.valueOf(5));
                }

            } catch (Exception e) {
                // 忽略解析错误
            }

            // 计算总分
            BigDecimal totalScore = baseScore.add(timeBonus).add(difficultyBonus).add(completionBonus);
            // 确保分数在0-100之间
            totalScore = totalScore.max(BigDecimal.ZERO).min(BigDecimal.valueOf(100));

            record.setScore(totalScore);
            record.setDuration(submitDTO.getDuration());
            record.setIsCompleted(1);
            record.setEndTime(LocalDateTime.now());

            recordMapper.updateById(record);

            // 更新场景平均分
            updateSceneAvgScore(scene.getId());

            // 清除相关缓存
            if (userId != null) {
                // 清除用户推荐场景缓存
                redisTemplate.delete("simulation:recommend:" + userId);
                // 清除用户记录缓存
                redisTemplate.delete("simulation:records:" + userId);
                redisTemplate.delete("simulation:stats:" + userId);
            }
            // 清除场景详情缓存
            redisTemplate.delete("simulation:scene:" + scene.getId());

            // 增加执行次数计数
            simulationCounter.increment();

            return totalScore.intValue();
        });
    }

    @Override
    public List<SceneVO> getMySimulationRecords() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("请先登录");
        }

        String cacheKey = "simulation:records:" + userId;
        
        // 尝试从缓存获取
        Object cachedObject = redisTemplate.opsForValue().get(cacheKey);
        if (cachedObject instanceof List) {
            @SuppressWarnings("unchecked")
            List<SceneVO> result = (List<SceneVO>) cachedObject;
            return result;
        }

        List<SimulationRecord> records = recordMapper.selectList(
                new LambdaQueryWrapper<SimulationRecord>()
                        .eq(SimulationRecord::getUserId, userId)
                        .eq(SimulationRecord::getIsCompleted, 1)
                        .orderByDesc(SimulationRecord::getCreateTime)
                        .last("LIMIT 20")
        );

        List<SceneVO> result = records.stream()
                .map(record -> {
                    SimulationScene scene = sceneMapper.selectById(record.getSceneId());
                    SceneVO vo = new SceneVO();
                    vo.setId(scene.getId());
                    vo.setSceneName(scene.getSceneName());
                    vo.setSceneType(scene.getSceneType());
                    vo.setDifficulty(scene.getDifficulty());
                    vo.setCoverImage(scene.getCoverImage());
                    return vo;
                })
                .collect(Collectors.toList());
        
        // 缓存结果，有效期15分钟
        redisTemplate.opsForValue().set(cacheKey, result, 15, TimeUnit.MINUTES);
        
        return result;
    }

    @Override
    public List<SimulationRecordVO> getMySimulationRecordDetails() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("请先登录");
        }

        List<SimulationRecord> records = recordMapper.selectList(
                new LambdaQueryWrapper<SimulationRecord>()
                        .eq(SimulationRecord::getUserId, userId)
                        .eq(SimulationRecord::getIsCompleted, 1)
                        .orderByDesc(SimulationRecord::getCreateTime)
                        .last("LIMIT 50")
        );

        return records.stream()
                .map(record -> {
                    SimulationScene scene = sceneMapper.selectById(record.getSceneId());
                    SimulationRecordVO vo = new SimulationRecordVO();
                    vo.setId(record.getId());
                    vo.setSceneId(scene.getId());
                    vo.setSceneName(scene.getSceneName());
                    vo.setSceneType(scene.getSceneType());
                    vo.setDifficulty(scene.getDifficulty());
                    vo.setScore(record.getScore());
                    vo.setDuration(record.getDuration());
                    vo.setStartTime(record.getStartTime());
                    vo.setEndTime(record.getEndTime());
                    vo.setIsPass(record.getScore().compareTo(BigDecimal.valueOf(60)) >= 0);
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public UserSimulationStatsVO getUserSimulationStats() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("请先登录");
        }

        String cacheKey = "simulation:stats:" + userId;
        
        // 尝试从缓存获取
        UserSimulationStatsVO cachedStats = (UserSimulationStatsVO) redisTemplate.opsForValue().get(cacheKey);
        if (cachedStats != null) {
            return cachedStats;
        }

        List<SimulationRecord> records = recordMapper.selectList(
                new LambdaQueryWrapper<SimulationRecord>()
                        .eq(SimulationRecord::getUserId, userId)
                        .eq(SimulationRecord::getIsCompleted, 1)
        );

        UserSimulationStatsVO stats = new UserSimulationStatsVO();
        if (records.isEmpty()) {
            stats.setTotalCount(0);
            stats.setPassCount(0);
            stats.setAvgScore(BigDecimal.ZERO);
            stats.setMaxScore(BigDecimal.ZERO);
            stats.setMinScore(BigDecimal.ZERO);
            stats.setTotalDuration(0);
            stats.setPassRate(BigDecimal.ZERO);
            
            // 缓存空结果，有效期5分钟
            redisTemplate.opsForValue().set(cacheKey, stats, 5, TimeUnit.MINUTES);
            return stats;
        }

        int totalCount = records.size();
        int passCount = (int) records.stream()
                .filter(record -> record.getScore().compareTo(BigDecimal.valueOf(60)) >= 0)
                .count();

        BigDecimal totalScore = records.stream()
                .map(SimulationRecord::getScore)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal avgScore = totalScore.divide(BigDecimal.valueOf(totalCount), 2, RoundingMode.HALF_UP);

        BigDecimal maxScore = records.stream()
                .map(SimulationRecord::getScore)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal minScore = records.stream()
                .map(SimulationRecord::getScore)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        int totalDuration = records.stream()
                .mapToInt(SimulationRecord::getDuration)
                .sum() / 60; // 转换为分钟

        BigDecimal passRate = BigDecimal.valueOf(passCount)
                .divide(BigDecimal.valueOf(totalCount), 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        stats.setTotalCount(totalCount);
        stats.setPassCount(passCount);
        stats.setAvgScore(avgScore);
        stats.setMaxScore(maxScore);
        stats.setMinScore(minScore);
        stats.setTotalDuration(totalDuration);
        stats.setPassRate(passRate);

        // 缓存结果，有效期15分钟
        redisTemplate.opsForValue().set(cacheKey, stats, 15, TimeUnit.MINUTES);
        
        return stats;
    }

    private void updateSceneAvgScore(Long sceneId) {
        List<SimulationRecord> records = recordMapper.selectList(
                new LambdaQueryWrapper<SimulationRecord>()
                        .eq(SimulationRecord::getSceneId, sceneId)
                        .eq(SimulationRecord::getIsCompleted, 1)
        );

        if (!records.isEmpty()) {
            BigDecimal avgScore = records.stream()
                    .map(SimulationRecord::getScore)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(records.size()), 2, RoundingMode.HALF_UP);

            SimulationScene scene = new SimulationScene();
            scene.setId(sceneId);
            scene.setAvgScore(avgScore);
            sceneMapper.updateById(scene);
        }
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
