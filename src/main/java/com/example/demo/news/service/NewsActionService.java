package com.example.demo.news.service;

import com.example.demo.entity.UserBehavior;
import com.example.demo.news.dao.BrowseHistoryDao;
import com.example.demo.news.dao.NewsDao;
import com.example.demo.news.dao.UserDao;
import com.example.demo.news.dao.UserPreferenceDao;
import com.example.demo.news.entity.BrowseHistory;
import com.example.demo.news.entity.News;
import com.example.demo.service.UserBehaviorSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户行为处理服务
 * 核心功能：处理用户对新闻的操作，同时更新新闻表和用户偏好表
 */
@Service
public class NewsActionService {

    @Autowired
    private NewsDao newsDao;

    @Autowired
    private UserPreferenceDao userPreferenceDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private BrowseHistoryDao browseHistoryDao;

    @Autowired
    private UserBehaviorSender userBehaviorSender;

    /**
     * 处理用户浏览新闻行为
     * 1. 新闻浏览量+1
     * 2. 用户对应类别偏好+1
     * 3. 发送数据到Hadoop（通过现有的DataCleanService）
     *
     * @param userId 用户ID
     * @param newsId 新闻ID
     * @param preferenceId 用户偏好ID
     */
    @Transactional
    public void handleViewAction(Long userId, Long newsId, Long preferenceId) {
        // 1. 查询新闻信息
        News news = newsDao.findById(newsId);
        if (news == null) {
            throw new RuntimeException("新闻不存在: " + newsId);
        }

        // 2. 更新新闻浏览量
        newsDao.incrementViewCount(newsId);

        // 3. 更新用户偏好（类别权值+1）
        userPreferenceDao.updatePreference(preferenceId, news.getCategory(), 1);

        // 4. 更新用户偏好类别
        updateUserPreferenceCategory(userId, preferenceId);

        // 5. 双写：保存浏览历史
        saveBrowseHistory(userId, news);

        // 6. 发送数据到Hadoop进行离线统计
        sendToHadoop(userId, newsId, "view");

        System.out.println("用户 " + userId + " 浏览了新闻 " + newsId + "（类别：" + news.getCategory() + "），偏好权值+1");
    }

    /**
     * 处理用户点赞新闻行为
     * 1. 新闻点赞量+1
     * 2. 用户对应类别偏好+2
     * 3. 发送数据到Hadoop
     */
    @Transactional
    public void handleLikeAction(Long userId, Long newsId, Long preferenceId) {
        News news = newsDao.findById(newsId);
        if (news == null) {
            throw new RuntimeException("新闻不存在: " + newsId);
        }

        // 更新新闻点赞量
        newsDao.incrementLikeCount(newsId);

        // 更新用户偏好（类别权值+2）
        userPreferenceDao.updatePreference(preferenceId, news.getCategory(), 2);

        // 更新用户偏好类别
        updateUserPreferenceCategory(userId, preferenceId);

        // 发送到Hadoop
        sendToHadoop(userId, newsId, "like");

        System.out.println("用户 " + userId + " 点赞了新闻 " + newsId + "（类别：" + news.getCategory() + "），偏好权值+2");
    }

    /**
     * 处理用户转发新闻行为
     * 1. 新闻转发量+1
     * 2. 用户对应类别偏好+2
     * 3. 发送数据到Hadoop
     */
    @Transactional
    public void handleShareAction(Long userId, Long newsId, Long preferenceId) {
        News news = newsDao.findById(newsId);
        if (news == null) {
            throw new RuntimeException("新闻不存在: " + newsId);
        }

        // 更新新闻转发量
        newsDao.incrementShareCount(newsId);

        // 更新用户偏好（类别权值+2）
        userPreferenceDao.updatePreference(preferenceId, news.getCategory(), 2);

        // 更新用户偏好类别
        updateUserPreferenceCategory(userId, preferenceId);

        // 发送到Hadoop
        sendToHadoop(userId, newsId, "share");

        System.out.println("用户 " + userId + " 转发了新闻 " + newsId + "（类别：" + news.getCategory() + "），偏好权值+2");
    }

    /**
     * 处理用户对新闻不感兴趣行为
     * 1. 新闻不感兴趣量+1
     * 2. 用户对应类别偏好-5
     * 3. 发送数据到Hadoop
     */
    @Transactional
    public void handleDislikeAction(Long userId, Long newsId, Long preferenceId) {
        News news = newsDao.findById(newsId);
        if (news == null) {
            throw new RuntimeException("新闻不存在: " + newsId);
        }

        // 更新新闻不感兴趣量
        newsDao.incrementDislikeCount(newsId);

        // 更新用户偏好（类别权值-5）
        userPreferenceDao.updatePreference(preferenceId, news.getCategory(), -5);

        // 更新用户偏好类别
        updateUserPreferenceCategory(userId, preferenceId);

        // 发送到Hadoop
        sendToHadoop(userId, newsId, "dislike");

        System.out.println("用户 " + userId + " 对新闻 " + newsId + "不感兴趣（类别：" + news.getCategory() + "），偏好权值-5");
    }

    /**
     * 更新用户偏好类别
     */
    private void updateUserPreferenceCategory(Long userId, Long preferenceId) {
        try {
            // 计算偏好类别
            String category = userPreferenceDao.calculatePreferenceCategory(preferenceId);
            // 更新到用户表
            userDao.updatePreferenceCategory(userId, category);
            System.out.println("用户 " + userId + " 的偏好类别更新为：" + category);
        } catch (Exception e) {
            System.err.println("更新用户偏好类别失败: " + e.getMessage());
        }
    }

    /**
     * 保存浏览历史（双写到browse_history表）
     */
    private void saveBrowseHistory(Long userId, News news) {
        try {
            BrowseHistory history = new BrowseHistory();
            history.setUserId(userId);
            history.setNewsId(news.getId());
            history.setNewsTitle(news.getTitle());
            history.setNewsCategory(news.getCategory());
            history.setDevice("web");  // 默认web，后续可从前端传入
            history.setBrowseTime(LocalDateTime.now());
            
            browseHistoryDao.insert(history);
            System.out.println("保存浏览历史成功：用户 " + userId + " -> 新闻 " + news.getId());
        } catch (Exception e) {
            System.err.println("保存浏览历史失败: " + e.getMessage());
            // 不抛出异常，避免影响主业务
        }
    }

    /**
     * 发送用户行为数据到Hadoop（复用现有的数据采集接口）
     */
    private void sendToHadoop(Long userId, Long newsId, String action) {
        try {
            // 构造UserBehavior对象
            UserBehavior behavior = new UserBehavior();
            behavior.setUserId(String.valueOf(userId));
            behavior.setArticleId(String.valueOf(newsId));
            behavior.setAction(action);
            
            // 直接使用本地时间，不做UTC转换
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            behavior.setTimestamp(sdf.format(new Date()));
            
            behavior.setDevice("web");  // 默认设备类型

            // 封装为List
            List<UserBehavior> behaviors = new ArrayList<>();
            behaviors.add(behavior);

            // 直接发送，不经过DataCleanService清洗
            userBehaviorSender.send(behaviors);
        } catch (Exception e) {
            System.err.println("发送数据到Hadoop失败: " + e.getMessage());
            // 不抛出异常，避免影响主业务流程
        }
    }
}
