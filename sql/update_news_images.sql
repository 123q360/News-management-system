-- 更新新闻封面图片链接
-- 使用稳定的随机图片服务

USE Hadoop;

-- 更新所有新闻的封面图片为随机图片
UPDATE news SET cover_image = CONCAT('https://picsum.photos/800/400?random=', id) WHERE id > 0;

-- 或者按类别设置不同的图片
UPDATE news SET cover_image = 'https://picsum.photos/800/400?tech' WHERE category = 'tech';
UPDATE news SET cover_image = 'https://picsum.photos/800/400?sports' WHERE category = 'sports';
UPDATE news SET cover_image = 'https://picsum.photos/800/400?game' WHERE category = 'game';
UPDATE news SET cover_image = 'https://picsum.photos/800/400?politics' WHERE category = 'politics';

-- 查看更新结果
SELECT id, title, category, cover_image FROM news LIMIT 10;
