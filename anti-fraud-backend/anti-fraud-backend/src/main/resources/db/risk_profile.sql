-- 风险画像表
DROP TABLE IF EXISTS `risk_profile`;
CREATE TABLE `risk_profile` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `risk_score` INT NOT NULL DEFAULT 0 COMMENT '风险评分',
    `risk_level` TINYINT NOT NULL DEFAULT 1 COMMENT '风险等级：1-低风险，2-中风险，3-高风险',
    `risk_tags` TEXT COMMENT '风险标签（JSON格式）',
    `behavior_features` TEXT COMMENT '行为特征（JSON格式）',
    `risk_analysis` TEXT COMMENT '风险分析结果（JSON格式）',
    `suggestions` TEXT COMMENT '建议措施（JSON格式）',
    `last_update_time` DATETIME COMMENT '最后更新时间',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-正常，2-异常',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_risk_level` (`risk_level`),
    KEY `idx_risk_score` (`risk_score`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='风险画像表';
