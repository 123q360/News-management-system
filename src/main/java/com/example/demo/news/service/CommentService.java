package com.example.demo.news.service;

import com.example.demo.entity.UserBehavior;
import com.example.demo.news.dao.CommentDao;
import com.example.demo.news.dao.NewsDao;
import com.example.demo.news.dao.UserDao;
import com.example.demo.news.dao.UserPreferenceDao;
import com.example.demo.news.entity.Comment;
import com.example.demo.news.entity.News;
import com.example.demo.news.entity.User;
import com.example.demo.service.UserBehaviorSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 评论服务层
 */
@Service
public class CommentService {

    @Autowired
    private CommentDao commentDao;

    @Autowired
    private NewsDao newsDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserPreferenceDao userPreferenceDao;

    @Autowired
    private UserBehaviorSender userBehaviorSender;

    /**
     * 发表评论
     */
    @Transactional
    public Comment addComment(Long newsId, Long userId, Long parentId, Long replyToUserId, String content) {
        // 1. 验证新闻是否存在
        News news = newsDao.findById(newsId);
        if (news == null) {
            throw new RuntimeException("新闻不存在");
        }

        // 2. 验证用户是否存在
        User user = userDao.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 3. 如果是回复，验证父评论是否存在
        Long rootId = null;
        if (parentId != null) {
            Comment parent = commentDao.findById(parentId);
            if (parent == null) {
                throw new RuntimeException("父评论不存在");
            }
            // 继承根评论ID
            rootId = parent.getRootId() != null ? parent.getRootId() : parent.getId();
        }

        // 4. 创建评论
        Comment comment = new Comment();
        comment.setNewsId(newsId);
        comment.setUserId(userId);
        comment.setParentId(parentId);
        comment.setRootId(rootId);
        comment.setReplyToUserId(replyToUserId);
        comment.setContent(content);
        comment.setStatus("NORMAL");

        Long commentId = commentDao.insert(comment);

        // 5. 如果rootId为null，说明这是顶级评论，更新rootId为自己的ID
        if (rootId == null) {
            comment.setRootId(commentId);
            // 这里不需要更新数据库，因为插入时已经处理了
        }

        // 6. 如果是回复，更新父评论的回复数
        if (parentId != null) {
            commentDao.incrementReplyCount(parentId);
        }

        // 7. 更新新闻的评论数
        newsDao.incrementCommentCount(newsId);

        // 8. 更新用户偏好（评论行为权值+3，比浏览高）
        if (user.getPreferenceId() != null) {
            userPreferenceDao.updatePreference(user.getPreferenceId(), news.getCategory(), 3);
            // 更新用户偏好类别
            String category = userPreferenceDao.calculatePreferenceCategory(user.getPreferenceId());
            userDao.updatePreferenceCategory(userId, category);
        }

        // 9. 发送数据到Hadoop（评论行为分析）
        sendToHadoop(userId, newsId, "comment");

        System.out.println("用户 " + userId + " 评论了新闻 " + newsId + "（类别：" + news.getCategory() + "），偏好权值+3");

        return commentDao.findById(commentId);
    }

    /**
     * 查询评论列表（带分页和回复）
     */
    public Map<String, Object> getComments(Long newsId, int page, int size, String sort) {
        int offset = (page - 1) * size;

        // 1. 查询顶级评论
        List<Comment> topComments = commentDao.findTopCommentsByNewsId(newsId, offset, size, sort);

        // 2. 为每个顶级评论查询前3条回复
        for (Comment comment : topComments) {
            List<Comment> replies = commentDao.findReplies(comment.getId(), 0, 3);
            comment.setReplies(replies);
        }

        // 3. 查询总数
        int total = commentDao.countByNewsId(newsId);

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("comments", topComments);
        result.put("page", page);
        result.put("size", size);

        return result;
    }

    /**
     * 查询更多回复
     */
    public List<Comment> getMoreReplies(Long parentId, int page, int size) {
        int offset = (page - 1) * size;
        return commentDao.findReplies(parentId, offset, size);
    }

    /**
     * 点赞评论
     */
    @Transactional
    public void likeComment(Long commentId) {
        commentDao.incrementLikeCount(commentId);
    }

    /**
     * 删除评论（软删除）
     */
    @Transactional
    public void deleteComment(Long commentId, Long userId, boolean isAdmin) {
        Comment comment = commentDao.findById(commentId);

        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }

        // 权限检查：只能删除自己的评论，或管理员可以删除任何评论
        if (!isAdmin && !comment.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此评论");
        }

        // 软删除
        commentDao.updateStatus(commentId, "DELETED");
        commentDao.updateContent(commentId, "该评论已删除");

        System.out.println("评论 " + commentId + " 已被删除");
    }

    /**
     * 发送评论行为数据到Hadoop
     */
    private void sendToHadoop(Long userId, Long newsId, String action) {
        try {
            UserBehavior behavior = new UserBehavior();
            behavior.setUserId(String.valueOf(userId));
            behavior.setArticleId(String.valueOf(newsId));
            behavior.setAction(action);

            // 直接使用本地时间，不做UTC转换
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            behavior.setTimestamp(sdf.format(new Date()));

            behavior.setDevice("web");
            List<UserBehavior> behaviors = new ArrayList<>();
            behaviors.add(behavior);
            userBehaviorSender.send(behaviors);
        } catch (Exception e) {
            System.err.println("发送评论数据到Hadoop失败: " + e.getMessage());
        }
    }
}
