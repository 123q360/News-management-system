# 评论功能实现指南

## 🎉 功能概述

已成功实现完整的评论功能模块，包括：
- ✅ 发表评论
- ✅ 回复评论（支持多级）
- ✅ 点赞评论
- ✅ 删除评论
- ✅ 与用户偏好系统集成
- ✅ 与Hadoop数据分析集成

---

## 📋 实现清单

### 1. 数据库表
**文件**: `comment_table.sql`

```sql
CREATE TABLE comment (
    id, news_id, user_id, parent_id, root_id,
    reply_to_user_id, content, like_count, reply_count,
    status, create_time, update_time
)
```

**关键字段说明**：
- `parent_id`: 父评论ID（NULL表示顶级评论）
- `root_id`: 根评论ID（方便查询整个评论树）
- `reply_to_user_id`: 回复给谁（用于显示@用户名）
- `status`: NORMAL/DELETED/HIDDEN

### 2. 后端代码

#### Entity层
- `Comment.java` - 评论实体类

#### DAO层  
- `CommentDao.java` - 评论数据访问
  - 插入评论
  - 查询顶级评论（分页）
  - 查询回复列表
  - 点赞、删除等操作

#### Service层
- `CommentService.java` - 评论业务逻辑
  - `addComment()` - 发表评论
  - `getComments()` - 获取评论列表（带回复）
  - `likeComment()` - 点赞
  - `deleteComment()` - 软删除

#### Controller层
- `CommentController.java` - 评论接口
  - `POST /api/comments` - 发表评论
  - `GET /api/comments?newsId=1&page=1` - 获取评论
  - `POST /api/comments/{id}/like` - 点赞
  - `DELETE /api/comments/{id}` - 删除

### 3. 前端页面

**文件**: `news-detail.html`

**新增功能**：
- 评论输入框（字数统计500字）
- 评论列表展示（顶级评论+前3条回复）
- 回复功能（点击回复显示输入框）
- 点赞功能
- 删除功能（仅自己或管理员）
- 加载更多评论

---

## 🔌 API接口文档

### 1. 发表评论

```http
POST /api/comments
Content-Type: application/json

{
    "newsId": 1,
    "content": "这篇文章不错",
    "parentId": null,        // 回复评论时填写
    "replyToUserId": null    // 回复评论时填写
}
```

**返回**：
```json
{
    "success": true,
    "data": {
        "id": 123,
        "content": "这篇文章不错",
        "createTime": "2025-12-01T21:00:00"
    }
}
```

### 2. 获取评论列表

```http
GET /api/comments?newsId=1&page=1&size=10&sort=time
```

**参数**：
- `newsId`: 新闻ID（必填）
- `page`: 页码（默认1）
- `size`: 每页数量（默认10）
- `sort`: 排序方式 time/hot（默认time）

**返回**：
```json
{
    "success": true,
    "data": {
        "total": 50,
        "comments": [
            {
                "id": 1,
                "userId": 3,
                "username": "张三",
                "content": "这篇文章不错",
                "likeCount": 25,
                "replyCount": 5,
                "createTime": "2025-12-01T21:00:00",
                "replies": [  // 前3条回复
                    {
                        "id": 2,
                        "username": "李四",
                        "replyToUsername": "张三",
                        "content": "我也觉得",
                        ...
                    }
                ]
            }
        ]
    }
}
```

### 3. 点赞评论

```http
POST /api/comments/123/like
```

### 4. 删除评论

```http
DELETE /api/comments/123
```

---

## 🎯 功能特性

### 1. 多级回复支持

```
顶级评论1
  ├─ 回复1 @顶级评论1
  ├─ 回复2 @顶级评论1
  └─ 回复3 @回复2
```

**实现方式**：
- 使用 `parent_id` 和 `root_id` 实现
- 前端只展示2层，超过的折叠
- 点击"查看更多回复"加载完整回复树

### 2. 与用户偏好系统集成

**评论行为权值+3**（比浏览的+1和点赞的+2都高）

```java
// CommentService.addComment()
userPreferenceDao.updatePreference(
    user.getPreferenceId(), 
    news.getCategory(), 
    3  // 评论权值
);

// 自动更新用户偏好类别
String category = userPreferenceDao.calculatePreferenceCategory(preferenceId);
userDao.updatePreferenceCategory(userId, category);
```

**意义**：
- 评论表示用户对某类新闻的深度兴趣
- 权值最高，对推荐系统影响最大
- 自动更新 `user.preference_category` 字段

### 3. 与Hadoop数据分析集成

**评论行为发送到HDFS**：

```java
// CommentService.sendToHadoop()
UserBehavior behavior = new UserBehavior();
behavior.setAction("comment");  // 新增comment行为
behavior.setUserId("3");
behavior.setArticleId("1");
behavior.setTimestamp("2025-12-01T21:00:00.000Z");
behavior.setDevice("web");

dataCleanService.cleanAndSend([behavior]);
```

**数据流**：
```
评论行为 → Flume → HDFS → MapReduce → MySQL统计表
```

**可分析内容**：
- 哪些文章评论最多
- 哪些用户最活跃
- 评论高峰时段
- 评论与浏览/点赞的关联

---

## 💡 使用步骤

### 1. 初始化数据库

```bash
mysql -u root -p < comment_table.sql
```

这会：
- ✅ 创建 `comment` 表
- ✅ 为 `news` 表添加 `comment_count` 字段
- ✅ 插入测试评论数据

### 2. 启动应用

```bash
mvn spring-boot:run
```

### 3. 测试评论功能

1. 访问新闻详情页：
   ```
   http://localhost:8080/news-detail.html?id=1
   ```

2. 在评论框输入内容，点击"发表评论"

3. 点击其他评论的"回复"按钮进行回复

4. 点击👍图标为评论点赞

5. 如果是自己的评论或管理员，可以看到删除按钮

---

## 🎨 界面展示

```
┌─────────────────────────────────────────────────┐
│  评论 (50)                                      │
├─────────────────────────────────────────────────┤
│  ┌────────────────────────────────────────────┐ │
│  │  写下你的评论...                            │ │
│  │                                             │ │
│  └────────────────────────────────────────────┘ │
│  0/500                          [发表评论]      │
├─────────────────────────────────────────────────┤
│  👤 张三                           2小时前       │
│     这篇文章分析得很透彻！                       │
│     👍 128  💬 回复  🗑️ 删除                   │
│                                                 │
│     ↳ 👤 李四 回复 @张三           1小时前      │
│        确实，我也学到了很多                     │
│                                                 │
│     ↳ 👤 王五 回复 @李四           30分钟前     │
│        同感 +1                                  │
│                                                 │
│     查看更多回复 (5条)                          │
├─────────────────────────────────────────────────┤
│  👤 赵六                           3小时前       │
│     支持！                                      │
│     👍 23  💬 回复                              │
└─────────────────────────────────────────────────┘
```

---

## 📊 数据统计

### 评论对用户偏好的影响

```
示例：用户A评论了科技类新闻

操作前：
  user_preference: tech=5, sports=3, game=2, politics=1
  user.preference_category = "tech"

评论后：
  user_preference: tech=8 (5+3), sports=3, game=2, politics=1
  user.preference_category = "tech" (仍然最高)
```

### Hadoop分析数据

```sql
-- 查看评论行为统计
SELECT 
    date,
    article_id,
    COUNT(*) as comment_count
FROM article_action_daily_stat
WHERE action = 'comment'
  AND date = '2025-12-01'
GROUP BY article_id
ORDER BY comment_count DESC;
```

---

## 🔒 权限控制

| 操作 | 游客 | 登录用户 | 评论作者 | 管理员 |
|------|------|----------|----------|--------|
| 查看评论 | ✅ | ✅ | ✅ | ✅ |
| 发表评论 | ❌ | ✅ | ✅ | ✅ |
| 回复评论 | ❌ | ✅ | ✅ | ✅ |
| 点赞评论 | ❌ | ✅ | ✅ | ✅ |
| 删除自己评论 | ❌ | ❌ | ✅ | ✅ |
| 删除他人评论 | ❌ | ❌ | ❌ | ✅ |

---

## ⚠️ 注意事项

### 1. 软删除机制
评论删除采用**软删除**：
- `status` 改为 `DELETED`
- `content` 改为 "该评论已删除"
- 不从数据库真正删除，保留历史记录

### 2. 评论层级限制
- 数据库支持无限层级
- 前端只展示2层（顶级评论+回复）
- 超过的通过"查看更多回复"展开

### 3. 性能优化
- 回复只加载前3条
- 评论列表分页加载
- 使用索引优化查询（`news_id`, `parent_id`）

### 4. 内容验证
- 评论内容不能为空
- 最大长度500字
- 可扩展：敏感词过滤、表情支持等

---

## 🚀 扩展功能建议

### 未来可以添加：

1. **评论排序**
   - 按时间排序（最新/最早）
   - 按热度排序（点赞数）
   - 只看作者回复

2. **用户点赞记录**
   - 新建 `comment_like` 表
   - 记录用户点赞过哪些评论
   - 防止重复点赞

3. **评论通知**
   - 评论被回复时通知原作者
   - 评论被点赞时通知
   - 与消息通知系统集成

4. **评论举报**
   - 用户举报不良评论
   - 管理员审核
   - 与举报系统集成

5. **富文本评论**
   - 支持Markdown
   - 支持图片
   - 支持表情

6. **评论搜索**
   - 搜索评论内容
   - 按用户筛选

---

## ✅ 验证清单

测试评论功能是否正常：

- [ ] 能够发表顶级评论
- [ ] 能够回复评论
- [ ] 评论字数统计正确
- [ ] 超过500字提示错误
- [ ] 点赞后数字增加
- [ ] 删除自己的评论成功
- [ ] 非作者无法删除
- [ ] 管理员可以删除任何评论
- [ ] 评论数实时更新
- [ ] 用户偏好权值正确增加
- [ ] preference_category自动更新
- [ ] 评论行为发送到Hadoop
- [ ] 分页加载正常
- [ ] 查看更多回复正常

---

## 🎉 完成！

评论功能已全部实现！现在您的新闻系统包括：

1. ✅ **用户系统**：登录、注册、偏好管理
2. ✅ **新闻系统**：浏览、点赞、转发、不感兴趣
3. ✅ **评论系统**：发表、回复、点赞、删除（新增）
4. ✅ **管理后台**：用户管理、新闻管理、数据统计
5. ✅ **数据分析**：Hadoop离线统计、用户行为分析

完整的功能闭环已形成！🎊
