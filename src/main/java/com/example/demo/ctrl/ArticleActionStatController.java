package com.example.demo.ctrl;

import com.example.demo.dao.ArticleActionDao;
import com.example.demo.entity.hadoop.StatDBWritable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ArticleActionStatController {

    private final JdbcTemplate jdbcTemplate;
    private final ArticleActionDao articleActionDao;

    // 注入JdbcTemplate和ArticleActionDao
    public ArticleActionStatController(JdbcTemplate jdbcTemplate, ArticleActionDao articleActionDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.articleActionDao = articleActionDao;
    }

    // localhost:8080/stat?date=2025-11-19
    @GetMapping("/stat")
    public String getStat(@RequestParam String date, Model model) {
        // SQL查询：按日期筛选数据（查询所有字段）
        String sql = "SELECT date, user_id, article_id, action, device, count FROM article_action_daily_stat WHERE DATE(date) = ?";
        List<StatDBWritable> stats = jdbcTemplate.query(
                sql,
                new Object[]{date},
                (rs, rowNum) -> new StatDBWritable(
                        rs.getString("date"),
                        rs.getString("user_id"),
                        rs.getString("article_id"),
                        rs.getString("action"),
                        rs.getString("device"),
                        rs.getInt("count")
                )
        );
        
        // 获取所有可用日期列表
        List<String> dates = articleActionDao.getDistinctDates();
        
        // 将数据传递给页面
        model.addAttribute("stats", stats);
        model.addAttribute("date", date);
        model.addAttribute("dates", dates); // 添加日期列表数据
        return "stat"; // 对应templates目录下的stat.html
    }
}