package com.example.demo.mr;

import com.example.demo.entity.hadoop.StatDBWritable;
import com.example.demo.entity.hadoop.StatKey;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Reducer;
import java.io.IOException;

public class ActionStatReducer extends Reducer<StatKey, IntWritable, StatDBWritable, NullWritable> {
    private final StatDBWritable outValue = new StatDBWritable();
    private final NullWritable nullWritable = NullWritable.get();

    @Override
    protected void reduce(StatKey key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        // 累加同一（时间+文章ID+用户ID+行为+设备）的次数
        int total = 0;
        for (IntWritable value : values) {
            total += value.get();
        }

        // 设置输出到MySQL的值
        outValue.setTimestamp(key.getTimestamp());
        outValue.setUserId(key.getUserId());
        outValue.setArticleId(key.getArticleId());
        outValue.setAction(key.getAction());
        outValue.setDevice(key.getDevice());
        outValue.setCount(total);
    
        context.write(outValue, nullWritable);
    }
}