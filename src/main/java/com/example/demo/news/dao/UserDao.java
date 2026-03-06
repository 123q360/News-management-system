package com.example.demo.news.dao;

import com.example.demo.news.entity.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 用户数据访问层
 */
@Repository
public class UserDao {

    private final JdbcTemplate jdbcTemplate;

    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 根据用户名查询用户
     */
    public User findByUsername(String username) {
        String sql = "SELECT * FROM user WHERE username = ?";
        List<User> results = jdbcTemplate.query(sql, new UserRowMapper(), username);
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * 根据ID查询用户
     */
    public User findById(Long id) {
        String sql = "SELECT * FROM user WHERE id = ?";
        List<User> results = jdbcTemplate.query(sql, new UserRowMapper(), id);
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * 插入用户
     * @return 插入的用户ID
     */
    public Long insert(User user) {
        String sql = "INSERT INTO user (username, password, preference_id, role, preference_category, create_time) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getUsername(), user.getPassword(), 
                user.getPreferenceId(), user.getRole(), user.getPreferenceCategory(), 
                java.time.LocalDateTime.now());
        
        // 获取插入的ID
        String getIdSql = "SELECT LAST_INSERT_ID()";
        return jdbcTemplate.queryForObject(getIdSql, Long.class);
    }

    /**
     * 更新用户信息
     */
    public int update(User user) {
        String sql = "UPDATE user SET username = ?, password = ?, role = ?, preference_category = ? WHERE id = ?";
        return jdbcTemplate.update(sql, user.getUsername(), user.getPassword(), 
                user.getRole(), user.getPreferenceCategory(), user.getId());
    }

    /**
     * 删除用户
     */
    public int delete(Long id) {
        String sql = "DELETE FROM user WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    /**
     * 检查用户名是否存在
     */
    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM user WHERE username = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username);
        return count != null && count > 0;
    }

    /**
     * 更新用户偏好类别
     */
    public int updatePreferenceCategory(Long userId, String category) {
        String sql = "UPDATE user SET preference_category = ? WHERE id = ?";
        return jdbcTemplate.update(sql, category, userId);
    }

    /**
     * RowMapper实现
     */
    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setPreferenceId(rs.getLong("preference_id"));
            user.setRole(rs.getString("role"));
            user.setPreferenceCategory(rs.getString("preference_category"));
            user.setCreateTime(rs.getTimestamp("create_time").toLocalDateTime());
            return user;
        }
    }
}
