# 管理后台使用指南

## 📋 目录
- [功能概述](#功能概述)
- [访问方式](#访问方式)
- [功能模块](#功能模块)
- [API接口](#api接口)

---

## 🎯 功能概述

管理后台提供以下功能：
- ✅ **数据概览**：实时统计用户数、新闻数、浏览量、点赞量
- ✅ **用户管理**：查看所有用户、查看用户偏好、删除用户
- ✅ **新闻管理**：查看所有新闻、删除新闻
- ✅ **行为统计**：查看Hadoop离线统计结果（原有功能）

---

## 🚀 访问方式

### 1. 访问地址
```
http://localhost:8080/admin.html
```

### 2. 登录要求
- 必须使用**管理员账号**登录
- 普通用户访问会被重定向到登录页

### 3. 创建管理员账号

在数据库中手动创建管理员：

```sql
-- 1. 创建偏好记录
INSERT INTO user_preference (tech, sports, game, politics) 
VALUES (0, 0, 0, 0);

-- 2. 创建管理员账号（假设上一步插入的ID为1）
INSERT INTO user (username, password, preference_id, role, preference_category) 
VALUES ('admin', 'admin123', 1, 'ADMIN', '无');
```

或使用注册页面注册后，修改角色：
```sql
UPDATE user SET role = 'ADMIN' WHERE username = 'yourusername';
```

---

## 📊 功能模块

### 1. 数据概览（Dashboard）

显示系统整体统计数据：

| 指标 | 说明 |
|------|------|
| 总用户数 | 系统注册用户总数 |
| 总新闻数 | 已发布的新闻总数 |
| 总浏览量 | 所有新闻的浏览量总和 |
| 总点赞数 | 所有新闻的点赞量总和 |

**最新新闻列表**：
- 显示最近5条新闻
- 包含：ID、标题、类别、浏览量、点赞量、发布时间

### 2. 用户管理（Users）

**用户列表字段**：
- ID：用户唯一标识
- 用户名：登录用户名
- 角色：USER（普通用户）/ ADMIN（管理员）
- 偏好类别：tech/sports/game/politics/无
- 注册时间：用户创建时间

**功能操作**：
- ✅ 查看所有用户
- ✅ 查看用户偏好类别
- ✅ 删除普通用户（管理员无法删除）
- ⚠️ 删除操作不可恢复

### 3. 新闻管理（News）

**新闻列表字段**：
- ID：新闻唯一标识
- 标题：新闻标题
- 类别：tech/sports/game/politics
- 作者：新闻作者
- 浏览量：阅读次数
- 状态：PUBLISHED/DRAFT/DELETED

**功能操作**：
- ✅ 查看所有新闻（包括草稿）
- ✅ 删除新闻（真正删除，非软删除）
- 🔨 发布新闻（功能开发中）

### 4. 行为统计（Stats）

**集成原有统计页面**：
- 嵌入iframe显示原有的Hadoop统计页面
- URL：`/stat?date=2025-12-01`
- 可查看MapReduce离线统计结果

---

## 🔌 API接口

### 权限验证

所有管理接口都需要管理员权限，通过Session检查：
```java
private boolean isAdmin(HttpSession session) {
    String role = (String) session.getAttribute("role");
    return "ADMIN".equals(role);
}
```

### 1. 获取统计数据

```http
GET /api/admin/stats
```

**返回示例**：
```json
{
  "success": true,
  "data": {
    "totalUsers": 10,
    "totalNews": 25,
    "totalViews": 1500,
    "totalLikes": 300
  }
}
```

### 2. 获取用户列表

```http
GET /api/admin/users
```

**返回示例**：
```json
{
  "success": true,
  "data": [
    {
      "id": 3,
      "username": "test",
      "role": "USER",
      "preference_category": "tech",
      "create_time": "2025-12-01T20:00:00",
      "tech": 10,
      "sports": 5,
      "game": 2,
      "politics": 0
    }
  ]
}
```

### 3. 获取新闻列表

```http
GET /api/admin/news
```

**返回示例**：
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "title": "AI技术突破",
      "category": "tech",
      "author": "张三",
      "view_count": 100,
      "status": "PUBLISHED",
      "publish_time": "2025-12-01T10:00:00"
    }
  ]
}
```

### 4. 删除用户

```http
DELETE /api/admin/users/{id}
```

**返回示例**：
```json
{
  "success": true,
  "message": "删除成功"
}
```

**限制**：
- ❌ 不能删除自己（当前登录的管理员）
- ❌ 不能删除其他管理员

### 5. 删除新闻

```http
DELETE /api/admin/news/{id}
```

**返回示例**：
```json
{
  "success": true,
  "message": "删除成功"
}
```

**说明**：此操作为**真正删除**，不是软删除，数据将从数据库中永久移除。

---

## 🎨 界面设计

### 布局结构

```
┌─────────────────────────────────────────────┐
│           顶部导航栏（管理后台）              │
│  管理员：admin                  [退出登录]    │
└─────────────────────────────────────────────┘
┌──────┬──────────────────────────────────────┐
│      │                                      │
│ 侧边 │           主内容区                    │
│ 菜单 │       （数据概览/用户管理...）         │
│      │                                      │
│ 📊   │                                      │
│ 👥   │                                      │
│ 📰   │                                      │
│ 📈   │                                      │
└──────┴──────────────────────────────────────┘
```

### 配色方案

- **主色调**：紫色渐变 `#667eea` → `#764ba2`
- **成功色**：绿色 `#43e97b`
- **警告色**：橙色 `#f5576c`
- **信息色**：蓝色 `#4facfe`

---

## 🔐 安全注意事项

1. **生产环境必须修改默认密码**
2. **建议添加密码加密**（当前为明文存储）
3. **建议添加操作日志**（记录管理员操作）
4. **删除操作不可恢复**，请谨慎使用

---

## 📝 待开发功能

- [ ] 发布新闻功能
- [ ] 编辑新闻功能
- [ ] 批量操作
- [ ] 数据导出
- [ ] 操作日志
- [ ] 权限细分管理
- [ ] 密码加密

---

## 🚀 快速开始

### 1. 创建管理员账号

```sql
-- 在MySQL中执行
INSERT INTO user_preference (tech, sports, game, politics) VALUES (0, 0, 0, 0);
INSERT INTO user (username, password, preference_id, role, preference_category) 
VALUES ('admin', 'admin123', LAST_INSERT_ID(), 'ADMIN', '无');
```

### 2. 启动应用

```bash
mvn spring-boot:run
```

### 3. 访问管理后台

```
http://localhost:8080/login.html
```

输入：
- 用户名：`admin`
- 密码：`admin123`

登录后会自动跳转到管理后台。

---

## 🎉 完成！

现在您可以：
1. ✅ 查看系统整体数据
2. ✅ 管理所有用户
3. ✅ 管理所有新闻
4. ✅ 查看Hadoop统计结果

祝使用愉快！🎊
