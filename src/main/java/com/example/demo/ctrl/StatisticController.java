package com.example.demo.ctrl;

import com.example.demo.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stat")
public class StatisticController {
    @Autowired
    private StatisticService statisticService;

    /**
     * 触发每日行为统计
     * 前端按钮点击后调用此接口
     */
    // localhost:8080/stat/daily?date=2025-11-19
    @GetMapping("/daily")
    public ResponseEntity<String> triggerDailyStat(@RequestParam String date) {
        try {
            statisticService.statisticByDate(date);
            return ResponseEntity.ok("统计成功：<a href='/stat?date=" + date + "'>" + date + "</a>");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("统计失败：" + e.getMessage());
        }
    }
}
