package com.example.demo.entity.hadoop;

import org.apache.hadoop.io.WritableComparable;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class StatKey implements WritableComparable<StatKey> {
    private String timestamp;  // 行为时间(精确到秒)
    private String articleId;  // 文章ID
    private String userId;     // 用户ID
    private String action;     // 行为类型
    private String device;     // 设备类型

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getArticleId() {  // 修改为getArticleId
        return articleId;
    }

    public void setArticleId(String articleId) {  // 修改为setArticleId
        this.articleId = articleId;
    }

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

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(timestamp);
        out.writeUTF(articleId);
        out.writeUTF(userId);
        out.writeUTF(action);
        out.writeUTF(device);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.timestamp = in.readUTF();
        this.articleId = in.readUTF();
        this.userId = in.readUTF();
        this.action = in.readUTF();
        this.device = in.readUTF();
    }

    // 排序规则：先按时间，再文章ID，再用户ID，再行为类型，最后设备
    @Override
    public int compareTo(StatKey o) {
        int cmp = this.timestamp.compareTo(o.timestamp);
        if (cmp != 0) return cmp;
        cmp = this.articleId.compareTo(o.articleId);
        if (cmp != 0) return cmp;
        cmp = this.userId.compareTo(o.userId);
        if (cmp != 0) return cmp;
        cmp = this.action.compareTo(o.action);
        return cmp != 0 ? cmp : this.device.compareTo(o.device);
    }
}