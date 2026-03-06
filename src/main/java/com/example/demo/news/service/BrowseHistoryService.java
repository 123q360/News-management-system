package com.example.demo.news.service;

import com.example.demo.news.dao.BrowseHistoryDao;
import com.example.demo.news.entity.BrowseHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 浏览历史服务层
 */
@Service
public class BrowseHistoryService {

    @Autowired
    private BrowseHistoryDao browseHistoryDao;

    /**
     * 获取用户浏览历史（带筛选和分页）
     */
    public Map<String, Object> getBrowseHistory(Long userId, String category, String date, int page, int size) {
        int offset = (page - 1) * size;
        
        // 查询历史记录
        List<BrowseHistory> historyList = browseHistoryDao.findByUserIdWithFilters(userId, category, date, offset, size);
        
        // 统计总数
        int total = browseHistoryDao.countByUserIdWithFilters(userId, category, date);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", historyList);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("totalPages", (int) Math.ceil((double) total / size));
        
        return result;
    }

    /**
     * 删除单条浏览记录
     */
    @Transactional
    public void deleteHistory(Long id, Long userId) {
        int rows = browseHistoryDao.deleteById(id, userId);
        if (rows == 0) {
            throw new RuntimeException("删除失败，记录不存在或无权删除");
        }
    }

    /**
     * 清空用户所有浏览历史
     */
    @Transactional
    public void clearHistory(Long userId) {
        browseHistoryDao.deleteByUserId(userId);
    }
}
