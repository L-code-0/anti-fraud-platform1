-- IRT模型表
DROP TABLE IF EXISTS `irt_model`;
CREATE TABLE `irt_model` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `model_id` VARCHAR(50) NOT NULL COMMENT '模型ID（UUID）',
    `model_name` VARCHAR(100) NOT NULL COMMENT '模型名称',
    `model_type` TINYINT NOT NULL DEFAULT 3 COMMENT '模型类型：1-1PL，2-2PL，3-3PL',
    `model_params` TEXT COMMENT '模型参数（JSON格式）',
    `status` TINYINT NOT NULL DEFAULT 3 COMMENT '模型状态：1-启用，2-禁用，3-待训练',
    `training_data_size` INT DEFAULT 0 COMMENT '训练数据量',
    `training_accuracy` DOUBLE DEFAULT 0 COMMENT '训练精度',
    `training_time` DATETIME COMMENT '训练时间',
    `description` VARCHAR(255) COMMENT '描述',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_model_id` (`model_id`),
    KEY `idx_model_type` (`model_type`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='IRT模型表';
