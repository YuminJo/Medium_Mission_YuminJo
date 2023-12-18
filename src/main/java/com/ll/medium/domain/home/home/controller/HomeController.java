package com.ll.medium.domain.home.home.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ll.medium.domain.article.article.entity.Article;
import com.ll.medium.domain.article.article.service.ArticleService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {
	private final ArticleService articleService;
	@GetMapping("/")
	public String showMain(Model model) {
		List<Article> recentArticle = this.articleService.getRecentArticle();
		model.addAttribute("recentArticle", recentArticle);

		return "domain/home/home/main";
	}
}
