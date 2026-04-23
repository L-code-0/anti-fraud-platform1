package com.anti.fraud.modules.share.service;

import java.util.Map;

/**
 * 社交分享服务接口
 */
public interface ShareService {

    /**
     * 生成分享链接
     * @param userId 用户ID
     * @param type 分享类型：1-知识分享，2-勋章分享，3-排行榜分享，4-预警信息分享
     * @param targetId 目标ID（如知识ID、勋章ID等）
     * @return 分享链接
     */
    String generateShareLink(Long userId, Integer type, Long targetId);

    /**
     * 处理分享回调
     * @param shareCode 分享码
     * @param platform 分享平台
     * @return 分享结果
     */
    Map<String, Object> handleShareCallback(String shareCode, String platform);

    /**
     * 验证分享码
     * @param shareCode 分享码
     * @return 验证结果
     */
    Map<String, Object> verifyShareCode(String shareCode);

    /**
     * 领取分享奖励
     * @param shareCode 分享码
     * @return 领取结果
     */
    Map<String, Object> claimShareReward(String shareCode);

    /**
     * 获取用户分享记录
     * @param userId 用户ID
     * @param limit 数量限制
     * @return 分享记录列表
     */
    Map<String, Object> getUserShareRecords(Long userId, Integer limit);

    /**
     * 获取分享统计
     * @param userId 用户ID
     * @return 分享统计信息
     */
    Map<String, Object> getShareStatistics(Long userId);

    /**
     * 检查分享奖励
     * @param userId 用户ID
     * @param type 分享类型
     * @param targetId 目标ID
     * @return 是否可以获得奖励
     */
    boolean checkShareReward(Long userId, Integer type, Long targetId);

    /**
     * 批量生成分享链接
     * @param userId 用户ID
     * @param shareData 分享数据
     * @return 分享链接列表
     */
    Map<String, String> batchGenerateShareLinks(Long userId, Map<Integer, Long> shareData);

    /**
     * 生成分享海报
     * @param userId 用户ID
     * @param type 分享类型
     * @param targetId 目标ID
     * @return 海报图片URL
     */
    String generateSharePoster(Long userId, Integer type, Long targetId);

    /**
     * 增加分享计数
     * @param targetId 目标ID
     * @param type 分享类型
     * @return 是否成功
     */
    boolean incrementShareCount(Long targetId, Integer type);

    /**
     * 获取分享平台列表
     * @return 分享平台列表
     */
    Map<String, Object> getSharePlatforms();
}
