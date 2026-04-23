-- 能力估计表
DROP TABLE IF EXISTS `ability_estimation`;
CREATE TABLE `ability_estimation` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `estimation_id` VARCHAR(50) NOT NULL COMMENT '估计ID（UUID）',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `ability` DOUBLE DEFAULT 0 COMMENT '能力值（θ）',
    `ability_std` DOUBLE DEFAULT 1 COMMENT '能力估计标准差',
    `estimation_method` TINYINT DEFAULT 1 COMMENT '估计方法：1-极大似然估计，2-贝叶斯估计，3-加权似然估计',
    `model_id` VARCHAR(50) NOT NULL COMMENT '模型ID',
    `test_id` VARCHAR(50) COMMENT '测试ID',
    `test_status` TINYINT NOT NULL DEFAULT 1 COMMENT '测试状态：1-进行中，2-已完成，3-已取消',
    `answered_count` INT DEFAULT 0 COMMENT '已答题数量',
    `correct_count` INT DEFAULT 0 COMMENT '正确答题数量',
    `estimation_time` DATETIME COMMENT '估计时间',
    `description` VARCHAR(255) COMMENT '描述',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_estimation_id` (`estimation_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_model_id` (`model_id`),
    KEY `idx_test_status` (`test_status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='能力估计表';
