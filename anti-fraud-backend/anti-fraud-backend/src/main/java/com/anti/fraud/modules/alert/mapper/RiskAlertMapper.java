package com.anti.fraud.modules.alert.mapper;

import com.anti.fraud.modules.alert.entity.RiskAlert;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 风险预警Mapper
 */
@Mapper
public interface RiskAlertMapper extends BaseMapper<RiskAlert> {

    /**
     * 根据预警ID查询预警
     * @param alertId 预警ID
     * @return 预警
     */
    RiskAlert selectByAlertId(@Param("alertId") String alertId);

    /**
     * 根据用户ID查询预警列表
     * @param userId 用户ID
     * @param alertType 预警类型
     * @param riskLevel 风险等级
     * @param page 页码
     * @param size 每页大小
     * @return 预警列表
     */
    List<RiskAlert> selectByUserId(@Param("userId") Long userId, @Param("alertType") Integer alertType, @Param("riskLevel") Integer riskLevel, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据预警类型查询预警列表
     * @param alertType 预警类型
     * @param riskLevel 风险等级
     * @param page 页码
     * @param size 每页大小
     * @return 预警列表
     */
    List<RiskAlert> selectByAlertType(@Param("alertType") Integer alertType, @Param("riskLevel") Integer riskLevel, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据时间范围查询预警列表
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param userId 用户ID
     * @param alertType 预警类型
     * @param riskLevel 风险等级
     * @return 预警列表
     */
    List<RiskAlert> selectByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("userId") Long userId, @Param("alertType") Integer alertType, @Param("riskLevel") Integer riskLevel);

    /**
     * 更新预警状态
     * @param id 预警ID
     * @param status 状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 更新WebSocket状态
     * @param id 预警ID
     * @param webSocketStatus WebSocket状态
     * @return 影响行数
     */
    int updateWebSocketStatus(@Param("id") Long id, @Param("webSocketStatus") Integer webSocketStatus);

    /**
     * 更新短信状态
     * @param id 预警ID
     * @param smsStatus 短信状态
     * @return 影响行数
     */
    int updateSmsStatus(@Param("id") Long id, @Param("smsStatus") Integer smsStatus);

    /**
     * 更新邮件状态
     * @param id 预警ID
     * @param emailStatus 邮件状态
     * @return 影响行数
     */
    int updateEmailStatus(@Param("id") Long id, @Param("emailStatus") Integer emailStatus);

    /**
     * 统计预警数量
     * @param userId 用户ID
     * @param alertType 预警类型
     * @param riskLevel 风险等级
     * @return 预警数量
     */
    Integer countByAlertType(@Param("userId") Long userId, @Param("alertType") Integer alertType, @Param("riskLevel") Integer riskLevel);

    /**
     * 获取最近的预警
     * @param limit 数量限制
     * @param userId 用户ID
     * @param alertType 预警类型
     * @param riskLevel 风险等级
     * @return 预警列表
     */
    List<RiskAlert> selectRecentAlerts(@Param("limit") Integer limit, @Param("userId") Long userId, @Param("alertType") Integer alertType, @Param("riskLevel") Integer riskLevel);

    /**
     * 批量插入预警
     * @param alerts 预警列表
     * @return 插入成功数量
     */
    int batchInsert(@Param("alerts") List<RiskAlert> alerts);

    /**
     * 批量更新预警状态
     * @param ids 预警ID列表
     * @param status 状态
     * @return 影响行数
     */
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") Integer status);
}
