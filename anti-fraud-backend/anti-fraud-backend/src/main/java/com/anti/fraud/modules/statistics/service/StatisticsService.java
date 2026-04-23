package com.anti.fraud.modules.statistics.service;

import com.anti.fraud.modules.statistics.entity.StatisticsData;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 统计数据服务接口
 */
public interface StatisticsService extends IService<StatisticsData> {

    /**
     * 创建统计数据
     * @param statisticsData 统计数据信息
     * @return 统计ID
     */
    String createStatisticsData(StatisticsData statisticsData);

    /**
     * 更新统计数据
     * @param statisticsData 统计数据信息
     * @return 是否成功
     */
    boolean updateStatisticsData(StatisticsData statisticsData);

    /**
     * 删除统计数据
     * @param statisticsId 统计ID
     * @return 是否成功
     */
    boolean deleteStatisticsData(String statisticsId);

    /**
     * 获取统计数据详情
     * @param statisticsId 统计ID
     * @return 统计数据详情
     */
    StatisticsData getStatisticsDataByStatisticsId(String statisticsId);

    /**
     * 分页查询统计数据列表
     * @param type 统计类型
     * @param dimension 统计维度
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @return 统计数据列表和总数
     */
    Map<String, Object> getStatisticsDataList(Integer type, Integer dimension, Integer status, int page, int size);

    /**
     * 根据时间范围查询统计数据
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param type 统计类型
     * @param status 状态
     * @return 统计数据列表
     */
    List<StatisticsData> getStatisticsDataByTimeRange(LocalDateTime startTime, LocalDateTime endTime, Integer type, Integer status);

    /**
     * 更新统计状态
     * @param statisticsId 统计ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateStatisticsDataStatus(String statisticsId, Integer status);

    /**
     * 更新统计结果
     * @param statisticsId 统计ID
     * @param value 统计值
     * @param data 统计数据
     * @return 是否成功
     */
    boolean updateStatisticsDataResult(String statisticsId, Double value, String data);

    /**
     * 统计统计数据数量
     * @param type 统计类型
     * @param dimension 统计维度
     * @param status 状态
     * @return 统计数据数量
     */
    Integer countStatisticsData(Integer type, Integer dimension, Integer status);

    /**
     * 计算统计值总和
     * @param type 统计类型
     * @param dimension 统计维度
     * @param status 状态
     * @return 统计值总和
     */
    Double calculateTotalValue(Integer type, Integer dimension, Integer status);

    /**
     * 计算统计值平均值
     * @param type 统计类型
     * @param dimension 统计维度
     * @param status 状态
     * @return 统计值平均值
     */
    Double calculateAverageValue(Integer type, Integer dimension, Integer status);

    /**
     * 获取最近的统计数据
     * @param type 统计类型
     * @param dimension 统计维度
     * @param limit 数量限制
     * @param status 状态
     * @return 统计数据列表
     */
    List<StatisticsData> getRecentStatisticsData(Integer type, Integer dimension, int limit, Integer status);

    /**
     * 批量创建统计数据
     * @param statisticsDataList 统计数据列表
     * @return 成功创建的数量
     */
    int batchCreateStatisticsData(List<StatisticsData> statisticsDataList);

    /**
     * 获取统计数据统计信息
     * @return 统计信息
     */
    Map<String, Object> getStatisticsDataStatistics();

    /**
     * 执行统计任务
     * @param type 统计类型
     * @param dimension 统计维度
     * @param periodStartTime 统计周期开始时间
     * @param periodEndTime 统计周期结束时间
     * @return 统计ID
     */
    String executeStatisticsTask(Integer type, Integer dimension, LocalDateTime periodStartTime, LocalDateTime periodEndTime);

    /**
     * 统计用户活跃度
     * @param periodStartTime 统计周期开始时间
     * @param periodEndTime 统计周期结束时间
     * @return 统计数据
     */
    StatisticsData statisticsUserActivity(LocalDateTime periodStartTime, LocalDateTime periodEndTime);

    /**
     * 统计演练参与度
     * @param periodStartTime 统计周期开始时间
     * @param periodEndTime 统计周期结束时间
     * @return 统计数据
     */
    StatisticsData statisticsExerciseParticipation(LocalDateTime periodStartTime, LocalDateTime periodEndTime);

    /**
     * 统计诈骗类型分布
     * @param periodStartTime 统计周期开始时间
     * @param periodEndTime 统计周期结束时间
     * @return 统计数据
     */
    StatisticsData statisticsFraudTypeDistribution(LocalDateTime periodStartTime, LocalDateTime periodEndTime);

    /**
     * 统计知识掌握度
     * @param periodStartTime 统计周期开始时间
     * @param periodEndTime 统计周期结束时间
     * @return 统计数据
     */
    StatisticsData statisticsKnowledgeMastery(LocalDateTime periodStartTime, LocalDateTime periodEndTime);
}
