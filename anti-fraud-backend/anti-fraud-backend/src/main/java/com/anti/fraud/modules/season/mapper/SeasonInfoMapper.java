package com.anti.fraud.modules.season.mapper;

import com.anti.fraud.modules.season.entity.SeasonInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 赛季信息Mapper
 */
@Mapper
public interface SeasonInfoMapper extends BaseMapper<SeasonInfo> {

    /**
     * 查询当前赛季
     * @param currentTime 当前时间
     * @return 当前赛季
     */
    SeasonInfo selectCurrentSeason(@Param("currentTime") LocalDateTime currentTime);

    /**
     * 查询历史赛季列表
     * @param currentTime 当前时间
     * @param limit 数量限制
     * @return 历史赛季列表
     */
    List<SeasonInfo> selectHistorySeasons(@Param("currentTime") LocalDateTime currentTime, @Param("limit") Integer limit);

    /**
     * 根据状态查询赛季列表
     * @param status 赛季状态
     * @return 赛季列表
     */
    List<SeasonInfo> selectByStatus(@Param("status") Integer status);

    /**
     * 更新赛季状态
     * @param id 赛季ID
     * @param status 赛季状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 批量更新赛季状态
     * @param ids 赛季ID列表
     * @param status 赛季状态
     * @return 影响行数
     */
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") Integer status);

    /**
     * 查询赛季详情
     * @param id 赛季ID
     * @return 赛季详情
     */
    SeasonInfo selectById(@Param("id") Long id);

    /**
     * 批量删除赛季
     * @param ids 赛季ID列表
     * @return 影响行数
     */
    int batchDelete(@Param("ids") List<Long> ids);
}
