-- 创建数据库触发器，自动更新用户偏好类别
-- 当 user_preference 表更新时，自动更新对应用户的 preference_category

USE Hadoop;

-- 删除旧触发器（如果存在）
DROP TRIGGER IF EXISTS update_user_preference_category;

-- 创建触发器：在 user_preference 更新后执行
DELIMITER $$

CREATE TRIGGER update_user_preference_category
AFTER UPDATE ON user_preference
FOR EACH ROW
BEGIN
    DECLARE new_category VARCHAR(20);
    DECLARE max_value INT;
    DECLARE count_max INT;
    
    -- 找出最大值
    SET max_value = GREATEST(NEW.tech, NEW.sports, NEW.game, NEW.politics);
    
    -- 如果最大值为0，设为"无"
    IF max_value = 0 THEN
        SET new_category = '无';
    ELSE
        -- 统计有多少个类别达到最大值
        SET count_max = 0;
        SET new_category = '无';
        
        IF NEW.tech = max_value THEN
            SET count_max = count_max + 1;
            SET new_category = 'tech';
        END IF;
        
        IF NEW.sports = max_value THEN
            SET count_max = count_max + 1;
            SET new_category = 'sports';
        END IF;
        
        IF NEW.game = max_value THEN
            SET count_max = count_max + 1;
            SET new_category = 'game';
        END IF;
        
        IF NEW.politics = max_value THEN
            SET count_max = count_max + 1;
            SET new_category = 'politics';
        END IF;
        
        -- 如果多个类别权值相同，设为"无"
        IF count_max > 1 THEN
            SET new_category = '无';
        END IF;
    END IF;
    
    -- 更新用户表
    UPDATE user 
    SET preference_category = new_category 
    WHERE preference_id = NEW.id;
END$$

DELIMITER ;

-- 测试：查看触发器是否创建成功
SHOW TRIGGERS LIKE 'user_preference';

-- 显示提示信息
SELECT '触发器创建成功！现在修改 user_preference 表会自动更新 user 表的 preference_category 字段' AS '提示';
