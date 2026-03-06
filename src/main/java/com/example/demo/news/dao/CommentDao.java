package com.example.demo.news.dao;

import com.example.demo.news.entity.Comment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 评论数据访问层
 */
@Repository
public class CommentDao {

    private final JdbcTemplate jdbcTemplate;

    public CommentDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 插入评论
     */
    public Long insert(Comment comment) {
        String sql = "INSERT INTO comment (news_id, user_id, parent_id, root_id, reply_to_user_id, content, status, create_time, update_time) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        jdbcTemplate.update(sql, 
            comment.getNewsId(), 
            comment.getUserId(), 
            comment.getParentId(),
            comment.getRootId(),
            comment.getReplyToUserId(),
            comment.getContent(),
            comment.getStatus() != null ? comment.getStatus() : "NORMAL",
            now,
            now
        );
        
        // 获取插入的ID
        String getIdSql = "SELECT LAST_INSERT_ID()";
        return jdbcTemplate.queryForObject(getIdSql, Long.class);
    }

    /**
     * 根据ID查询评论
     */
    public Comment findById(Long id) {
        String sql = "SELECT c.*, u.username, ru.username as reply_to_username " +
                    "FROM comment c " +
                    "LEFT JOIN user u ON c.user_id = u.id " +
                    "LEFT JOIN user ru ON c.reply_to_user_id = ru.id " +
                    "WHERE c.id = ?";
        List<Comment> results = jdbcTemplate.query(sql, new CommentRowMapper(), id);
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * 查询新闻的顶级评论（分页）
     */
    public List<Comment> findTopCommentsByNewsId(Long newsId, int offset, int limit, String sort) {
        String orderBy = "hot".equals(sort) ? "c.like_count DESC, c.create_time DESC" : "c.create_time DESC";
        
        String sql = "SELECT c.*, u.username " +
                    "FROM comment c " +
                    "LEFT JOIN user u ON c.user_id = u.id " +
                    "WHERE c.news_id = ? AND c.parent_id IS NULL AND c.status = 'NORMAL' " +
                    "ORDER BY " + orderBy + " " +
                    "LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, new CommentRowMapper(), newsId, limit, offset);
    }

    /**
     * 查询评论的回复（分页）
     */
    public List<Comment> findReplies(Long parentId, int offset, int limit) {
        String sql = "SELECT c.*, u.username, ru.username as reply_to_username " +
                    "FROM comment c " +
                    "LEFT JOIN user u ON c.user_id = u.id " +
                    "LEFT JOIN user ru ON c.reply_to_user_id = ru.id " +
                    "WHERE c.parent_id = ? AND c.status = 'NORMAL' " +
                    "ORDER BY c.create_time ASC " +
                    "LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, new CommentRowMapper(), parentId, limit, offset);
    }

    /**
     * 统计新闻的评论数
     */
    public int countByNewsId(Long newsId) {
        String sql = "SELECT COUNT(*) FROM comment WHERE news_id = ? AND status = 'NORMAL'";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, newsId);
        return count != null ? count : 0;
    }

    /**
     * 增加评论点赞数
     */
    public int incrementLikeCount(Long commentId) {
        String sql = "UPDATE comment SET like_count = like_count + 1 WHERE id = ?";
        return jdbcTemplate.update(sql, commentId);
    }

    /**
     * 增加评论回复数
     */
    public int incrementReplyCount(Long commentId) {
        String sql = "UPDATE comment SET reply_count = reply_count + 1 WHERE id = ?";
        return jdbcTemplate.update(sql, commentId);
    }

    /**
     * 更新评论状态
     */
    public int updateStatus(Long commentId, String status) {
        String sql = "UPDATE comment SET status = ?, update_time = ? WHERE id = ?";
        return jdbcTemplate.update(sql, status, java.time.LocalDateTime.now(), commentId);
    }

    /**
     * 更新评论内容
     */
    public int updateContent(Long commentId, String content) {
        String sql = "UPDATE comment SET content = ?, update_time = ? WHERE id = ?";
        return jdbcTemplate.update(sql, content, java.time.LocalDateTime.now(), commentId);
    }

    /**
     * 删除评论（真删除）
     */
    public int delete(Long commentId) {
        String sql = "DELETE FROM comment WHERE id = ?";
        return jdbcTemplate.update(sql, commentId);
    }

    /**
     * 查询所有评论（管理员用）
     */
    public List<Comment> findAll(int offset, int limit) {
        String sql = "SELECT c.*, u.username, n.title as news_title " +
                    "FROM comment c " +
                    "LEFT JOIN user u ON c.user_id = u.id " +
                    "LEFT JOIN news n ON c.news_id = n.id " +
                    "ORDER BY c.create_time DESC " +
                    "LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, new CommentRowMapper(), limit, offset);
    }

    /**
     * RowMapper实现
     */
    private static class CommentRowMapper implements RowMapper<Comment> {
        @Override
        public Comment mapRow(ResultSet rs, int rowNum) throws SQLException {
            Comment comment = new Comment();
            comment.setId(rs.getLong("id"));
            comment.setNewsId(rs.getLong("news_id"));
            comment.setUserId(rs.getLong("user_id"));
            comment.setUsername(rs.getString("username"));
            
            long parentId = rs.getLong("parent_id");
            comment.setParentId(rs.wasNull() ? null : parentId);
            
            long rootId = rs.getLong("root_id");
            comment.setRootId(rs.wasNull() ? null : rootId);
            
            long replyToUserId = rs.getLong("reply_to_user_id");
            comment.setReplyToUserId(rs.wasNull() ? null : replyToUserId);
            
            // reply_to_username可能不存在
            try {
                comment.setReplyToUsername(rs.getString("reply_to_username"));
            } catch (SQLException e) {
                comment.setReplyToUsername(null);
            }
            
            comment.setContent(rs.getString("content"));
            comment.setLikeCount(rs.getInt("like_count"));
            comment.setReplyCount(rs.getInt("reply_count"));
            comment.setStatus(rs.getString("status"));
            comment.setCreateTime(rs.getTimestamp("create_time").toLocalDateTime());
            comment.setUpdateTime(rs.getTimestamp("update_time").toLocalDateTime());
            
            return comment;
        }
    }
}
