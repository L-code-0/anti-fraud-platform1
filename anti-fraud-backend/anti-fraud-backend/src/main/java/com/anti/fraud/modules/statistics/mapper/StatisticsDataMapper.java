package com.anti.fraud.modules.statistics.mapper;

import com.anti.fraud.modules.statistics.entity.StatisticsData;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 统计数据Mapper
 */
@Mapper
public interface StatisticsDataMapper extends BaseMapper<StatisticsData> {

    /**
     * 根据统计ID查询统计数据
     * @param statisticsId 统计ID
     * @return 统计数据
     */
    StatisticsData selectByStatisticsId(@Param("statisticsId") String statisticsId);

    /**
     * 根据统计类型查询统计数据列表
     * @param type 统计类型
     * @param dimension 统计维度
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @return 统计数据列表
     */
    List<StatisticsData> selectByType(@Param("type") Integer type, @Param("dimension") Integer dimension, @Param("status") Integer status, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据统计时间范围查询统计数据
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param type 统计类型
     * @param status 状态
     * @return 统计数据列表
     */
    List<StatisticsData> selectByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("type") Integer type, @Param("status") Integer status);

    /**
     * 更新统计状态
     * @param id 统计数据ID
     * @param status 状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 更新统计结果
     * @param id 统计数据ID
     * @param value 统计值
     * @param data 统计数据
     * @param statisticsTime 统计时间
     * @return 影响行数
     */
    int updateStatisticsResult(@Param("id") Long id, @Param("value") Double value, @Param("data") String data, @Param("statisticsTime") LocalDateTime statisticsTime);

    /**
     * 统计统计数据数量
     * @param type 统计类型
     * @param dimension 统计维度
     * @param status 状态
     * @return 统计数据数量
     */
    Integer countByType(@Param("type") Integer type, @Param("dimension") Integer dimension, @Param("status") Integer status);

    /**
     * 计算统计值总和
     * @param type 统计类型
     * @param dimension 统计维度
     * @param status 状态
     * @return 统计值总和
     */
    Double calculateTotalValue(@Param("type") Integer type, @Param("dimension") Integer dimension, @Param("status") Integer status);

    /**
     * 计算统计值平均值
     * @param type 统计类型
     * @param dimension 统计维度
     * @param status 状态
     * @return 统计值平均值
     */
    Double calculateAverageValue(@Param("type") Integer type, @Param("dimension") Integer dimension, @Param("status") Integer status);

    /**
     * 获取最近的统计数据
     * @param type 统计类型
     * @param dimension 统计维度
     * @param limit 数量限制
     * @param status 状态
     * @return 统计数据列表
     */
    List<StatisticsData> selectRecentStatistics(@Param("type") Integer type, @Param("dimension") Integer dimension, @Param("limit") Integer limit, @Param("status") Integer status);

    /**
     * 批量插入统计数据
     * @param statisticsDataList 统计数据列表
     * @return 插入成功数量
     */
    int batchInsert(@Param("statisticsDataList") List<StatisticsData> statisticsDataList);
}
