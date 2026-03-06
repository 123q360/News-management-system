-- 评论表
USE Hadoop;

CREATE TABLE IF NOT EXISTS comment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
    news_id BIGINT NOT NULL COMMENT '新闻ID',
    user_id BIGINT NOT NULL COMMENT '评论用户ID',
    parent_id BIGINT DEFAULT NULL COMMENT '父评论ID（NULL表示顶级评论）',
    root_id BIGINT DEFAULT NULL COMMENT '根评论ID（方便查询整个评论树）',
    reply_to_user_id BIGINT DEFAULT NULL COMMENT '回复给谁（用于显示@用户名）',
    content TEXT NOT NULL COMMENT '评论内容',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    reply_count INT DEFAULT 0 COMMENT '回复数（冗余字段，提升性能）',
    status VARCHAR(20) DEFAULT 'NORMAL' COMMENT '状态：NORMAL/DELETED/HIDDEN',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_news_id (news_id),
    INDEX idx_user_id (user_id),
    INDEX idx_parent_id (parent_id),
    INDEX idx_root_id (root_id),
    INDEX idx_create_time (create_time),
    
    FOREIGN KEY (news_id) REFERENCES news(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
) COMMENT '评论表';

-- 为news表添加评论数字段
-- MySQL 不支持 ADD COLUMN IF NOT EXISTS，所以先检查是否存在
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = 'Hadoop' 
  AND TABLE_NAME = 'news' 
  AND COLUMN_NAME = 'comment_count';

SET @sql = IF(@col_exists = 0, 
    'ALTER TABLE news ADD COLUMN comment_count INT DEFAULT 0 COMMENT ''评论数'' AFTER dislike_count',
    'SELECT ''字段已存在，跳过添加'' AS message');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 插入测试数据
INSERT INTO comment (news_id, user_id, parent_id, root_id, content, like_count, status) VALUES
-- 新闻1的评论
(1, 1, NULL, NULL, '这篇AI文章分析得很透彻！', 25, 'NORMAL'),
(1, 2, 1, 1, '确实，技术发展太快了', 10, 'NORMAL'),
(1, 1, 1, 1, '期待未来的突破', 5, 'NORMAL'),
-- 新闻2的评论
(2, 2, NULL, NULL, '体育新闻报道及时', 15, 'NORMAL'),
(2, 1, 4, 4, '比赛很精彩', 8, 'NORMAL');

-- 更新评论数
UPDATE comment c1 
LEFT JOIN (
    SELECT parent_id, COUNT(*) as cnt 
    FROM comment 
    WHERE parent_id IS NOT NULL
    GROUP BY parent_id
) c2 ON c1.id = c2.parent_id
SET c1.reply_count = IFNULL(c2.cnt, 0)
WHERE c1.parent_id IS NULL;

-- 更新新闻评论数
UPDATE news n 
SET comment_count = (
    SELECT COUNT(*) FROM comment c 
    WHERE c.news_id = n.id AND c.status = 'NORMAL'
);

-- 查看结果
SELECT 
    c.id,
    c.news_id,
    u.username,
    c.content,
    c.like_count,
    c.reply_count,
    c.create_time
FROM comment c
JOIN user u ON c.user_id = u.id
ORDER BY c.create_time DESC;
