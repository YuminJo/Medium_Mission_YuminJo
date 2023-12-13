package com.ll.medium.domain.article.article.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;

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

	protected String increaseHitAndRedirect(Integer articleId, String redirectPath, Principal principal) {
		Article article = this.articleService.getArticle(articleId).getData();

		if (!articleService.articleIsNotPublished(article, principal)) {
			return rq.historyBack(UserErrorMessage.PRIVATE_ARTICLE);
		}

		this.articleService.increaseHit(article);
		return "redirect:" + redirectPath;
	}
}
