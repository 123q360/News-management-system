-- 为现有数据库添加 preference_category 字段
-- 如果您的数据库中已经有数据，需要执行此脚本

USE Hadoop;

-- 1. 添加 preference_category 字段（如果不存在）
-- MySQL 不支持 ADD COLUMN IF NOT EXISTS，所以先检查是否存在
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = 'Hadoop' 
  AND TABLE_NAME = 'user' 
  AND COLUMN_NAME = 'preference_category';

SET @sql = IF(@col_exists = 0, 
    'ALTER TABLE user ADD COLUMN preference_category VARCHAR(20) DEFAULT ''无'' COMMENT ''用户偏好类别：tech/sports/game/politics/无'' AFTER role',
    'SELECT ''字段已存在，跳过添加'' AS message');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 2. 为现有用户更新偏好类别
-- 这个脚本会根据用户偏好表计算出偏好类别并更新

UPDATE user u
JOIN user_preference up ON u.preference_id = up.id
SET u.preference_category = CASE
    -- 如果所有权值都为0，设为"无"
    WHEN up.tech = 0 AND up.sports = 0 AND up.game = 0 AND up.politics = 0 THEN '无'
    
    -- 如果tech最高且唯一
    WHEN up.tech > up.sports AND up.tech > up.game AND up.tech > up.politics THEN 'tech'
    
    -- 如果sports最高且唯一
    WHEN up.sports > up.tech AND up.sports > up.game AND up.sports > up.politics THEN 'sports'
    
    -- 如果game最高且唯一
    WHEN up.game > up.tech AND up.game > up.sports AND up.game > up.politics THEN 'game'
    
    -- 如果politics最高且唯一
    WHEN up.politics > up.tech AND up.politics > up.sports AND up.politics > up.game THEN 'politics'
    
    -- 其他情况（多个权值相同），设为"无"
    ELSE '无'
END
WHERE u.preference_id IS NOT NULL;

-- 3. 查看更新结果
SELECT 
    u.id,
    u.username,
    u.preference_category,
    up.tech,
    up.sports,
    up.game,
    up.politics
FROM user u
LEFT JOIN user_preference up ON u.preference_id = up.id
ORDER BY u.id;
