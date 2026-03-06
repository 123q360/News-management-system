package com.example.demo.ctrl;

import com.example.demo.entity.UserBehavior;
import com.example.demo.service.DataCleanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class UserBehaviorController {
    @Autowired
    private DataCleanService dataCleanService;

    // 接收前端发送的JSON数组
    @PostMapping("/api/logs")
    public String receiveBehaviors(@RequestBody List<UserBehavior> behaviors) {
        // 处理行为数据（例如存入数据库或发送到Flume）
        System.out.println("收到" + behaviors.size() + "条行为数据");
        System.out.println(behaviors);
        dataCleanService.cleanAndSend(behaviors);
        return "success"; // 前端根据此响应判断是否发送成功
    }
}
