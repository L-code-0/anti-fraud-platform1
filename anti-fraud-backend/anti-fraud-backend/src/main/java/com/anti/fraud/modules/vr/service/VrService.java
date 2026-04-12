package com.anti.fraud.modules.vr.service;

import com.anti.fraud.modules.vr.entity.VrScene;
import com.anti.fraud.modules.vr.entity.VrDrillRecord;
import java.util.List;
import java.util.Map;

public interface VrService {

    /**
     * 创建VR场景
     * @param scene VR场景信息
     * @return 是否成功
     */
    boolean createVrScene(VrScene scene);

    /**
     * 更新VR场景
     * @param scene VR场景信息
     * @return 是否成功
     */
    boolean updateVrScene(VrScene scene);

    /**
     * 删除VR场景
     * @param id 场景ID
     * @return 是否成功
     */
    boolean deleteVrScene(Long id);

    /**
     * 获取VR场景详情
     * @param id 场景ID
     * @return 场景详情
     */
    VrScene getVrSceneById(Long id);

    /**
     * 获取VR场景列表
     * @param sceneType 场景类型
     * @param difficulty 难度
     * @param page 页码
     * @param size 每页大小
     * @return 场景列表
     */
    List<VrScene> getVrSceneList(String sceneType, Integer difficulty, int page, int size);

    /**
     * 开始VR演练
     * @param sceneId 场景ID
     * @param userId 用户ID
     * @param userName 用户名
     * @param deviceInfo 设备信息
     * @return 演练信息
     */
    Map<String, Object> startVrDrill(Long sceneId, Long userId, String userName, String deviceInfo);

    /**
     * 提交VR演练结果
     * @param record 演练记录
     * @return 提交结果
     */
    Map<String, Object> submitVrDrillResult(VrDrillRecord record);

    /**
     * 获取用户VR演练历史
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 演练历史
     */
    List<VrDrillRecord> getUserVrDrillHistory(Long userId, int page, int size);

    /**
     * 获取VR场景统计信息
     * @param sceneId 场景ID
     * @return 统计信息
     */
    Map<String, Object> getVrSceneStats(Long sceneId);

    /**
     * 获取用户VR演练统计信息
     * @param userId 用户ID
     * @return 统计信息
     */
    Map<String, Object> getUserVrDrillStats(Long userId);

    /**
     * 搜索VR场景
     * @param keyword 关键词
     * @param page 页码
     * @param size 每页大小
     * @return 场景列表
     */
    List<VrScene> searchVrScenes(String keyword, int page, int size);
}
