-- 通知表
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `type` TINYINT NOT NULL DEFAULT 1 COMMENT '通知类型：1-系统通知，2-学习通知，3-演练通知，4-举报通知，5-风险通知，6-其他',
    `title` VARCHAR(100) NOT NULL COMMENT '通知标题',
    `content` TEXT NOT NULL COMMENT '通知内容',
    `method` TINYINT NOT NULL DEFAULT 1 COMMENT '通知方式：1-站内信，2-短信，3-邮件，4-微信',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '发送状态：1-待发送，2-已发送，3-发送失败',
    `read_status` TINYINT NOT NULL DEFAULT 1 COMMENT '阅读状态：1-未读，2-已读',
    `send_time` DATETIME COMMENT '发送时间',
    `read_time` DATETIME COMMENT '阅读时间',
    `failure_reason` VARCHAR(200) COMMENT '失败原因',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_type` (`type`),
    KEY `idx_method` (`method`),
    KEY `idx_status` (`status`),
    KEY `idx_read_status` (`read_status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知表';
