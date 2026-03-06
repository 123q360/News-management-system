# 用户认证功能使用指南

## 📋 功能概述

已实现的用户认证功能包括：
- ✅ 用户注册
- ✅ 用户登录
- ✅ 用户登出
- ✅ 获取当前用户信息
- ✅ 检查登录状态
- ✅ 修改密码

## 🔧 技术实现

### 后端
- **UserDao** - 用户数据访问层
- **AuthService** - 用户认证业务逻辑
- **AuthController** - 用户认证API接口
- **Session** - 基于HttpSession的会话管理

### 前端
- **login.html** - 登录页面
- **register.html** - 注册页面

## 🚀 使用方法

### 1. 访问页面

启动应用后，访问以下地址：

- 登录页面：http://localhost:8080/login.html
- 注册页面：http://localhost:8080/register.html

### 2. API接口文档

#### 2.1 用户注册

**接口地址**：`POST /api/auth/register`

**请求参数**：
```json
{
    "username": "testuser",
    "password": "123456"
}
```

**返回结果**：
```json
{
    "success": true,
    "message": "注册成功",
    "data": {
        "id": 3,
        "username": "testuser",
        "preferenceId": 3,
        "role": "USER",
        "createTime": "2025-12-01T10:30:00"
    }
}
```

**验证规则**：
- 用户名：3-20个字符
- 密码：至少6位字符
- 用户名不能重复

---

#### 2.2 用户登录

**接口地址**：`POST /api/auth/login`

**请求参数**：
```json
{
    "username": "testuser",
    "password": "123456"
}
```

**返回结果**：
```json
{
    "success": true,
    "message": "登录成功",
    "data": {
        "id": 2,
        "username": "testuser",
        "preferenceId": 2,
        "role": "USER",
        "createTime": "2025-11-19T10:00:00"
    }
}
```

**Session存储信息**：
- userId - 用户ID
- username - 用户名
- role - 角色（USER/ADMIN）
- preferenceId - 用户偏好ID

---

#### 2.3 用户登出

**接口地址**：`POST /api/auth/logout`

**请求参数**：无

**返回结果**：
```json
{
    "success": true,
    "message": "登出成功"
}
```

---

#### 2.4 获取当前用户信息

**接口地址**：`GET /api/auth/current`

**请求参数**：无（需要登录状态）

**返回结果**：
```json
{
    "success": true,
    "data": {
        "id": 2,
        "username": "testuser",
        "preferenceId": 2,
        "role": "USER",
        "createTime": "2025-11-19T10:00:00"
    }
}
```

---

#### 2.5 检查登录状态

**接口地址**：`GET /api/auth/check`

**请求参数**：无

**返回结果（已登录）**：
```json
{
    "success": true,
    "loggedIn": true,
    "userId": 2,
    "username": "testuser",
    "role": "USER"
}
```

**返回结果（未登录）**：
```json
{
    "success": true,
    "loggedIn": false
}
```

---

#### 2.6 修改密码

**接口地址**：`POST /api/auth/changePassword`

**请求参数**：
```json
{
    "oldPassword": "123456",
    "newPassword": "654321"
}
```

**返回结果**：
```json
{
    "success": true,
    "message": "密码修改成功"
}
```

## 🧪 测试步骤

### 1. 命令行测试（使用curl）

#### 测试注册
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "newuser", "password": "123456"}'
```

#### 测试登录
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "123456"}' \
  -c cookies.txt
```

#### 测试获取当前用户（需要Session）
```bash
curl -X GET http://localhost:8080/api/auth/current \
  -b cookies.txt
```

#### 测试登出
```bash
curl -X POST http://localhost:8080/api/auth/logout \
  -b cookies.txt
```

### 2. 浏览器测试

1. 访问注册页面：http://localhost:8080/register.html
2. 填写用户名和密码，点击注册
3. 注册成功后会自动跳转到登录页面
4. 填写用户名和密码，点击登录
5. 登录成功后会根据角色跳转：
   - 普通用户 → `/news.html`（新闻列表页）
   - 管理员 → `/admin.html`（管理后台）

### 3. 测试用户账号

数据库初始化脚本中已经创建了两个测试账号：

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | ADMIN |
| testuser | 123456 | USER |

## 📝 注意事项

### 1. 密码安全
当前版本密码是**明文存储**，仅用于开发测试。

**生产环境必须加密**！推荐使用BCrypt：
```java
// 注册时加密
String encodedPassword = new BCryptPasswordEncoder().encode(password);

// 登录时验证
boolean matches = new BCryptPasswordEncoder().matches(password, user.getPassword());
```

### 2. Session配置
Session默认配置在 `application.properties` 或 `application.yml` 中：
```yaml
server:
  servlet:
    session:
      timeout: 30m  # Session超时时间30分钟
```

### 3. 跨域问题
如果前后端分离部署，需要配置CORS：
```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000")
                .allowCredentials(true);
    }
}
```

## 🔄 与新闻行为功能的集成

登录后，Session中存储了以下信息可供其他接口使用：
```java
Long userId = (Long) session.getAttribute("userId");
Long preferenceId = (Long) session.getAttribute("preferenceId");
String role = (String) session.getAttribute("role");
```

**示例**：修改新闻行为接口，自动从Session获取用户信息
```java
@PostMapping("/api/news/action/view")
public Map<String, Object> handleView(@RequestBody Map<String, Long> params, HttpSession session) {
    Long userId = (Long) session.getAttribute("userId");
    Long preferenceId = (Long) session.getAttribute("preferenceId");
    Long newsId = params.get("newsId");
    
    newsActionService.handleViewAction(userId, newsId, preferenceId);
    // ...
}
```

## ✅ 功能清单

- [x] 用户注册功能
- [x] 用户登录功能
- [x] 用户登出功能
- [x] Session会话管理
- [x] 获取当前用户信息
- [x] 检查登录状态
- [x] 修改密码功能
- [x] 登录页面
- [x] 注册页面
- [ ] 密码加密（TODO）
- [ ] 记住登录状态（TODO）
- [ ] 忘记密码功能（TODO）

## 🎉 下一步

现在您可以：
1. 启动应用
2. 执行数据库初始化脚本
3. 访问 http://localhost:8080/register.html 注册新用户
4. 访问 http://localhost:8080/login.html 登录
5. 开始开发新闻列表和详情页面
