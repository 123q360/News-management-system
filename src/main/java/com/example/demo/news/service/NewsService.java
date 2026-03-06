package com.example.demo.news.service;

import com.example.demo.news.dao.NewsDao;
import com.example.demo.news.entity.News;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 新闻服务
 */
@Service
public class NewsService {

    @Autowired
    private NewsDao newsDao;

    /**
     * 获取所有新闻
     */
    public List<News> getAllNews() {
        return newsDao.findAll();
    }

    /**
     * 根据类别获取新闻
     */
    public List<News> getNewsByCategory(String category) {
        return newsDao.findByCategory(category);
    }

    /**
     * 根据ID获取新闻详情
     */
    public News getNewsById(Long id) {
        return newsDao.findById(id);
    }

    /**
     * 创建新闻
     */
    public Long createNews(News news) {
        newsDao.insert(news);
        return news.getId();
    }

    /**
     * 更新新闻
     */
    public int updateNews(News news) {
        return newsDao.update(news);
    }

    /**
     * 删除新闻
     */
    public int deleteNews(Long id) {
        return newsDao.delete(id);
    }
}
