package com.anti.fraud.modules.case.service;

import com.anti.fraud.modules.case.entity.Case;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 案例服务接口
 */
public interface CaseService extends IService<Case> {

    /**
     * 新增案例
     * @param caseInfo 案例信息
     * @return 是否成功
     */
    boolean addCase(Case caseInfo);

    /**
     * 更新案例
     * @param caseInfo 案例信息
     * @return 是否成功
     */
    boolean updateCase(Case caseInfo);

    /**
     * 删除案例
     * @param id 案例ID
     * @return 是否成功
     */
    boolean deleteCase(Long id);

    /**
     * 获取案例详情
     * @param id 案例ID
     * @return 案例详情
     */
    Case getCaseById(Long id);

    /**
     * 分页查询案例
     * @param params 查询参数
     * @param page 页码
     * @param size 每页大小
     * @return 案例列表和总数
     */
    Map<String, Object> getCaseList(Map<String, Object> params, int page, int size);

    /**
     * 根据案例类型查询案例
     * @param type 案例类型
     * @param page 页码
     * @param size 每页大小
     * @return 案例列表和总数
     */
    Map<String, Object> getCasesByType(Integer type, int page, int size);

    /**
     * 增加浏览量
     * @param id 案例ID
     * @return 是否成功
     */
    boolean increaseViewCount(Long id);

    /**
     * 增加点赞数
     * @param id 案例ID
     * @return 是否成功
     */
    boolean increaseLikeCount(Long id);

    /**
     * 增加评论数
     * @param id 案例ID
     * @return 是否成功
     */
    boolean increaseCommentCount(Long id);

    /**
     * 增加分享数
     * @param id 案例ID
     * @return 是否成功
     */
    boolean increaseShareCount(Long id);

    /**
     * 获取热门案例
     * @param limit 数量限制
     * @return 热门案例列表
     */
    List<Case> getHotCases(int limit);

    /**
     * 获取最新案例
     * @param limit 数量限制
     * @return 最新案例列表
     */
    List<Case> getLatestCases(int limit);

    /**
     * 统计案例信息
     * @return 统计信息
     */
    Map<String, Object> getCaseStats();

    /**
     * 搜索案例
     * @param keyword 关键词
     * @param page 页码
     * @param size 每页大小
     * @return 搜索结果
     */
    Map<String, Object> searchCases(String keyword, int page, int size);

    /**
     * 批量导入案例
     * @param cases 案例列表
     * @return 导入结果
     */
    List<Case> batchImportCases(List<Case> cases);
}
