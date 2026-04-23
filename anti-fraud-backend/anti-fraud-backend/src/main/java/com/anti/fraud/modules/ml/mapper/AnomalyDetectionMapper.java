package com.anti.fraud.modules.ml.mapper;

import com.anti.fraud.modules.ml.entity.AnomalyDetection;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 异常检测Mapper
 */
@Mapper
public interface AnomalyDetectionMapper extends BaseMapper<AnomalyDetection> {

    /**
     * 根据检测ID查询异常检测
     * @param detectionId 检测ID
     * @return 异常检测
     */
    AnomalyDetection selectByDetectionId(@Param("detectionId") String detectionId);

    /**
     * 根据用户ID查询异常检测列表
     * @param userId 用户ID
     * @param detectionType 检测类型
     * @param anomalyLevel 异常等级
     * @param page 页码
     * @param size 每页大小
     * @return 异常检测列表
     */
    List<AnomalyDetection> selectByUserId(@Param("userId") Long userId, @Param("detectionType") Integer detectionType, @Param("anomalyLevel") Integer anomalyLevel, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据检测类型查询异常检测列表
     * @param detectionType 检测类型
     * @param anomalyLevel 异常等级
     * @param page 页码
     * @param size 每页大小
     * @return 异常检测列表
     */
    List<AnomalyDetection> selectByDetectionType(@Param("detectionType") Integer detectionType, @Param("anomalyLevel") Integer anomalyLevel, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据时间范围查询异常检测列表
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param userId 用户ID
     * @param detectionType 检测类型
     * @param anomalyLevel 异常等级
     * @return 异常检测列表
     */
    List<AnomalyDetection> selectByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("userId") Long userId, @Param("detectionType") Integer detectionType, @Param("anomalyLevel") Integer anomalyLevel);

    /**
     * 更新异常检测状态
     * @param id 异常检测ID
     * @param status 状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 统计异常检测数量
     * @param detectionType 检测类型
     * @param anomalyLevel 异常等级
     * @return 异常检测数量
     */
    Integer countByDetectionType(@Param("detectionType") Integer detectionType, @Param("anomalyLevel") Integer anomalyLevel);

    /**
     * 统计用户异常检测数量
     * @param userId 用户ID
     * @param detectionType 检测类型
     * @param anomalyLevel 异常等级
     * @return 异常检测数量
     */
    Integer countByUserId(@Param("userId") Long userId, @Param("detectionType") Integer detectionType, @Param("anomalyLevel") Integer anomalyLevel);

    /**
     * 获取最近的异常检测
     * @param limit 数量限制
     * @param detectionType 检测类型
     * @param anomalyLevel 异常等级
     * @return 异常检测列表
     */
    List<AnomalyDetection> selectRecentDetections(@Param("limit") Integer limit, @Param("detectionType") Integer detectionType, @Param("anomalyLevel") Integer anomalyLevel);

    /**
     * 批量插入异常检测
     * @param detections 异常检测列表
     * @return 插入成功数量
     */
    int batchInsert(@Param("detections") List<AnomalyDetection> detections);

    /**
     * 批量更新异常等级
     * @param ids 异常检测ID列表
     * @param anomalyLevel 异常等级
     * @return 影响行数
     */
    int batchUpdateAnomalyLevel(@Param("ids") List<Long> ids, @Param("anomalyLevel") Integer anomalyLevel);
}
