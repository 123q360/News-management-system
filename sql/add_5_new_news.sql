-- 新增5条新闻使用剩余5张图片
USE Hadoop;

-- 新增科技类新闻2条（使用剩余的2张科技图片）
INSERT INTO news (category, title, content, cover_image, author, view_count, like_count, share_count, comment_count, publish_time) VALUES
('tech', '芯片技术突破：3纳米工艺量产在即', '半导体制造技术持续突破，3纳米芯片工艺即将实现量产，更强大的算力、更低的功耗，将推动智能设备性能再上新台阶...', '/images/科技/premium_photo-1683120963435-6f9355d4a776.jpg', '赵强', 0, 0, 0, 0, NOW()),
('tech', '自动驾驶技术迈入L4级别新阶段', '多家科技公司的自动驾驶系统达到L4级别，在限定区域实现完全无人驾驶，标志着智能出行时代即将到来...', '/images/科技/premium_photo-1683120972279-87efe2ba252f.jpg', '刘洋', 0, 0, 0, 0, NOW());

-- 新增运动类新闻1条（使用剩余的1张运动图片）
INSERT INTO news (category, title, content, cover_image, author, view_count, like_count, share_count, comment_count, publish_time) VALUES
('sports', '足球青训体系改革：培养未来之星', '中国足球青训体系全面改革，引入先进训练理念和科学培养模式，为中国足球未来发展储备人才...', '/images/运动/photo-1612872087720-bb876e2e67d1.jpg', '吴涛', 0, 0, 0, 0, NOW());

-- 新增游戏类新闻1条（使用剩余的1张游戏图片）
INSERT INTO news (category, title, content, cover_image, author, view_count, like_count, share_count, comment_count, publish_time) VALUES
('game', '独立游戏崛起：创意作品赢得市场认可', '越来越多的独立游戏凭借独特创意和精良制作脱颖而出，小团队大作品正在改写游戏行业格局...', '/images/游戏/photo-1593305841991-05c297ba4575.jpg', '林峰', 0, 0, 0, 0, NOW());

-- 新增政治类新闻1条（使用剩余的1张政治图片）
INSERT INTO news (category, title, content, cover_image, author, view_count, like_count, share_count, comment_count, publish_time) VALUES
('politics', '基层治理现代化：数字政务惠民便民', '数字政务平台全面推广，群众办事更加便捷高效，基层治理能力和水平显著提升...', '/images/政治/premium_photo-1683140843967-4020783d15cc.jpg', '谢娟', 0, 0, 0, 0, NOW());

-- 验证插入结果
SELECT '新增新闻统计：' AS '';
SELECT category, COUNT(*) as count FROM news GROUP BY category;

SELECT '所有新闻列表：' AS '';
SELECT id, category, title, LEFT(cover_image, 50) as cover_image FROM news ORDER BY id;
