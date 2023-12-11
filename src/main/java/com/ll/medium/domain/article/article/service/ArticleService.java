package com.ll.medium.domain.article.article.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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

	public Page<Article> getList(int page) {
		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("createDate"));
		Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
		return this.articleRepository.findAll(pageable);
	}
}