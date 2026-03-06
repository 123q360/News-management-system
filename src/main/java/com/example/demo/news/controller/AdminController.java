package com.example.demo.news.controller;

import com.example.demo.news.dao.NewsDao;
import com.example.demo.news.dao.UserDao;
import com.example.demo.news.entity.News;
import com.example.demo.news.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员控制器
 * 提供管理后台所需的各种接口
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserDao userDao;

    @Autowired
    private NewsDao newsDao;

    /**
     * 获取统计数据
     * GET /api/admin/stats
     */
    @GetMapping("/stats")
    public Map<String, Object> getStats(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 检查管理员权限
            if (!isAdmin(session)) {
                result.put("success", false);
                result.put("message", "无权限");
                return result;
            }

            // 统计总用户数
            String userCountSql = "SELECT COUNT(*) FROM user";
            Integer totalUsers = jdbcTemplate.queryForObject(userCountSql, Integer.class);

            // 统计总新闻数
            String newsCountSql = "SELECT COUNT(*) FROM news WHERE status = 'PUBLISHED'";
            Integer totalNews = jdbcTemplate.queryForObject(newsCountSql, Integer.class);

            // 统计今日浏览量（从所有新闻的浏览量总和）
            String viewCountSql = "SELECT SUM(view_count) FROM news";
            Integer totalViews = jdbcTemplate.queryForObject(viewCountSql, Integer.class);

            // 统计总点赞数
            String likeCountSql = "SELECT SUM(like_count) FROM news";
            Integer totalLikes = jdbcTemplate.queryForObject(likeCountSql, Integer.class);

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalUsers", totalUsers != null ? totalUsers : 0);
            stats.put("totalNews", totalNews != null ? totalNews : 0);
            stats.put("totalViews", totalViews != null ? totalViews : 0);
            stats.put("totalLikes", totalLikes != null ? totalLikes : 0);

            result.put("success", true);
            result.put("data", stats);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取统计数据失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取所有用户列表
     * GET /api/admin/users
     */
    @GetMapping("/users")
    public Map<String, Object> getAllUsers(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 检查管理员权限
            if (!isAdmin(session)) {
                result.put("success", false);
                result.put("message", "无权限");
                return result;
            }

            String sql = "SELECT u.id, u.username, u.role, u.preference_category, u.create_time, " +
                        "up.tech, up.sports, up.game, up.politics " +
                        "FROM user u " +
                        "LEFT JOIN user_preference up ON u.preference_id = up.id " +
                        "ORDER BY u.id DESC";
            
            List<Map<String, Object>> users = jdbcTemplate.queryForList(sql);

            result.put("success", true);
            result.put("data", users);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取用户列表失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取所有新闻列表（包括草稿）
     * GET /api/admin/news
     */
    @GetMapping("/news")
    public Map<String, Object> getAllNews(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 检查管理员权限
            if (!isAdmin(session)) {
                result.put("success", false);
                result.put("message", "无权限");
                return result;
            }

            String sql = "SELECT * FROM news ORDER BY publish_time DESC";
            List<Map<String, Object>> newsList = jdbcTemplate.queryForList(sql);

            result.put("success", true);
            result.put("data", newsList);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取新闻列表失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 删除用户
     * DELETE /api/admin/users/{id}
     */
    @DeleteMapping("/users/{id}")
    public Map<String, Object> deleteUser(@PathVariable Long id, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 检查管理员权限
            if (!isAdmin(session)) {
                result.put("success", false);
                result.put("message", "无权限");
                return result;
            }

            // 不能删除自己
            Long currentUserId = (Long) session.getAttribute("userId");
            if (currentUserId != null && currentUserId.equals(id)) {
                result.put("success", false);
                result.put("message", "不能删除当前登录的管理员");
                return result;
            }

            int rows = userDao.delete(id);
            
            if (rows > 0) {
                result.put("success", true);
                result.put("message", "删除成功");
            } else {
                result.put("success", false);
                result.put("message", "用户不存在");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 删除新闻
     * DELETE /api/admin/news/{id}
     */
    @DeleteMapping("/news/{id}")
    public Map<String, Object> deleteNews(@PathVariable Long id, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 检查管理员权限
            if (!isAdmin(session)) {
                result.put("success", false);
                result.put("message", "无权限");
                return result;
            }

            // 真正删除新闻
            String sql = "DELETE FROM news WHERE id = ?";
            int rows = jdbcTemplate.update(sql, id);
            
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

    /**
     * 获取新闻排行榜
     * GET /api/admin/news/ranking?metric=view_count&limit=10
     * 支持的指标：view_count, like_count, comment_count, share_count, hot_score
     */
    @GetMapping("/news/ranking")
    public Map<String, Object> getNewsRanking(
            @RequestParam(defaultValue = "view_count") String metric,
            @RequestParam(defaultValue = "10") int limit,
            HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 检查管理员权限
            if (!isAdmin(session)) {
                result.put("success", false);
                result.put("message", "无权限");
                return result;
            }

            // 验证指标参数
            String orderBy;
            switch (metric) {
                case "view_count":
                    orderBy = "view_count";
                    break;
                case "like_count":
                    orderBy = "like_count";
                    break;
                case "comment_count":
                    orderBy = "comment_count";
                    break;
                case "share_count":
                    orderBy = "share_count";
                    break;
                case "hot_score":
                    // 综合热度 = 浏览量*1 + 点赞量*2 + 评论量*3 + 转发量*2
                    orderBy = "(view_count * 1 + like_count * 2 + comment_count * 3 + share_count * 2)";
                    break;
                default:
                    orderBy = "view_count";
            }

            // 查询排行榜数据
            String sql = "SELECT " +
                    "id, " +
                    "category, " +
                    "title, " +
                    "author, " +
                    "view_count, " +
                    "like_count, " +
                    "comment_count, " +
                    "share_count, " +
                    "(view_count * 1 + like_count * 2 + comment_count * 3 + share_count * 2) AS hot_score, " +
                    "publish_time, " +
                    "cover_image " +
                    "FROM news " +
                    "WHERE status = 'PUBLISHED' " +
                    "ORDER BY " + orderBy + " DESC " +
                    "LIMIT ?";
            
            List<Map<String, Object>> ranking = jdbcTemplate.queryForList(sql, limit);

            result.put("success", true);
            result.put("metric", metric);
            result.put("limit", limit);
            result.put("data", ranking);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取排行榜失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }

    /**
     * 获取用户偏好分析数据
     * GET /api/admin/user-preference-analysis
     */
    @GetMapping("/user-preference-analysis")
    public Map<String, Object> getUserPreferenceAnalysis(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 检查管理员权限
            if (!isAdmin(session)) {
                result.put("success", false);
                result.put("message", "无权限");
                return result;
            }

            Map<String, Object> data = new HashMap<>();

            // 1. 偏好分布统计
            String distributionSql = 
                "SELECT " +
                "  preference_category, " +
                "  COUNT(*) as count " +
                "FROM user " +
                "GROUP BY preference_category";
            List<Map<String, Object>> distribution = jdbcTemplate.queryForList(distributionSql);
            data.put("distribution", distribution);

            // 2. 各类别统计（用户数和平均权值）
            String categorySql = 
                "SELECT " +
                "  COUNT(CASE WHEN preference_category = 'tech' THEN 1 END) as tech_users, " +
                "  COUNT(CASE WHEN preference_category = 'sports' THEN 1 END) as sports_users, " +
                "  COUNT(CASE WHEN preference_category = 'game' THEN 1 END) as game_users, " +
                "  COUNT(CASE WHEN preference_category = 'politics' THEN 1 END) as politics_users, " +
                "  AVG(COALESCE(up.tech, 0)) as avg_tech, " +
                "  AVG(COALESCE(up.sports, 0)) as avg_sports, " +
                "  AVG(COALESCE(up.game, 0)) as avg_game, " +
                "  AVG(COALESCE(up.politics, 0)) as avg_politics " +
                "FROM user u " +
                "LEFT JOIN user_preference up ON u.preference_id = up.id";
            Map<String, Object> categoryStats = jdbcTemplate.queryForMap(categorySql);
            data.put("categoryStats", categoryStats);

            // 3. 偏好权值TOP10用户
            String topUsersSql = 
                "SELECT " +
                "  u.id, " +
                "  u.username, " +
                "  u.preference_category, " +
                "  COALESCE(up.tech, 0) as tech, " +
                "  COALESCE(up.sports, 0) as sports, " +
                "  COALESCE(up.game, 0) as game, " +
                "  COALESCE(up.politics, 0) as politics, " +
                "  (COALESCE(up.tech, 0) + COALESCE(up.sports, 0) + COALESCE(up.game, 0) + COALESCE(up.politics, 0)) as total " +
                "FROM user u " +
                "LEFT JOIN user_preference up ON u.preference_id = up.id " +
                "WHERE u.role = 'USER' " +
                "ORDER BY total DESC " +
                "LIMIT 10";
            List<Map<String, Object>> topUsers = jdbcTemplate.queryForList(topUsersSql);
            data.put("topUsers", topUsers);

            // 4. 兴趣广度分析
            String interestBreadthSql = 
                "SELECT " +
                "  CASE " +
                "    WHEN (CASE WHEN up.tech > 0 THEN 1 ELSE 0 END + " +
                "          CASE WHEN up.sports > 0 THEN 1 ELSE 0 END + " +
                "          CASE WHEN up.game > 0 THEN 1 ELSE 0 END + " +
                "          CASE WHEN up.politics > 0 THEN 1 ELSE 0 END) = 0 THEN 'none' " +
                "    WHEN (CASE WHEN up.tech > 0 THEN 1 ELSE 0 END + " +
                "          CASE WHEN up.sports > 0 THEN 1 ELSE 0 END + " +
                "          CASE WHEN up.game > 0 THEN 1 ELSE 0 END + " +
                "          CASE WHEN up.politics > 0 THEN 1 ELSE 0 END) = 1 THEN 'single' " +
                "    WHEN (CASE WHEN up.tech > 0 THEN 1 ELSE 0 END + " +
                "          CASE WHEN up.sports > 0 THEN 1 ELSE 0 END + " +
                "          CASE WHEN up.game > 0 THEN 1 ELSE 0 END + " +
                "          CASE WHEN up.politics > 0 THEN 1 ELSE 0 END) = 2 THEN 'double' " +
                "    ELSE 'multiple' " +
                "  END as interest_type, " +
                "  COUNT(*) as count " +
                "FROM user u " +
                "LEFT JOIN user_preference up ON u.preference_id = up.id " +
                "WHERE u.role = 'USER' " +
                "GROUP BY interest_type";
            List<Map<String, Object>> interestBreadth = jdbcTemplate.queryForList(interestBreadthSql);
            data.put("interestBreadth", interestBreadth);

            // 5. 各类别总权值（用于对比图）
            String totalScoreSql = 
                "SELECT " +
                "  SUM(COALESCE(tech, 0)) as tech_total, " +
                "  SUM(COALESCE(sports, 0)) as sports_total, " +
                "  SUM(COALESCE(game, 0)) as game_total, " +
                "  SUM(COALESCE(politics, 0)) as politics_total " +
                "FROM user_preference";
            Map<String, Object> totalScores = jdbcTemplate.queryForMap(totalScoreSql);
            data.put("totalScores", totalScores);

            result.put("success", true);
            result.put("data", data);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取偏好分析数据失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }

    /**
     * 获取用户活跃时段统计数据（按小时）
     * GET /api/admin/hourly-stats?date=2025-12-01&action=view
     */
    @GetMapping("/hourly-stats")
    public Map<String, Object> getHourlyStats(
            @RequestParam(required = false) String date,
            @RequestParam(defaultValue = "view") String action,
            HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 检查管理员权限
            if (!isAdmin(session)) {
                result.put("success", false);
                result.put("message", "无权限");
                return result;
            }

            // 默认使用当前日期
            if (date == null || date.isEmpty()) {
                date = java.time.LocalDate.now().toString();
            }

            // 验证action参数
            if (!action.equals("view") && !action.equals("like") && !action.equals("comment")) {
                action = "view";
            }

            // 按小时统计数据
            String sql = "SELECT HOUR(date) as hour, SUM(count) as total " +
                        "FROM article_action_daily_stat " +
                        "WHERE DATE(date) = ? AND action = ? " +
                        "GROUP BY HOUR(date) " +
                        "ORDER BY hour";
            
            List<Map<String, Object>> hourlyData = jdbcTemplate.queryForList(sql, date, action);

            // 补全24小时数据（没有数据的小时填0）
            int[] hourCounts = new int[24];
            for (Map<String, Object> row : hourlyData) {
                int hour = ((Number) row.get("hour")).intValue();
                int total = ((Number) row.get("total")).intValue();
                hourCounts[hour] = total;
            }

            // 获取可用日期列表
            String datesSql = "SELECT DISTINCT DATE(date) as d FROM article_action_daily_stat ORDER BY d DESC";
            List<String> availableDates = jdbcTemplate.queryForList(datesSql, String.class);

            result.put("success", true);
            result.put("date", date);
            result.put("action", action);
            result.put("hourCounts", hourCounts);
            result.put("availableDates", availableDates);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取统计数据失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }

    /**
     * 检查是否为管理员
     */
    private boolean isAdmin(HttpSession session) {
        String role = (String) session.getAttribute("role");
        return "ADMIN".equals(role);
    }
}
