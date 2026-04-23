-- 知识图谱节点表
DROP TABLE IF EXISTS `knowledge_node`;
CREATE TABLE `knowledge_node` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `node_id` VARCHAR(50) NOT NULL COMMENT '节点ID（UUID）',
    `name` VARCHAR(100) NOT NULL COMMENT '节点名称',
    `type` TINYINT NOT NULL DEFAULT 1 COMMENT '节点类型：1-诈骗类型，2-防范措施，3-案例，4-知识，5-其他',
    `description` TEXT COMMENT '节点描述',
    `related_id` BIGINT COMMENT '关联ID（如诈骗类型ID、案例ID等）',
    `properties` TEXT COMMENT '节点属性（JSON格式）',
    `hotness` INT NOT NULL DEFAULT 0 COMMENT '热度',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-启用，2-禁用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_node_id` (`node_id`),
    KEY `idx_type` (`type`),
    KEY `idx_related_id` (`related_id`),
    KEY `idx_hotness` (`hotness`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识图谱节点表';

-- 知识图谱边表
DROP TABLE IF EXISTS `knowledge_edge`;
CREATE TABLE `knowledge_edge` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `edge_id` VARCHAR(50) NOT NULL COMMENT '边ID（UUID）',
    `source_node_id` VARCHAR(50) NOT NULL COMMENT '源节点ID',
    `target_node_id` VARCHAR(50) NOT NULL COMMENT '目标节点ID',
    `type` TINYINT NOT NULL DEFAULT 1 COMMENT '边类型：1-属于，2-包含，3-关联，4-推荐，5-其他',
    `name` VARCHAR(100) NOT NULL COMMENT '边名称',
    `description` TEXT COMMENT '边描述',
    `weight` DOUBLE NOT NULL DEFAULT 1.0 COMMENT '权重',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-启用，2-禁用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_edge_id` (`edge_id`),
    KEY `idx_source_node_id` (`source_node_id`),
    KEY `idx_target_node_id` (`target_node_id`),
    KEY `idx_type` (`type`),
    KEY `idx_weight` (`weight`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识图谱边表';
