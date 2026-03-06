-- 新闻系统数据库初始化脚本
-- 数据库：Hadoop

USE Hadoop;

-- 1. 创建用户偏好表
CREATE TABLE IF NOT EXISTS user_preference (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tech INT DEFAULT 0 COMMENT '科技类偏好权值',
    sports INT DEFAULT 0 COMMENT '运动类偏好权值',
    game INT DEFAULT 0 COMMENT '游戏类偏好权值',
    politics INT DEFAULT 0 COMMENT '政治类偏好权值',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '用户偏好表';

-- 2. 创建用户表
CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    preference_id BIGINT,
    role VARCHAR(20) DEFAULT 'USER' COMMENT 'USER或ADMIN',
    preference_category VARCHAR(20) DEFAULT '无' COMMENT '用户偏好类别：tech/sports/game/politics/无',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (preference_id) REFERENCES user_preference(id) ON DELETE SET NULL
) COMMENT '用户表';

-- 3. 创建新闻表
CREATE TABLE IF NOT EXISTS news (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category VARCHAR(50) NOT NULL COMMENT '类别：tech/sports/game/politics',
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    cover_image VARCHAR(255),
    view_count INT DEFAULT 0 COMMENT '浏览量',
    like_count INT DEFAULT 0 COMMENT '点赞量',
    share_count INT DEFAULT 0 COMMENT '转发量',
    dislike_count INT DEFAULT 0 COMMENT '不感兴趣量',
    author VARCHAR(50),
    publish_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'PUBLISHED' COMMENT 'PUBLISHED/DRAFT/DELETED'
) COMMENT '新闻表';

-- 创建索引提高查询性能
CREATE INDEX idx_category ON news(category);
CREATE INDEX idx_status ON news(status);
CREATE INDEX idx_publish_time ON news(publish_time);

-- 插入测试数据

-- 插入管理员用户偏好
INSERT INTO user_preference (tech, sports, game, politics) VALUES (0, 0, 0, 0);
SET @admin_pref_id = LAST_INSERT_ID();

-- 插入管理员账户（密码：admin123，实际使用时应该加密）
INSERT INTO user (username, password, preference_id, role) 
VALUES ('admin', 'admin123', @admin_pref_id, 'ADMIN');

-- 插入普通用户偏好
INSERT INTO user_preference (tech, sports, game, politics) VALUES (0, 0, 0, 0);
SET @user1_pref_id = LAST_INSERT_ID();

-- 插入测试用户（密码：123456）
INSERT INTO user (username, password, preference_id, role) 
VALUES ('testuser', '123456', @user1_pref_id, 'USER');

-- 插入测试新闻数据

-- 科技类新闻
INSERT INTO news (category, title, content, cover_image, author) VALUES
('tech', 'AI技术取得重大突破', '人工智能领域近日取得重大进展，新的深度学习算法在多个任务上超越了人类水平...', 'https://via.placeholder.com/300x200', '张三'),
('tech', '5G网络全面商用', '第五代移动通信技术已经在全国范围内实现全面商用，为用户带来更快的网络体验...', 'https://via.placeholder.com/300x200', '李四'),
('tech', '量子计算机研发成功', '我国科学家成功研发出具有实用价值的量子计算机，计算能力大幅提升...', 'https://via.placeholder.com/300x200', '王五');

-- 运动类新闻
INSERT INTO news (category, title, content, cover_image, author) VALUES
('sports', '国足晋级世界杯', '中国国家足球队在预选赛中表现出色，成功晋级世界杯决赛圈...', 'https://via.placeholder.com/300x200', '赵六'),
('sports', 'NBA总决赛精彩对决', 'NBA总决赛进入白热化阶段，两队实力旗鼓相当，比分交替上升...', 'https://via.placeholder.com/300x200', '孙七'),
('sports', '奥运会金牌榜更新', '中国代表团在奥运会上再创佳绩，金牌数继续领跑...', 'https://via.placeholder.com/300x200', '周八');

-- 游戏类新闻
INSERT INTO news (category, title, content, cover_image, author) VALUES
('game', '年度最佳游戏发布', '备受期待的年度最佳游戏正式发布，玩家评价极高...', 'https://via.placeholder.com/300x200', '吴九'),
('game', '电竞选手夺得世界冠军', '中国电竞选手在世界大赛中夺得冠军，为国争光...', 'https://via.placeholder.com/300x200', '郑十'),
('game', '游戏产业市场分析', '全球游戏产业市场规模持续扩大，移动游戏占据主导地位...', 'https://via.placeholder.com/300x200', '陈十一');

-- 政治类新闻
INSERT INTO news (category, title, content, cover_image, author) VALUES
('politics', '国家政策新调整', '国家发布新的政策文件，对相关产业进行调整和优化...', 'https://via.placeholder.com/300x200', '刘十二'),
('politics', '国际关系新进展', '我国与多个国家建立更紧密的合作关系，推动共同发展...', 'https://via.placeholder.com/300x200', '黄十三'),
('politics', '重要会议顺利召开', '全国性重要会议在北京顺利召开，讨论未来发展规划...', 'https://via.placeholder.com/300x200', '林十四');

-- 查询验证
SELECT '用户表数据：' AS '';
SELECT * FROM user;

SELECT '用户偏好表数据：' AS '';
SELECT * FROM user_preference;

SELECT '新闻表数据：' AS '';
SELECT id, category, title, author, view_count, like_count FROM news;
