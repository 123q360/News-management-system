package com.example.demo.service;

import com.example.demo.mr.ActionStatJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Service
public class StatisticService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 按日期统计交互行为
     * @param date 格式：yyyy-MM-dd
     */
    public void statisticByDate(String date) throws Exception {
        // 1. 验证日期格式
        validateDateFormat(date);

        // 2. 先删除该日期的旧数据（避免重复）
        jdbcTemplate.update("DELETE FROM article_action_daily_stat WHERE date = ?", date);  // 修改表名

        // 3. 执行MapReduce作业
        ActionStatJob job = new ActionStatJob();
        job.run(date);
    }

    // 验证日期格式
    private void validateDateFormat(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);  // 严格校验
        sdf.parse(date);
    }
}