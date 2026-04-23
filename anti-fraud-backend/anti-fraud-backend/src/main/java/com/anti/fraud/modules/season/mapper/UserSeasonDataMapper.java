package com.anti.fraud.modules.season.mapper;

import com.anti.fraud.modules.season.entity.UserSeasonData;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户赛季数据Mapper
 */
@Mapper
public interface UserSeasonDataMapper extends BaseMapper<UserSeasonData> {

    /**
     * 根据用户ID和赛季ID查询用户赛季数据
     * @param userId 用户ID
     * @param seasonId 赛季ID
     * @return 用户赛季数据
     */
    UserSeasonData selectByUserIdAndSeasonId(@Param("userId") Long userId, @Param("seasonId") Long seasonId);

    /**
     * 根据用户ID查询赛季数据列表
     * @param userId 用户ID
     * @param status 状态
     * @return 赛季数据列表
     */
    List<UserSeasonData> selectByUserId(@Param("userId") Long userId, @Param("status") Integer status);

    /**
     * 根据赛季ID查询用户赛季数据列表
     * @param seasonId 赛季ID
     * @param limit 数量限制
     * @return 用户赛季数据列表
     */
    List<UserSeasonData> selectBySeasonId(@Param("seasonId") Long seasonId, @Param("limit") Integer limit);

    /**
     * 更新用户赛季积分
     * @param id 记录ID
     * @param seasonPoints 赛季积分
     * @return 影响行数
     */
    int updateSeasonPoints(@Param("id") Long id, @Param("seasonPoints") Integer seasonPoints);

    /**
     * 更新用户赛季排名
     * @param id 记录ID
     * @param seasonRank 赛季排名
     * @return 影响行数
     */
    int updateSeasonRank(@Param("id") Long id, @Param("seasonRank") Integer seasonRank);

    /**
     * 更新用户赛季状态
     * @param id 记录ID
     * @param status 状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 批量更新用户赛季状态
     * @param ids 记录ID列表
     * @param status 状态
     * @return 影响行数
     */
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") Integer status);

    /**
     * 统计用户赛季数据
     * @param userId 用户ID
     * @param seasonId 赛季ID
     * @return 统计结果
     */
    UserSeasonData countUserSeasonData(@Param("userId") Long userId, @Param("seasonId") Long seasonId);

    /**
     * 查询赛季排行榜
     * @param seasonId 赛季ID
     * @param limit 数量限制
     * @return 赛季排行榜
     */
    List<UserSeasonData> selectSeasonRanking(@Param("seasonId") Long seasonId, @Param("limit") Integer limit);
}
