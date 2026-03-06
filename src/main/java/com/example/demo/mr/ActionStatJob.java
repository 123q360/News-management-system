package com.example.demo.mr;

import com.example.demo.entity.hadoop.StatDBWritable;
import com.example.demo.entity.hadoop.StatKey;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.db.DBConfiguration;
import org.apache.hadoop.mapreduce.lib.db.DBOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

public class ActionStatJob {
    public void run(String statDate) throws Exception {
        Configuration conf = new Configuration();
        // 配置HDFS和YARN（与集群一致）
        conf.set("fs.defaultFS", "hdfs://192.168.227.139:9000");
        conf.set("yarn.resourcemanager.address", "192.168.227.139:8032");

        // 配置MySQL连接
        DBConfiguration.configureDB(
                conf,
                "com.mysql.cj.jdbc.Driver",  // MySQL驱动
                "jdbc:mysql://localhost:3306/hadoop?useSSL=false&serverTimezone=UTC",  // 数据库URL
                "root",  // 用户名
                "chr20.20.20"  // 密码
        );

        // ========== 新增：运行MR前先删除该日期的旧数据 ==========
        try (java.sql.Connection conn = java.sql.DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/hadoop?useSSL=false&serverTimezone=UTC",
                "root",
                "chr20.20.20")) {
            String deleteSql = "DELETE FROM article_action_daily_stat WHERE DATE(date) = ?";
            try (java.sql.PreparedStatement ps = conn.prepareStatement(deleteSql)) {
                ps.setString(1, statDate);
                int deleted = ps.executeUpdate();
                System.out.println("已删除 " + statDate + " 的旧数据，共 " + deleted + " 条记录");
            }
        } catch (Exception e) {
            System.err.println("删除旧数据失败（可能是首次运行）: " + e.getMessage());
        }
        // ========================================================

        // 创建作业
        Job job = Job.getInstance(conf, "ArticleActionDailyStat-" + statDate);  // 修改作业名称
        job.setJarByClass(ActionStatJob.class);

        // 设置Mapper和Reducer
        job.setMapperClass(ActionStatMapper.class);
        job.setReducerClass(ActionStatReducer.class);

        // 设置Map输出类型
        job.setMapOutputKeyClass(StatKey.class);
        job.setMapOutputValueClass(IntWritable.class);

        // 设置最终输出类型（MySQL）
        job.setOutputKeyClass(StatDBWritable.class);
        job.setOutputValueClass(NullWritable.class);

        // 输入路径：HDFS上指定日期的所有小时数据
        FileInputFormat.addInputPath(job, new Path("/flume/data/" + statDate + "/*"));

        // 输出到MySQL：指定表名和字段
        DBOutputFormat.setOutput(
                job,
                "article_action_daily_stat",
                new String[]{"date", "user_id", "article_id", "action", "device", "count"}  // 新增 user_id 和 device 字段
        );

        // 提交作业并等待完成
        boolean success = job.waitForCompletion(true);
        if (!success) {
            throw new RuntimeException("统计作业执行失败：" + statDate);
        }
    }
}