package com.ll.medium.domain.article.article.controller;

import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ll.medium.domain.article.article.entity.Article;
import com.ll.medium.domain.article.article.service.ArticleService;
import com.ll.medium.domain.article.form.ArticleForm;
import com.ll.medium.domain.member.member.entity.Member;
import com.ll.medium.domain.member.member.service.MemberService;
import com.ll.medium.global.rq.Rq;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/post")
@RequiredArgsConstructor
public class ArticleController {
	private final ArticleService articleService;
	private final MemberService memberService;
	private final Rq rq;

	private static final String ARTICLE_LIST_VIEW = "domain/article/article/list";
	private static final String ARTICLE_DETAIL_VIEW = "domain/article/article/detail";
	private static final String FORM_VIEW = "domain/article/article/form";

	@GetMapping("/list")
	public String showAllPost(Model model,
		@RequestParam(value = "page", required = false, defaultValue = "0") int page,
		@RequestParam(value = "kw", required = false, defaultValue = "") String kw) {

		Page<Article> paging = this.articleService.getList(page, kw);
		model.addAttribute("paging", paging);
		model.addAttribute("kw", kw);
		return ARTICLE_LIST_VIEW;
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/myList")
	public String showMyPost(Model model,
		@RequestParam(value = "page", required = false, defaultValue = "0") int page,
		Principal principal) {

		Member member = this.memberService.getUser(principal.getName());
		Page<Article> paging = this.articleService.getList(page, member.getUsername());
		model.addAttribute("paging", paging);
		model.addAttribute("myList", true);
		return ARTICLE_LIST_VIEW;
	}

	@GetMapping("/{id}")
	public String detail(Model model, @PathVariable("id") Integer id, ArticleForm articleForm, Principal principal) {
		Article article = this.articleService.getArticle(id);

		if (!articleService.articleIsNotPublished(article, principal)) {
			return rq.redirect("/post/list","해당 게시물은 비공개 상태입니다.");
		}

		model.addAttribute("article", article);
		return ARTICLE_DETAIL_VIEW;
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/write")
	public String write(Model model, ArticleForm articleForm) {
		articleForm.setIsPublished(true);

		model.addAttribute("articleForm", articleForm);
		return FORM_VIEW;
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/write")
	public String write(@Valid ArticleForm articleForm, BindingResult bindingResult, Principal principal) {
		if (bindingResult.hasErrors()) {
			return FORM_VIEW;
		}

		Member member = this.memberService.getUser(principal.getName());
		this.articleService.create(articleForm.getSubject(), articleForm.getContent(),
			articleForm.getIsPublished(), member);
		return "redirect:/post/list";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{id}/modify")
	public String articleModify(Model model,ArticleForm articleForm, @PathVariable("id") Integer id, Principal principal) {
		Article article = this.articleService.getArticle(id);

		if(!article.getAuthor().getUsername().equals(principal.getName())) {
			return rq.redirect("/post/list","수정권한이 없습니다.");
		}

		articleForm.setSubject(article.getTitle());
		articleForm.setContent(article.getBody());
		articleForm.setIsPublished(article.isPublished());

		model.addAttribute("articleForm", articleForm);
		return FORM_VIEW;
	}
}