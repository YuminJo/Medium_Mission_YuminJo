package com.ll.medium.domain.article.article.controller;

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
import com.ll.medium.domain.member.member.entity.Member;
import com.ll.medium.domain.member.member.service.MemberService;
import com.ll.medium.global.rq.Rq;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/b")
@RequiredArgsConstructor
public class ArticleUserController {
	private final ArticleService articleService;
	private final MemberService memberService;
	private final Rq rq;

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{userid}")
	public String showUserPost(Model model,@PathVariable("userid") String userid,
		@RequestParam(value = "page", required = false, defaultValue = "0") int page) {

		Member member = this.memberService.getUser(userid);

		if(member == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다.");
		}
		Page<Article> paging = this.articleService.getList(page, member.getUsername(), false);
		model.addAttribute("paging", paging);
		model.addAttribute("myList", true);
		return "domain/article/article/form";
	}
}
