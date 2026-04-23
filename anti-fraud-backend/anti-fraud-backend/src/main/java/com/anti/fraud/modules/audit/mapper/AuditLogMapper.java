package com.anti.fraud.modules.audit.mapper;

import com.anti.fraud.modules.audit.entity.AuditLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 操作审计日志Mapper
 */
@Mapper
public interface AuditLogMapper extends BaseMapper<AuditLog> {

    /**
     * 根据条件查询审计日志列表
     * @param params 查询参数
     * @param page 页码
     * @param size 每页大小
     * @return 审计日志列表
     */
    List<AuditLog> selectByCondition(@Param("params") Map<String, Object> params, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据条件查询审计日志总数
     * @param params 查询参数
     * @return 审计日志总数
     */
    Integer selectCountByCondition(@Param("params") Map<String, Object> params);

    /**
     * 根据用户ID查询审计日志
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 审计日志列表
     */
    List<AuditLog> selectByUserId(@Param("userId") Long userId, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据用户ID查询审计日志总数
     * @param userId 用户ID
     * @return 审计日志总数
     */
    Integer selectCountByUserId(@Param("userId") Long userId);

    /**
     * 按操作类型统计审计日志
     * @return 类型统计
     */
    List<Map<String, Object>> selectOperationTypeStats();

    /**
     * 按操作模块统计审计日志
     * @return 模块统计
     */
    List<Map<String, Object>> selectModuleStats();

    /**
     * 按操作结果统计审计日志
     * @return 结果统计
     */
    List<Map<String, Object>> selectOperationResultStats();

    /**
     * 按日期统计审计日志
     * @param days 天数
     * @return 日期统计
     */
    List<Map<String, Object>> selectDailyStats(@Param("days") Integer days);

    /**
     * 获取最近的审计日志
     * @param limit 数量限制
     * @return 最近的审计日志列表
     */
    List<AuditLog> selectLatestLogs(@Param("limit") Integer limit);

    /**
     * 获取失败的审计日志
     * @param limit 数量限制
     * @return 失败的审计日志列表
     */
    List<AuditLog> selectFailedLogs(@Param("limit") Integer limit);

    /**
     * 清理指定时间之前的审计日志
     * @param beforeTime 清理时间
     * @return 清理数量
     */
    Integer deleteBeforeTime(@Param("beforeTime") LocalDateTime beforeTime);
}
