-- 新增更多新闻数据，使用本地图片
USE Hadoop;

-- 科技类新闻（5张图片）
INSERT INTO news (category, title, content, cover_image, author, view_count, like_count, share_count, comment_count) VALUES
('tech', '人工智能重塑未来：深度学习技术新突破', 'AI技术在计算机视觉、自然语言处理等领域取得重大进展，新一代深度学习模型展现出惊人的能力，正在重塑各行各业的发展模式...', '/images/科技/file-1719664959749-d56c4ff96871image.jpg', '李明', 0, 0, 0, 0),
('tech', '量子计算机商业化进程加速', '全球科技巨头纷纷加大量子计算研发投入，量子计算机的实用化进程正在加速，预计未来5年内将在特定领域实现商业化应用...', '/images/科技/photo-1581092795360-fd1ca04f0952.jpg', '王芳', 0, 0, 0, 0),
('tech', '5G+物联网开启智慧城市新时代', '5G技术与物联网深度融合，智慧交通、智慧医疗、智慧能源等应用场景加速落地，为城市管理和居民生活带来革命性变化...', '/images/科技/premium_photo-1661963212517-830bbb7d76fc.jpg', '张伟', 0, 0, 0, 0),
('tech', '芯片技术突破：3纳米工艺量产在即', '半导体制造技术持续突破，3纳米芯片工艺即将实现量产，更强大的算力、更低的功耗，将推动智能设备性能再上新台阶...', '/images/科技/premium_photo-1683120963435-6f9355d4a776.jpg', '赵强', 0, 0, 0, 0),
('tech', '自动驾驶技术迈入L4级别新阶段', '多家科技公司的自动驾驶系统达到L4级别，在限定区域实现完全无人驾驶，标志着智能出行时代即将到来...', '/images/科技/premium_photo-1683120972279-87efe2ba252f.jpg', '刘洋', 0, 0, 0, 0);

-- 运动类新闻（4张图片）
INSERT INTO news (category, title, content, cover_image, author, view_count, like_count, share_count, comment_count) VALUES
('sports', '马拉松热潮席卷全国：健康生活新风尚', '全国各地马拉松赛事如火如荼，跑步健身成为新时尚，越来越多的人加入到跑步大军中，享受运动带来的健康与快乐...', '/images/运动/photo-1461896836934-ffe607ba8211.jpg', '陈静', 0, 0, 0, 0),
('sports', '篮球世界杯精彩对决：中国队表现出色', '篮球世界杯小组赛激战正酣，中国男篮展现出色竞技状态，球员们的精彩表现赢得球迷热烈掌声...', '/images/运动/photo-1522778119026-d647f0596c20.jpg', '孙杰', 0, 0, 0, 0),
('sports', '冬奥会回顾：冰雪运动走进千家万户', '冬奥会的成功举办激发了全民参与冰雪运动的热情，滑雪场、溜冰场人气爆棚，冰雪运动正在成为新的消费热点...', '/images/运动/photo-1563299796-b729d0af54a5.jpg', '周敏', 0, 0, 0, 0),
('sports', '足球青训体系改革：培养未来之星', '中国足球青训体系全面改革，引入先进训练理念和科学培养模式，为中国足球未来发展储备人才...', '/images/运动/photo-1612872087720-bb876e2e67d1.jpg', '吴涛', 0, 0, 0, 0);

-- 游戏类新闻（4张图片）
INSERT INTO news (category, title, content, cover_image, author, view_count, like_count, share_count, comment_count) VALUES
('game', '开放世界大作震撼发布：玩家好评如潮', '年度最受期待的开放世界游戏正式发布，精美的画面、丰富的剧情、自由的玩法，让玩家沉浸其中无法自拔...', '/images/游戏/photo-1493711662062-fa541adb3fc8.jpg', '郑浩', 0, 0, 0, 0),
('game', '电竞产业蓬勃发展：职业选手收入创新高', '电子竞技产业规模持续扩大，职业选手薪酬水平大幅提升，电竞正在成为一项真正的体育事业...', '/images/游戏/photo-1538481199705-c710c4e965fc.jpg', '钱磊', 0, 0, 0, 0),
('game', 'VR游戏技术革新：沉浸式体验更上层楼', '虚拟现实技术在游戏领域的应用取得重大突破，新一代VR设备带来更加逼真的沉浸式游戏体验...', '/images/游戏/photo-1560419015-7c427e8ae5ba.jpg', '孙莉', 0, 0, 0, 0),
('game', '独立游戏崛起：创意作品赢得市场认可', '越来越多的独立游戏凭借独特创意和精良制作脱颖而出，小团队大作品正在改写游戏行业格局...', '/images/游戏/photo-1593305841991-05c297ba4575.jpg', '林峰', 0, 0, 0, 0);

-- 政治类新闻（4张图片）
INSERT INTO news (category, title, content, cover_image, author, view_count, like_count, share_count, comment_count) VALUES
('politics', '全国两会胜利召开：规划发展新蓝图', '全国两会在北京隆重开幕，代表委员们齐聚一堂，共商国是，为国家未来发展建言献策...', '/images/政治/photo-1607778102165-6a418ee9adf2.jpg', '黄勇', 0, 0, 0, 0),
('politics', '外交新突破：多边合作成果丰硕', '我国积极开展多边外交活动，与多个国家和国际组织达成重要合作协议，为全球治理贡献中国智慧...', '/images/政治/photo-1607778417094-1fef13315e6e.jpg', '徐丽', 0, 0, 0, 0),
('politics', '法治建设新进展：完善立法保障民生', '多项重要法律法规出台实施，法治体系不断完善，为经济社会发展和人民权益保障提供坚实法律支撑...', '/images/政治/photo-1637102134162-7dc2c4995c22.jpg', '何建', 0, 0, 0, 0),
('politics', '基层治理现代化：数字政务惠民便民', '数字政务平台全面推广，群众办事更加便捷高效，基层治理能力和水平显著提升...', '/images/政治/premium_photo-1683140843967-4020783d15cc.jpg', '谢娟', 0, 0, 0, 0);

-- 验证插入结果
SELECT '新增新闻数据统计：' AS '';
SELECT category, COUNT(*) as count FROM news GROUP BY category;

SELECT '所有新闻列表：' AS '';
SELECT id, category, title, LEFT(cover_image, 50) as cover_image FROM news ORDER BY id;
