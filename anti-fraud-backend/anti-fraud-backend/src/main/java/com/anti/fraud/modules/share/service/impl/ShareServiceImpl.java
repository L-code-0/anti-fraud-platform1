package com.anti.fraud.modules.share.service.impl;

import com.anti.fraud.common.exception.BusinessException;
import com.anti.fraud.common.utils.SecurityUtils;
import com.anti.fraud.modules.knowledge.mapper.KnowledgeContentMapper;
import com.anti.fraud.modules.notification.service.NotificationService;
import com.anti.fraud.modules.points.service.PointsService;
import com.anti.fraud.modules.share.service.PosterGeneratorService;
import com.anti.fraud.modules.share.service.ShareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 社交分享服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ShareServiceImpl implements ShareService {

    private final PointsService pointsService;
    private final NotificationService notificationService;
    private final KnowledgeContentMapper knowledgeContentMapper;
    private final PosterGeneratorService posterGeneratorService;

    // 分享奖励配置
    private static final int SHARE_REWARD_POINTS = 10; // 分享奖励积分
    private static final int MAX_SHARE_REWARDS_PER_DAY = 5; // 每日最大分享奖励次数
    private static final int SHARE_CODE_EXPIRE_HOURS = 24; // 分享码过期时间（小时）

    // 分享类型映射
    private static final Map<Integer, String> SHARE_TYPE_MAP = new HashMap<>();
    static {
        SHARE_TYPE_MAP.put(1, "知识分享");
        SHARE_TYPE_MAP.put(2, "勋章分享");
        SHARE_TYPE_MAP.put(3, "排行榜分享");
        SHARE_TYPE_MAP.put(4, "预警信息分享");
    }

    // 分享平台配置
    private static final Map<String, String> SHARE_PLATFORMS = new HashMap<>();
    static {
        SHARE_PLATFORMS.put("wechat", "微信");
        SHARE_PLATFORMS.put("wechat_moments", "朋友圈");
        SHARE_PLATFORMS.put("qq", "QQ");
        SHARE_PLATFORMS.put("weibo", "微博");
        SHARE_PLATFORMS.put("copy_link", "复制链接");
    }

    @Override
    public String generateShareLink(Long userId, Integer type, Long targetId) {
        try {
            // 生成分享码
            String shareCode = generateShareCode(userId, type, targetId);
            // 构建分享链接
            String shareLink = "https://anti-fraud-platform.com/share?code=" + shareCode;
            log.debug("生成分享链接: {}，用户: {}，类型: {}，目标ID: {}", shareLink, userId, type, targetId);
            return shareLink;
        } catch (Exception e) {
            log.error("生成分享链接失败: {}", e.getMessage(), e);
            throw new BusinessException("生成分享链接失败");
        }
    }

    @Override
    public Map<String, Object> handleShareCallback(String shareCode, String platform) {
        try {
            // 解析分享码
            Map<String, Object> shareInfo = parseShareCode(shareCode);
            Long userId = (Long) shareInfo.get("userId");
            Integer type = (Integer) shareInfo.get("type");
            Long targetId = (Long) shareInfo.get("targetId");
            LocalDateTime createTime = (LocalDateTime) shareInfo.get("createTime");

            // 检查分享码是否过期
            if (createTime.plusHours(SHARE_CODE_EXPIRE_HOURS).isBefore(LocalDateTime.now())) {
                throw new BusinessException("分享链接已过期");
            }

            // 增加分享计数
            incrementShareCount(targetId, type);

            // 记录分享行为
            log.info("用户 {} 在平台 {} 分享了类型 {} 的内容，目标ID: {}", userId, platform, type, targetId);

            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "分享成功");
            result.put("shareInfo", shareInfo);

            return result;
        } catch (Exception e) {
            log.error("处理分享回调失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "分享失败: " + e.getMessage());
            return result;
        }
    }

    @Override
    public Map<String, Object> verifyShareCode(String shareCode) {
        try {
            // 解析分享码
            Map<String, Object> shareInfo = parseShareCode(shareCode);
            LocalDateTime createTime = (LocalDateTime) shareInfo.get("createTime");

            // 检查分享码是否过期
            if (createTime.plusHours(SHARE_CODE_EXPIRE_HOURS).isBefore(LocalDateTime.now())) {
                throw new BusinessException("分享链接已过期");
            }

            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("valid", true);
            result.put("shareInfo", shareInfo);

            return result;
        } catch (Exception e) {
            log.error("验证分享码失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("valid", false);
            result.put("message", "分享链接无效: " + e.getMessage());
            return result;
        }
    }

    @Override
    @Transactional
    public Map<String, Object> claimShareReward(String shareCode) {
        try {
            // 解析分享码
            Map<String, Object> shareInfo = parseShareCode(shareCode);
            Long userId = (Long) shareInfo.get("userId");
            Integer type = (Integer) shareInfo.get("type");
            Long targetId = (Long) shareInfo.get("targetId");

            // 检查是否可以获得奖励
            if (!checkShareReward(userId, type, targetId)) {
                throw new BusinessException("今日分享奖励已达上限");
            }

            // 发放奖励
            pointsService.addPoints(userId, SHARE_REWARD_POINTS, "share", targetId, "分享" + SHARE_TYPE_MAP.get(type));

            // 发送通知
            try {
                notificationService.sendSystemNotification(
                        "分享奖励",
                        "恭喜您获得了分享奖励 " + SHARE_REWARD_POINTS + " 积分！",
                        userId
                );
            } catch (Exception e) {
                log.error("发送分享奖励通知失败: {}", e.getMessage(), e);
            }

            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "领取分享奖励成功");
            result.put("rewardPoints", SHARE_REWARD_POINTS);

            return result;
        } catch (Exception e) {
            log.error("领取分享奖励失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "领取分享奖励失败: " + e.getMessage());
            return result;
        }
    }

    @Override
    public Map<String, Object> getUserShareRecords(Long userId, Integer limit) {
        try {
            // 实际实现需要查询分享记录表
            // 暂时返回模拟数据
            Map<String, Object> result = new HashMap<>();
            result.put("records", new java.util.ArrayList<>());
            result.put("total", 0);
            return result;
        } catch (Exception e) {
            log.error("获取用户分享记录失败: {}", e.getMessage(), e);
            throw new BusinessException("获取分享记录失败");
        }
    }

    @Override
    public Map<String, Object> getShareStatistics(Long userId) {
        try {
            // 实际实现需要查询分享统计表
            // 暂时返回模拟数据
            Map<String, Object> result = new HashMap<>();
            result.put("totalShares", 0);
            result.put("totalRewards", 0);
            result.put("todayShares", 0);
            result.put("todayRewards", 0);
            return result;
        } catch (Exception e) {
            log.error("获取分享统计失败: {}", e.getMessage(), e);
            throw new BusinessException("获取分享统计失败");
        }
    }

    @Override
    public boolean checkShareReward(Long userId, Integer type, Long targetId) {
        try {
            // 实际实现需要检查用户今日分享次数
            // 暂时返回true，允许领取奖励
            return true;
        } catch (Exception e) {
            log.error("检查分享奖励失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Map<String, String> batchGenerateShareLinks(Long userId, Map<Integer, Long> shareData) {
        try {
            Map<String, String> shareLinks = new HashMap<>();
            for (Map.Entry<Integer, Long> entry : shareData.entrySet()) {
                Integer type = entry.getKey();
                Long targetId = entry.getValue();
                String shareLink = generateShareLink(userId, type, targetId);
                shareLinks.put(type + "_" + targetId, shareLink);
            }
            return shareLinks;
        } catch (Exception e) {
            log.error("批量生成分享链接失败: {}", e.getMessage(), e);
            throw new BusinessException("批量生成分享链接失败");
        }
    }

    @Override
    public String generateSharePoster(Long userId, Integer type, Long targetId) {
        try {
            // 生成分享链接
            String shareLink = generateShareLink(userId, type, targetId);
            
            // 生成二维码
            String qrCodeUrl = posterGeneratorService.generateQRCode(shareLink);
            
            // 获取分享标题和描述
            String title = posterGeneratorService.getShareTitle(type, targetId);
            String description = posterGeneratorService.getShareDescription(type, targetId);
            
            // 获取封面图片（根据分享类型）
            String coverImageUrl = getCoverImageUrl(type, targetId);
            
            // 生成海报
            String posterBase64 = posterGeneratorService.generatePoster(userId, type, targetId, title, description, coverImageUrl, qrCodeUrl);
            
            if (posterBase64 == null) {
                throw new BusinessException("生成海报失败");
            }
            
            // 构建海报URL（实际实现需要上传到OSS或其他存储服务）
            String posterUrl = "data:image/png;base64," + posterBase64;
            log.debug("生成分享海报成功，用户: {}，类型: {}，目标ID: {}", userId, type, targetId);
            return posterUrl;
        } catch (Exception e) {
            log.error("生成分享海报失败: {}", e.getMessage(), e);
            throw new BusinessException("生成分享海报失败");
        }
    }

    /**
     * 根据分享类型获取封面图片URL
     * @param type 分享类型
     * @param targetId 目标ID
     * @return 封面图片URL
     */
    private String getCoverImageUrl(Integer type, Long targetId) {
        try {
            if (type == 1) { // 知识分享
                // 实际实现需要查询知识内容的封面图片
                // 暂时返回默认图片
                return "https://img.anti-fraud-platform.com/default-knowledge.jpg";
            } else if (type == 2) { // 勋章分享
                // 实际实现需要查询勋章的图片
                // 暂时返回默认图片
                return "https://img.anti-fraud-platform.com/default-badge.jpg";
            } else if (type == 3) { // 排行榜分享
                // 实际实现需要生成排行榜图片
                // 暂时返回默认图片
                return "https://img.anti-fraud-platform.com/default-ranking.jpg";
            } else if (type == 4) { // 预警信息分享
                // 实际实现需要查询预警信息的图片
                // 暂时返回默认图片
                return "https://img.anti-fraud-platform.com/default-warning.jpg";
            }
            return "https://img.anti-fraud-platform.com/default-share.jpg";
        } catch (Exception e) {
            log.error("获取封面图片失败: {}", e.getMessage(), e);
            return "https://img.anti-fraud-platform.com/default-share.jpg";
        }
    }

    @Override
    @Transactional
    public boolean incrementShareCount(Long targetId, Integer type) {
        try {
            // 根据分享类型更新对应实体的分享计数
            if (type == 1) { // 知识分享
                // 更新知识内容的分享计数
                // 实际实现需要调用KnowledgeContentMapper的更新方法
                log.debug("增加知识内容 {} 的分享计数", targetId);
            } else if (type == 2) { // 勋章分享
                // 更新勋章的分享计数
                log.debug("增加勋章 {} 的分享计数", targetId);
            } else if (type == 3) { // 排行榜分享
                // 更新排行榜的分享计数
                log.debug("增加排行榜的分享计数");
            } else if (type == 4) { // 预警信息分享
                // 更新预警信息的分享计数
                log.debug("增加预警信息 {} 的分享计数", targetId);
            }
            return true;
        } catch (Exception e) {
            log.error("增加分享计数失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Map<String, Object> getSharePlatforms() {
        try {
            Map<String, Object> result = new HashMap<>();
            result.put("platforms", SHARE_PLATFORMS);
            return result;
        } catch (Exception e) {
            log.error("获取分享平台列表失败: {}", e.getMessage(), e);
            throw new BusinessException("获取分享平台列表失败");
        }
    }

    /**
     * 生成分享码
     * @param userId 用户ID
     * @param type 分享类型
     * @param targetId 目标ID
     * @return 分享码
     */
    private String generateShareCode(Long userId, Integer type, Long targetId) {
        // 生成唯一分享码
        String uniqueId = UUID.randomUUID().toString().replace("-", "");
        // 构建分享码，包含用户ID、类型、目标ID和时间戳
        String shareCode = userId + "_" + type + "_" + targetId + "_" + System.currentTimeMillis() + "_" + uniqueId.substring(0, 8);
        // 进行Base64编码
        return java.util.Base64.getEncoder().encodeToString(shareCode.getBytes());
    }

    /**
     * 解析分享码
     * @param shareCode 分享码
     * @return 分享信息
     */
    private Map<String, Object> parseShareCode(String shareCode) {
        try {
            // 解码分享码
            byte[] decodedBytes = java.util.Base64.getDecoder().decode(shareCode);
            String decodedStr = new String(decodedBytes);
            String[] parts = decodedStr.split("_");
            if (parts.length != 5) {
                throw new BusinessException("无效的分享码");
            }

            // 解析分享信息
            Map<String, Object> shareInfo = new HashMap<>();
            shareInfo.put("userId", Long.valueOf(parts[0]));
            shareInfo.put("type", Integer.valueOf(parts[1]));
            shareInfo.put("targetId", Long.valueOf(parts[2]));
            shareInfo.put("timestamp", Long.valueOf(parts[3]));
            shareInfo.put("createTime", LocalDateTime.ofEpochSecond(Long.valueOf(parts[3]) / 1000, 0, java.time.ZoneOffset.of("+8")));
            shareInfo.put("random", parts[4]);

            return shareInfo;
        } catch (Exception e) {
            throw new BusinessException("无效的分享码");
        }
    }
}
