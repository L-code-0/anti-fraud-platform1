-- 协作演练消息表
DROP TABLE IF EXISTS `collaboration_message`;
CREATE TABLE `collaboration_message` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `session_id` BIGINT NOT NULL COMMENT '会话ID',
    `sender_id` BIGINT NOT NULL COMMENT '发送者ID',
    `sender_name` VARCHAR(50) NOT NULL COMMENT '发送者名称',
    `message_type` TINYINT NOT NULL COMMENT '消息类型：1-文本，2-图片，3-文件，4-系统通知',
    `content` TEXT COMMENT '消息内容',
    `message_time` DATETIME NOT NULL COMMENT '消息时间',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-已发送，2-已读',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_session_id` (`session_id`),
    KEY `idx_sender_id` (`sender_id`),
    KEY `idx_message_type` (`message_type`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='协作演练消息表';
