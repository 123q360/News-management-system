package com.example.demo.service;

import com.example.demo.entity.UserBehavior;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

// 示例：清洗后的数据发送
@Service
public class DataCleanService {
    @Autowired
    private UserBehaviorSender sender;

    public void cleanAndSend() {
        // 模拟清洗后的数据（实际场景从数据库/消息队列读取并清洗）
        UserBehavior behavior = new UserBehavior();
        behavior.setUserId("u12345");
        behavior.setAction("click");
        behavior.setArticleId("a67890");
        behavior.setTimestamp("2025-11-09 15:30:00");
        behavior.setDevice("mobile");

        // 发送到Flume
        // sender.send(behavior);
    }

    /**
     * 清洗并发送用户行为数据
     * @param behaviors 用户行为列表
     */
    public void cleanAndSend(List<UserBehavior> behaviors) {
        if (behaviors == null || behaviors.isEmpty()) {
            System.out.println("警告: 用户行为列表为空");
            return;
        }

        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 设置时区为UTC，因为输入时间戳包含'Z'表示UTC时间
        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        targetFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        for (UserBehavior behavior : behaviors) {
            try {
                // 1. 去除字符串字段的空格
                if (behavior.getUserId() != null) {
                    behavior.setUserId(behavior.getUserId().trim());
                }
                if (behavior.getAction() != null) {
                    behavior.setAction(behavior.getAction().trim());
                }
                if (behavior.getArticleId() != null) {  // 已修改为articleId
                    behavior.setArticleId(behavior.getArticleId().trim());
                }
                if (behavior.getDevice() != null) {
                    behavior.setDevice(behavior.getDevice().trim());
                }

                // 2. 时间戳格式转换 (ISO 8601 -> yyyy-MM-dd HH:mm:ss)
                if (behavior.getTimestamp() != null && !behavior.getTimestamp().isEmpty()) {
                    String originalTimestamp = behavior.getTimestamp().trim();

                    // 解析ISO 8601格式的时间戳
                    Date date = isoFormat.parse(originalTimestamp);

                    // 格式化为目标格式
                    String formattedTimestamp = targetFormat.format(date);
                    behavior.setTimestamp(formattedTimestamp);
                }

            } catch (Exception e) {
                System.err.println("清洗数据时发生错误: " + e.getMessage());
                System.err.println("问题数据: " + behavior);
            }
        }

        // 打印清洗后的数据（实际项目中这里可能是发送到消息队列、数据库等）
        System.out.println("清洗后的数据:");
        for (UserBehavior behavior : behaviors) {
            System.out.println(behavior);
        }

        sender.send(behaviors);
    }
}