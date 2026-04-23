-- 行为风险表
DROP TABLE IF EXISTS `behavior_risk`;
CREATE TABLE `behavior_risk` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `risk_id` VARCHAR(50) NOT NULL COMMENT '风险ID（UUID）',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `behavior_type` TINYINT NOT NULL DEFAULT 5 COMMENT '行为类型：1-登录行为，2-交易行为，3-搜索行为，4-浏览行为，5-其他',
    `behavior_content` TEXT COMMENT '行为内容（JSON格式）',
    `behavior_time` DATETIME NOT NULL COMMENT '行为时间',
    `risk_score` DOUBLE DEFAULT 0 COMMENT '风险分数（0-100）',
    `risk_level` TINYINT NOT NULL DEFAULT 1 COMMENT '风险等级：1-低风险，2-中风险，3-高风险，4-极高风险',
    `time_decay_factor` DOUBLE DEFAULT 1 COMMENT '时间衰减因子（0-1）',
    `risk_details` TEXT COMMENT '风险详情（JSON格式）',
    `status` TINYINT NOT NULL DEFAULT 2 COMMENT '状态：1-已分析，2-分析中，3-分析失败',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_risk_id` (`risk_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_behavior_type` (`behavior_type`),
    KEY `idx_behavior_time` (`behavior_time`),
    KEY `idx_risk_level` (`risk_level`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='行为风险表';
