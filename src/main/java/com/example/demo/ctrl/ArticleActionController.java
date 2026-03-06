package com.example.demo.ctrl;

import com.example.demo.dao.ArticleActionDao;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ArticleActionController {

    private final ArticleActionDao articleActionDao;

    public ArticleActionController(ArticleActionDao articleActionDao) {
        this.articleActionDao = articleActionDao;
    }

    // 首页：展示所有已统计日期
    @GetMapping("/")
    public String index(Model model) {
        List<String> dates = articleActionDao.getDistinctDates();
        model.addAttribute("dates", dates);
        return "index";
    }
}