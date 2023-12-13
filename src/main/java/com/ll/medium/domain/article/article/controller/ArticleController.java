package com.ll.medium.domain.article.article.controller;

import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.ll.medium.domain.article.article.entity.Article;
import com.ll.medium.domain.article.article.service.ArticleService;
import com.ll.medium.domain.article.form.ArticleForm;
import com.ll.medium.domain.member.member.entity.Member;
import com.ll.medium.domain.member.member.service.MemberService;
import com.ll.medium.global.errors.UserErrorMessage;
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
	private static final String REDIRECT_POST_LIST = "redirect:/post/list";

	@GetMapping("/list")
	public String showAllPost(Model model,
		@RequestParam(value = "page", required = false, defaultValue = "0") int page,
		@RequestParam(value = "kw", required = false, defaultValue = "") String kw) {
		Page<Article> paging = articleService.getList(page, kw, true);
		model.addAttribute("paging", paging);
		model.addAttribute("kw", kw);
		return ARTICLE_LIST_VIEW;
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/myList")
	public String showMyPost(Model model,
		@RequestParam(value = "page", required = false, defaultValue = "0") int page,
		Principal principal) {
		Member member = memberService.getUser(principal.getName());
		Page<Article> paging = articleService.getList(page, member.getUsername(), false);
		model.addAttribute("paging", paging);
		model.addAttribute("listusername", member.getUsername());
		return ARTICLE_LIST_VIEW;
	}
	@GetMapping("/{id}")
	public String detail(Model model, @PathVariable("id") Integer id, ArticleForm articleForm, Principal principal) {
		Article article = articleService.getArticle(id);

		if (article == null) {
			return rq.historyBack(UserErrorMessage.NO_ARTICLE);
		}

		if (!articleService.articleIsNotPublished(article, principal)) {
			return rq.historyBack(UserErrorMessage.PRIVATE_ARTICLE);
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
		this.articleService.create(articleForm.getSubject(), articleForm.getContent(), articleForm.getIsPublished(),
			member);
		return "redirect:/post/list";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{id}/modify")
	public String articleModify(Model model, ArticleForm articleForm, @PathVariable("id") Integer id,
		Principal principal) {
		Article article = this.articleService.getArticle(id);

		if (!article.getAuthor().getUsername().equals(principal.getName())) {
			return rq.redirect(String.format("/post/%s", id), UserErrorMessage.USER_NO_MODIFY_PERMISSION);
		}

		articleForm.setSubject(article.getTitle());
		articleForm.setContent(article.getBody());
		articleForm.setIsPublished(article.isPublished());

		model.addAttribute("articleForm", articleForm);
		return FORM_VIEW;
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/{id}/modify")
	public String articleModify(@Valid ArticleForm articleForm, @PathVariable("id") Integer id, Principal principal) {
		Article article = this.articleService.getArticle(id);

		if (!article.getAuthor().getUsername().equals(principal.getName())) {
			return rq.redirect(String.format("/post/%s", id), UserErrorMessage.USER_NO_MODIFY_PERMISSION);
		}

		this.articleService.modify(article, articleForm.getSubject(), articleForm.getContent(),
			articleForm.getIsPublished());

		return rq.redirect(String.format("/post/%s", id), "게시글 수정 완료!");
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{id}/delete")
	public String answerDelete(Principal principal, @PathVariable("id") Integer id) {
		Article article = this.articleService.getArticle(id);
		if (!article.getAuthor().getUsername().equals(principal.getName())) {
			return rq.redirect(String.format("/post/%s", id), UserErrorMessage.USER_NO_DELETE_PERMISSION);
		}
		this.articleService.delete(article);
		return rq.redirect("/post/list", "게시글이 삭제되었습니다.");
	}
}