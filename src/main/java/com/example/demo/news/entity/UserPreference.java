package com.example.demo.news.entity;

import java.time.LocalDateTime;

/**
 * 用户偏好实体类
 */
public class UserPreference {
    private Long id;
    private Integer tech;       // 科技类偏好权值
    private Integer sports;     // 运动类偏好权值
    private Integer game;       // 游戏类偏好权值
    private Integer politics;   // 政治类偏好权值
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public UserPreference() {
        this.tech = 0;
        this.sports = 0;
        this.game = 0;
        this.politics = 0;
    }

    public UserPreference(Long id, Integer tech, Integer sports, Integer game, Integer politics) {
        this.id = id;
        this.tech = tech;
        this.sports = sports;
        this.game = game;
        this.politics = politics;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTech() {
        return tech;
    }

    public void setTech(Integer tech) {
        this.tech = tech;
    }

    public Integer getSports() {
        return sports;
    }

    public void setSports(Integer sports) {
        this.sports = sports;
    }

    public Integer getGame() {
        return game;
    }

    public void setGame(Integer game) {
        this.game = game;
    }

    public Integer getPolitics() {
        return politics;
    }

    public void setPolitics(Integer politics) {
        this.politics = politics;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 根据类别更新偏好权值
     * @param category 类别：tech/sports/game/politics
     * @param delta 变化值（可为正或负）
     */
    public void updatePreference(String category, int delta) {
        switch (category.toLowerCase()) {
            case "tech":
                this.tech = Math.max(0, this.tech + delta);  // 确保不为负数
                break;
            case "sports":
                this.sports = Math.max(0, this.sports + delta);
                break;
            case "game":
                this.game = Math.max(0, this.game + delta);
                break;
            case "politics":
                this.politics = Math.max(0, this.politics + delta);
                break;
        }
    }

    @Override
    public String toString() {
        return "UserPreference{" +
                "id=" + id +
                ", tech=" + tech +
                ", sports=" + sports +
                ", game=" + game +
                ", politics=" + politics +
                ", updateTime=" + updateTime +
                '}';
    }
}
