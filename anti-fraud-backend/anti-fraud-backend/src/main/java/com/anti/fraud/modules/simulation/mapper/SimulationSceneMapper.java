package com.anti.fraud.modules.simulation.mapper;

import com.anti.fraud.modules.simulation.entity.SimulationScene;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SimulationSceneMapper extends BaseMapper<SimulationScene> {

    /**
     * 统计场景总数
     */
    Integer countAll();

    /**
     * 统计启用场景数量
     */
    Integer countActive();

    /**
     * 更新场景统计数据
     */
    void updateStatistics(@Param("sceneId") Long sceneId);

    /**
     * 获取热门场景
     */
    List<SimulationScene> selectHotScenes(@Param("limit") Integer limit);

    /**
     * 根据类型获取场景
     */
    List<SimulationScene> selectByType(@Param("sceneType") String sceneType);
}
