-- 学习路径表
DROP TABLE IF EXISTS `learning_path`;
CREATE TABLE `learning_path` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(100) NOT NULL COMMENT '路径名称',
    `description` VARCHAR(500) COMMENT '路径描述',
    `target_user_type` TINYINT NOT NULL DEFAULT 1 COMMENT '目标用户类型：1-新手，2-进阶，3-专家',
    `estimated_duration` INT NOT NULL DEFAULT 10 COMMENT '预估时长（小时）',
    `difficulty` TINYINT NOT NULL DEFAULT 1 COMMENT '难度等级：1-简单，2-中等，3-困难',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '路径状态：1-启用，2-禁用',
    `creator_id` BIGINT NOT NULL COMMENT '创建者ID',
    `creator_name` VARCHAR(50) NOT NULL COMMENT '创建者名称',
    `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览量',
    `completion_count` INT NOT NULL DEFAULT 0 COMMENT '完成人数',
    `average_score` DOUBLE NOT NULL DEFAULT 0 COMMENT '平均评分',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_target_user_type` (`target_user_type`),
    KEY `idx_difficulty` (`difficulty`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学习路径表';

-- 学习路径节点表
DROP TABLE IF EXISTS `learning_path_node`;
CREATE TABLE `learning_path_node` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `path_id` BIGINT NOT NULL COMMENT '学习路径ID',
    `name` VARCHAR(100) NOT NULL COMMENT '节点名称',
    `node_type` TINYINT NOT NULL DEFAULT 1 COMMENT '节点类型：1-知识学习，2-视频学习，3-练习测试，4-实战演练',
    `resource_id` BIGINT COMMENT '资源ID（根据节点类型对应不同的资源）',
    `resource_name` VARCHAR(100) COMMENT '资源名称',
    `node_order` INT NOT NULL DEFAULT 0 COMMENT '节点顺序',
    `estimated_duration` INT NOT NULL DEFAULT 30 COMMENT '预估时长（分钟）',
    `description` VARCHAR(200) COMMENT '描述',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_path_id` (`path_id`),
    KEY `idx_node_type` (`node_type`),
    KEY `idx_node_order` (`node_order`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学习路径节点表';
