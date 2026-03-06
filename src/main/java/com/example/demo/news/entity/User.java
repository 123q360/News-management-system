package com.example.demo.news.entity;

import java.time.LocalDateTime;

/**
 * 用户实体类
 */
public class User {
    private Long id;
    private String username;
    private String password;
    private Long preferenceId;  // 关联用户偏好表ID
    private String role;  // USER 或 ADMIN
    private String preferenceCategory;  // 用户偏好类别：tech/sports/game/politics/无
    private LocalDateTime createTime;

    public User() {
    }

    public User(Long id, String username, String password, Long preferenceId, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.preferenceId = preferenceId;
        this.role = role;
        this.preferenceCategory = "无";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getPreferenceId() {
        return preferenceId;
    }

    public void setPreferenceId(Long preferenceId) {
        this.preferenceId = preferenceId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPreferenceCategory() {
        return preferenceCategory;
    }

    public void setPreferenceCategory(String preferenceCategory) {
        this.preferenceCategory = preferenceCategory;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", preferenceId=" + preferenceId +
                ", role='" + role + '\'' +
                ", preferenceCategory='" + preferenceCategory + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
