# 新闻系统使用指南

## 🎉 功能概述

新闻系统已完成以下功能：
- ✅ 用户注册/登录
- ✅ 新闻列表展示（支持分类筛选）
- ✅ 新闻详情查看
- ✅ 用户行为数据采集（浏览、点赞、转发、不感兴趣）
- ✅ 实时更新MySQL统计数据
- ✅ 自动发送数据到Hadoop进行离线分析
- ✅ 用户偏好权值自动更新

## 📁 文件结构

```
src/main/resources/static/
├── login.html          # 登录页面
├── register.html       # 注册页面
├── news-list.html      # 新闻列表页面（主页）
└── news-detail.html    # 新闻详情页面

src/main/java/com/example/demo/news/
├── controller/
│   ├── AuthController.java          # 认证接口
│   ├── NewsController.java          # 新闻接口
│   └── NewsActionController.java    # 新闻行为接口
├── service/
│   ├── AuthService.java             # 认证服务
│   ├── NewsService.java             # 新闻服务
│   └── NewsActionService.java       # 新闻行为服务
├── dao/
│   ├── UserDao.java
│   ├── NewsDao.java
│   └── UserPreferenceDao.java
└── entity/
    ├── User.java
    ├── News.java
    └── UserPreference.java
```

## 🚀 使用流程

### 1. 用户访问流程

```
访问首页
    ↓
检测登录状态
    ↓
未登录 → 跳转到登录页 (/login.html)
    ↓
登录成功 → 跳转到新闻列表 (/news-list.html)
    ↓
选择分类（全部/科技/运动/游戏/政治）
    ↓
点击新闻卡片
    ↓
【触发浏览行为】→ 发送到 /api/news/action/view
    ├─→ 更新MySQL（新闻浏览量+1，用户偏好+1）
    └─→ 发送到Hadoop（离线分析）
    ↓
跳转到新闻详情页 (/news-detail.html?id=xxx)
    ↓
用户可以进行操作：
    ├─ 点赞（偏好+2）
    ├─ 转发（偏好+2）
    └─ 不感兴趣（偏好-5）
```

### 2. 数据流详解

#### 浏览行为
```javascript
// 用户点击新闻卡片时触发
点击新闻卡片
    ↓
发送POST请求到 /api/news/action/view
参数：{userId, newsId, preferenceId}
    ↓
NewsActionService.handleViewAction()
    ├─→ NewsDao.incrementViewCount() - 浏览量+1
    ├─→ UserPreferenceDao.updatePreference() - 类别偏好+1
    └─→ DataCleanService.cleanAndSend() - 发送到Flume
         ↓
    UserBehaviorSender.send()
         ↓
    Flume HTTP Source (http://192.168.227.139:5140)
         ↓
    HDFS (/flume/data/2025-12-01/xxx)
         ↓
    MapReduce统计分析
         ↓
    MySQL (article_action_daily_stat表)
```

#### 点赞/转发/不感兴趣行为
```javascript
// 在新闻详情页点击按钮时触发
点击按钮
    ↓
发送POST请求到 /api/news/action/{like|share|dislike}
    ↓
同样的流程，权值变化不同：
    - 点赞：like_count +1, 偏好 +2
    - 转发：share_count +1, 偏好 +2
    - 不感兴趣：dislike_count +1, 偏好 -5
```

## 📊 数据格式

### 发送到Hadoop的数据格式
```json
[{
    "userId": "2",
    "action": "view",
    "articleId": "1",
    "timestamp": "2025-12-01 18:33:22",
    "device": "pc"
}]
```

### 设备检测
```javascript
function detectDevice() {
    const ua = navigator.userAgent;
    if (/iPad/i.test(ua)) return "tablet";
    if (/iPhone|iPod|Android/i.test(ua)) return "mobile";
    return "pc";
}
```

## 🎯 API接口文档

### 新闻相关接口

#### 1. 获取新闻列表
```
GET /api/news/list?category=tech
```
**参数**：
- `category`（可选）：tech/sports/game/politics

**返回**：
```json
{
    "success": true,
    "data": [
        {
            "id": 1,
            "category": "tech",
            "title": "AI技术取得重大突破",
            "content": "...",
            "coverImage": "https://...",
            "viewCount": 100,
            "likeCount": 20,
            "shareCount": 5,
            "dislikeCount": 2,
            "author": "张三",
            "publishTime": "2025-12-01T10:00:00"
        }
    ]
}
```

#### 2. 获取新闻详情
```
GET /api/news/detail/1
```
**返回**：同上，单个新闻对象

### 行为接口

#### 3. 浏览新闻
```
POST /api/news/action/view
Content-Type: application/json

{
    "userId": 2,
    "newsId": 1,
    "preferenceId": 2
}
```

#### 4. 点赞新闻
```
POST /api/news/action/like
```

#### 5. 转发新闻
```
POST /api/news/action/share
```

#### 6. 标记不感兴趣
```
POST /api/news/action/dislike
```

## 🧪 测试步骤

### 1. 准备工作
```bash
# 1. 确保MySQL已启动并执行初始化脚本
mysql -u root -p < init_database.sql

# 2. 确保Flume已启动（如果需要Hadoop数据采集）
# 检查 Flume HTTP Source 是否监听 http://192.168.227.139:5140

# 3. 启动Spring Boot应用
mvn spring-boot:run
```

### 2. 功能测试

#### 测试注册和登录
1. 访问 http://localhost:8080/register.html
2. 注册新用户：用户名 `testuser2`，密码 `123456`
3. 自动跳转到登录页
4. 登录成功后跳转到新闻列表页

#### 测试新闻浏览
1. 在新闻列表页选择分类（如"科技"）
2. 点击任意新闻卡片
3. 查看控制台输出，应该看到：
   ```
   用户 2 浏览了新闻 1（类别：tech），偏好权值+1
   ```
4. 自动跳转到新闻详情页

#### 测试新闻详情页操作
1. 在详情页点击"点赞"按钮
2. 查看控制台输出，应该看到：
   ```
   用户 2 点赞了新闻 1（类别：tech），偏好权值+2
   ```
3. 页面上的统计数据会实时更新

#### 验证数据库变化
```sql
-- 查看新闻统计
SELECT id, title, view_count, like_count, share_count, dislike_count 
FROM news WHERE id = 1;

-- 查看用户偏好
SELECT u.username, up.tech, up.sports, up.game, up.politics
FROM user u
JOIN user_preference up ON u.preference_id = up.id
WHERE u.id = 2;
```

#### 验证Hadoop数据流
```bash
# 检查HDFS中的数据
hdfs dfs -ls /flume/data/2025-12-01/

# 查看文件内容
hdfs dfs -cat /flume/data/2025-12-01/FlumeData.xxxxx
```

## ⚙️ 配置说明

### Flume配置
确保 `application.yml` 中的Flume地址正确：
```java
// UserBehaviorSender.java
private static final String FLUME_HTTP_URL = "http://192.168.227.139:5140";
```

### Session超时配置
```yaml
# application.yml
server:
  servlet:
    session:
      timeout: 30m  # 30分钟
```

## 🔍 常见问题

### 1. 点击新闻后没有跳转
**原因**：用户未登录或Session过期
**解决**：重新登录

### 2. 新闻列表为空
**原因**：数据库中没有新闻数据
**解决**：执行 `init_database.sql` 初始化测试数据

### 3. 行为数据没有发送到Hadoop
**原因**：Flume未启动或地址配置错误
**解决**：
- 检查Flume是否运行
- 检查 `UserBehaviorSender.java` 中的Flume地址
- 查看控制台是否有错误日志

### 4. 偏好权值没有更新
**原因**：用户ID或偏好ID不正确
**解决**：检查Session中是否正确存储了用户信息

## 📈 数据流完整示例

### 用户操作：浏览科技类新闻

#### 1. 前端发送请求
```javascript
fetch('/api/news/action/view', {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({
        userId: 2,
        newsId: 1,
        preferenceId: 2
    })
});
```

#### 2. 后端处理
```java
// NewsActionService.handleViewAction()
newsDao.incrementViewCount(1);           // news表 view_count +1
userPreferenceDao.updatePreference(2, "tech", 1);  // user_preference表 tech +1
```

#### 3. 发送到Hadoop
```java
// UserBehaviorSender.send()
POST http://192.168.227.139:5140
Body: [{
    "headers": {},
    "body": "[{\"userId\":\"2\",\"action\":\"view\",\"articleId\":\"1\",\"timestamp\":\"2025-12-01 18:33:22\",\"device\":\"pc\"}]"
}]
```

#### 4. Flume处理
```
Flume接收 → 写入HDFS → /flume/data/2025-12-01/FlumeData.xxxxx
```

#### 5. MapReduce统计（定时任务）
```java
// ActionStatJob
输入：/flume/data/2025-12-01/*
输出：MySQL article_action_daily_stat表
结果：date=2025-12-01, article_id=1, action=view, count=1
```

## 🎊 总结

现在您的新闻系统已经完整实现了：

1. ✅ **前端页面**：登录、注册、新闻列表、新闻详情
2. ✅ **用户认证**：Session管理、权限控制
3. ✅ **新闻展示**：分类筛选、列表展示、详情查看
4. ✅ **行为采集**：浏览、点赞、转发、不感兴趣
5. ✅ **实时统计**：MySQL中的新闻统计和用户偏好
6. ✅ **离线分析**：数据发送到Hadoop进行MapReduce分析
7. ✅ **设备检测**：自动识别PC/Mobile/Tablet

整个系统实现了**实时+离线**双轨数据处理，完美结合了Spring Boot和Hadoop生态！
