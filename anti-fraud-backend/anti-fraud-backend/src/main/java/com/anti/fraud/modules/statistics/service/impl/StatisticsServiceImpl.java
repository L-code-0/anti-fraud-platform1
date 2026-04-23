package com.anti.fraud.modules.statistics.service.impl;

import com.anti.fraud.modules.statistics.entity.StatisticsData;
import com.anti.fraud.modules.statistics.mapper.StatisticsDataMapper;
import com.anti.fraud.modules.statistics.service.StatisticsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 统计数据服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class StatisticsServiceImpl extends ServiceImpl<StatisticsDataMapper, StatisticsData> implements StatisticsService {

    private final StatisticsDataMapper statisticsDataMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public String createStatisticsData(StatisticsData statisticsData) {
        try {
            // 生成统计ID
            String statisticsId = UUID.randomUUID().toString();
            statisticsData.setStatisticsId(statisticsId);
            statisticsData.setStatus(2); // 统计中
            statisticsData.setDeleted(0);
            statisticsData.setCreateTime(LocalDateTime.now());
            statisticsData.setUpdateTime(LocalDateTime.now());

            boolean success = save(statisticsData);
            if (success) {
                // 异步执行统计任务
                executeStatisticsTaskAsync(statisticsData);
                return statisticsId;
            } else {
                throw new RuntimeException("创建统计数据失败");
            }
        } catch (Exception e) {
            log.error("创建统计数据失败: {}", e.getMessage(), e);
            throw new RuntimeException("创建统计数据失败");
        }
    }

    @Override
    @Transactional
    public boolean updateStatisticsData(StatisticsData statisticsData) {
        try {
            statisticsData.setUpdateTime(LocalDateTime.now());
            return updateById(statisticsData);
        } catch (Exception e) {
            log.error("更新统计数据失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteStatisticsData(String statisticsId) {
        try {
            StatisticsData data = statisticsDataMapper.selectByStatisticsId(statisticsId);
            if (data != null) {
                data.setDeleted(1);
                data.setUpdateTime(LocalDateTime.now());
                return updateById(data);
            }
            return false;
        } catch (Exception e) {
            log.error("删除统计数据失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public StatisticsData getStatisticsDataByStatisticsId(String statisticsId) {
        try {
            return statisticsDataMapper.selectByStatisticsId(statisticsId);
        } catch (Exception e) {
            log.error("获取统计数据详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getStatisticsDataList(Integer type, Integer dimension, Integer status, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<StatisticsData> dataList = statisticsDataMapper.selectByType(type, dimension, status, offset, size);
            // 计算总数
            int total = statisticsDataMapper.countByType(type, dimension, status);

            Map<String, Object> result = new HashMap<>();
            result.put("list", dataList);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("查询统计数据列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public List<StatisticsData> getStatisticsDataByTimeRange(LocalDateTime startTime, LocalDateTime endTime, Integer type, Integer status) {
        try {
            return statisticsDataMapper.selectByTimeRange(startTime, endTime, type, status);
        } catch (Exception e) {
            log.error("根据时间范围查询统计数据失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public boolean updateStatisticsDataStatus(String statisticsId, Integer status) {
        try {
            StatisticsData data = statisticsDataMapper.selectByStatisticsId(statisticsId);
            if (data != null) {
                return statisticsDataMapper.updateStatus(data.getId(), status) > 0;
            }
            return false;
        } catch (Exception e) {
            log.error("更新统计状态失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateStatisticsDataResult(String statisticsId, Double value, String data) {
        try {
            StatisticsData statisticsData = statisticsDataMapper.selectByStatisticsId(statisticsId);
            if (statisticsData != null) {
                return statisticsDataMapper.updateStatisticsResult(statisticsData.getId(), value, data, LocalDateTime.now()) > 0;
            }
            return false;
        } catch (Exception e) {
            log.error("更新统计结果失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Integer countStatisticsData(Integer type, Integer dimension, Integer status) {
        try {
            return statisticsDataMapper.countByType(type, dimension, status);
        } catch (Exception e) {
            log.error("统计统计数据数量失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public Double calculateTotalValue(Integer type, Integer dimension, Integer status) {
        try {
            return statisticsDataMapper.calculateTotalValue(type, dimension, status);
        } catch (Exception e) {
            log.error("计算统计值总和失败: {}", e.getMessage(), e);
            return 0.0;
        }
    }

    @Override
    public Double calculateAverageValue(Integer type, Integer dimension, Integer status) {
        try {
            return statisticsDataMapper.calculateAverageValue(type, dimension, status);
        } catch (Exception e) {
            log.error("计算统计值平均值失败: {}", e.getMessage(), e);
            return 0.0;
        }
    }

    @Override
    public List<StatisticsData> getRecentStatisticsData(Integer type, Integer dimension, int limit, Integer status) {
        try {
            return statisticsDataMapper.selectRecentStatistics(type, dimension, limit, status);
        } catch (Exception e) {
            log.error("获取最近的统计数据失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public int batchCreateStatisticsData(List<StatisticsData> statisticsDataList) {
        try {
            for (StatisticsData data : statisticsDataList) {
                data.setStatisticsId(UUID.randomUUID().toString());
                data.setStatus(2); // 统计中
                data.setDeleted(0);
                data.setCreateTime(LocalDateTime.now());
                data.setUpdateTime(LocalDateTime.now());
            }
            int count = statisticsDataMapper.batchInsert(statisticsDataList);
            // 异步执行统计任务
            for (StatisticsData data : statisticsDataList) {
                executeStatisticsTaskAsync(data);
            }
            return count;
        } catch (Exception e) {
            log.error("批量创建统计数据失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public Map<String, Object> getStatisticsDataStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 统计总统计数据数量
            int total = statisticsDataMapper.countByType(null, null, 1);
            statistics.put("total", total);
            
            // 统计各类型统计数据数量
            int userActivity = statisticsDataMapper.countByType(1, null, 1);
            int exerciseParticipation = statisticsDataMapper.countByType(2, null, 1);
            int fraudTypeDistribution = statisticsDataMapper.countByType(3, null, 1);
            int knowledgeMastery = statisticsDataMapper.countByType(4, null, 1);
            int other = statisticsDataMapper.countByType(5, null, 1);
            statistics.put("userActivity", userActivity);
            statistics.put("exerciseParticipation", exerciseParticipation);
            statistics.put("fraudTypeDistribution", fraudTypeDistribution);
            statistics.put("knowledgeMastery", knowledgeMastery);
            statistics.put("other", other);
            
            // 统计各维度统计数据数量
            int daily = statisticsDataMapper.countByType(null, 1, 1);
            int weekly = statisticsDataMapper.countByType(null, 2, 1);
            int monthly = statisticsDataMapper.countByType(null, 3, 1);
            int yearly = statisticsDataMapper.countByType(null, 4, 1);
            int custom = statisticsDataMapper.countByType(null, 5, 1);
            statistics.put("daily", daily);
            statistics.put("weekly", weekly);
            statistics.put("monthly", monthly);
            statistics.put("yearly", yearly);
            statistics.put("custom", custom);
            
            // 统计状态分布
            int analyzed = statisticsDataMapper.countByType(null, null, 1);
            int analyzing = statisticsDataMapper.countByType(null, null, 2);
            int failed = statisticsDataMapper.countByType(null, null, 3);
            statistics.put("analyzed", analyzed);
            statistics.put("analyzing", analyzing);
            statistics.put("failed", failed);
            
            return statistics;
        } catch (Exception e) {
            log.error("获取统计数据统计信息失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    public String executeStatisticsTask(Integer type, Integer dimension, LocalDateTime periodStartTime, LocalDateTime periodEndTime) {
        try {
            StatisticsData statisticsData = new StatisticsData();
            statisticsData.setType(type);
            statisticsData.setDimension(dimension);
            statisticsData.setPeriodStartTime(periodStartTime);
            statisticsData.setPeriodEndTime(periodEndTime);
            return createStatisticsData(statisticsData);
        } catch (Exception e) {
            log.error("执行统计任务失败: {}", e.getMessage(), e);
            throw new RuntimeException("执行统计任务失败");
        }
    }

    @Override
    public StatisticsData statisticsUserActivity(LocalDateTime periodStartTime, LocalDateTime periodEndTime) {
        try {
            // 这里简化处理，实际应该从数据库查询用户活跃数据
            // 例如查询用户登录次数、操作次数等
            StatisticsData data = new StatisticsData();
            data.setType(1); // 用户活跃度
            data.setDimension(1); // 日
            data.setValue(100.0); // 示例值
            data.setData("{\"loginCount\": 100, \"operationCount\": 500, \"activeUserCount\": 50}");
            data.setStatisticsTime(LocalDateTime.now());
            data.setPeriodStartTime(periodStartTime);
            data.setPeriodEndTime(periodEndTime);
            data.setStatus(1); // 已统计
            return data;
        } catch (Exception e) {
            log.error("统计用户活跃度失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public StatisticsData statisticsExerciseParticipation(LocalDateTime periodStartTime, LocalDateTime periodEndTime) {
        try {
            // 这里简化处理，实际应该从数据库查询演练参与数据
            // 例如查询演练参与人数、完成率等
            StatisticsData data = new StatisticsData();
            data.setType(2); // 演练参与度
            data.setDimension(1); // 日
            data.setValue(80.0); // 示例值
            data.setData("{\"participationCount\": 80, \"completionRate\": 0.8, \"averageScore\": 85}");
            data.setStatisticsTime(LocalDateTime.now());
            data.setPeriodStartTime(periodStartTime);
            data.setPeriodEndTime(periodEndTime);
            data.setStatus(1); // 已统计
            return data;
        } catch (Exception e) {
            log.error("统计演练参与度失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public StatisticsData statisticsFraudTypeDistribution(LocalDateTime periodStartTime, LocalDateTime periodEndTime) {
        try {
            // 这里简化处理，实际应该从数据库查询诈骗类型分布数据
            // 例如查询各类型诈骗的数量、占比等
            StatisticsData data = new StatisticsData();
            data.setType(3); // 诈骗类型分布
            data.setDimension(1); // 日
            data.setValue(100.0); // 示例值
            data.setData("{\"types\": [{\"type\": \"电信诈骗\", \"count\": 50, \"percentage\": 0.5}, {\"type\": \"网络诈骗\", \"count\": 30, \"percentage\": 0.3}, {\"type\": \"其他诈骗\", \"count\": 20, \"percentage\": 0.2}]}");
            data.setStatisticsTime(LocalDateTime.now());
            data.setPeriodStartTime(periodStartTime);
            data.setPeriodEndTime(periodEndTime);
            data.setStatus(1); // 已统计
            return data;
        } catch (Exception e) {
            log.error("统计诈骗类型分布失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public StatisticsData statisticsKnowledgeMastery(LocalDateTime periodStartTime, LocalDateTime periodEndTime) {
        try {
            // 这里简化处理，实际应该从数据库查询知识掌握度数据
            // 例如查询用户测试得分、知识学习时间等
            StatisticsData data = new StatisticsData();
            data.setType(4); // 知识掌握度
            data.setDimension(1); // 日
            data.setValue(75.0); // 示例值
            data.setData("{\"averageScore\": 75, \"masteryRate\": 0.75, \"studyTime\": 3600}");
            data.setStatisticsTime(LocalDateTime.now());
            data.setPeriodStartTime(periodStartTime);
            data.setPeriodEndTime(periodEndTime);
            data.setStatus(1); // 已统计
            return data;
        } catch (Exception e) {
            log.error("统计知识掌握度失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 异步执行统计任务
     * @param statisticsData 统计数据信息
     */
    private void executeStatisticsTaskAsync(StatisticsData statisticsData) {
        // 这里简化处理，实际应该使用线程池或消息队列进行异步处理
        new Thread(() -> {
            try {
                StatisticsData result = null;
                Integer type = statisticsData.getType();
                LocalDateTime periodStartTime = statisticsData.getPeriodStartTime();
                LocalDateTime periodEndTime = statisticsData.getPeriodEndTime();

                switch (type) {
                    case 1: // 用户活跃度
                        result = statisticsUserActivity(periodStartTime, periodEndTime);
                        break;
                    case 2: // 演练参与度
                        result = statisticsExerciseParticipation(periodStartTime, periodEndTime);
                        break;
                    case 3: // 诈骗类型分布
                        result = statisticsFraudTypeDistribution(periodStartTime, periodEndTime);
                        break;
                    case 4: // 知识掌握度
                        result = statisticsKnowledgeMastery(periodStartTime, periodEndTime);
                        break;
                    default: // 其他
                        result = new StatisticsData();
                        result.setType(type);
                        result.setDimension(statisticsData.getDimension());
                        result.setValue(0.0);
                        result.setData("{}");
                        result.setStatisticsTime(LocalDateTime.now());
                        result.setPeriodStartTime(periodStartTime);
                        result.setPeriodEndTime(periodEndTime);
                        result.setStatus(1); // 已统计
                        break;
                }

                if (result != null) {
                    updateStatisticsDataResult(
                            statisticsData.getStatisticsId(),
                            result.getValue(),
                            result.getData()
                    );
                    updateStatisticsDataStatus(statisticsData.getStatisticsId(), 1); // 已统计
                } else {
                    updateStatisticsDataStatus(statisticsData.getStatisticsId(), 3); // 统计失败
                }
            } catch (Exception e) {
                log.error("异步执行统计任务失败: {}", e.getMessage(), e);
                updateStatisticsDataStatus(statisticsData.getStatisticsId(), 3); // 统计失败
            }
        }).start();
    }
}
