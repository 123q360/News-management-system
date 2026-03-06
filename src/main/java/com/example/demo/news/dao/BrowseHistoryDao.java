package com.example.demo.news.dao;

import com.example.demo.news.entity.BrowseHistory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 浏览历史数据访问层
 */
@Repository
public class BrowseHistoryDao {

    private final JdbcTemplate jdbcTemplate;

    public BrowseHistoryDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 插入浏览记录
     */
    public Long insert(BrowseHistory history) {
        String sql = "INSERT INTO browse_history (user_id, news_id, news_title, news_category, device, browse_time, create_time) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
            history.getUserId(),
            history.getNewsId(),
            history.getNewsTitle(),
            history.getNewsCategory(),
            history.getDevice(),
            history.getBrowseTime(),
            java.time.LocalDateTime.now()
        );
        
        // 获取插入的ID
        String getIdSql = "SELECT LAST_INSERT_ID()";
        return jdbcTemplate.queryForObject(getIdSql, Long.class);
    }

    /**
     * 查询用户浏览历史（分页）
     */
    public List<BrowseHistory> findByUserId(Long userId, int offset, int limit) {
        String sql = "SELECT * FROM browse_history " +
                    "WHERE user_id = ? " +
                    "ORDER BY browse_time DESC " +
                    "LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, new BrowseHistoryRowMapper(), userId, limit, offset);
    }

    /**
     * 查询用户浏览历史（带分类筛选）
     */
    public List<BrowseHistory> findByUserIdAndCategory(Long userId, String category, int offset, int limit) {
        String sql = "SELECT * FROM browse_history " +
                    "WHERE user_id = ? AND news_category = ? " +
                    "ORDER BY browse_time DESC " +
                    "LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, new BrowseHistoryRowMapper(), userId, category, limit, offset);
    }

    /**
     * 查询用户浏览历史（带日期筛选）
     */
    public List<BrowseHistory> findByUserIdAndDate(Long userId, String date, int offset, int limit) {
        String sql = "SELECT * FROM browse_history " +
                    "WHERE user_id = ? AND DATE(browse_time) = ? " +
                    "ORDER BY browse_time DESC " +
                    "LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, new BrowseHistoryRowMapper(), userId, date, limit, offset);
    }

    /**
     * 查询用户浏览历史（带分类和日期筛选）
     */
    public List<BrowseHistory> findByUserIdWithFilters(Long userId, String category, String date, int offset, int limit) {
        StringBuilder sql = new StringBuilder("SELECT * FROM browse_history WHERE user_id = ?");
        List<Object> params = new ArrayList<>();
        params.add(userId);
        
        if (category != null && !category.isEmpty()) {
            sql.append(" AND news_category = ?");
            params.add(category);
        }
        
        if (date != null && !date.isEmpty()) {
            sql.append(" AND DATE(browse_time) = ?");
            params.add(date);
        }
        
        sql.append(" ORDER BY browse_time DESC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);
        
        return jdbcTemplate.query(sql.toString(), new BrowseHistoryRowMapper(), params.toArray());
    }

    /**
     * 统计用户浏览历史总数
     */
    public int countByUserId(Long userId) {
        String sql = "SELECT COUNT(*) FROM browse_history WHERE user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count != null ? count : 0;
    }

    /**
     * 统计用户浏览历史总数（带筛选）
     */
    public int countByUserIdWithFilters(Long userId, String category, String date) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM browse_history WHERE user_id = ?");
        List<Object> params = new ArrayList<>();
        params.add(userId);
        
        if (category != null && !category.isEmpty()) {
            sql.append(" AND news_category = ?");
            params.add(category);
        }
        
        if (date != null && !date.isEmpty()) {
            sql.append(" AND DATE(browse_time) = ?");
            params.add(date);
        }
        
        Integer count = jdbcTemplate.queryForObject(sql.toString(), Integer.class, params.toArray());
        return count != null ? count : 0;
    }

    /**
     * 删除单条浏览记录
     */
    public int deleteById(Long id, Long userId) {
        String sql = "DELETE FROM browse_history WHERE id = ? AND user_id = ?";
        return jdbcTemplate.update(sql, id, userId);
    }

    /**
     * 清空用户所有浏览历史
     */
    public int deleteByUserId(Long userId) {
        String sql = "DELETE FROM browse_history WHERE user_id = ?";
        return jdbcTemplate.update(sql, userId);
    }

    /**
     * RowMapper实现
     */
    private static class BrowseHistoryRowMapper implements RowMapper<BrowseHistory> {
        @Override
        public BrowseHistory mapRow(ResultSet rs, int rowNum) throws SQLException {
            BrowseHistory history = new BrowseHistory();
            history.setId(rs.getLong("id"));
            history.setUserId(rs.getLong("user_id"));
            history.setNewsId(rs.getLong("news_id"));
            history.setNewsTitle(rs.getString("news_title"));
            history.setNewsCategory(rs.getString("news_category"));
            history.setDevice(rs.getString("device"));
            history.setBrowseTime(rs.getTimestamp("browse_time").toLocalDateTime());
            history.setCreateTime(rs.getTimestamp("create_time").toLocalDateTime());
            return history;
        }
    }
}
