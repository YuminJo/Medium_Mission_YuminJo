package com.ll.medium.domain.article.article.service;

import java.time.LocalDateTime;
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
import com.ll.medium.global.rsData.RsData.RsData;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArticleService {
	private final ArticleRepository articleRepository;

	public RsData<Article> create(String title, String body, boolean isPublished, Member member) {

		Article article = Article.builder()
			.title(title)
			.body(body)
			.author(member)
			.isPublished(isPublished)
			.createDate(LocalDateTime.now())
			.build();
		this.articleRepository.save(article);

		return RsData.of("200", "게시글 작성 완료.");
	}

	public Page<Article> getList(int page) {
		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("createDate"));
		Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
		return this.articleRepository.findAll(pageable);
	}

	public List<Article> getRecentArticle() {
		Pageable pageable = PageRequest.of(0, 30, Sort.by(Sort.Order.desc("createDate")));
		Page<Article> recentArticlesPage = this.articleRepository.findAll(pageable);
		return recentArticlesPage.getContent();
	}
}