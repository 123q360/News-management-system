package com.example.demo.news.controller;

import com.example.demo.news.entity.News;
import com.example.demo.news.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 新闻控制器
 * 提供新闻列表、详情等接口
 */
@RestController
@RequestMapping("/api/news")
public class NewsController {

    @Autowired
    private NewsService newsService;

    /**
     * 获取新闻列表
     * GET /api/news/list
     * 可选参数：category（类别）
     */
    @GetMapping("/list")
    public Map<String, Object> getNewsList(@RequestParam(required = false) String category) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<News> newsList;
            
            if (category != null && !category.isEmpty()) {
                newsList = newsService.getNewsByCategory(category);
            } else {
                newsList = newsService.getAllNews();
            }
            
            result.put("success", true);
            result.put("data", newsList);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取新闻列表失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取新闻详情
     * GET /api/news/detail/{id}
     */
    @GetMapping("/detail/{id}")
    public Map<String, Object> getNewsDetail(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            News news = newsService.getNewsById(id);
            
            if (news == null) {
                result.put("success", false);
                result.put("message", "新闻不存在");
            } else {
                result.put("success", true);
                result.put("data", news);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取新闻详情失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 创建新闻（管理员功能）
     * POST /api/news/create
     */
    @PostMapping("/create")
    public Map<String, Object> createNews(@RequestBody News news) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long newsId = newsService.createNews(news);
            result.put("success", true);
            result.put("message", "创建成功");
            result.put("newsId", newsId);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "创建失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 更新新闻（管理员功能）
     * POST /api/news/update
     */
    @PostMapping("/update")
    public Map<String, Object> updateNews(@RequestBody News news) {
        Map<String, Object> result = new HashMap<>();
        try {
            int rows = newsService.updateNews(news);
            if (rows > 0) {
                result.put("success", true);
                result.put("message", "更新成功");
            } else {
                result.put("success", false);
                result.put("message", "新闻不存在");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "更新失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 删除新闻（管理员功能）
     * POST /api/news/delete/{id}
     */
    @PostMapping("/delete/{id}")
    public Map<String, Object> deleteNews(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            int rows = newsService.deleteNews(id);
            if (rows > 0) {
                result.put("success", true);
                result.put("message", "删除成功");
            } else {
                result.put("success", false);
                result.put("message", "新闻不存在");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除失败: " + e.getMessage());
        }
        return result;
    }
}
