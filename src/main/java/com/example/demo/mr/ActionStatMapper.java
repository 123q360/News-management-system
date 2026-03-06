package com.example.demo.mr;

import com.example.demo.entity.hadoop.StatKey;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class ActionStatMapper extends Mapper<LongWritable, Text, StatKey, IntWritable> {
    private final Gson gson = new Gson();
    private final IntWritable one = new IntWritable(1);
    private final StatKey outKey = new StatKey();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        // 解析JSON数组
        try {
            Type listType = new TypeToken<List<ActionEvent>>(){}.getType();
            List<ActionEvent> events = gson.fromJson(value.toString(), listType);

            // 输出每个行为的键值对（时间+文章ID+用户ID+行为+设备，1）
            for (ActionEvent event : events) {
                outKey.setTimestamp(event.getTimestamp());  // 从 JSON 中直接读取时间戳
                outKey.setArticleId(event.getArticleId());
                outKey.setUserId(event.getUserId());        // 新增用户ID
                outKey.setAction(event.getAction());
                outKey.setDevice(event.getDevice());        // 新增设备类型
                context.write(outKey, one);
            }
        } catch (Exception e) {
            // 记录解析错误
            context.getCounter("MapperError", "JSON_PARSE_ERROR").increment(1);
        }
    }

    // 内部类：对应JSON中的行为事件结构
    static class ActionEvent {
        private String userId;
        private String action;
        private String articleId;
        private String timestamp;
        private String device;
    
        // getter方法
        public String getUserId() { return userId; }
        public String getArticleId() { return articleId; }
        public String getAction() { return action; }
        public String getTimestamp() { return timestamp; }
        public String getDevice() { return device; }
    }
}