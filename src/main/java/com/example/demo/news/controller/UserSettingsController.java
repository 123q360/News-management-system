package com.example.demo.news.controller;

import com.example.demo.news.dao.UserDao;
import com.example.demo.news.entity.User;
import com.example.demo.news.service.BrowseHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户设置控制器
 */
@RestController
@RequestMapping("/api/user")
public class UserSettingsController {

    @Autowired
    private UserDao userDao;

    @Autowired
    private BrowseHistoryService browseHistoryService;

    /**
     * 修改用户名
     * PUT /api/user/username
     */
    @PutMapping("/username")
    public Map<String, Object> updateUsername(@RequestBody Map<String, String> request, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 检查登录状态
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                result.put("success", false);
                result.put("message", "请先登录");
                return result;
            }
            
            String newUsername = request.get("username");
            if (newUsername == null || newUsername.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "用户名不能为空");
                return result;
            }
            
            // 检查用户名是否已存在
            if (userDao.existsByUsername(newUsername.trim())) {
                result.put("success", false);
                result.put("message", "用户名已存在");
                return result;
            }
            
            // 更新用户名
            User user = userDao.findById(userId);
            user.setUsername(newUsername.trim());
            userDao.update(user);
            
            // 更新session
            session.setAttribute("username", newUsername.trim());
            
            result.put("success", true);
            result.put("message", "用户名修改成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }

    /**
     * 修改密码
     * PUT /api/user/password
     */
    @PutMapping("/password")
    public Map<String, Object> updatePassword(@RequestBody Map<String, String> request, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 检查登录状态
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                result.put("success", false);
                result.put("message", "请先登录");
                return result;
            }
            
            String oldPassword = request.get("oldPassword");
            String newPassword = request.get("newPassword");
            
            if (oldPassword == null || newPassword == null) {
                result.put("success", false);
                result.put("message", "密码不能为空");
                return result;
            }
            
            // 验证旧密码
            User user = userDao.findById(userId);
            if (!user.getPassword().equals(oldPassword)) {
                result.put("success", false);
                result.put("message", "旧密码错误");
                return result;
            }
            
            // 更新密码
            user.setPassword(newPassword);
            userDao.update(user);
            
            // 清除session，强制重新登录
            session.invalidate();
            
            result.put("success", true);
            result.put("message", "密码修改成功，请重新登录");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }

    /**
     * 获取浏览历史
     * GET /api/user/history?category=&date=&page=1&size=20
     */
    @GetMapping("/history")
    public Map<String, Object> getBrowseHistory(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String date,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpSession session) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 检查登录状态
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                result.put("success", false);
                result.put("message", "请先登录");
                return result;
            }
            
            Map<String, Object> data = browseHistoryService.getBrowseHistory(userId, category, date, page, size);
            result.put("success", true);
            result.put("data", data);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }

    /**
     * 删除单条浏览记录
     * DELETE /api/user/history/{id}
     */
    @DeleteMapping("/history/{id}")
    public Map<String, Object> deleteHistory(@PathVariable Long id, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 检查登录状态
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                result.put("success", false);
                result.put("message", "请先登录");
                return result;
            }
            
            browseHistoryService.deleteHistory(id, userId);
            result.put("success", true);
            result.put("message", "删除成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }

    /**
     * 清空所有浏览历史
     * DELETE /api/user/history
     */
    @DeleteMapping("/history")
    public Map<String, Object> clearHistory(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 检查登录状态
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                result.put("success", false);
                result.put("message", "请先登录");
                return result;
            }
            
            browseHistoryService.clearHistory(userId);
            result.put("success", true);
            result.put("message", "清空成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
}
