-- 更新现有12条新闻的封面图片为本地图片
USE Hadoop;

-- 假设现有新闻ID为1-12，按照类别更新封面图片
-- 科技类新闻（假设ID 1-3）更新为科技类图片
UPDATE news SET cover_image = '/images/科技/file-1719664959749-d56c4ff96871image.jpg' WHERE id = 1;
UPDATE news SET cover_image = '/images/科技/photo-1581092795360-fd1ca04f0952.jpg' WHERE id = 2;
UPDATE news SET cover_image = '/images/科技/premium_photo-1661963212517-830bbb7d76fc.jpg' WHERE id = 3;

-- 运动类新闻（假设ID 4-6）更新为运动类图片
UPDATE news SET cover_image = '/images/运动/photo-1461896836934-ffe607ba8211.jpg' WHERE id = 4;
UPDATE news SET cover_image = '/images/运动/photo-1522778119026-d647f0596c20.jpg' WHERE id = 5;
UPDATE news SET cover_image = '/images/运动/photo-1563299796-b729d0af54a5.jpg' WHERE id = 6;

-- 游戏类新闻（假设ID 7-9）更新为游戏类图片
UPDATE news SET cover_image = '/images/游戏/photo-1493711662062-fa541adb3fc8.jpg' WHERE id = 7;
UPDATE news SET cover_image = '/images/游戏/photo-1538481199705-c710c4e965fc.jpg' WHERE id = 8;
UPDATE news SET cover_image = '/images/游戏/photo-1560419015-7c427e8ae5ba.jpg' WHERE id = 9;

-- 政治类新闻（假设ID 10-12）更新为政治类图片
UPDATE news SET cover_image = '/images/政治/photo-1607778102165-6a418ee9adf2.jpg' WHERE id = 10;
UPDATE news SET cover_image = '/images/政治/photo-1607778417094-1fef13315e6e.jpg' WHERE id = 11;
UPDATE news SET cover_image = '/images/政治/photo-1637102134162-7dc2c4995c22.jpg' WHERE id = 12;

-- 验证更新结果
SELECT id, category, title, cover_image FROM news WHERE id <= 12 ORDER BY id;
