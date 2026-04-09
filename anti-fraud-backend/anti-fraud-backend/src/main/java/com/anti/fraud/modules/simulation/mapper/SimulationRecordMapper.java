package com.anti.fraud.modules.simulation.mapper;

import com.anti.fraud.modules.simulation.entity.SimulationRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface SimulationRecordMapper extends BaseMapper<SimulationRecord> {

    /**
     * 统计用户演练次数
     */
    Integer countByUserId(@Param("userId") Long userId);

    /**
     * 统计用户平均得分
     */
    BigDecimal avgScoreByUserId(@Param("userId") Long userId);

    /**
     * 统计用户通过次数
     */
    Integer countPassedByUserId(@Param("userId") Long userId);

    /**
     * 获取用户演练记录
     */
    List<SimulationRecord> selectByUserId(@Param("userId") Long userId);

    /**
     * 获取场景演练记录
     */
    List<SimulationRecord> selectBySceneId(@Param("sceneId") Long sceneId);

    /**
     * 获取用户某场景的最高分记录
     */
    SimulationRecord selectBestRecord(@Param("userId") Long userId, @Param("sceneId") Long sceneId);

    /**
     * 获取排行榜
     */
    List<Map<String, Object>> selectRanking(@Param("limit") Integer limit);

    /**
     * 统计今日演练人数
     */
    Integer countTodayPlayers();

    /**
     * 统计今日演练次数
     */
    Integer countTodayPlays();
}
