package com.example.demo.news.dao;

import com.example.demo.news.entity.News;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 新闻数据访问层
 */
@Repository
public class NewsDao {

    private final JdbcTemplate jdbcTemplate;

    public NewsDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 查询所有新闻
     */
    public List<News> findAll() {
        String sql = "SELECT * FROM news WHERE status = 'PUBLISHED' ORDER BY publish_time DESC";
        return jdbcTemplate.query(sql, new NewsRowMapper());
    }

    /**
     * 根据类别查询新闻
     */
    public List<News> findByCategory(String category) {
        String sql = "SELECT * FROM news WHERE category = ? AND status = 'PUBLISHED' ORDER BY publish_time DESC";
        return jdbcTemplate.query(sql, new NewsRowMapper(), category);
    }

    /**
     * 根据ID查询新闻
     */
    public News findById(Long id) {
        String sql = "SELECT * FROM news WHERE id = ?";
        List<News> results = jdbcTemplate.query(sql, new NewsRowMapper(), id);
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * 插入新闻
     */
    public int insert(News news) {
        String sql = "INSERT INTO news (category, title, content, cover_image, author, status) VALUES (?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, news.getCategory(), news.getTitle(), news.getContent(),
                news.getCoverImage(), news.getAuthor(), news.getStatus());
    }

    /**
     * 更新新闻
     */
    public int update(News news) {
        String sql = "UPDATE news SET category = ?, title = ?, content = ?, cover_image = ?, author = ?, status = ? WHERE id = ?";
        return jdbcTemplate.update(sql, news.getCategory(), news.getTitle(), news.getContent(),
                news.getCoverImage(), news.getAuthor(), news.getStatus(), news.getId());
    }

    /**
     * 删除新闻（软删除）
     */
    public int delete(Long id) {
        String sql = "UPDATE news SET status = 'DELETED' WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    /**
     * 增加浏览量
     */
    public int incrementViewCount(Long newsId) {
        String sql = "UPDATE news SET view_count = view_count + 1 WHERE id = ?";
        return jdbcTemplate.update(sql, newsId);
    }

    /**
     * 增加点赞量
     */
    public int incrementLikeCount(Long newsId) {
        String sql = "UPDATE news SET like_count = like_count + 1 WHERE id = ?";
        return jdbcTemplate.update(sql, newsId);
    }

    /**
     * 增加转发量
     */
    public int incrementShareCount(Long newsId) {
        String sql = "UPDATE news SET share_count = share_count + 1 WHERE id = ?";
        return jdbcTemplate.update(sql, newsId);
    }

    /**
     * 增加不感兴趣量
     */
    public int incrementDislikeCount(Long newsId) {
        String sql = "UPDATE news SET dislike_count = dislike_count + 1 WHERE id = ?";
        return jdbcTemplate.update(sql, newsId);
    }

    /**
     * 增加评论数
     */
    public int incrementCommentCount(Long newsId) {
        String sql = "UPDATE news SET comment_count = comment_count + 1 WHERE id = ?";
        return jdbcTemplate.update(sql, newsId);
    }

    /**
     * RowMapper实现
     */
    private static class NewsRowMapper implements RowMapper<News> {
        @Override
        public News mapRow(ResultSet rs, int rowNum) throws SQLException {
            News news = new News();
            news.setId(rs.getLong("id"));
            news.setCategory(rs.getString("category"));
            news.setTitle(rs.getString("title"));
            news.setContent(rs.getString("content"));
            news.setCoverImage(rs.getString("cover_image"));
            news.setViewCount(rs.getInt("view_count"));
            news.setLikeCount(rs.getInt("like_count"));
            news.setShareCount(rs.getInt("share_count"));
            news.setDislikeCount(rs.getInt("dislike_count"));
            news.setAuthor(rs.getString("author"));
            news.setPublishTime(rs.getTimestamp("publish_time").toLocalDateTime());
            news.setStatus(rs.getString("status"));
            return news;
        }
    }
}
