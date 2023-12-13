package com.ll.medium.domain.article.article.controller;

import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/b")
public class ArticleUserController extends ArticleBaseController {

	public ArticleUserController(ArticleService articleService, MemberService memberService, Rq rq) {
		super(articleService, memberService, rq);
	}

	@GetMapping("/{userid}")
	public String showUserPost(Model model,@PathVariable("userid") String userid,
		@RequestParam(value = "page", required = false, defaultValue = "0") int page) {
		Member member = memberService.getUser(userid);
		return showUserList(model, page, member.getUsername(), member.getUsername(), "b"+userid, true);
	}

	@GetMapping("/{userid}/{id}")
	public String showUserPostDetail(Model model, Principal principal, ArticleForm articleForm,
		@PathVariable("userid") String userid,
		@PathVariable("id") Integer id) {

		Article article = this.articleService.getArticle(id).getData();

		if (!article.getAuthor().getUsername().equals(userid)) {
			return rq.historyBack(UserErrorMessage.NO_ARTICLE);
		}

		if (!articleService.articleIsNotPublished(article, principal)) {
			return rq.historyBack(UserErrorMessage.PRIVATE_ARTICLE);
		}

		model.addAttribute("article", article);
		return "domain/article/article/detail";
	}

	@PostMapping("/{id}/increaseHit")
	public String increaseHit(@PathVariable("id") Integer id, Principal principal) {
		Article article = this.articleService.getArticle(id).getData();
		String path = String.format("/b/%s/%s", article.getAuthor().getUsername(),id);
		return increaseHitAndRedirect(article, path, principal);
	}
}