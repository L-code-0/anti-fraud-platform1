-- 风险评估表
DROP TABLE IF EXISTS `risk_assessment`;
CREATE TABLE `risk_assessment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `assessment_id` VARCHAR(50) NOT NULL COMMENT '评估ID（UUID）',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `behavior_risk_score` DOUBLE DEFAULT 0 COMMENT '行为风险分数（0-100）',
    `device_risk_score` DOUBLE DEFAULT 0 COMMENT '设备风险分数（0-100）',
    `social_risk_score` DOUBLE DEFAULT 0 COMMENT '社交风险分数（0-100）',
    `overall_risk_score` DOUBLE DEFAULT 0 COMMENT '综合风险分数（0-100）',
    `risk_level` TINYINT NOT NULL DEFAULT 1 COMMENT '风险等级：1-低风险，2-中风险，3-高风险，4-极高风险',
    `behavior_risk_details` TEXT COMMENT '行为风险详情（JSON格式）',
    `device_risk_details` TEXT COMMENT '设备风险详情（JSON格式）',
    `social_risk_details` TEXT COMMENT '社交风险详情（JSON格式）',
    `assessment_time` DATETIME NOT NULL COMMENT '评估时间',
    `status` TINYINT NOT NULL DEFAULT 2 COMMENT '评估状态：1-已评估，2-评估中，3-评估失败',
    `description` VARCHAR(255) COMMENT '描述',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_assessment_id` (`assessment_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_risk_level` (`risk_level`),
    KEY `idx_assessment_time` (`assessment_time`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='风险评估表';
