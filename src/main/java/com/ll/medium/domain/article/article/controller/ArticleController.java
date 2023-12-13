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
import com.ll.medium.global.errors.UserErrorMessage;
import com.ll.medium.global.rq.Rq;
import com.ll.medium.global.rsData.RsData.RsData;

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
	private static final String CUSTOM_PATH_ALL_POSTS = "/post/list";
	private static final String CUSTOM_PATH_MY_POSTS = "/post/myList";

	private Page<Article> getArticleList(Model model, int page, String kw, boolean isAllPosts) {
		Page<Article> paging = articleService.getList(page, kw, isAllPosts);
		model.addAttribute("paging", paging);
		model.addAttribute("kw", kw);
		return paging;
	}

	@GetMapping("/list")
	public String showAllPost(Model model,
		@RequestParam(value = "page", required = false, defaultValue = "0") int page,
		@RequestParam(value = "kw", required = false, defaultValue = "") String kw) {
		getArticleList(model, page, kw, true);
		model.addAttribute("customPath", CUSTOM_PATH_ALL_POSTS);
		return ARTICLE_LIST_VIEW;
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/myList")
	public String showMyPost(Model model,
		@RequestParam(value = "page", required = false, defaultValue = "0") int page,
		@RequestParam(value = "kw", required = false, defaultValue = "") String kw,
		Principal principal) {
		Member member = memberService.getUser(principal.getName());
		getArticleList(model, page, member.getUsername(), false);
		model.addAttribute("listusername", member.getUsername());
		model.addAttribute("customPath", CUSTOM_PATH_MY_POSTS);
		return ARTICLE_LIST_VIEW;
	}

	@GetMapping("/{id}")
	public String detail(Model model, @PathVariable("id") Integer id, ArticleForm articleForm, Principal principal) {
		Article article = articleService.getArticle(id).getData();

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

		Member member = memberService.getUser(principal.getName());
		articleService.create(articleForm.getSubject(), articleForm.getContent(), articleForm.getIsPublished(), member);
		return REDIRECT_POST_LIST;
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{id}/modify")
	public String articleModify(Model model, ArticleForm articleForm, @PathVariable("id") Integer id, Principal principal) {
		Article article = this.articleService.getArticle(id).getData();

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
		Article article = this.articleService.getArticle(id).getData();

		if (!article.getAuthor().getUsername().equals(principal.getName())) {
			return rq.redirect(String.format("/post/%s", id), UserErrorMessage.USER_NO_MODIFY_PERMISSION);
		}

		articleService.modify(article, articleForm.getSubject(), articleForm.getContent(), articleForm.getIsPublished());
		return rq.redirect(String.format("/post/%s", id), "게시글 수정 완료!");
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{id}/delete")
	public String answerDelete(Principal principal, @PathVariable("id") Integer id) {
		Article article = this.articleService.getArticle(id).getData();
		if (!article.getAuthor().getUsername().equals(principal.getName())) {
			return rq.redirect(String.format("/post/%s", id), UserErrorMessage.USER_NO_DELETE_PERMISSION);
		}
		this.articleService.delete(article);
		return rq.redirect("/post/list", "게시글이 삭제되었습니다.");
	}

	@PostMapping("/{id}/increaseHit")
	public String increaseHit(@PathVariable("id") Integer id, Principal principal) {
		Article article = this.articleService.getArticle(id).getData();

		if (!articleService.articleIsNotPublished(article, principal)) {
			return rq.historyBack(UserErrorMessage.PRIVATE_ARTICLE);
		}

		this.articleService.increaseHit(article);
		return String.format("redirect:/post/%s", id);
	}
}