package com.anti.fraud.modules.risk.mapper;

import com.anti.fraud.modules.risk.entity.SocialRisk;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 社交风险Mapper
 */
@Mapper
public interface SocialRiskMapper extends BaseMapper<SocialRisk> {

    /**
     * 根据风险ID查询社交风险
     * @param riskId 风险ID
     * @return 社交风险
     */
    SocialRisk selectByRiskId(@Param("riskId") String riskId);

    /**
     * 根据用户ID查询社交风险列表
     * @param userId 用户ID
     * @param socialType 社交类型
     * @param riskLevel 风险等级
     * @param page 页码
     * @param size 每页大小
     * @return 社交风险列表
     */
    List<SocialRisk> selectByUserId(@Param("userId") Long userId, @Param("socialType") Integer socialType, @Param("riskLevel") Integer riskLevel, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据社交对象ID查询社交风险列表
     * @param targetUserId 社交对象ID
     * @param socialType 社交类型
     * @param riskLevel 风险等级
     * @param page 页码
     * @param size 每页大小
     * @return 社交风险列表
     */
    List<SocialRisk> selectByTargetUserId(@Param("targetUserId") String targetUserId, @Param("socialType") Integer socialType, @Param("riskLevel") Integer riskLevel, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据时间范围查询社交风险列表
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param userId 用户ID
     * @param socialType 社交类型
     * @param riskLevel 风险等级
     * @return 社交风险列表
     */
    List<SocialRisk> selectByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("userId") Long userId, @Param("socialType") Integer socialType, @Param("riskLevel") Integer riskLevel);

    /**
     * 更新社交风险状态
     * @param id 社交风险ID
     * @param status 状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 更新社交风险分数
     * @param id 社交风险ID
     * @param riskScore 风险分数
     * @param riskLevel 风险等级
     * @return 影响行数
     */
    int updateRiskScore(@Param("id") Long id, @Param("riskScore") Double riskScore, @Param("riskLevel") Integer riskLevel);

    /**
     * 统计社交风险数量
     * @param userId 用户ID
     * @param socialType 社交类型
     * @param riskLevel 风险等级
     * @return 社交风险数量
     */
    Integer countBySocialType(@Param("userId") Long userId, @Param("socialType") Integer socialType, @Param("riskLevel") Integer riskLevel);

    /**
     * 获取最近的社交风险
     * @param limit 数量限制
     * @param userId 用户ID
     * @param socialType 社交类型
     * @param riskLevel 风险等级
     * @return 社交风险列表
     */
    List<SocialRisk> selectRecentSocialRisks(@Param("limit") Integer limit, @Param("userId") Long userId, @Param("socialType") Integer socialType, @Param("riskLevel") Integer riskLevel);

    /**
     * 批量插入社交风险
     * @param socialRisks 社交风险列表
     * @return 插入成功数量
     */
    int batchInsert(@Param("socialRisks") List<SocialRisk> socialRisks);

    /**
     * 批量更新风险等级
     * @param ids 社交风险ID列表
     * @param riskLevel 风险等级
     * @return 影响行数
     */
    int batchUpdateRiskLevel(@Param("ids") List<Long> ids, @Param("riskLevel") Integer riskLevel);
}
