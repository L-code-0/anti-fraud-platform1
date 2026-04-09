-- 创建数据库
CREATE DATABASE IF NOT EXISTS anti_fraud DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE anti_fraud;

-- 用户表
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    avatar VARCHAR(255) COMMENT '头像URL',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    user_no VARCHAR(50) COMMENT '学号/工号',
    gender TINYINT DEFAULT 0 COMMENT '性别：0-未知 1-男 2-女',
    department_id BIGINT COMMENT '院系ID',
    class_id BIGINT COMMENT '班级ID',
    role_id TINYINT DEFAULT 1 COMMENT '角色ID：1-学生 2-教师 3-管理员 4-专家 5-系统管理员',
    points INT DEFAULT 0 COMMENT '积分',
    level INT DEFAULT 1 COMMENT '等级',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-正常',
    last_login_time DATETIME COMMENT '最后登录时间',
    last_login_ip VARCHAR(50) COMMENT '最后登录IP',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志：0-未删除 1-已删除',
    UNIQUE KEY uk_username (username),
    UNIQUE KEY uk_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 插入默认管理员账号 (密码: 123456)
INSERT INTO sys_user (username, password, real_name, role_id, status, points, level) VALUES
('admin', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '系统管理员', 5, 1, 0, 1),
('manager', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '平台管理员', 3, 1, 0, 1),
('student1', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '张三', 1, 1, 100, 2),
('teacher1', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '李老师', 2, 1, 200, 3),
('expert1', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '王专家', 4, 1, 500, 4);

-- 知识分类表
DROP TABLE IF EXISTS knowledge_category;
CREATE TABLE knowledge_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    category_name VARCHAR(100) NOT NULL COMMENT '分类名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID',
    level TINYINT DEFAULT 1 COMMENT '层级',
    icon VARCHAR(255) COMMENT '图标',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识分类表';

-- 初始化分类数据
INSERT INTO knowledge_category (category_name, parent_id, level, sort_order) VALUES
('电信诈骗', 0, 1, 1),
('网络诈骗', 0, 1, 2),
('校园贷诈骗', 0, 1, 3),
('兼职诈骗', 0, 1, 4),
('冒充诈骗', 0, 1, 5),
('投资理财诈骗', 0, 1, 6),
('其他类型', 0, 1, 7);

-- 二级分类
INSERT INTO knowledge_category (category_name, parent_id, level, sort_order) VALUES
('冒充公检法', 1, 2, 1),
('冒充客服', 1, 2, 2),
('冒充熟人', 1, 2, 3),
('虚假中奖', 1, 2, 4),
('刷单诈骗', 2, 2, 1),
('网购诈骗', 2, 2, 2),
('虚假投资', 2, 2, 3);

-- 知识内容表
DROP TABLE IF EXISTS knowledge_content;
CREATE TABLE knowledge_content (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '内容ID',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    category_id BIGINT COMMENT '分类ID',
    content_type TINYINT DEFAULT 1 COMMENT '内容类型：1-文章 2-视频 3-案例',
    summary VARCHAR(500) COMMENT '摘要',
    content LONGTEXT COMMENT '内容详情',
    cover_image VARCHAR(255) COMMENT '封面图',
    video_url VARCHAR(255) COMMENT '视频URL',
    video_duration INT COMMENT '视频时长(秒)',
    tags VARCHAR(255) COMMENT '标签',
    author_id BIGINT COMMENT '作者ID',
    author_name VARCHAR(50) COMMENT '作者名称',
    source VARCHAR(100) COMMENT '来源',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    like_count INT DEFAULT 0 COMMENT '点赞次数',
    collect_count INT DEFAULT 0 COMMENT '收藏次数',
    share_count INT DEFAULT 0 COMMENT '分享次数',
    is_top TINYINT DEFAULT 0 COMMENT '是否置顶',
    is_hot TINYINT DEFAULT 0 COMMENT '是否热门',
    is_recommend TINYINT DEFAULT 0 COMMENT '是否推荐',
    status TINYINT DEFAULT 1 COMMENT '状态：0-下架 1-上架',
    audit_status TINYINT DEFAULT 1 COMMENT '审核状态：0-待审核 1-通过 2-拒绝',
    publish_time DATETIME COMMENT '发布时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识内容表';

-- 插入示例知识内容
INSERT INTO knowledge_content (title, category_id, content_type, summary, content, author_name, view_count, like_count, collect_count, is_hot, is_recommend, status, audit_status, publish_time) VALUES
('冒充公检法诈骗案例分析', 8, 1, '详细分析冒充公检法诈骗的常见手法和防范措施', '<h2>什么是冒充公检法诈骗</h2><p>冒充公检法诈骗是指骗子冒充公安、检察院、法院等机关工作人员...</p>', '王专家', 2356, 128, 56, 1, 1, 1, 1, NOW()),
('网络兼职刷单诈骗防范指南', 11, 1, '揭露网络兼职刷单诈骗的真实套路', '<h2>刷单诈骗的常见套路</h2><p>刷单诈骗是目前最常见的网络诈骗之一...</p>', '李老师', 1892, 98, 45, 1, 1, 1, 1, NOW()),
('校园贷陷阱识别指南', 3, 1, '教你识别校园贷的各种隐藏陷阱', '<h2>校园贷的危害</h2><p>校园贷往往以低门槛、快速放款为诱饵...</p>', '王专家', 3210, 156, 78, 1, 1, 1, 1, NOW());

-- 题库表
DROP TABLE IF EXISTS test_question;
CREATE TABLE test_question (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '题目ID',
    question_type TINYINT DEFAULT 1 COMMENT '题目类型：1-单选 2-多选 3-判断',
    category_id BIGINT COMMENT '分类ID',
    content TEXT NOT NULL COMMENT '题目内容',
    options JSON COMMENT '选项列表',
    answer VARCHAR(50) NOT NULL COMMENT '正确答案',
    analysis TEXT COMMENT '答案解析',
    difficulty TINYINT DEFAULT 1 COMMENT '难度等级：1-5',
    tags VARCHAR(255) COMMENT '标签',
    score INT DEFAULT 10 COMMENT '分值',
    status TINYINT DEFAULT 1 COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题库表';

-- 插入示例题目
INSERT INTO test_question (question_type, category_id, content, options, answer, analysis, difficulty, score) VALUES
(1, 1, '接到自称是公安机关的电话，说你涉嫌洗钱，需要将资金转入"安全账户"，你应该？', 
'["A. 立即挂断电话", "B. 按照对方指示转账", "C. 提供个人信息", "D. 询问对方警号"]', 
'A', '公安机关不会通过电话要求转账，这是典型的冒充公检法诈骗', 2, 10),
(1, 2, '在网上看到兼职刷单广告，声称"日赚百元、无需押金"，你应该？', 
'["A. 立即联系对方开始刷单", "B. 不理会，这是诈骗", "C. 先试试看，投少量资金", "D. 分享给朋友一起做"]', 
'B', '刷单是违法行为，且绝大多数刷单广告都是诈骗', 1, 10),
(3, 3, '校园贷只要按时还款就不会有问题。', 
'["A. 对", "B. 错"]', 
'B', '校园贷往往存在高利贷、暴力催收等风险，即使按时还款也可能陷入债务陷阱', 1, 5);

-- 试卷表
DROP TABLE IF EXISTS test_paper;
CREATE TABLE test_paper (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '试卷ID',
    paper_name VARCHAR(200) NOT NULL COMMENT '试卷名称',
    paper_type TINYINT DEFAULT 1 COMMENT '试卷类型：1-随机组卷 2-固定试卷',
    total_score INT DEFAULT 100 COMMENT '总分',
    pass_score INT DEFAULT 60 COMMENT '及格分',
    duration INT DEFAULT 60 COMMENT '考试时长(分钟)',
    question_count INT COMMENT '题目数量',
    status TINYINT DEFAULT 1 COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试卷表';

-- 插入试卷
INSERT INTO test_paper (paper_name, paper_type, total_score, pass_score, duration, question_count) VALUES
('反诈知识入门测试', 2, 100, 60, 30, 10),
('反诈知识进阶测试', 2, 100, 70, 45, 20),
('电信诈骗专项测试', 1, 100, 60, 30, 15);

-- 演练场景表
DROP TABLE IF EXISTS simulation_scene;
CREATE TABLE simulation_scene (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '场景ID',
    scene_name VARCHAR(200) NOT NULL COMMENT '场景名称',
    scene_type VARCHAR(50) COMMENT '场景类型',
    category_id BIGINT COMMENT '分类ID',
    background TEXT COMMENT '背景描述',
    script_config JSON COMMENT '脚本配置',
    difficulty TINYINT DEFAULT 1 COMMENT '难度等级',
    cover_image VARCHAR(255) COMMENT '封面图',
    play_count INT DEFAULT 0 COMMENT '演练次数',
    avg_score DECIMAL(5,2) COMMENT '平均得分',
    is_recommend TINYINT DEFAULT 0 COMMENT '是否推荐',
    status TINYINT DEFAULT 1 COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='演练场景表';

-- 插入演练场景
INSERT INTO simulation_scene (scene_name, scene_type, category_id, background, difficulty, is_recommend, status) VALUES
('冒充客服退款诈骗', '电信诈骗', 9, '你接到一个自称是某购物平台客服的电话，说你购买的商品存在质量问题，需要办理退款...', 2, 1, 1),
('冒充公检法诈骗', '电信诈骗', 8, '你接到自称是公安局的电话，说你涉嫌洗钱，需要配合调查...', 3, 1, 1),
('网络兼职刷单诈骗', '网络诈骗', 11, '你在网上看到兼职刷单的广告，对方声称日赚百元...', 1, 1, 1);

-- 举报表
DROP TABLE IF EXISTS report_info;
CREATE TABLE report_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '举报ID',
    report_no VARCHAR(50) COMMENT '举报编号',
    user_id BIGINT COMMENT '举报人ID',
    report_type TINYINT COMMENT '举报类型：1-可疑电话 2-可疑短信 3-可疑链接 4-其他',
    fraud_type VARCHAR(50) COMMENT '诈骗类型',
    title VARCHAR(200) COMMENT '标题',
    description TEXT COMMENT '描述',
    phone_number VARCHAR(50) COMMENT '可疑电话号码',
    link_url VARCHAR(500) COMMENT '可疑链接',
    images JSON COMMENT '图片列表',
    contact_name VARCHAR(50) COMMENT '联系人',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    is_anonymous TINYINT DEFAULT 0 COMMENT '是否匿名',
    status TINYINT DEFAULT 0 COMMENT '状态：0-待处理 1-处理中 2-已处理',
    risk_level TINYINT COMMENT '风险等级：1-低 2-中 3-高',
    handler_id BIGINT COMMENT '处理人ID',
    handle_time DATETIME COMMENT '处理时间',
    handle_result TEXT COMMENT '处理结果',
    reward_points INT COMMENT '奖励积分',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='举报表';

-- 预警信息表
DROP TABLE IF EXISTS warning_info;
CREATE TABLE warning_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '预警ID',
    title VARCHAR(200) NOT NULL COMMENT '预警标题',
    warning_level TINYINT DEFAULT 1 COMMENT '预警等级：1-蓝色 2-黄色 3-橙色 4-红色',
    fraud_type VARCHAR(50) COMMENT '诈骗类型',
    content TEXT COMMENT '预警内容',
    prevention_tips TEXT COMMENT '防范要点',
    view_count INT DEFAULT 0 COMMENT '阅读次数',
    status TINYINT DEFAULT 1 COMMENT '状态：0-草稿 1-已发布',
    publisher_id BIGINT COMMENT '发布人ID',
    publish_time DATETIME COMMENT '发布时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预警信息表';

-- 插入预警信息
INSERT INTO warning_info (title, warning_level, fraud_type, content, prevention_tips, status, publish_time) VALUES
('警惕新型"AI换脸"诈骗手法', 4, '电信诈骗', '近期出现利用AI技术进行视频通话诈骗的新手法...', '1. 视频通话时注意观察对方面部细节\n2. 涉及转账务必电话核实', 1, NOW()),
('近期冒充客服退款诈骗高发', 3, '电信诈骗', '近期冒充电商平台客服退款诈骗案件高发...', '1. 官方客服不会私下联系退款\n2. 不要点击陌生链接', 1, NOW()),
('寒假期间注意兼职诈骗', 2, '兼职诈骗', '寒假期间是兼职诈骗高发期...', '1. 不缴纳任何押金\n2. 选择正规平台求职', 1, NOW());

-- 活动表
DROP TABLE IF EXISTS activity_info;
CREATE TABLE activity_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '活动ID',
    activity_name VARCHAR(200) NOT NULL COMMENT '活动名称',
    activity_type TINYINT DEFAULT 1 COMMENT '活动类型：1-征文比赛 2-短视频 3-知识竞赛 4-讲座',
    cover_image VARCHAR(255) COMMENT '封面图',
    description TEXT COMMENT '活动描述',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    max_participants INT COMMENT '最大参与人数',
    current_participants INT DEFAULT 0 COMMENT '当前参与人数',
    points_reward INT COMMENT '参与积分奖励',
    status TINYINT DEFAULT 1 COMMENT '状态：0-草稿 1-报名中 2-进行中 3-已结束',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动表';

-- 插入活动
INSERT INTO activity_info (activity_name, activity_type, description, start_time, end_time, max_participants, points_reward, status) VALUES
('反诈知识竞赛', 3, '参与知识竞赛，测试你的反诈能力', DATE_ADD(NOW(), INTERVAL 7 DAY), DATE_ADD(NOW(), INTERVAL 14 DAY), 500, 50, 1),
('反诈短视频大赛', 2, '拍摄反诈短视频，传播反诈知识', DATE_ADD(NOW(), INTERVAL 5 DAY), DATE_ADD(NOW(), INTERVAL 20 DAY), 200, 100, 1),
('线下反诈讲座', 4, '专家现场讲解反诈知识', DATE_ADD(NOW(), INTERVAL 10 DAY), DATE_ADD(NOW(), INTERVAL 10 DAY), 100, 30, 1);

-- 积分记录表
DROP TABLE IF EXISTS points_record;
CREATE TABLE points_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    points INT NOT NULL COMMENT '积分变动',
    type TINYINT COMMENT '类型：1-获取 2-消耗',
    source VARCHAR(50) COMMENT '来源',
    source_id BIGINT COMMENT '来源ID',
    description VARCHAR(200) COMMENT '描述',
    balance_after INT COMMENT '变动后余额',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分记录表';

-- 勋章表
DROP TABLE IF EXISTS badge_info;
CREATE TABLE badge_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '勋章ID',
    badge_name VARCHAR(100) NOT NULL COMMENT '勋章名称',
    badge_type TINYINT COMMENT '类型：1-学习 2-测试 3-演练 4-特殊',
    badge_icon VARCHAR(255) COMMENT '勋章图标',
    description VARCHAR(500) COMMENT '描述',
    points_reward INT COMMENT '奖励积分',
    status TINYINT DEFAULT 1 COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='勋章表';

-- 插入勋章
INSERT INTO badge_info (badge_name, badge_type, description, points_reward) VALUES
('初学者', 1, '完成首次学习', 10),
('勤奋学习者', 1, '累计学习100小时', 100),
('满分达人', 2, '获得10次测试满分', 200),
('场景大师', 3, '通关所有演练场景', 300),
('举报先锋', 4, '有效举报10次', 150);

-- 用户勋章表
DROP TABLE IF EXISTS user_badge;
CREATE TABLE user_badge (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    badge_id BIGINT NOT NULL COMMENT '勋章ID',
    acquire_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '获得时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户勋章表';

-- 收藏表
DROP TABLE IF EXISTS user_collection;
CREATE TABLE user_collection (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '收藏ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    target_id BIGINT NOT NULL COMMENT '目标ID',
    target_type TINYINT DEFAULT 1 COMMENT '目标类型：1-知识 2-视频 3-案例',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_target (user_id, target_id, target_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- 学习记录表
DROP TABLE IF EXISTS study_record;
CREATE TABLE study_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    knowledge_id BIGINT NOT NULL COMMENT '知识ID',
    study_duration INT DEFAULT 0 COMMENT '学习时长(秒)',
    progress TINYINT DEFAULT 0 COMMENT '学习进度(%)',
    is_completed TINYINT DEFAULT 0 COMMENT '是否完成',
    last_study_time DATETIME COMMENT '最后学习时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_knowledge (user_id, knowledge_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习记录表';

-- 测试记录表
DROP TABLE IF EXISTS test_record;
CREATE TABLE test_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    paper_id BIGINT NOT NULL COMMENT '试卷ID',
    total_score INT COMMENT '总分',
    user_score DECIMAL(5,2) COMMENT '用户得分',
    correct_count INT COMMENT '正确题数',
    wrong_count INT COMMENT '错误题数',
    duration INT COMMENT '答题时长(秒)',
    is_completed TINYINT DEFAULT 0 COMMENT '是否完成',
    is_passed TINYINT DEFAULT 0 COMMENT '是否及格',
    start_time DATETIME COMMENT '开始时间',
    submit_time DATETIME COMMENT '提交时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='测试记录表';

-- 演练记录表
DROP TABLE IF EXISTS simulation_record;
CREATE TABLE simulation_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    scene_id BIGINT NOT NULL COMMENT '场景ID',
    score DECIMAL(5,2) COMMENT '得分',
    duration INT COMMENT '演练时长(秒)',
    is_completed TINYINT DEFAULT 0 COMMENT '是否完成',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='演练记录表';

-- 活动报名表
DROP TABLE IF EXISTS activity_registration;
CREATE TABLE activity_registration (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    activity_id BIGINT NOT NULL COMMENT '活动ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    status TINYINT DEFAULT 1 COMMENT '状态：0-待审核 1-已通过',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_activity_user (activity_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动报名表';
