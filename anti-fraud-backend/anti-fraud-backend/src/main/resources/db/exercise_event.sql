-- 演练事件表
DROP TABLE IF EXISTS `exercise_event`;
CREATE TABLE `exercise_event` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `event_id` VARCHAR(50) NOT NULL COMMENT '事件ID（UUID）',
    `exercise_id` VARCHAR(50) NOT NULL COMMENT '演练ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `event_type` TINYINT NOT NULL DEFAULT 6 COMMENT '事件类型：1-开始演练，2-结束演练，3-选择答案，4-查看解析，5-获得分数，6-其他',
    `content` TEXT COMMENT '事件内容（JSON格式）',
    `event_time` DATETIME NOT NULL COMMENT '事件时间',
    `status` TINYINT NOT NULL DEFAULT 2 COMMENT '事件状态：1-已处理，2-处理中，3-处理失败',
    `error_message` TEXT COMMENT '错误信息',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_event_id` (`event_id`),
    KEY `idx_exercise_id` (`exercise_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_event_type` (`event_type`),
    KEY `idx_event_time` (`event_time`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='演练事件表';
