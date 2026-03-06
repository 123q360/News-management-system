-- 刷新所有用户的偏好类别
-- 当您手动修改了 user_preference 表后，可以执行此脚本更新 user 表

USE Hadoop;

-- 更新所有用户的偏好类别
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

-- 查看更新后的结果
SELECT 
    u.id,
    u.username,
    u.preference_category AS '偏好类别',
    up.tech AS '科技',
    up.sports AS '运动',
    up.game AS '游戏',
    up.politics AS '政治'
FROM user u
LEFT JOIN user_preference up ON u.preference_id = up.id
ORDER BY u.id;
