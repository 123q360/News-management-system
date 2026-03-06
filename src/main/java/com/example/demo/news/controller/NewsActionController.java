package com.example.demo.news.controller;

import com.example.demo.news.service.NewsActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 新闻行为控制器
 * 提供用户对新闻的各种操作接口
 */
@RestController
@RequestMapping("/api/news/action")
public class NewsActionController {

    @Autowired
    private NewsActionService newsActionService;

    /**
     * 处理用户浏览新闻
     * POST /api/news/action/view
     * 参数：userId, newsId, preferenceId
     */
    @PostMapping("/view")
    public Map<String, Object> handleView(@RequestBody Map<String, Long> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long userId = params.get("userId");
            Long newsId = params.get("newsId");
            Long preferenceId = params.get("preferenceId");

            if (userId == null || newsId == null || preferenceId == null) {
                result.put("success", false);
                result.put("message", "参数不完整");
                return result;
            }

            newsActionService.handleViewAction(userId, newsId, preferenceId);
            
            result.put("success", true);
            result.put("message", "浏览记录成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "处理失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 处理用户点赞新闻
     * POST /api/news/action/like
     */
    @PostMapping("/like")
    public Map<String, Object> handleLike(@RequestBody Map<String, Long> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long userId = params.get("userId");
            Long newsId = params.get("newsId");
            Long preferenceId = params.get("preferenceId");

            if (userId == null || newsId == null || preferenceId == null) {
                result.put("success", false);
                result.put("message", "参数不完整");
                return result;
            }

            newsActionService.handleLikeAction(userId, newsId, preferenceId);
            
            result.put("success", true);
            result.put("message", "点赞成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "处理失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 处理用户转发新闻
     * POST /api/news/action/share
     */
    @PostMapping("/share")
    public Map<String, Object> handleShare(@RequestBody Map<String, Long> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long userId = params.get("userId");
            Long newsId = params.get("newsId");
            Long preferenceId = params.get("preferenceId");

            if (userId == null || newsId == null || preferenceId == null) {
                result.put("success", false);
                result.put("message", "参数不完整");
                return result;
            }

            newsActionService.handleShareAction(userId, newsId, preferenceId);
            
            result.put("success", true);
            result.put("message", "转发成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "处理失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 处理用户对新闻不感兴趣
     * POST /api/news/action/dislike
     */
    @PostMapping("/dislike")
    public Map<String, Object> handleDislike(@RequestBody Map<String, Long> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long userId = params.get("userId");
            Long newsId = params.get("newsId");
            Long preferenceId = params.get("preferenceId");

            if (userId == null || newsId == null || preferenceId == null) {
                result.put("success", false);
                result.put("message", "参数不完整");
                return result;
            }

            newsActionService.handleDislikeAction(userId, newsId, preferenceId);
            
            result.put("success", true);
            result.put("message", "已标记为不感兴趣");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "处理失败: " + e.getMessage());
        }
        return result;
    }
}
