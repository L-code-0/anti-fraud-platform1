-- 题目表
DROP TABLE IF EXISTS `question`;
CREATE TABLE `question` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `content` TEXT NOT NULL COMMENT '题目内容',
    `type` TINYINT NOT NULL COMMENT '题目类型：1-单选题，2-多选题，3-判断题，4-填空题，5-简答题',
    `difficulty` TINYINT NOT NULL COMMENT '题目难度：1-简单，2-中等，3-困难',
    `knowledge_point` VARCHAR(100) NOT NULL COMMENT '知识点',
    `options` TEXT COMMENT '选项（JSON格式）',
    `correct_answer` VARCHAR(100) NOT NULL COMMENT '正确答案',
    `explanation` TEXT COMMENT '解析',
    `source` VARCHAR(100) COMMENT '来源',
    `tags` VARCHAR(200) COMMENT '标签',
    `usage_count` INT NOT NULL DEFAULT 0 COMMENT '使用率',
    `correct_rate` DECIMAL(5,2) NOT NULL DEFAULT 0.00 COMMENT '正确率',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-启用，2-禁用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_type` (`type`),
    KEY `idx_difficulty` (`difficulty`),
    KEY `idx_knowledge_point` (`knowledge_point`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='题目表';
