-- 对话会话表
DROP TABLE IF EXISTS `chat_session`;
CREATE TABLE `chat_session` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `session_id` VARCHAR(50) NOT NULL COMMENT '会话ID（UUID）',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `title` VARCHAR(100) NOT NULL COMMENT '对话标题',
    `type` TINYINT NOT NULL DEFAULT 1 COMMENT '对话类型：1-普通对话，2-反诈咨询，3-学习指导',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '对话状态：1-活跃，2-已结束',
    `last_interaction_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后交互时间',
    `chat_history` TEXT COMMENT '对话历史（JSON格式）',
    `context` TEXT COMMENT '对话上下文（JSON格式）',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_session_id` (`session_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_last_interaction_time` (`last_interaction_time`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='对话会话表';

-- 对话消息表
DROP TABLE IF EXISTS `chat_message`;
CREATE TABLE `chat_message` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `session_id` VARCHAR(50) NOT NULL COMMENT '会话ID',
    `message_id` VARCHAR(50) NOT NULL COMMENT '消息ID（UUID）',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `sender_type` TINYINT NOT NULL DEFAULT 1 COMMENT '发送者类型：1-用户，2-AI',
    `message_type` TINYINT NOT NULL DEFAULT 1 COMMENT '消息类型：1-文本，2-图片，3-语音，4-视频',
    `content` TEXT NOT NULL COMMENT '消息内容',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '消息状态：1-已发送，2-已读，3-已删除',
    `send_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    `read_time` DATETIME COMMENT '阅读时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_message_id` (`message_id`),
    KEY `idx_session_id` (`session_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_sender_type` (`sender_type`),
    KEY `idx_status` (`status`),
    KEY `idx_send_time` (`send_time`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='对话消息表';
