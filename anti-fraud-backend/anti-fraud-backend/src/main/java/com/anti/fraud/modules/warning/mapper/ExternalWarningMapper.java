package com.anti.fraud.modules.warning.mapper;

import com.anti.fraud.modules.warning.entity.ExternalWarning;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 外部反诈预警信息Mapper
 */
@Mapper
public interface ExternalWarningMapper extends BaseMapper<ExternalWarning> {

    /**
     * 查询最新的预警信息
     * @param limit 数量限制
     * @return 预警信息列表
     */
    List<ExternalWarning> selectLatestWarnings(@Param("limit") Integer limit);

    /**
     * 根据预警类型查询预警信息
     * @param warningType 预警类型
     * @param limit 数量限制
     * @return 预警信息列表
     */
    List<ExternalWarning> selectByWarningType(@Param("warningType") Integer warningType, @Param("limit") Integer limit);

    /**
     * 根据来源查询预警信息
     * @param source 预警来源
     * @param limit 数量限制
     * @return 预警信息列表
     */
    List<ExternalWarning> selectBySource(@Param("source") String source, @Param("limit") Integer limit);

    /**
     * 查询指定时间范围内的预警信息
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 预警信息列表
     */
    List<ExternalWarning> selectByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 检查是否已存在相同的预警信息
     * @param title 预警标题
     * @param source 预警来源
     * @return 存在数量
     */
    Integer checkExists(@Param("title") String title, @Param("source") String source);

    /**
     * 批量插入预警信息
     * @param warnings 预警信息列表
     * @return 影响行数
     */
    int batchInsert(@Param("warnings") List<ExternalWarning> warnings);

    /**
     * 批量更新预警信息状态
     * @param ids 预警ID列表
     * @param status 状态
     * @return 影响行数
     */
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") Integer status);

    /**
     * 统计预警信息数量
     * @param status 状态
     * @return 预警信息数量
     */
    Integer countWarnings(@Param("status") Integer status);
}
