package com.ll.medium.domain.article.article.service;

import org.springframework.stereotype.Service;

import com.ll.medium.domain.article.article.entity.Article;
import com.ll.medium.domain.article.article.repository.ArticleRepository;
import com.ll.medium.domain.member.member.entity.Member;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArticleService {
	private final ArticleRepository articleRepository;

	public void create(String title, String body, Member member) {
		Article article = new Article();
		article.setTitle(title);
		article.setBody(body);
		article.setAuthor(member);
		this.articleRepository.save(article);
	}
}
