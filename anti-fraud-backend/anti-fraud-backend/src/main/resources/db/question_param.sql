-- 题目参数表
DROP TABLE IF EXISTS `question_param`;
CREATE TABLE `question_param` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `param_id` VARCHAR(50) NOT NULL COMMENT '参数ID（UUID）',
    `question_id` VARCHAR(50) NOT NULL COMMENT '题目ID',
    `question_name` VARCHAR(255) NOT NULL COMMENT '题目名称',
    `model_id` VARCHAR(50) NOT NULL COMMENT '模型ID',
    `difficulty` DOUBLE DEFAULT 0 COMMENT '难度参数（b）',
    `discrimination` DOUBLE DEFAULT 1 COMMENT '区分度参数（a）',
    `guessing` DOUBLE DEFAULT 0 COMMENT '猜测参数（c）',
    `estimation_method` TINYINT DEFAULT 1 COMMENT '参数估计方法：1-极大似然估计，2-贝叶斯估计，3-EM算法',
    `estimation_accuracy` DOUBLE DEFAULT 0 COMMENT '估计精度',
    `sample_size` INT DEFAULT 0 COMMENT '样本量',
    `status` TINYINT NOT NULL DEFAULT 2 COMMENT '状态：1-已标定，2-待标定，3-标定失败',
    `calibration_time` DATETIME COMMENT '标定时间',
    `description` VARCHAR(255) COMMENT '描述',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_param_id` (`param_id`),
    UNIQUE KEY `uk_question_id` (`question_id`),
    KEY `idx_model_id` (`model_id`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='题目参数表';
