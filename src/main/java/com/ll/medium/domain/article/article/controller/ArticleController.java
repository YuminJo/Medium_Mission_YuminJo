package com.ll.medium.domain.article.article.controller;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ll.medium.domain.article.article.entity.Article;
import com.ll.medium.domain.article.article.service.ArticleService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/post")
public class ArticleController {
	private final ArticleService articleService;
	@GetMapping("/list")
	public String showAllPost(Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
		Page<Article> paging = this.articleService.getList(page);
		model.addAttribute("paging", paging);
		return "domain/article/article/list";
	}

	@GetMapping("/myList")
	public String showMyPost() {
		return "domain/article/article/list";
	}

	@GetMapping("/{id}")
	public String detail() {
		return "article/article/list";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/write")
	public String write() {
		return "domain/article/article/form";
	}

	// @PreAuthorize("isAuthenticated()")
	// @PostMapping("/write")
	// public String write() {
	// 	return "article/article/list";
	// }
}
