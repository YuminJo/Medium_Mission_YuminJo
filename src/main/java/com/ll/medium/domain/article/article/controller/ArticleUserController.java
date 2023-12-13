package com.ll.medium.domain.article.article.controller;

import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/b")
@RequiredArgsConstructor
public class ArticleUserController {
	private final ArticleService articleService;
	private final MemberService memberService;
	private final Rq rq;

	@GetMapping("/{userid}")
	public String showUserPost(Model model,@PathVariable("userid") String userid,
		@RequestParam(value = "page", required = false, defaultValue = "0") int page) {

		Member member = this.memberService.getUser(userid);

		Page<Article> paging = articleService.getList(page, member.getUsername(), true);
		model.addAttribute("paging", paging);
		model.addAttribute("listusername", member.getUsername());
		model.addAttribute("customPath","/b/"+userid);
		return "domain/article/article/list";
	}

	@GetMapping("/{userid}/{id}")
	public String showUserPostDetail(Model model, Principal principal, ArticleForm articleForm,
		@PathVariable("userid") String userid,
		@PathVariable("id") Integer id) {

		Article article = this.articleService.getArticle(id);

		if (article == null || !article.getAuthor().getUsername().equals(userid)) {
			return rq.historyBack(UserErrorMessage.NO_ARTICLE);
		}

		if (!articleService.articleIsNotPublished(article, principal)) {
			return rq.historyBack(UserErrorMessage.PRIVATE_ARTICLE);
		}

		model.addAttribute("article", article);
		return "domain/article/article/detail";
	}
}