package com.example.demo.news.controller;

import com.example.demo.news.entity.Comment;
import com.example.demo.news.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 评论控制器
 */
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 发表评论
     * POST /api/comments
     */
    @PostMapping
    public Map<String, Object> addComment(@RequestBody Map<String, Object> request, HttpSession session) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 检查登录状态
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                result.put("success", false);
                result.put("message", "请先登录");
                return result;
            }

            // 获取参数
            Long newsId = Long.valueOf(request.get("newsId").toString());
            String content = request.get("content").toString().trim();

            // 验证内容
            if (content.isEmpty()) {
                result.put("success", false);
                result.put("message", "评论内容不能为空");
                return result;
            }

            if (content.length() > 500) {
                result.put("success", false);
                result.put("message", "评论内容不能超过500字");
                return result;
            }

            // 可选参数
            Long parentId = request.get("parentId") != null ? 
                Long.valueOf(request.get("parentId").toString()) : null;
            Long replyToUserId = request.get("replyToUserId") != null ? 
                Long.valueOf(request.get("replyToUserId").toString()) : null;

            // 发表评论
            Comment comment = commentService.addComment(newsId, userId, parentId, replyToUserId, content);

            result.put("success", true);
            result.put("data", comment);
            result.put("message", "评论成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 查询评论列表
     * GET /api/comments?newsId=1&page=1&size=10&sort=time
     */
    @GetMapping
    public Map<String, Object> getComments(
            @RequestParam Long newsId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "time") String sort) {
        
        Map<String, Object> result = new HashMap<>();

        try {
            Map<String, Object> data = commentService.getComments(newsId, page, size, sort);
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
     * 查询更多回复
     * GET /api/comments/{parentId}/replies?page=1&size=10
     */
    @GetMapping("/{parentId}/replies")
    public Map<String, Object> getMoreReplies(
            @PathVariable Long parentId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> result = new HashMap<>();

        try {
            List<Comment> replies = commentService.getMoreReplies(parentId, page, size);
            result.put("success", true);
            result.put("data", replies);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 点赞评论
     * POST /api/comments/{id}/like
     */
    @PostMapping("/{id}/like")
    public Map<String, Object> likeComment(@PathVariable Long id, HttpSession session) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 检查登录状态
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                result.put("success", false);
                result.put("message", "请先登录");
                return result;
            }

            commentService.likeComment(id);
            result.put("success", true);
            result.put("message", "点赞成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 删除评论
     * DELETE /api/comments/{id}
     */
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteComment(@PathVariable Long id, HttpSession session) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 检查登录状态
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                result.put("success", false);
                result.put("message", "请先登录");
                return result;
            }

            // 检查是否为管理员
            String role = (String) session.getAttribute("role");
            boolean isAdmin = "ADMIN".equals(role);

            commentService.deleteComment(id, userId, isAdmin);
            result.put("success", true);
            result.put("message", "删除成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            e.printStackTrace();
        }

        return result;
    }
}
