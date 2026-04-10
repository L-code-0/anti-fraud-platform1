-- ================================================
-- 高校反诈安全知识普及平台 - 数据库性能优化脚本
-- ================================================
-- 优化说明：
-- 1. 为高频查询字段添加索引
-- 2. 为外键关系添加索引
-- 3. 为排序字段添加索引
-- 4. 添加复合索引优化联合查询
-- ================================================

-- 使用数据库
USE anti_fraud_db;

-- ================================================
-- 1. 活动管理模块索引优化
-- ================================================

-- 活动表：按状态和时间查询的索引
ALTER TABLE activity_info 
ADD INDEX idx_status (status);

-- 活动表：按类型和状态查询的复合索引
ALTER TABLE activity_info 
ADD INDEX idx_type_status (activity_type, status);

-- 活动表：按创建时间排序的索引
ALTER TABLE activity_info 
ADD INDEX idx_create_time (create_time);

-- 活动报名表：按活动ID和用户ID查询（检查是否已报名）
ALTER TABLE activity_registration 
ADD INDEX idx_activity_user (activity_id, user_id);

-- 活动报名表：按用户ID查询
ALTER TABLE activity_registration 
ADD INDEX idx_user_id (user_id);

-- ================================================
-- 2. 举报预警模块索引优化
-- ================================================

-- 举报表：按用户ID查询
ALTER TABLE report_info 
ADD INDEX idx_user_id (user_id);

-- 举报表：按状态查询（待处理举报）
ALTER TABLE report_info 
ADD INDEX idx_status (status);

-- 举报表：按风险等级查询
ALTER TABLE report_info 
ADD INDEX idx_risk_level (risk_level);

-- 举报表：按创建时间排序
ALTER TABLE report_info 
ADD INDEX idx_create_time (create_time);

-- 预警表：按状态和等级查询
ALTER TABLE warning_alert 
ADD INDEX idx_status_level (status, warning_level);

-- 预警表：按发布时间排序
ALTER TABLE warning_alert 
ADD INDEX idx_publish_time (publish_time);

-- ================================================
-- 3. 用户管理模块索引优化
-- ================================================

-- 用户表：按用户名查询（登录）
ALTER TABLE user_info 
ADD INDEX idx_username (username);

-- 用户表：按角色查询
ALTER TABLE user_info 
ADD INDEX idx_role_id (role_id);

-- 用户表：按院系查询
ALTER TABLE user_info 
ADD INDEX idx_department (department);

-- 用户表：按创建时间排序
ALTER TABLE user_info 
ADD INDEX idx_create_time (create_time);

-- ================================================
-- 4. 学习记录模块索引优化
-- ================================================

-- 学习记录表：按用户ID和知识ID查询
ALTER TABLE user_learning_record 
ADD INDEX idx_user_knowledge (user_id, knowledge_id);

-- 学习记录表：按用户ID查询
ALTER TABLE user_learning_record 
ADD INDEX idx_user_id (user_id);

-- 测试记录表：按用户ID查询
ALTER TABLE user_test_record 
ADD INDEX idx_user_id (user_id);

-- 测试记录表：按测试ID查询
ALTER TABLE user_test_record 
ADD INDEX idx_test_id (test_id);

-- ================================================
-- 5. 班级管理模块索引优化
-- ================================================

-- 班级表：按创建时间排序
ALTER TABLE class_info 
ADD INDEX idx_create_time (create_time);

-- 班级学生表：按班级ID和学生ID查询
ALTER TABLE class_student 
ADD INDEX idx_class_student (class_id, student_id);

-- 班级学生表：按学生ID查询
ALTER TABLE class_student 
ADD INDEX idx_student_id (student_id);

-- ================================================
-- 6. 专家管理模块索引优化
-- ================================================

-- 专家信息表：按状态查询
ALTER TABLE expert_info 
ADD INDEX idx_status (status);

-- 专家信息表：按专业领域查询
ALTER TABLE expert_info 
ADD INDEX idx_expertise_field (expertise_field);

-- ================================================
-- 7. 积分管理模块索引优化
-- ================================================

-- 积分记录表：按用户ID查询
ALTER TABLE points_record 
ADD INDEX idx_user_id (user_id);

-- 积分记录表：按用户ID和时间查询
ALTER TABLE points_record 
ADD INDEX idx_user_time (user_id, create_time);

-- ================================================
-- 8. 作业管理模块索引优化
-- ================================================

-- 作业表：按班级ID查询
ALTER TABLE class_work 
ADD INDEX idx_class_id (class_id);

-- 作业表：按截止时间排序
ALTER TABLE class_work 
ADD INDEX idx_deadline (deadline);

-- 作业提交表：按作业ID和用户ID查询
ALTER TABLE work_submission 
ADD INDEX idx_work_user (work_id, student_id);

-- ================================================
-- 9. 问答管理模块索引优化
-- ================================================

-- 问题表：按状态查询
ALTER TABLE qa_question 
ADD INDEX idx_status (status);

-- 问题表：按分类查询
ALTER TABLE qa_question 
ADD INDEX idx_category (category);

-- 问题表：按创建时间排序
ALTER TABLE qa_question 
ADD INDEX idx_create_time (create_time);

-- 回答表：按问题ID查询
ALTER TABLE qa_answer 
ADD INDEX idx_question_id (question_id);

-- ================================================
-- 10. 模拟演练模块索引优化
-- ================================================

-- 演练记录表：按用户ID查询
ALTER TABLE simulation_record 
ADD INDEX idx_user_id (user_id);

-- 演练记录表：按用户ID和时间查询
ALTER TABLE simulation_record 
ADD INDEX idx_user_time (user_id, start_time);

-- ================================================
-- 查询优化说明
-- ================================================
/*
 索引使用建议：
 
 1. 单字段查询优先使用单字段索引
    SELECT * FROM activity_info WHERE status = 1
    -> 使用 idx_status 索引
 
 2. 多字段查询优先使用复合索引
    SELECT * FROM activity_info WHERE status = 1 AND activity_type = 2
    -> 使用 idx_type_status 复合索引（注意字段顺序）
 
 3. 排序查询使用覆盖索引
    SELECT * FROM activity_info ORDER BY create_time DESC
    -> 使用 idx_create_time 索引
 
 4. 外键关联查询使用复合索引
    SELECT * FROM activity_registration WHERE activity_id = ? AND user_id = ?
    -> 使用 idx_activity_user 复合索引
 
 5. 范围查询后不要使用复合索引的后续字段
    SELECT * FROM activity_info WHERE status > 1 AND activity_type = 2
    -> status使用范围，activity_type无法使用索引
 
 6. 避免在索引列上使用函数
    SELECT * FROM user_info WHERE DATE(create_time) = '2024-01-01'
    -> 建议改为范围查询
 
 7. 使用EXPLAIN分析查询计划
    EXPLAIN SELECT * FROM activity_info WHERE status = 1;
*/
