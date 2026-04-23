-- 案例回放表
DROP TABLE IF EXISTS `case_replay`;
CREATE TABLE `case_replay` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `case_id` BIGINT NOT NULL COMMENT '案例ID',
    `case_title` VARCHAR(200) NOT NULL COMMENT '案例标题',
    `replay_title` VARCHAR(200) NOT NULL COMMENT '回放标题',
    `replay_description` TEXT COMMENT '回放描述',
    `replay_type` TINYINT NOT NULL COMMENT '回放类型：1-文字回放，2-语音回放，3-视频回放，4-互动回放',
    `replay_content` TEXT COMMENT '回放内容（JSON格式）',
    `cover_image` VARCHAR(500) COMMENT '封面图片',
    `duration` INT COMMENT '时长（秒）',
    `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览量',
    `like_count` INT NOT NULL DEFAULT 0 COMMENT '点赞数',
    `share_count` INT NOT NULL DEFAULT 0 COMMENT '分享数',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-启用，2-禁用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_case_id` (`case_id`),
    KEY `idx_replay_type` (`replay_type`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='案例回放表';
