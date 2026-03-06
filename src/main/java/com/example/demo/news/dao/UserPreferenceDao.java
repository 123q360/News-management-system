package com.example.demo.news.dao;

import com.example.demo.news.entity.UserPreference;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 用户偏好数据访问层
 */
@Repository
public class UserPreferenceDao {

    private final JdbcTemplate jdbcTemplate;

    public UserPreferenceDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 根据ID查询用户偏好
     */
    public UserPreference findById(Long id) {
        String sql = "SELECT * FROM user_preference WHERE id = ?";
        List<UserPreference> results = jdbcTemplate.query(sql, new UserPreferenceRowMapper(), id);
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * 插入用户偏好（默认所有类别为0）
     */
    public Long insert(UserPreference preference) {
        String sql = "INSERT INTO user_preference (tech, sports, game, politics, create_time) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, preference.getTech(), preference.getSports(),
                preference.getGame(), preference.getPolitics(), 
                java.time.LocalDateTime.now());
        
        // 获取插入的ID
        String getIdSql = "SELECT LAST_INSERT_ID()";
        return jdbcTemplate.queryForObject(getIdSql, Long.class);
    }

    /**
     * 更新用户偏好的某个类别权值
     * @param preferenceId 偏好ID
     * @param category 类别：tech/sports/game/politics
     * @param delta 变化值（可为正或负）
     */
    public int updatePreference(Long preferenceId, String category, int delta) {
        String columnName = getCategoryColumnName(category);
        if (columnName == null) {
            throw new IllegalArgumentException("Invalid category: " + category);
        }
        
        // 使用GREATEST确保权值不为负数，同时更新update_time
        String sql = "UPDATE user_preference SET " + columnName + " = GREATEST(0, " + columnName + " + ?), update_time = ? WHERE id = ?";
        return jdbcTemplate.update(sql, delta, java.time.LocalDateTime.now(), preferenceId);
    }

    /**
     * 批量更新用户偏好
     */
    public int update(UserPreference preference) {
        String sql = "UPDATE user_preference SET tech = ?, sports = ?, game = ?, politics = ?, update_time = ? WHERE id = ?";
        return jdbcTemplate.update(sql, preference.getTech(), preference.getSports(),
                preference.getGame(), preference.getPolitics(), 
                java.time.LocalDateTime.now(), preference.getId());
    }

    /**
     * 将类别名称映射到数据库列名
     */
    private String getCategoryColumnName(String category) {
        switch (category.toLowerCase()) {
            case "tech":
                return "tech";
            case "sports":
                return "sports";
            case "game":
                return "game";
            case "politics":
                return "politics";
            default:
                return null;
        }
    }

    /**
     * 计算用户偏好类别（权值最高的类别）
     * @param preferenceId 偏好ID
     * @return 类别名称：tech/sports/game/politics/无
     */
    public String calculatePreferenceCategory(Long preferenceId) {
        UserPreference preference = findById(preferenceId);
        if (preference == null) {
            return "无";
        }

        // 获取各个类别的权值
        int tech = preference.getTech();
        int sports = preference.getSports();
        int game = preference.getGame();
        int politics = preference.getPolitics();

        // 找出最大值
        int maxValue = Math.max(Math.max(tech, sports), Math.max(game, politics));

        // 如果最大值为0，返回"无"
        if (maxValue == 0) {
            return "无";
        }

        // 统计有多少个类别达到最大值
        int count = 0;
        String topCategory = "无";

        if (tech == maxValue) {
            count++;
            topCategory = "tech";
        }
        if (sports == maxValue) {
            count++;
            topCategory = "sports";
        }
        if (game == maxValue) {
            count++;
            topCategory = "game";
        }
        if (politics == maxValue) {
            count++;
            topCategory = "politics";
        }

        // 如果多个类别权值相同，返回"无"
        if (count > 1) {
            return "无";
        }

        return topCategory;
    }

    /**
     * RowMapper实现
     */
    private static class UserPreferenceRowMapper implements RowMapper<UserPreference> {
        @Override
        public UserPreference mapRow(ResultSet rs, int rowNum) throws SQLException {
            UserPreference preference = new UserPreference();
            preference.setId(rs.getLong("id"));
            preference.setTech(rs.getInt("tech"));
            preference.setSports(rs.getInt("sports"));
            preference.setGame(rs.getInt("game"));
            preference.setPolitics(rs.getInt("politics"));
            preference.setCreateTime(rs.getTimestamp("create_time").toLocalDateTime());
            preference.setUpdateTime(rs.getTimestamp("update_time").toLocalDateTime());
            return preference;
        }
    }
}
