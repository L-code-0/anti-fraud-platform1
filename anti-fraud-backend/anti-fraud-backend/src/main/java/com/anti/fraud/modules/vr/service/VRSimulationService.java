package com.anti.fraud.modules.vr.service;

import com.anti.fraud.modules.vr.entity.VRSimulation;
import com.anti.fraud.modules.vr.entity.VRSimulationRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * VR演练服务接口
 */
public interface VRSimulationService extends IService<VRSimulation> {

    /**
     * 创建VR演练
     * @param vrSimulation VR演练信息
     * @return 是否成功
     */
    boolean createVRSimulation(VRSimulation vrSimulation);

    /**
     * 更新VR演练
     * @param vrSimulation VR演练信息
     * @return 是否成功
     */
    boolean updateVRSimulation(VRSimulation vrSimulation);

    /**
     * 删除VR演练
     * @param id VR演练ID
     * @return 是否成功
     */
    boolean deleteVRSimulation(Long id);

    /**
     * 获取VR演练详情
     * @param id VR演练ID
     * @return VR演练详情
     */
    VRSimulation getVRSimulationById(Long id);

    /**
     * 分页查询VR演练
     * @param params 查询参数
     * @param page 页码
     * @param size 每页大小
     * @return VR演练列表和总数
     */
    Map<String, Object> getVRSimulationList(Map<String, Object> params, int page, int size);

    /**
     * 根据类型查询VR演练
     * @param type 演练类型
     * @param page 页码
     * @param size 每页大小
     * @return VR演练列表和总数
     */
    Map<String, Object> getVRSimulationsByType(Integer type, int page, int size);

    /**
     * 根据难度查询VR演练
     * @param difficulty 难度等级
     * @param page 页码
     * @param size 每页大小
     * @return VR演练列表和总数
     */
    Map<String, Object> getVRSimulationsByDifficulty(Integer difficulty, int page, int size);

    /**
     * 根据场景查询VR演练
     * @param scenarioId 场景ID
     * @param page 页码
     * @param size 每页大小
     * @return VR演练列表和总数
     */
    Map<String, Object> getVRSimulationsByScenarioId(Long scenarioId, int page, int size);

    /**
     * 统计VR演练信息
     * @return 统计信息
     */
    Map<String, Object> getVRSimulationStats();

    /**
     * 增加浏览量
     * @param id VR演练ID
     * @return 是否成功
     */
    boolean incrementViewCount(Long id);

    /**
     * 增加参与人数
     * @param id VR演练ID
     * @return 是否成功
     */
    boolean incrementParticipantCount(Long id);

    /**
     * 更新平均评分
     * @param id VR演练ID
     * @param score 新评分
     * @return 是否成功
     */
    boolean updateAverageScore(Long id, Double score);

    /**
     * 开始VR演练
     * @param simulationId 演练ID
     * @param userId 用户ID
     * @param username 用户名
     * @return 演练记录ID
     */
    Long startVRSimulation(Long simulationId, Long userId, String username);

    /**
     * 结束VR演练
     * @param recordId 演练记录ID
     * @param score 得分
     * @param correctRate 正确率
     * @param operationRecord 操作记录
     * @param evaluationResult 评估结果
     * @return 是否成功
     */
    boolean endVRSimulation(Long recordId, Integer score, Double correctRate, String operationRecord, String evaluationResult);

    /**
     * 获取VR演练记录
     * @param id 记录ID
     * @return 演练记录
     */
    VRSimulationRecord getVRSimulationRecordById(Long id);

    /**
     * 根据用户ID查询VR演练记录
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 演练记录列表和总数
     */
    Map<String, Object> getVRSimulationRecordsByUserId(Long userId, int page, int size);

    /**
     * 根据演练ID查询VR演练记录
     * @param simulationId 演练ID
     * @param page 页码
     * @param size 每页大小
     * @return 演练记录列表和总数
     */
    Map<String, Object> getVRSimulationRecordsBySimulationId(Long simulationId, int page, int size);

    /**
     * 获取用户VR演练统计信息
     * @param userId 用户ID
     * @return 统计信息
     */
    Map<String, Object> getUserVRSimulationStats(Long userId);

    /**
     * 获取演练VR演练统计信息
     * @param simulationId 演练ID
     * @return 统计信息
     */
    Map<String, Object> getSimulationVRSimulationStats(Long simulationId);

    /**
     * 获取VR演练趋势
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 趋势数据
     */
    List<Map<String, Object>> getVRSimulationTrend(String startDate, String endDate);
}
