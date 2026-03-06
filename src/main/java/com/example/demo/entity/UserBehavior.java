package com.example.demo.entity;

// 用户行为实体类
public class UserBehavior {
    private String userId;       // 用户ID
    private String action;       // 行为类型
    private String articleId;    // 文章ID
    private String timestamp;    // 行为时间（格式：yyyy-MM-dd HH:mm:ss）
    private String device;       // 设备：mobile/pc/tablet
    // 构造器、getter、setter

    public UserBehavior() {
    }

    public UserBehavior(String articleId, String userId, String action, String timestamp, String device) {
        this.articleId = articleId;
        this.userId = userId;
        this.action = action;
        this.timestamp = timestamp;
        this.device = device;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "UserBehavior{" +
                "userId='" + userId + '\'' +
                ", action='" + action + '\'' +
                ", articleId='" + articleId + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", device='" + device + '\'' +
                '}';
    }
}