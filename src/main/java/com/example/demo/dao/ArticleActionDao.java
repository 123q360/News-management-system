package com.example.demo.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ArticleActionDao {

    private final JdbcTemplate jdbcTemplate;

    public ArticleActionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 查询所有 distinct 日期（只取日期部分，不含时间）
    public List<String> getDistinctDates() {
        String sql = "SELECT DISTINCT DATE(date) as date FROM article_action_daily_stat ORDER BY date DESC";
        return jdbcTemplate.queryForList(sql, String.class);
    }
}