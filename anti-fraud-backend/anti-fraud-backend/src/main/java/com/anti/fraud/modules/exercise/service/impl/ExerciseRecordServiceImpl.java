package com.anti.fraud.modules.exercise.service.impl;

import com.anti.fraud.modules.exercise.entity.ExerciseRecord;
import com.anti.fraud.modules.exercise.mapper.ExerciseRecordMapper;
import com.anti.fraud.modules.exercise.service.ExerciseRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 演练记录服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ExerciseRecordServiceImpl extends ServiceImpl<ExerciseRecordMapper, ExerciseRecord> implements ExerciseRecordService {

    private final ExerciseRecordMapper exerciseRecordMapper;

    @Override
    @Transactional
    public boolean addExerciseRecord(ExerciseRecord exerciseRecord) {
        try {
            exerciseRecord.setStatus(1);
            exerciseRecord.setDeleted(0);
            exerciseRecord.setCreateTime(LocalDateTime.now());
            exerciseRecord.setUpdateTime(LocalDateTime.now());
            return save(exerciseRecord);
        } catch (Exception e) {
            log.error("新增演练记录失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateExerciseRecord(ExerciseRecord exerciseRecord) {
        try {
            exerciseRecord.setUpdateTime(LocalDateTime.now());
            return updateById(exerciseRecord);
        } catch (Exception e) {
            log.error("更新演练记录失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteExerciseRecord(Long id) {
        try {
            ExerciseRecord exerciseRecord = getById(id);
            if (exerciseRecord != null) {
                exerciseRecord.setDeleted(1);
                exerciseRecord.setUpdateTime(LocalDateTime.now());
                return updateById(exerciseRecord);
            }
            return false;
        } catch (Exception e) {
            log.error("删除演练记录失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public ExerciseRecord getExerciseRecordById(Long id) {
        try {
            return getById(id);
        } catch (Exception e) {
            log.error("获取演练记录详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getExerciseRecordList(Map<String, Object> params, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<ExerciseRecord> exerciseRecords = exerciseRecordMapper.selectByCondition(params, offset, size);
            int total = exerciseRecordMapper.selectCountByCondition(params);

            Map<String, Object> result = new HashMap<>();
            result.put("list", exerciseRecords);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("查询演练记录列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public Map<String, Object> getExerciseRecordsByUserId(Long userId, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<ExerciseRecord> exerciseRecords = exerciseRecordMapper.selectByUserId(userId, offset, size);
            int total = exerciseRecordMapper.selectCountByUserId(userId);

            Map<String, Object> result = new HashMap<>();
            result.put("list", exerciseRecords);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("根据用户ID查询演练记录失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public Map<String, Object> getUserExerciseStats(Long userId) {
        try {
            return exerciseRecordMapper.selectUserExerciseStats(userId);
        } catch (Exception e) {
            log.error("获取用户演练统计信息失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    public List<ExerciseRecord> getLatestExerciseRecordsByUserId(Long userId, int limit) {
        try {
            return exerciseRecordMapper.selectLatestByUserId(userId, limit);
        } catch (Exception e) {
            log.error("获取用户最近的演练记录失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public ExerciseRecord getBestScoreByUserId(Long userId, Integer exerciseType) {
        try {
            return exerciseRecordMapper.selectBestScoreByUserId(userId, exerciseType);
        } catch (Exception e) {
            log.error("获取用户最佳成绩失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getExerciseRecordStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("typeStats", exerciseRecordMapper.selectTypeStats());
            stats.put("userStats", exerciseRecordMapper.selectUserStats(10));
            return stats;
        } catch (Exception e) {
            log.error("统计演练记录信息失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    public List<Map<String, Object>> getExerciseRecordTrend(Long userId, int days) {
        try {
            return exerciseRecordMapper.selectTrendByUserId(userId, days);
        } catch (Exception e) {
            log.error("获取演练记录趋势失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public Map<String, Object> compareExerciseRecords(Long recordId1, Long recordId2) {
        try {
            ExerciseRecord record1 = getById(recordId1);
            ExerciseRecord record2 = getById(recordId2);

            if (record1 == null || record2 == null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "演练记录不存在");
                return result;
            }

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("record1", record1);
            result.put("record2", record2);

            // 计算差异
            Map<String, Object> diff = new HashMap<>();
            diff.put("scoreDiff", record2.getScore() - record1.getScore());
            diff.put("durationDiff", record2.getDuration() - record1.getDuration());
            diff.put("correctRateDiff", record2.getCorrectRate() - record1.getCorrectRate());
            result.put("diff", diff);

            // 分析结果
            String analysis = "";
            if (record2.getScore() > record1.getScore()) {
                analysis += "成绩有所提高，";
            } else if (record2.getScore() < record1.getScore()) {
                analysis += "成绩有所下降，";
            } else {
                analysis += "成绩保持稳定，";
            }

            if (record2.getDuration() < record1.getDuration()) {
                analysis += "用时减少，";
            } else if (record2.getDuration() > record1.getDuration()) {
                analysis += "用时增加，";
            } else {
                analysis += "用时保持稳定，";
            }

            if (record2.getCorrectRate() > record1.getCorrectRate()) {
                analysis += "正确率提高。";
            } else if (record2.getCorrectRate() < record1.getCorrectRate()) {
                analysis += "正确率下降。";
            } else {
                analysis += "正确率保持稳定。";
            }

            result.put("analysis", analysis);
            return result;
        } catch (Exception e) {
            log.error("对比演练记录失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "对比演练记录失败");
            return result;
        }
    }

    @Override
    public Map<String, Object> analyzeUserExercisePerformance(Long userId, int days) {
        try {
            List<Map<String, Object>> trend = exerciseRecordMapper.selectTrendByUserId(userId, days);
            Map<String, Object> stats = exerciseRecordMapper.selectUserExerciseStats(userId);

            Map<String, Object> result = new HashMap<>();
            result.put("trend", trend);
            result.put("stats", stats);

            // 分析表现
            String analysis = "";
            if (trend != null && !trend.isEmpty()) {
                // 计算趋势
                double scoreSum = 0;
                double correctRateSum = 0;
                for (Map<String, Object> item : trend) {
                    scoreSum += (Integer) item.get("score");
                    correctRateSum += (Double) item.get("correctRate");
                }
                double avgScore = scoreSum / trend.size();
                double avgCorrectRate = correctRateSum / trend.size();

                analysis += String.format("最近%d天的平均成绩为%.2f分，平均正确率为%.2f%%。", days, avgScore, avgCorrectRate * 100);

                // 分析趋势
                Map<String, Object> firstItem = trend.get(0);
                Map<String, Object> lastItem = trend.get(trend.size() - 1);
                int firstScore = (Integer) firstItem.get("score");
                int lastScore = (Integer) lastItem.get("score");
                double firstCorrectRate = (Double) firstItem.get("correctRate");
                double lastCorrectRate = (Double) lastItem.get("correctRate");

                if (lastScore > firstScore) {
                    analysis += " 成绩呈上升趋势，";
                } else if (lastScore < firstScore) {
                    analysis += " 成绩呈下降趋势，";
                } else {
                    analysis += " 成绩保持稳定，";
                }

                if (lastCorrectRate > firstCorrectRate) {
                    analysis += "正确率呈上升趋势。";
                } else if (lastCorrectRate < firstCorrectRate) {
                    analysis += "正确率呈下降趋势。";
                } else {
                    analysis += "正确率保持稳定。";
                }
            } else {
                analysis = "暂无演练记录。";
            }

            result.put("analysis", analysis);
            return result;
        } catch (Exception e) {
            log.error("分析用户演练表现失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "分析用户演练表现失败");
            return result;
        }
    }
}
