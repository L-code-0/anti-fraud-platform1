package com.anti.fraud.modules.vr.mapper;

import com.anti.fraud.modules.vr.entity.VRSimulationRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * VR演练记录Mapper
 */
@Mapper
public interface VRSimulationRecordMapper extends BaseMapper<VRSimulationRecord> {

    /**
     * 根据用户ID查询VR演练记录
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return VR演练记录列表
     */
    List<VRSimulationRecord> selectByUserId(@Param("userId") Long userId, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据演练ID查询VR演练记录
     * @param simulationId 演练ID
     * @param page 页码
     * @param size 每页大小
     * @return VR演练记录列表
     */
    List<VRSimulationRecord> selectBySimulationId(@Param("simulationId") Long simulationId, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据完成状态查询VR演练记录
     * @param completionStatus 完成状态
     * @param page 页码
     * @param size 每页大小
     * @return VR演练记录列表
     */
    List<VRSimulationRecord> selectByCompletionStatus(@Param("completionStatus") Integer completionStatus, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 按用户统计VR演练记录
     * @param userId 用户ID
     * @return 统计信息
     */
    Map<String, Object> selectUserStats(@Param("userId") Long userId);

    /**
     * 按演练统计VR演练记录
     * @param simulationId 演练ID
     * @return 统计信息
     */
    Map<String, Object> selectSimulationStats(@Param("simulationId") Long simulationId);

    /**
     * 按日期统计VR演练记录
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计信息
     */
    List<Map<String, Object>> selectDateStats(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 获取用户最新的VR演练记录
     * @param userId 用户ID
     * @return VR演练记录
     */
    VRSimulationRecord selectLatestByUserId(@Param("userId") Long userId);

    /**
     * 获取演练的平均得分
     * @param simulationId 演练ID
     * @return 平均得分
     */
    Double selectAverageScoreBySimulationId(@Param("simulationId") Long simulationId);

    /**
     * 获取演练的完成率
     * @param simulationId 演练ID
     * @return 完成率
     */
    Double selectCompletionRateBySimulationId(@Param("simulationId") Long simulationId);
}
