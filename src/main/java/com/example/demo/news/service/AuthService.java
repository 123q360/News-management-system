package com.example.demo.news.service;

import com.example.demo.news.dao.UserDao;
import com.example.demo.news.dao.UserPreferenceDao;
import com.example.demo.news.entity.User;
import com.example.demo.news.entity.UserPreference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户认证服务
 */
@Service
public class AuthService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserPreferenceDao userPreferenceDao;

    /**
     * 用户注册
     * @param username 用户名
     * @param password 密码
     * @return 注册成功的用户对象
     */
    @Transactional
    public User register(String username, String password) {
        // 1. 检查用户名是否已存在
        if (userDao.existsByUsername(username)) {
            throw new RuntimeException("用户名已存在");
        }

        // 2. 验证用户名和密码格式
        if (username == null || username.trim().isEmpty()) {
            throw new RuntimeException("用户名不能为空");
        }
        if (username.length() < 3 || username.length() > 20) {
            throw new RuntimeException("用户名长度必须在3-20个字符之间");
        }
        if (password == null || password.length() < 6) {
            throw new RuntimeException("密码长度不能少于6位");
        }

        // 3. 创建用户偏好（默认所有类别为0）
        UserPreference preference = new UserPreference();
        Long preferenceId = userPreferenceDao.insert(preference);

        // 4. 创建用户（密码直接存储，实际项目中应该加密）
        User user = new User();
        user.setUsername(username.trim());
        user.setPassword(password);  // TODO: 实际项目中应该使用加密（如BCrypt）
        user.setPreferenceId(preferenceId);
        user.setRole("USER");  // 默认角色为普通用户
        user.setPreferenceCategory("无");  // 初始偏好为"无"

        Long userId = userDao.insert(user);
        user.setId(userId);

        System.out.println("用户注册成功：" + username + "（ID：" + userId + "）");
        return user;
    }

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录成功的用户对象（不包含密码）
     */
    public User login(String username, String password) {
        // 1. 验证参数
        if (username == null || username.trim().isEmpty()) {
            throw new RuntimeException("用户名不能为空");
        }
        if (password == null || password.isEmpty()) {
            throw new RuntimeException("密码不能为空");
        }

        // 2. 查询用户
        User user = userDao.findByUsername(username.trim());
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 3. 验证密码
        if (!password.equals(user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 4. 清除密码信息（安全考虑）
        user.setPassword(null);

        System.out.println("用户登录成功：" + username + "（角色：" + user.getRole() + "）");
        return user;
    }

    /**
     * 根据用户ID获取用户信息
     * @param userId 用户ID
     * @return 用户对象（不包含密码）
     */
    public User getUserById(Long userId) {
        User user = userDao.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 清除密码信息
        user.setPassword(null);
        return user;
    }

    /**
     * 修改密码
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        // 1. 查询用户
        User user = userDao.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 2. 验证旧密码
        if (!oldPassword.equals(user.getPassword())) {
            throw new RuntimeException("旧密码错误");
        }

        // 3. 验证新密码
        if (newPassword == null || newPassword.length() < 6) {
            throw new RuntimeException("新密码长度不能少于6位");
        }

        // 4. 更新密码
        user.setPassword(newPassword);
        userDao.update(user);

        System.out.println("用户修改密码成功：" + user.getUsername());
    }
}
