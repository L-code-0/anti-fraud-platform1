package com.anti.fraud.modules.vr.service.impl;

import com.anti.fraud.modules.vr.entity.VRSimulation;
import com.anti.fraud.modules.vr.entity.VRSimulationRecord;
import com.anti.fraud.modules.vr.mapper.VRSimulationMapper;
import com.anti.fraud.modules.vr.mapper.VRSimulationRecordMapper;
import com.anti.fraud.modules.vr.service.VRSimulationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * VR演练服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class VRSimulationServiceImpl extends ServiceImpl<VRSimulationMapper, VRSimulation> implements VRSimulationService {

    private final VRSimulationMapper vrSimulationMapper;
    private final VRSimulationRecordMapper vrSimulationRecordMapper;

    @Override
    @Transactional
    public boolean createVRSimulation(VRSimulation vrSimulation) {
        try {
            vrSimulation.setStatus(1);
            vrSimulation.setViewCount(0);
            vrSimulation.setParticipantCount(0);
            vrSimulation.setAverageScore(0.0);
            vrSimulation.setDeleted(0);
            vrSimulation.setCreateTime(LocalDateTime.now());
            vrSimulation.setUpdateTime(LocalDateTime.now());
            return save(vrSimulation);
        } catch (Exception e) {
            log.error("创建VR演练失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateVRSimulation(VRSimulation vrSimulation) {
        try {
            vrSimulation.setUpdateTime(LocalDateTime.now());
            return updateById(vrSimulation);
        } catch (Exception e) {
            log.error("更新VR演练失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteVRSimulation(Long id) {
        try {
            VRSimulation vrSimulation = getById(id);
            if (vrSimulation != null) {
                vrSimulation.setDeleted(1);
                vrSimulation.setUpdateTime(LocalDateTime.now());
                return updateById(vrSimulation);
            }
            return false;
        } catch (Exception e) {
            log.error("删除VR演练失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public VRSimulation getVRSimulationById(Long id) {
        try {
            return getById(id);
        } catch (Exception e) {
            log.error("获取VR演练详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getVRSimulationList(Map<String, Object> params, int page, int size) {
        try {
            // 这里可以根据params构建查询条件
            // 暂时使用BaseMapper的selectList方法
            List<VRSimulation> vrSimulations = list();
            int total = vrSimulations.size();
            int offset = (page - 1) * size;
            int end = Math.min(offset + size, total);
            List<VRSimulation> pageList = vrSimulations.subList(offset, end);

            Map<String, Object> result = new HashMap<>();
            result.put("list", pageList);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("查询VR演练列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public Map<String, Object> getVRSimulationsByType(Integer type, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<VRSimulation> vrSimulations = vrSimulationMapper.selectByType(type, offset, size);
            // 计算总数
            int total = vrSimulations.size();

            Map<String, Object> result = new HashMap<>();
            result.put("list", vrSimulations);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("根据类型查询VR演练失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public Map<String, Object> getVRSimulationsByDifficulty(Integer difficulty, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<VRSimulation> vrSimulations = vrSimulationMapper.selectByDifficulty(difficulty, offset, size);
            // 计算总数
            int total = vrSimulations.size();

            Map<String, Object> result = new HashMap<>();
            result.put("list", vrSimulations);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("根据难度查询VR演练失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public Map<String, Object> getVRSimulationsByScenarioId(Long scenarioId, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<VRSimulation> vrSimulations = vrSimulationMapper.selectByScenarioId(scenarioId, offset, size);
            // 计算总数
            int total = vrSimulations.size();

            Map<String, Object> result = new HashMap<>();
            result.put("list", vrSimulations);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("根据场景查询VR演练失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public Map<String, Object> getVRSimulationStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("typeStats", vrSimulationMapper.selectTypeStats());
            stats.put("difficultyStats", vrSimulationMapper.selectDifficultyStats());
            stats.put("scenarioStats", vrSimulationMapper.selectScenarioStats());
            return stats;
        } catch (Exception e) {
            log.error("统计VR演练信息失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    @Transactional
    public boolean incrementViewCount(Long id) {
        try {
            vrSimulationMapper.incrementViewCount(id);
            return true;
        } catch (Exception e) {
            log.error("增加浏览量失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean incrementParticipantCount(Long id) {
        try {
            vrSimulationMapper.incrementParticipantCount(id);
            return true;
        } catch (Exception e) {
            log.error("增加参与人数失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateAverageScore(Long id, Double score) {
        try {
            vrSimulationMapper.updateAverageScore(id, score);
            return true;
        } catch (Exception e) {
            log.error("更新平均评分失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public Long startVRSimulation(Long simulationId, Long userId, String username) {
        try {
            VRSimulation vrSimulation = getById(simulationId);
            if (vrSimulation != null) {
                // 增加参与人数
                incrementParticipantCount(simulationId);
                // 创建演练记录
                VRSimulationRecord record = new VRSimulationRecord();
                record.setSimulationId(simulationId);
                record.setSimulationName(vrSimulation.getName());
                record.setUserId(userId);
                record.setUsername(username);
                record.setStartTime(LocalDateTime.now());
                record.setCompletionStatus(1); // 未完成
                record.setDeleted(0);
                record.setCreateTime(LocalDateTime.now());
                record.setUpdateTime(LocalDateTime.now());
                vrSimulationRecordMapper.insert(record);
                return record.getId();
            }
            return null;
        } catch (Exception e) {
            log.error("开始VR演练失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    @Transactional
    public boolean endVRSimulation(Long recordId, Integer score, Double correctRate, String operationRecord, String evaluationResult) {
        try {
            VRSimulationRecord record = vrSimulationRecordMapper.selectById(recordId);
            if (record != null) {
                record.setEndTime(LocalDateTime.now());
                record.setActualDuration((int) ChronoUnit.MINUTES.between(record.getStartTime(), record.getEndTime()));
                record.setScore(score);
                record.setCorrectRate(correctRate);
                record.setCompletionStatus(2); // 已完成
                record.setOperationRecord(operationRecord);
                record.setEvaluationResult(evaluationResult);
                record.setUpdateTime(LocalDateTime.now());
                vrSimulationRecordMapper.updateById(record);
                // 更新演练的平均评分
                Double averageScore = vrSimulationRecordMapper.selectAverageScoreBySimulationId(record.getSimulationId());
                if (averageScore != null) {
                    updateAverageScore(record.getSimulationId(), averageScore);
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("结束VR演练失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public VRSimulationRecord getVRSimulationRecordById(Long id) {
        try {
            return vrSimulationRecordMapper.selectById(id);
        } catch (Exception e) {
            log.error("获取VR演练记录失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getVRSimulationRecordsByUserId(Long userId, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<VRSimulationRecord> records = vrSimulationRecordMapper.selectByUserId(userId, offset, size);
            // 计算总数
            int total = records.size();

            Map<String, Object> result = new HashMap<>();
            result.put("list", records);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("根据用户ID查询VR演练记录失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public Map<String, Object> getVRSimulationRecordsBySimulationId(Long simulationId, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<VRSimulationRecord> records = vrSimulationRecordMapper.selectBySimulationId(simulationId, offset, size);
            // 计算总数
            int total = records.size();

            Map<String, Object> result = new HashMap<>();
            result.put("list", records);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("根据演练ID查询VR演练记录失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public Map<String, Object> getUserVRSimulationStats(Long userId) {
        try {
            return vrSimulationRecordMapper.selectUserStats(userId);
        } catch (Exception e) {
            log.error("获取用户VR演练统计信息失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Object> getSimulationVRSimulationStats(Long simulationId) {
        try {
            return vrSimulationRecordMapper.selectSimulationStats(simulationId);
        } catch (Exception e) {
            log.error("获取演练VR演练统计信息失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    public List<Map<String, Object>> getVRSimulationTrend(String startDate, String endDate) {
        try {
            return vrSimulationRecordMapper.selectDateStats(startDate, endDate);
        } catch (Exception e) {
            log.error("获取VR演练趋势失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
}
