package com.example.demo.service;

import com.example.demo.entity.UserBehavior;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class UserBehaviorSender {
    @Autowired
    private RestTemplate restTemplate;
    // Flume HTTP Source接收地址（与Flume配置一致）
    private static final String FLUME_HTTP_URL = "http://192.168.111.10:5140";
    // Gson实例（可配置序列化规则，如日期格式等）
    private static final Gson gson = new GsonBuilder()
            // 可选：配置日期序列化格式（如果timestamp是Date类型而非String）
            // .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();
    /**
     * 异步发送用户行为数据（不阻塞主线程）
     */
    @Async
    public CompletableFuture<Void> send(List<UserBehavior> behaviors) {
        // 1. 将UserBehavior对象转为JSON字符串
        String behaviorJson = gson.toJson(behaviors);
        System.out.println("原始用户行为JSON：" + behaviorJson);
        // 2. 构造Flume要求的格式（包含headers和body，外层用数组）
        Map<String, Object> flumeData = new HashMap<>();
        flumeData.put("headers", Collections.emptyMap());  // 空headers
        flumeData.put("body", behaviorJson);
        System.out.println(gson.toJson(flumeData));
        // 外层用数组包裹（即使单条数据）
        String flumeJson = gson.toJson(Collections.singletonList(flumeData));
        System.out.println(flumeJson);
        // 4. 发送POST请求到Flume
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(flumeJson, headers);
        try {
            restTemplate.postForEntity(FLUME_HTTP_URL, request, String.class);
            System.out.println("用户行为数据发送成功");
        } catch (Exception e) {
            System.err.println("发送数据到Hadoop失败: " + e.getMessage());
        }
        return CompletableFuture.completedFuture(null);
    }
}