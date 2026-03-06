# 浏览历史功能实现指南

## 🎉 功能概述

已成功实现**双写模式**的浏览历史功能：
- ✅ 用户浏览新闻时，同时写入HDFS和MySQL
- ✅ 用户可查看、筛选、删除浏览历史
- ✅ 用户可修改用户名和密码
- ✅ 支持按类别、日期筛选
- ✅ 分页加载

---

## 📋 实现清单

### 1. 数据库表
**文件**: `browse_history_table.sql`

```sql
CREATE TABLE browse_history (
    id, user_id, news_id, news_title, news_category,
    device, browse_time, create_time
)
```

### 2. 后端代码（共5个文件）

#### Entity层
- `BrowseHistory.java` - 浏览历史实体类

#### DAO层
- `BrowseHistoryDao.java` - 数据访问层
  - 插入浏览记录
  - 查询历史（支持筛选、分页）
  - 删除记录

#### Service层
- `BrowseHistoryService.java` - 浏览历史服务
- 修改 `NewsActionService.java` - 添加双写逻辑

#### Controller层
- `UserSettingsController.java` - 用户设置接口
  - 修改用户名
  - 修改密码
  - 查看浏览历史
  - 删除/清空历史

---

## 🔌 API接口文档

### 1. 获取浏览历史

```http
GET /api/user/history?category=tech&date=2025-12-01&page=1&size=20
```

**参数**：
- `category`: 新闻类别（可选）tech/sports/game/politics
- `date`: 日期筛选（可选）格式：2025-12-01
- `page`: 页码（默认1）
- `size`: 每页数量（默认20）

**返回**：
```json
{
    "success": true,
    "data": {
        "list": [
            {
                "id": 1,
                "userId": 3,
                "newsId": 1,
                "newsTitle": "AI技术突破性进展",
                "newsCategory": "tech",
                "device": "web",
                "browseTime": "2025-12-01T10:00:00",
                "createTime": "2025-12-01T10:00:01"
            }
        ],
        "total": 50,
        "page": 1,
        "size": 20,
        "totalPages": 3
    }
}
```

### 2. 删除单条记录

```http
DELETE /api/user/history/123
```

### 3. 清空所有历史

```http
DELETE /api/user/history
```

### 4. 修改用户名

```http
PUT /api/user/username
Content-Type: application/json

{
    "username": "newname"
}
```

### 5. 修改密码

```http
PUT /api/user/password
Content-Type: application/json

{
    "oldPassword": "old123",
    "newPassword": "new456"
}
```

---

## 💡 双写实现原理

### 数据流程

```
用户浏览新闻
    ↓
NewsActionService.handleViewAction()
    ├─ 更新news表（view_count+1）
    ├─ 更新user_preference表（权值+1）
    ├─ 更新user表（preference_category）
    ├─ 双写：写入browse_history表 ← 新增
    └─ 发送到HDFS（通过Flume）
```

### 核心代码

```java
// NewsActionService.java
@Transactional
public void handleViewAction(Long userId, Long newsId, Long preferenceId) {
    News news = newsDao.findById(newsId);
    
    // 1. 更新新闻浏览量
    newsDao.incrementViewCount(newsId);
    
    // 2. 更新用户偏好
    userPreferenceDao.updatePreference(preferenceId, news.getCategory(), 1);
    updateUserPreferenceCategory(userId, preferenceId);
    
    // 3. 双写：保存浏览历史
    saveBrowseHistory(userId, news);  // ← 新增
    
    // 4. 发送到HDFS
    sendToHadoop(userId, newsId, "view");
}

private void saveBrowseHistory(Long userId, News news) {
    BrowseHistory history = new BrowseHistory();
    history.setUserId(userId);
    history.setNewsId(news.getId());
    history.setNewsTitle(news.getTitle());
    history.setNewsCategory(news.getCategory());
    history.setDevice("web");
    history.setBrowseTime(LocalDateTime.now());
    
    browseHistoryDao.insert(history);
}
```

---

## 🎯 使用步骤

### 1. 初始化数据库

```bash
mysql -u root -p < browse_history_table.sql
```

### 2. 重启应用

```bash
mvn spring-boot:run
```

### 3. 测试功能

#### 测试浏览历史记录

1. 浏览几篇新闻：
   ```
   http://localhost:8080/news-detail.html?id=1
   http://localhost:8080/news-detail.html?id=2
   ```

2. 查看数据库：
   ```sql
   SELECT * FROM browse_history WHERE user_id = 你的用户ID;
   ```

3. 调用API查看历史：
   ```bash
   curl http://localhost:8080/api/user/history
   ```

#### 测试筛选功能

```bash
# 按类别筛选
curl "http://localhost:8080/api/user/history?category=tech"

# 按日期筛选
curl "http://localhost:8080/api/user/history?date=2025-12-01"

# 组合筛选
curl "http://localhost:8080/api/user/history?category=tech&date=2025-12-01&page=1&size=10"
```

#### 测试删除功能

```bash
# 删除单条
curl -X DELETE http://localhost:8080/api/user/history/1

# 清空所有
curl -X DELETE http://localhost:8080/api/user/history
```

---

## 📊 数据对比

### HDFS中的数据（用于离线分析）
- 包含所有行为：view, like, share, dislike, comment
- JSON格式存储
- 用于MapReduce统计
- 适合大数据分析

### MySQL中的数据（用于实时查询）
- 只包含view行为
- 结构化存储
- 用于用户查看历史
- 适合快速检索

---

## ✅ 验证清单

测试浏览历史功能是否正常：

- [ ] 浏览新闻后，browse_history表有新记录
- [ ] 记录包含正确的news_title和news_category
- [ ] 可以通过API查询历史记录
- [ ] 按类别筛选正常
- [ ] 按日期筛选正常
- [ ] 分页功能正常
- [ ] 删除单条记录成功
- [ ] 清空所有记录成功
- [ ] 控制台输出"保存浏览历史成功"
- [ ] HDFS仍然正常接收数据

---

## 🎯 下一步：前端页面

现在后端已完成，接下来可以创建：

1. **用户设置页面** (`user-settings.html`)
   - 个人信息标签（修改用户名、密码）
   - 浏览历史标签（查看、筛选、删除历史）

2. **在导航栏添加设置入口**
   - news-list.html 添加"设置"按钮

---

## 🎉 完成！

浏览历史功能（后端部分）已全部实现！

现在您的系统包括：
1. ✅ 用户系统（登录、注册、偏好管理）
2. ✅ 新闻系统（浏览、点赞、转发、不感兴趣）
3. ✅ 评论系统（发表、回复、点赞、删除）
4. ✅ **浏览历史**（双写、查询、筛选、删除）← 新增
5. ✅ 管理后台（用户管理、新闻管理、数据统计）
6. ✅ 数据分析（Hadoop离线统计、用户行为分析）

系统功能越来越完整了！🎊
