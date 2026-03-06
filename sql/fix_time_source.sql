-- 统一时间来源：移除MySQL自动生成时间，改为Java应用设置
-- 这样可以避免虚拟机时间错误的影响

USE Hadoop;

-- 先检查表结构，只修改存在的字段

-- 1. 修改 user 表（只有create_time，没有update_time）
ALTER TABLE user 
    MODIFY COLUMN create_time DATETIME NOT NULL COMMENT '创建时间';

-- 2. 修改 user_preference 表
ALTER TABLE user_preference 
    MODIFY COLUMN create_time DATETIME NOT NULL COMMENT '创建时间',
    MODIFY COLUMN update_time DATETIME NULL COMMENT '更新时间';

-- 3. 修改 news 表
ALTER TABLE news 
    MODIFY COLUMN publish_time DATETIME NOT NULL COMMENT '发布时间',
    MODIFY COLUMN update_time DATETIME NULL COMMENT '更新时间';

-- 4. 修改 comment 表
ALTER TABLE comment 
    MODIFY COLUMN create_time DATETIME NOT NULL COMMENT '创建时间',
    MODIFY COLUMN update_time DATETIME NULL COMMENT '更新时间';

-- 5. 修改 browse_history 表
ALTER TABLE browse_history 
    MODIFY COLUMN create_time DATETIME NOT NULL COMMENT '创建时间';

-- 验证修改结果
SHOW CREATE TABLE user;
SHOW CREATE TABLE user_preference;
SHOW CREATE TABLE news;
SHOW CREATE TABLE comment;
SHOW CREATE TABLE browse_history;

SELECT '时间字段已统一改为由Java应用设置，不再依赖MySQL服务器时间' AS message;
