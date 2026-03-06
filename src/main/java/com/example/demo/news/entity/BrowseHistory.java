package com.example.demo.news.entity;

import java.time.LocalDateTime;

/**
 * 浏览历史实体类
 */
public class BrowseHistory {
    private Long id;
    private Long userId;              // 用户ID
    private Long newsId;              // 新闻ID
    private String newsTitle;         // 新闻标题
    private String newsCategory;      // 新闻类别
    private String device;            // 设备类型
    private LocalDateTime browseTime; // 浏览时间
    private LocalDateTime createTime; // 创建时间

    public BrowseHistory() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getNewsId() {
        return newsId;
    }

    public void setNewsId(Long newsId) {
        this.newsId = newsId;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getNewsCategory() {
        return newsCategory;
    }

    public void setNewsCategory(String newsCategory) {
        this.newsCategory = newsCategory;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public LocalDateTime getBrowseTime() {
        return browseTime;
    }

    public void setBrowseTime(LocalDateTime browseTime) {
        this.browseTime = browseTime;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "BrowseHistory{" +
                "id=" + id +
                ", userId=" + userId +
                ", newsId=" + newsId +
                ", newsTitle='" + newsTitle + '\'' +
                ", newsCategory='" + newsCategory + '\'' +
                ", device='" + device + '\'' +
                ", browseTime=" + browseTime +
                '}';
    }
}
