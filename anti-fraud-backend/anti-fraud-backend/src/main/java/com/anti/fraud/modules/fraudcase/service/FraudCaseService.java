package com.anti.fraud.modules.fraudcase.service;

import com.anti.fraud.modules.fraudcase.entity.FraudCase;
import java.util.List;
import java.util.Map;

public interface FraudCaseService {

    /**
     * 创建案例
     * @param fraudCase 案例信息
     * @return 是否成功
     */
    boolean createCase(FraudCase fraudCase);

    /**
     * 更新案例
     * @param fraudCase 案例信息
     * @return 是否成功
     */
    boolean updateCase(FraudCase fraudCase);

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
    FraudCase getCaseById(Long id);

    /**
     * 获取案例列表
     * @param category 分类
     * @param caseType 案例类型
     * @param difficulty 难度
     * @param page 页码
     * @param size 每页大小
     * @return 案例列表
     */
    List<FraudCase> getCaseList(String category, String caseType, String difficulty, int page, int size);

    /**
     * 搜索案例
     * @param keyword 关键词
     * @param page 页码
     * @param size 每页大小
     * @return 案例列表
     */
    List<FraudCase> searchCases(String keyword, int page, int size);

    /**
     * 增加案例浏览次数
     * @param id 案例ID
     * @return 是否成功
     */
    boolean increaseViewCount(Long id);

    /**
     * 增加案例点赞次数
     * @param id 案例ID
     * @return 是否成功
     */
    boolean increaseLikeCount(Long id);

    /**
     * 增加案例评论次数
     * @param id 案例ID
     * @return 是否成功
     */
    boolean increaseCommentCount(Long id);

    /**
     * 获取热门案例
     * @param count 数量
     * @return 热门案例列表
     */
    List<FraudCase> getHotCases(int count);

    /**
     * 获取推荐案例
     * @param userId 用户ID
     * @param count 数量
     * @return 推荐案例列表
     */
    List<FraudCase> getRecommendedCases(Long userId, int count);

    /**
     * 获取案例统计信息
     * @return 统计信息
     */
    Map<String, Object> getCaseStats();

    /**
     * 导入案例
     * @param cases 案例列表
     * @return 导入结果
     */
    Map<String, Object> importCases(List<FraudCase> cases);

    /**
     * 导出案例
     * @param ids 案例ID列表
     * @return 导出数据
     */
    Map<String, Object> exportCases(List<Long> ids);
}
