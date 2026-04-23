-- 协作演练会话表
DROP TABLE IF EXISTS `collaboration_session`;
CREATE TABLE `collaboration_session` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `session_name` VARCHAR(100) NOT NULL COMMENT '会话名称',
    `description` VARCHAR(500) COMMENT '会话描述',
    `scenario_id` BIGINT NOT NULL COMMENT '演练场景ID',
    `scenario_name` VARCHAR(100) NOT NULL COMMENT '演练场景名称',
    `creator_id` BIGINT NOT NULL COMMENT '创建者ID',
    `creator_name` VARCHAR(50) NOT NULL COMMENT '创建者名称',
    `start_time` DATETIME COMMENT '开始时间',
    `end_time` DATETIME COMMENT '结束时间',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-准备中，2-进行中，3-已结束，4-已取消',
    `max_participants` INT NOT NULL DEFAULT 10 COMMENT '最大参与人数',
    `current_participants` INT NOT NULL DEFAULT 0 COMMENT '当前参与人数',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_creator_id` (`creator_id`),
    KEY `idx_scenario_id` (`scenario_id`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='协作演练会话表';
