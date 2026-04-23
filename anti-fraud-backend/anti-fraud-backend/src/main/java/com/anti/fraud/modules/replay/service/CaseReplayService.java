package com.anti.fraud.modules.replay.service;

import com.anti.fraud.modules.replay.entity.CaseReplay;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 案例回放服务接口
 */
public interface CaseReplayService extends IService<CaseReplay> {

    /**
     * 新增案例回放
     * @param caseReplay 案例回放信息
     * @return 是否成功
     */
    boolean addCaseReplay(CaseReplay caseReplay);

    /**
     * 更新案例回放
     * @param caseReplay 案例回放信息
     * @return 是否成功
     */
    boolean updateCaseReplay(CaseReplay caseReplay);

    /**
     * 删除案例回放
     * @param id 案例回放ID
     * @return 是否成功
     */
    boolean deleteCaseReplay(Long id);

    /**
     * 获取案例回放详情
     * @param id 案例回放ID
     * @return 案例回放详情
     */
    CaseReplay getCaseReplayById(Long id);

    /**
     * 分页查询案例回放
     * @param params 查询参数
     * @param page 页码
     * @param size 每页大小
     * @return 案例回放列表和总数
     */
    Map<String, Object> getCaseReplayList(Map<String, Object> params, int page, int size);

    /**
     * 根据案例ID查询案例回放
     * @param caseId 案例ID
     * @return 案例回放列表
     */
    List<CaseReplay> getCaseReplaysByCaseId(Long caseId);

    /**
     * 增加浏览量
     * @param id 案例回放ID
     * @return 是否成功
     */
    boolean increaseViewCount(Long id);

    /**
     * 增加点赞数
     * @param id 案例回放ID
     * @return 是否成功
     */
    boolean increaseLikeCount(Long id);

    /**
     * 增加分享数
     * @param id 案例回放ID
     * @return 是否成功
     */
    boolean increaseShareCount(Long id);

    /**
     * 获取热门案例回放
     * @param limit 数量限制
     * @return 热门案例回放列表
     */
    List<CaseReplay> getHotReplays(int limit);

    /**
     * 获取最新案例回放
     * @param limit 数量限制
     * @return 最新案例回放列表
     */
    List<CaseReplay> getLatestReplays(int limit);

    /**
     * 统计案例回放信息
     * @return 统计信息
     */
    Map<String, Object> getCaseReplayStats();

    /**
     * 开始案例回放
     * @param id 案例回放ID
     * @return 回放信息
     */
    Map<String, Object> startReplay(Long id);

    /**
     * 获取回放进度
     * @param id 案例回放ID
     * @param userId 用户ID
     * @return 回放进度
     */
    Map<String, Object> getReplayProgress(Long id, Long userId);

    /**
     * 更新回放进度
     * @param id 案例回放ID
     * @param userId 用户ID
     * @param progress 进度（秒）
     * @return 是否成功
     */
    boolean updateReplayProgress(Long id, Long userId, int progress);
}
