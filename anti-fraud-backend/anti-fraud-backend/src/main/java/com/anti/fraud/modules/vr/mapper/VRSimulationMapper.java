package com.anti.fraud.modules.vr.mapper;

import com.anti.fraud.modules.vr.entity.VRSimulation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * VR演练Mapper
 */
@Mapper
public interface VRSimulationMapper extends BaseMapper<VRSimulation> {

    /**
     * 根据类型查询VR演练
     * @param type 演练类型
     * @param page 页码
     * @param size 每页大小
     * @return VR演练列表
     */
    List<VRSimulation> selectByType(@Param("type") Integer type, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据难度查询VR演练
     * @param difficulty 难度等级
     * @param page 页码
     * @param size 每页大小
     * @return VR演练列表
     */
    List<VRSimulation> selectByDifficulty(@Param("difficulty") Integer difficulty, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据场景ID查询VR演练
     * @param scenarioId 场景ID
     * @param page 页码
     * @param size 每页大小
     * @return VR演练列表
     */
    List<VRSimulation> selectByScenarioId(@Param("scenarioId") Long scenarioId, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 按类型统计VR演练数量
     * @return 类型统计
     */
    List<Map<String, Object>> selectTypeStats();

    /**
     * 按难度统计VR演练数量
     * @return 难度统计
     */
    List<Map<String, Object>> selectDifficultyStats();

    /**
     * 按场景统计VR演练数量
     * @return 场景统计
     */
    List<Map<String, Object>> selectScenarioStats();

    /**
     * 增加浏览量
     * @param id VR演练ID
     */
    void incrementViewCount(@Param("id") Long id);

    /**
     * 增加参与人数
     * @param id VR演练ID
     */
    void incrementParticipantCount(@Param("id") Long id);

    /**
     * 更新平均评分
     * @param id VR演练ID
     * @param score 新评分
     */
    void updateAverageScore(@Param("id") Long id, @Param("score") Double score);
}
