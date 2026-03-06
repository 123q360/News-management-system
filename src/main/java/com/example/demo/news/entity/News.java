package com.example.demo.news.entity;

import java.time.LocalDateTime;

/**
 * 新闻实体类
 */
public class News {
    private Long id;
    private String category;        // 类别：tech/sports/game/politics
    private String title;           // 标题
    private String content;         // 内容
    private String coverImage;      // 封面图
    private Integer viewCount;      // 浏览量
    private Integer likeCount;      // 点赞量
    private Integer shareCount;     // 转发量
    private Integer dislikeCount;   // 不感兴趣量
    private String author;          // 作者
    private LocalDateTime publishTime;
    private LocalDateTime updateTime;
    private String status;          // 状态：PUBLISHED/DRAFT

    public News() {
        this.viewCount = 0;
        this.likeCount = 0;
        this.shareCount = 0;
        this.dislikeCount = 0;
        this.status = "PUBLISHED";
    }

    public News(Long id, String category, String title, String content, String coverImage, String author) {
        this.id = id;
        this.category = category;
        this.title = title;
        this.content = content;
        this.coverImage = coverImage;
        this.author = author;
        this.viewCount = 0;
        this.likeCount = 0;
        this.shareCount = 0;
        this.dislikeCount = 0;
        this.status = "PUBLISHED";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getShareCount() {
        return shareCount;
    }

    public void setShareCount(Integer shareCount) {
        this.shareCount = shareCount;
    }

    public Integer getDislikeCount() {
        return dislikeCount;
    }

    public void setDislikeCount(Integer dislikeCount) {
        this.dislikeCount = dislikeCount;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public LocalDateTime getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(LocalDateTime publishTime) {
        this.publishTime = publishTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 增加浏览量
     */
    public void incrementViewCount() {
        this.viewCount++;
    }

    /**
     * 增加点赞量
     */
    public void incrementLikeCount() {
        this.likeCount++;
    }

    /**
     * 增加转发量
     */
    public void incrementShareCount() {
        this.shareCount++;
    }

    /**
     * 增加不感兴趣量
     */
    public void incrementDislikeCount() {
        this.dislikeCount++;
    }

    @Override
    public String toString() {
        return "News{" +
                "id=" + id +
                ", category='" + category + '\'' +
                ", title='" + title + '\'' +
                ", viewCount=" + viewCount +
                ", likeCount=" + likeCount +
                ", shareCount=" + shareCount +
                ", dislikeCount=" + dislikeCount +
                ", publishTime=" + publishTime +
                '}';
    }
}
