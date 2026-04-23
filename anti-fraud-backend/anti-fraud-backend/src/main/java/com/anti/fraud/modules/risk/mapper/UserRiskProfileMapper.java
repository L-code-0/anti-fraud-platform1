package com.anti.fraud.modules.risk.mapper;

import com.anti.fraud.modules.risk.entity.UserRiskProfile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户风险画像Mapper
 */
@Mapper
public interface UserRiskProfileMapper extends BaseMapper<UserRiskProfile> {

    /**
     * 根据用户ID查询用户风险画像
     * @param userId 用户ID
     * @return 用户风险画像
     */
    UserRiskProfile selectByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID查询用户风险画像列表
     * @param userId 用户ID
     * @param riskLevel 风险等级
     * @param page 页码
     * @param size 每页大小
     * @return 用户风险画像列表
     */
    List<UserRiskProfile> selectByUserIdWithPaging(@Param("userId") Long userId, @Param("riskLevel") Integer riskLevel, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据风险等级查询用户风险画像列表
     * @param riskLevel 风险等级
     * @param page 页码
     * @param size 每页大小
     * @return 用户风险画像列表
     */
    List<UserRiskProfile> selectByRiskLevel(@Param("riskLevel") Integer riskLevel, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据时间范围查询用户风险画像列表
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param userId 用户ID
     * @param riskLevel 风险等级
     * @return 用户风险画像列表
     */
    List<UserRiskProfile> selectByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("userId") Long userId, @Param("riskLevel") Integer riskLevel);

    /**
     * 更新用户风险画像
     * @param userRiskProfile 用户风险画像
     * @return 影响行数
     */
    int updateByUserId(UserRiskProfile userRiskProfile);

    /**
     * 更新用户风险画像状态
     * @param userId 用户ID
     * @param status 状态
     * @return 影响行数
     */
    int updateStatus(@Param("userId") Long userId, @Param("status") Integer status);

    /**
     * 统计用户风险画像数量
     * @param riskLevel 风险等级
     * @return 用户风险画像数量
     */
    Integer countByRiskLevel(@Param("riskLevel") Integer riskLevel);

    /**
     * 获取高风险用户列表
     * @param limit 数量限制
     * @return 高风险用户列表
     */
    List<UserRiskProfile> selectHighRiskUsers(@Param("limit") Integer limit);

    /**
     * 获取用户风险趋势
     * @param userId 用户ID
     * @param days 天数
     * @return 用户风险趋势
     */
    List<UserRiskProfile> selectUserRiskTrend(@Param("userId") Long userId, @Param("days") Integer days);

    /**
     * 批量插入用户风险画像
     * @param profiles 用户风险画像列表
     * @return 插入成功数量
     */
    int batchInsert(@Param("profiles") List<UserRiskProfile> profiles);

    /**
     * 批量更新用户风险画像
     * @param profiles 用户风险画像列表
     * @return 影响行数
     */
    int batchUpdate(@Param("profiles") List<UserRiskProfile> profiles);
}
