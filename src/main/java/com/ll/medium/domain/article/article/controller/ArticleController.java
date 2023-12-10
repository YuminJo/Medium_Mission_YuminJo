package com.ll.medium.domain.article.article.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/post")
public class ArticleController {

	@GetMapping("/list")
	public String showAllPost() {
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
		return "article/article/list";
	}

	// @PreAuthorize("isAuthenticated()")
	// @PostMapping("/write")
	// public String write() {
	// 	return "article/article/list";
	// }
}
