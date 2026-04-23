-- 行为分析表
DROP TABLE IF EXISTS `behavior_analysis`;
CREATE TABLE `behavior_analysis` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `analysis_id` VARCHAR(50) NOT NULL COMMENT '分析ID（UUID）',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `behavior_type` TINYINT NOT NULL DEFAULT 7 COMMENT '行为类型：1-浏览，2-点击，3-停留，4-搜索，5-答题，6-演练，7-其他',
    `behavior_content` TEXT COMMENT '行为内容（JSON格式）',
    `behavior_time` DATETIME NOT NULL COMMENT '行为时间',
    `analysis_result` TEXT COMMENT '分析结果（JSON格式）',
    `feedback_content` TEXT COMMENT '反馈内容（JSON格式）',
    `status` TINYINT NOT NULL DEFAULT 2 COMMENT '分析状态：1-已分析，2-分析中，3-分析失败',
    `error_message` TEXT COMMENT '错误信息',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_analysis_id` (`analysis_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_behavior_type` (`behavior_type`),
    KEY `idx_behavior_time` (`behavior_time`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='行为分析表';
