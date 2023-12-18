package com.ll.medium.domain.article.article.controller;

import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.ll.medium.domain.article.article.entity.Article;
import com.ll.medium.domain.article.article.service.ArticleService;
import com.ll.medium.domain.member.member.service.MemberService;
import com.ll.medium.global.errors.UserErrorMessage;
import com.ll.medium.global.rq.Rq;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public abstract class ArticleBaseController {
	protected final ArticleService articleService;
	protected final MemberService memberService;
	protected final Rq rq;

	protected String increaseHitAndRedirect(Article article, String redirectPath, Principal principal) {
		if (!articleService.articleIsNotPublished(article, principal)) {
			return rq.historyBack(UserErrorMessage.PRIVATE_ARTICLE);
		}

		this.articleService.increaseHit(article);
		return "redirect:" + redirectPath;
	}

	protected String showUserList(Model model, int page, String username, String kw, String customPath,boolean showOnlyPublished) {
		Page<Article> paging = articleService.getList(page, kw, showOnlyPublished);
		model.addAttribute("paging", paging);
		model.addAttribute("kw", kw);
		model.addAttribute("listusername", username);
		model.addAttribute("customPath", customPath);
		return "domain/article/article/list";
	}

	protected String showUserPostDetail(Model model, int id, String username, Principal principal) {
		Article article = articleService.getArticle(id).getData();

		if (username != null && !article.getAuthor().getUsername().equals(username)) {
			return rq.historyBack(UserErrorMessage.NO_ARTICLE);
		}

		if (!articleService.articleIsNotPublished(article, principal)) {
			return rq.historyBack(UserErrorMessage.PRIVATE_ARTICLE);
		}

		model.addAttribute("article", article);
		return "domain/article/article/detail";
	}
}
