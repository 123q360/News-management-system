# 新闻系统实现方案总结（已重构）

## 📁 代码结构（已优化）

所有新闻系统相关代码已封装到 `com.example.demo.news` 包下：

```
com.example.demo.news/
├── entity/              # 实体类
│   ├── User.java       # 用户实体
│   ├── UserPreference.java  # 用户偏好实体
│   └── News.java       # 新闻实体
├── dao/                # 数据访问层
│   ├── NewsDao.java    # 新闻数据访问
│   └── UserPreferenceDao.java  # 用户偏好数据访问
├── service/            # 业务逻辑层
│   └── NewsActionService.java  # 新闻行为处理服务
└── controller/         # 控制器层
    └── NewsActionController.java  # 新闻行为API接口
```

### 与原有项目的关系
- 新闻系统代码独立于原有Hadoop统计项目
- 通过 `com.example.demo.service.DataCleanService` 对接原有数据采集流程
- 使用 `com.example.demo.entity.UserBehavior` 实体发送数据到Flume

---

# 新闻系统实现方案总结

## 一、需求复述

您的设计思路：
1. **三个核心表**：用户表、用户偏好表、新闻表
2. **双重更新机制**：用户的每个操作都会同时更新新闻表和用户偏好表
3. **权值变化规则**：
   - 浏览新闻：新闻浏览量+1，用户对应类别偏好+1
   - 点赞新闻：新闻点赞量+1，用户对应类别偏好+2
   - 转发新闻：新闻转发量+1，用户对应类别偏好+2
   - 不感兴趣：新闻不感兴趣量+1，用户对应类别偏好-5

## 二、可行性分析

✅ **完全可行！** 您的设计与现有项目能够完美结合：

### 与现有项目的结合
1. **数据采集层**：复用现有的 `/api/logs` 接口和 `DataCleanService`
2. **Hadoop统计**：现有的MapReduce任务可以继续统计用户行为
3. **实时更新**：新增的Service层实时更新MySQL中的新闻表和用户偏好表
4. **离线统计**：Hadoop处理HDFS中的历史数据，用于长期分析

### 数据流架构
```
用户操作（浏览/点赞/转发/不感兴趣）
    ↓
前端调用 NewsActionController
    ↓
NewsActionService 处理业务逻辑
    ├─→ 更新新闻表（NewsDao）
    ├─→ 更新用户偏好表（UserPreferenceDao）
    └─→ 发送到Hadoop（DataCleanService → Flume → HDFS）
```

## 三、已实现的核心功能

### 1. 数据库层
- ✅ 用户表（user）
- ✅ 用户偏好表（user_preference）
- ✅ 新闻表（news）
- ✅ 数据库初始化脚本（init_database.sql）

### 2. 实体类
- ✅ User.java - 用户实体
- ✅ UserPreference.java - 用户偏好实体（带权值更新方法）
- ✅ News.java - 新闻实体（带计数器增加方法）

### 3. DAO层
- ✅ NewsDao.java - 新闻数据访问（包含增加浏览/点赞/转发/不感兴趣量的方法）
- ✅ UserPreferenceDao.java - 用户偏好数据访问（支持动态更新指定类别权值）

### 4. Service层
- ✅ NewsActionService.java - **核心业务逻辑**
  - `handleViewAction()` - 处理浏览行为
  - `handleLikeAction()` - 处理点赞行为
  - `handleShareAction()` - 处理转发行为
  - `handleDislikeAction()` - 处理不感兴趣行为
  - 每个方法都实现了：新闻表更新 + 用户偏好表更新 + 数据发送到Hadoop

### 5. Controller层
- ✅ NewsActionController.java - 提供REST API接口
  - `POST /api/news/action/view` - 浏览接口
  - `POST /api/news/action/like` - 点赞接口
  - `POST /api/news/action/share` - 转发接口
  - `POST /api/news/action/dislike` - 不感兴趣接口

## 四、实现细节

### 1. 事务管理
所有操作使用 `@Transactional` 注解，确保：
- 新闻表和用户偏好表同时更新成功
- 任何一步失败都会回滚

### 2. 权值变化实现
```java
// UserPreferenceDao.updatePreference() 方法
// 使用 SQL 的 GREATEST 函数确保权值不为负数
UPDATE user_preference 
SET tech = GREATEST(0, tech + delta) 
WHERE id = ?
```

### 3. 数据对接Hadoop
```java
// NewsActionService.sendToHadoop() 方法
// 构造UserBehavior对象，调用现有的DataCleanService
UserBehavior behavior = new UserBehavior();
behavior.setUserId(String.valueOf(userId));
behavior.setArticleId(String.valueOf(newsId));
behavior.setAction(action);
behavior.setTimestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
behavior.setDevice("web");

dataCleanService.cleanAndSend(Collections.singletonList(behavior));
```

## 五、使用示例

### 1. 初始化数据库
```bash
mysql -u root -p < init_database.sql
```

### 2. 启动应用
```bash
mvn spring-boot:run
```

### 3. 测试API调用

#### 用户浏览新闻（科技类）
```bash
curl -X POST http://localhost:8080/api/news/action/view \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 2,
    "newsId": 1,
    "preferenceId": 2
  }'
```
**结果**：
- news表：ID=1的新闻 view_count +1
- user_preference表：ID=2的用户 tech字段 +1
- 数据发送到Flume → HDFS

#### 用户点赞新闻（运动类）
```bash
curl -X POST http://localhost:8080/api/news/action/like \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 2,
    "newsId": 4,
    "preferenceId": 2
  }'
```
**结果**：
- news表：ID=4的新闻 like_count +1
- user_preference表：ID=2的用户 sports字段 +2
- 数据发送到Flume → HDFS

#### 用户对新闻不感兴趣（游戏类）
```bash
curl -X POST http://localhost:8080/api/news/action/dislike \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 2,
    "newsId": 7,
    "preferenceId": 2
  }'
```
**结果**：
- news表：ID=7的新闻 dislike_count +1
- user_preference表：ID=2的用户 game字段 -5（最小为0）
- 数据发送到Flume → HDFS

## 六、验证数据变化

### 查询新闻统计
```sql
SELECT id, category, title, view_count, like_count, share_count, dislike_count 
FROM news 
WHERE id = 1;
```

### 查询用户偏好
```sql
SELECT u.username, up.tech, up.sports, up.game, up.politics
FROM user u
JOIN user_preference up ON u.preference_id = up.id
WHERE u.id = 2;
```

## 七、下一步工作

### 必须完成的功能
1. **用户认证系统**
   - 登录功能（UserDao + AuthService + LoginController）
   - Session管理或JWT认证
   - 角色权限控制（用户/管理员）

2. **新闻管理功能**
   - 新闻列表展示（NewsService + 前端页面）
   - 新闻详情页面（带行为采集JavaScript）
   - 管理员新闻CRUD（AdminNewsController）

3. **前端页面**
   - 用户登录页
   - 新闻列表页
   - 新闻详情页（集成行为数据采集）
   - 管理员后台（用户管理 + 新闻管理 + 统计查看）

### 推荐实现顺序
1. 用户认证（登录/注册）
2. 新闻列表和详情展示
3. 前端集成行为采集（调用已实现的API）
4. 管理员后台开发
5. 集成现有统计界面

## 八、关键优势

✅ **实时 + 离线双轨统计**
- 实时：MySQL存储，快速查询用户偏好和新闻热度
- 离线：Hadoop处理历史数据，深度分析用户行为模式

✅ **业务逻辑清晰**
- 所有权值变化规则封装在Service层
- 易于维护和调整规则

✅ **数据一致性**
- 使用事务保证数据同步更新
- 权值计算使用数据库函数，避免并发问题

✅ **扩展性强**
- 易于添加新的新闻类别
- 易于添加新的用户行为类型
- 易于调整权值计算规则

## 九、总结

您的构想**完全可以实现**，而且与现有项目结合得非常好！

核心实现已经完成，包括：
- 数据库设计和初始化
- 完整的DAO/Service/Controller三层架构
- 双重更新机制（新闻表 + 用户偏好表）
- 与Hadoop的数据对接

现在只需要添加前端页面和用户认证功能，整个系统就可以完整运行了！
