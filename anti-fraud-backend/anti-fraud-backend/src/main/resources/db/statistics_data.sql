-- 统计数据表
DROP TABLE IF EXISTS `statistics_data`;
CREATE TABLE `statistics_data` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `statistics_id` VARCHAR(50) NOT NULL COMMENT '统计ID（UUID）',
    `type` TINYINT NOT NULL DEFAULT 5 COMMENT '统计类型：1-用户活跃度，2-演练参与度，3-诈骗类型分布，4-知识掌握度，5-其他',
    `dimension` TINYINT NOT NULL DEFAULT 1 COMMENT '统计维度：1-日，2-周，3-月，4-年，5-自定义',
    `value` DOUBLE NOT NULL DEFAULT 0.0 COMMENT '统计值',
    `data` TEXT COMMENT '统计数据（JSON格式）',
    `statistics_time` DATETIME COMMENT '统计时间',
    `period_start_time` DATETIME NOT NULL COMMENT '统计周期开始时间',
    `period_end_time` DATETIME NOT NULL COMMENT '统计周期结束时间',
    `status` TINYINT NOT NULL DEFAULT 2 COMMENT '状态：1-已统计，2-统计中，3-统计失败',
    `error_message` TEXT COMMENT '错误信息',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_statistics_id` (`statistics_id`),
    KEY `idx_type` (`type`),
    KEY `idx_dimension` (`dimension`),
    KEY `idx_value` (`value`),
    KEY `idx_statistics_time` (`statistics_time`),
    KEY `idx_period_start_time` (`period_start_time`),
    KEY `idx_period_end_time` (`period_end_time`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='统计数据表';
