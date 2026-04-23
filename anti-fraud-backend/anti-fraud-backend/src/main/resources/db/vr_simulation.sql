-- VR沉浸式演练表
DROP TABLE IF EXISTS `vr_simulation`;
CREATE TABLE `vr_simulation` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(100) NOT NULL COMMENT '演练名称',
    `description` VARCHAR(500) COMMENT '演练描述',
    `scenario_id` BIGINT NOT NULL COMMENT '演练场景ID',
    `scenario_name` VARCHAR(100) NOT NULL COMMENT '演练场景名称',
    `type` TINYINT NOT NULL DEFAULT 1 COMMENT '演练类型：1-电信诈骗，2-网络诈骗，3-金融诈骗，4-其他',
    `difficulty` TINYINT NOT NULL DEFAULT 1 COMMENT '难度等级：1-简单，2-中等，3-困难',
    `duration` INT NOT NULL DEFAULT 10 COMMENT '预计时长（分钟）',
    `model_path` VARCHAR(200) COMMENT '3D模型路径',
    `scene_path` VARCHAR(200) COMMENT '场景资源路径',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-启用，2-禁用',
    `creator_id` BIGINT NOT NULL COMMENT '创建者ID',
    `creator_name` VARCHAR(50) NOT NULL COMMENT '创建者名称',
    `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览量',
    `participant_count` INT NOT NULL DEFAULT 0 COMMENT '参与人数',
    `average_score` DOUBLE NOT NULL DEFAULT 0 COMMENT '平均评分',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_scenario_id` (`scenario_id`),
    KEY `idx_type` (`type`),
    KEY `idx_difficulty` (`difficulty`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='VR沉浸式演练表';

-- VR演练记录表
DROP TABLE IF EXISTS `vr_simulation_record`;
CREATE TABLE `vr_simulation_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `simulation_id` BIGINT NOT NULL COMMENT '演练ID',
    `simulation_name` VARCHAR(100) NOT NULL COMMENT '演练名称',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `start_time` DATETIME NOT NULL COMMENT '开始时间',
    `end_time` DATETIME COMMENT '结束时间',
    `actual_duration` INT COMMENT '实际时长（分钟）',
    `score` INT COMMENT '得分',
    `correct_rate` DOUBLE COMMENT '正确率',
    `completion_status` TINYINT NOT NULL DEFAULT 1 COMMENT '完成状态：1-未完成，2-已完成',
    `operation_record` TEXT COMMENT '操作记录（JSON格式）',
    `evaluation_result` TEXT COMMENT '评估结果（JSON格式）',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_simulation_id` (`simulation_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_completion_status` (`completion_status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='VR演练记录表';
