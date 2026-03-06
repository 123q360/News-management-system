package com.example.demo.news.controller;

import com.example.demo.news.entity.User;
import com.example.demo.news.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户认证控制器
 * 提供注册、登录、登出、获取用户信息等接口
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 用户注册
     * POST /api/auth/register
     * 参数：username, password
     */
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            String username = params.get("username");
            String password = params.get("password");

            User user = authService.register(username, password);
            
            result.put("success", true);
            result.put("message", "注册成功");
            result.put("data", user);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 用户登录
     * POST /api/auth/login
     * 参数：username, password
     */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> params, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            String username = params.get("username");
            String password = params.get("password");

            User user = authService.login(username, password);
            
            // 将用户信息存入Session
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("role", user.getRole());
            session.setAttribute("preferenceId", user.getPreferenceId());
            
            result.put("success", true);
            result.put("message", "登录成功");
            result.put("data", user);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 用户登出
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    public Map<String, Object> logout(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            session.invalidate();  // 清除Session
            result.put("success", true);
            result.put("message", "登出成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "登出失败");
        }
        return result;
    }

    /**
     * 获取当前登录用户信息
     * GET /api/auth/current
     */
    @GetMapping("/current")
    public Map<String, Object> getCurrentUser(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                result.put("success", false);
                result.put("message", "未登录");
                return result;
            }

            User user = authService.getUserById(userId);
            result.put("success", true);
            result.put("data", user);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 检查登录状态
     * GET /api/auth/check
     */
    @GetMapping("/check")
    public Map<String, Object> checkLogin(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId != null) {
            result.put("success", true);
            result.put("loggedIn", true);
            result.put("userId", userId);
            result.put("username", session.getAttribute("username"));
            result.put("role", session.getAttribute("role"));
        } else {
            result.put("success", true);
            result.put("loggedIn", false);
        }
        return result;
    }

    /**
     * 修改密码
     * POST /api/auth/changePassword
     * 参数：oldPassword, newPassword
     */
    @PostMapping("/changePassword")
    public Map<String, Object> changePassword(@RequestBody Map<String, String> params, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                result.put("success", false);
                result.put("message", "请先登录");
                return result;
            }

            String oldPassword = params.get("oldPassword");
            String newPassword = params.get("newPassword");

            authService.changePassword(userId, oldPassword, newPassword);
            
            result.put("success", true);
            result.put("message", "密码修改成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
}
