package com.ll.medium.domain.article.article.controller;

import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ll.medium.domain.article.article.entity.Article;
import com.ll.medium.domain.article.article.service.ArticleService;
import com.ll.medium.domain.article.form.ArticleCreateForm;
import com.ll.medium.domain.member.member.entity.Member;
import com.ll.medium.domain.member.member.service.MemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/post")
public class ArticleController {
	private final ArticleService articleService;
	private final MemberService memberService;

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
	public String write(Model model, ArticleCreateForm articleCreateForm) {
		articleCreateForm.setIsPublished(true);

		model.addAttribute("articleCreateForm", articleCreateForm);
		return "domain/article/article/form";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/write")
	public String write(@Valid ArticleCreateForm articleCreateForm, BindingResult bindingResult, Principal principal) {
		if (bindingResult.hasErrors()) {
			return "domain/article/article/form";
		}
		Member member = this.memberService.getUser(principal.getName());
		this.articleService.create(articleCreateForm.getSubject(), articleCreateForm.getContent(),
			articleCreateForm.getIsPublished(), member);
		return "redirect:/post/list";
	}
}