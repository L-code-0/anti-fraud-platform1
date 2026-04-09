-- 班级任务表
CREATE TABLE IF NOT EXISTS `class_task` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `task_name` VARCHAR(200) NOT NULL COMMENT '任务名称',
    `task_type` VARCHAR(20) NOT NULL COMMENT '任务类型：VIDEO-视频学习, TEST-测试, KNOWLEDGE-知识学习',
    `creator_id` BIGINT NOT NULL COMMENT '创建者ID',
    `creator_name` VARCHAR(50) COMMENT '创建者姓名',
    `class_id` BIGINT NOT NULL COMMENT '班级ID',
    `class_name` VARCHAR(100) COMMENT '班级名称',
    `start_time` DATETIME NOT NULL COMMENT '开始时间',
    `end_time` DATETIME NOT NULL COMMENT '结束时间',
    `total_students` INT NOT NULL DEFAULT 0 COMMENT '总学生数',
    `completed_students` INT NOT NULL DEFAULT 0 COMMENT '已完成学生数',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-未开始, 1-进行中, 2-已结束',
    `description` VARCHAR(500) COMMENT '任务描述',
    `related_id` BIGINT COMMENT '关联资源ID（视频/测试/知识）',
    `related_name` VARCHAR(200) COMMENT '关联资源名称',
    `points` INT NOT NULL DEFAULT 10 COMMENT '奖励积分',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    INDEX `idx_creator_id` (`creator_id`),
    INDEX `idx_class_id` (`class_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_time` (`start_time`, `end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='班级任务表';

-- 任务完成记录表
CREATE TABLE IF NOT EXISTS `task_completion` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `task_id` BIGINT NOT NULL COMMENT '任务ID',
    `student_id` BIGINT NOT NULL COMMENT '学生ID',
    `student_name` VARCHAR(50) COMMENT '学生姓名',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '完成状态：0-未完成, 1-已完成',
    `score` INT COMMENT '测试得分（如果是测试任务）',
    `complete_time` DATETIME COMMENT '完成时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_task_student` (`task_id`, `student_id`),
    INDEX `idx_student_id` (`student_id`),
    INDEX `idx_task_id` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务完成记录表';

-- 插入测试任务数据
INSERT INTO `class_task` (`task_name`, `task_type`, `creator_id`, `creator_name`, `class_id`, `class_name`, `start_time`, `end_time`, `total_students`, `completed_students`, `status`, `description`, `related_id`, `related_name`, `points`, `create_time`) VALUES
('电信诈骗防范视频学习', 'VIDEO', 1, '张老师', 1, '反诈安全 1 班', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_ADD(NOW(), INTERVAL 2 DAY), 45, 32, 1, '观看电信诈骗防范教育视频，了解常见诈骗手段', 1, '电信诈骗防范教程', 15, NOW()),
('网络安全知识学习', 'KNOWLEDGE', 2, '李老师', 2, '反诈安全 2 班', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_ADD(NOW(), INTERVAL 4 DAY), 38, 15, 1, '学习网络安全基础知识，提升防骗意识', 2, '网络安全知识手册', 10, NOW()),
('防诈骗知识测试', 'TEST', 1, '张老师', 1, '反诈安全 1 班', NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), 45, 0, 1, '完成防诈骗知识测试，检验学习成果', 3, '防诈骗测试卷A', 20, NOW()),
('AI换脸诈骗案例学习', 'KNOWLEDGE', 3, '王老师', 3, '反诈安全 3 班', DATE_ADD(NOW(), INTERVAL 2 DAY), DATE_ADD(NOW(), INTERVAL 9 DAY), 42, 0, 0, '学习AI换脸诈骗案例，了解新型诈骗手段', 4, 'AI诈骗案例分析', 15, NOW()),
('校园贷防范视频', 'VIDEO', 2, '李老师', 2, '反诈安全 2 班', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), 38, 35, 2, '观看校园贷防范视频，了解校园贷陷阱', 5, '校园贷防范教育', 10, DATE_SUB(NOW(), INTERVAL 10 DAY));

-- 插入测试完成记录
INSERT INTO `task_completion` (`task_id`, `student_id`, `student_name`, `status`, `score`, `complete_time`, `create_time`) VALUES
-- 任务1的完成记录
(1, 101, '张三', 1, NULL, DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 102, '李四', 1, NULL, DATE_SUB(NOW(), INTERVAL 1 DAY), NOW()),
(1, 103, '王五', 1, NULL, NOW(), NOW()),
(1, 104, '赵六', 0, NULL, NULL, NOW()),
-- 任务2的完成记录
(2, 201, '孙八', 1, NULL, NOW(), NOW()),
(2, 202, '周九', 1, NULL, NOW(), NOW()),
(2, 203, '吴十', 0, NULL, NULL, NOW()),
-- 任务3的完成记录（测试）
(3, 101, '张三', 0, NULL, NULL, NOW()),
(3, 102, '李四', 0, NULL, NULL, NOW());
