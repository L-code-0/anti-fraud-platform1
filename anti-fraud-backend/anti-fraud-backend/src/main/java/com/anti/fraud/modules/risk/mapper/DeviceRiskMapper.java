package com.anti.fraud.modules.risk.mapper;

import com.anti.fraud.modules.risk.entity.DeviceRisk;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 设备风险Mapper
 */
@Mapper
public interface DeviceRiskMapper extends BaseMapper<DeviceRisk> {

    /**
     * 根据风险ID查询设备风险
     * @param riskId 风险ID
     * @return 设备风险
     */
    DeviceRisk selectByRiskId(@Param("riskId") String riskId);

    /**
     * 根据用户ID查询设备风险列表
     * @param userId 用户ID
     * @param deviceType 设备类型
     * @param riskLevel 风险等级
     * @param page 页码
     * @param size 每页大小
     * @return 设备风险列表
     */
    List<DeviceRisk> selectByUserId(@Param("userId") Long userId, @Param("deviceType") Integer deviceType, @Param("riskLevel") Integer riskLevel, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据设备ID查询设备风险
     * @param deviceId 设备ID
     * @return 设备风险
     */
    DeviceRisk selectByDeviceId(@Param("deviceId") String deviceId);

    /**
     * 根据IP地址查询设备风险列表
     * @param ipAddress IP地址
     * @param riskLevel 风险等级
     * @param page 页码
     * @param size 每页大小
     * @return 设备风险列表
     */
    List<DeviceRisk> selectByIpAddress(@Param("ipAddress") String ipAddress, @Param("riskLevel") Integer riskLevel, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 更新设备风险状态
     * @param id 设备风险ID
     * @param status 状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 更新设备风险分数
     * @param id 设备风险ID
     * @param riskScore 风险分数
     * @param riskLevel 风险等级
     * @param lastUsedTime 最后使用时间
     * @return 影响行数
     */
    int updateRiskScore(@Param("id") Long id, @Param("riskScore") Double riskScore, @Param("riskLevel") Integer riskLevel, @Param("lastUsedTime") LocalDateTime lastUsedTime);

    /**
     * 统计设备风险数量
     * @param userId 用户ID
     * @param deviceType 设备类型
     * @param riskLevel 风险等级
     * @return 设备风险数量
     */
    Integer countByDeviceType(@Param("userId") Long userId, @Param("deviceType") Integer deviceType, @Param("riskLevel") Integer riskLevel);

    /**
     * 获取最近的设备风险
     * @param limit 数量限制
     * @param userId 用户ID
     * @param deviceType 设备类型
     * @param riskLevel 风险等级
     * @return 设备风险列表
     */
    List<DeviceRisk> selectRecentDeviceRisks(@Param("limit") Integer limit, @Param("userId") Long userId, @Param("deviceType") Integer deviceType, @Param("riskLevel") Integer riskLevel);

    /**
     * 批量插入设备风险
     * @param deviceRisks 设备风险列表
     * @return 插入成功数量
     */
    int batchInsert(@Param("deviceRisks") List<DeviceRisk> deviceRisks);

    /**
     * 批量更新风险等级
     * @param ids 设备风险ID列表
     * @param riskLevel 风险等级
     * @return 影响行数
     */
    int batchUpdateRiskLevel(@Param("ids") List<Long> ids, @Param("riskLevel") Integer riskLevel);
}
