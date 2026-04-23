package com.anti.fraud.modules.report.mapper;

import com.anti.fraud.modules.report.entity.ReportRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 举报记录Mapper
 */
@Mapper
public interface ReportRecordMapper extends BaseMapper<ReportRecord> {

    /**
     * 根据条件查询举报记录列表
     * @param params 查询参数
     * @param page 页码
     * @param size 每页大小
     * @return 举报记录列表
     */
    List<ReportRecord> selectByCondition(@Param("params") Map<String, Object> params, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据条件查询举报记录总数
     * @param params 查询参数
     * @return 举报记录总数
     */
    Integer selectCountByCondition(@Param("params") Map<String, Object> params);

    /**
     * 根据用户ID查询举报记录
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 举报记录列表
     */
    List<ReportRecord> selectByUserId(@Param("userId") Long userId, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据用户ID查询举报记录总数
     * @param userId 用户ID
     * @return 举报记录总数
     */
    Integer selectCountByUserId(@Param("userId") Long userId);

    /**
     * 获取待处理的举报记录
     * @param limit 数量限制
     * @return 待处理的举报记录列表
     */
    List<ReportRecord> selectPendingReports(@Param("limit") Integer limit);

    /**
     * 按举报类型统计举报记录
     * @return 类型统计
     */
    List<Map<String, Object>> selectTypeStats();

    /**
     * 按处理状态统计举报记录
     * @return 状态统计
     */
    List<Map<String, Object>> selectStatusStats();

    /**
     * 获取用户举报统计信息
     * @param userId 用户ID
     * @return 统计信息
     */
    Map<String, Object> selectUserReportStats(@Param("userId") Long userId);

    /**
     * 获取用户获得的积分奖励
     * @param userId 用户ID
     * @return 积分奖励
     */
    Integer selectUserRewardPoints(@Param("userId") Long userId);

    /**
     * 按月份统计举报记录
     * @param months 月份数
     * @return 月度统计
     */
    List<Map<String, Object>> selectMonthlyStats(@Param("months") Integer months);
}
