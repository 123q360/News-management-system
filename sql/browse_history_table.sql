-- 浏览历史表
USE Hadoop;

CREATE TABLE IF NOT EXISTS browse_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '历史记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    news_id BIGINT NOT NULL COMMENT '新闻ID',
    news_title VARCHAR(200) COMMENT '新闻标题（冗余字段，方便展示）',
    news_category VARCHAR(50) COMMENT '新闻类别（冗余字段）',
    device VARCHAR(20) DEFAULT 'web' COMMENT '设备类型：pc/mobile/tablet/web',
    browse_time DATETIME NOT NULL COMMENT '浏览时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    INDEX idx_user_id (user_id),
    INDEX idx_browse_time (browse_time),
    INDEX idx_user_browse (user_id, browse_time),
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (news_id) REFERENCES news(id) ON DELETE CASCADE
) COMMENT '用户浏览历史表';

-- 插入测试数据
INSERT INTO browse_history (user_id, news_id, news_title, news_category, device, browse_time) VALUES
(1, 1, 'AI技术突破性进展', 'tech', 'web', '2025-12-01 10:00:00'),
(1, 2, '世界杯精彩瞬间', 'sports', 'web', '2025-12-01 11:30:00'),
(1, 3, '热门游戏新版本发布', 'game', 'mobile', '2025-12-01 14:20:00'),
(2, 1, 'AI技术突破性进展', 'tech', 'web', '2025-12-01 15:00:00'),
(2, 4, '国际政治局势分析', 'politics', 'pc', '2025-12-01 16:00:00');

-- 查看结果
SELECT 
    bh.id,
    u.username,
    bh.news_title,
    bh.news_category,
    bh.device,
    bh.browse_time
FROM browse_history bh
JOIN user u ON bh.user_id = u.id
ORDER BY bh.browse_time DESC;
