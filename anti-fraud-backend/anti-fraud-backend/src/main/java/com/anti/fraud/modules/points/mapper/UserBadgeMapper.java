package com.anti.fraud.modules.points.mapper;

import com.anti.fraud.modules.points.entity.UserBadge;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户勋章Mapper
 */
@Mapper
public interface UserBadgeMapper extends BaseMapper<UserBadge> {

    /**
     * 根据用户ID查询已获得的勋章
     * @param userId 用户ID
     * @return 用户勋章列表
     */
    List<UserBadge> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID和勋章ID查询用户是否已获得该勋章
     * @param userId 用户ID
     * @param badgeId 勋章ID
     * @return 用户勋章
     */
    UserBadge selectByUserIdAndBadgeId(@Param("userId") Long userId, @Param("badgeId") Long badgeId);

    /**
     * 统计用户已获得的勋章数量
     * @param userId 用户ID
     * @return 勋章数量
     */
    Integer countByUserId(@Param("userId") Long userId);

    /**
     * 根据勋章ID查询获得该勋章的用户数量
     * @param badgeId 勋章ID
     * @return 用户数量
     */
    Integer countByBadgeId(@Param("badgeId") Long badgeId);

    /**
     * 批量插入用户勋章
     * @param userBadges 用户勋章列表
     * @return 插入成功数量
     */
    int batchInsert(@Param("userBadges") List<UserBadge> userBadges);

    /**
     * 批量删除用户勋章
     * @param userId 用户ID
     * @param badgeIds 勋章ID列表
     * @return 删除成功数量
     */
    int batchDelete(@Param("userId") Long userId, @Param("badgeIds") List<Long> badgeIds);

    /**
     * 按获得时间倒序查询用户勋章
     * @param userId 用户ID
     * @param limit 数量限制
     * @return 用户勋章列表
     */
    List<UserBadge> selectRecentByUserId(@Param("userId") Long userId, @Param("limit") Integer limit);
}
