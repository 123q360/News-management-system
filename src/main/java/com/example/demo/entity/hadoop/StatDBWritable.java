package com.example.demo.entity.hadoop;

import org.apache.hadoop.mapreduce.lib.db.DBWritable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

// Reduce端输出键
public class StatDBWritable implements DBWritable {
    private String timestamp;  // 行为时间(yyyy-MM-dd HH:mm:ss)
    private String userId;     // 用户ID
    private String articleId;  // 文章ID
    private String action;     // 行为类型
    private String device;     // 设备类型
    private int count;         // 统计次数

    public StatDBWritable() {
    }

    public StatDBWritable(String timestamp, String userId, String articleId, String action, String device, int count) {
        this.timestamp = timestamp;
        this.userId = userId;
        this.articleId = articleId;
        this.action = action;
        this.device = device;
        this.count = count;
    }

    // getter、setter
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    // 写入MySQL的PreparedStatement
    @Override
    public void write(PreparedStatement ps) throws SQLException {
        ps.setString(1, timestamp);  // DATETIME类型用setString
        ps.setString(2, userId);
        ps.setString(3, articleId);
        ps.setString(4, action);
        ps.setString(5, device);
        ps.setInt(6, count);
    }

    // 从ResultSet读取（本场景暂用不到，可空实现）
    @Override
    public void readFields(ResultSet rs) throws SQLException {}
}