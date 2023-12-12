package com.ll.medium.domain.article.article.service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArticleService {
	private final ArticleRepository articleRepository;

	private Specification<Article> search(String kw) {
		return new Specification<>() {
			@Override
			public Predicate toPredicate(Root<Article> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
				query.distinct(true);
				Join<Article, Member> u1 = q.join("author", JoinType.LEFT);
				return cb.or(cb.like(u1.get("username"), "%" + kw + "%"));
			}
		};
	}

	public RsData<Article> create(String title, String body, boolean isPublished, Member member) {
		if(title.length() > 50) {
			title = title.substring(0, 47) + "...";
		}

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

	public Page<Article> getList(int page, String kw) {
		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("createDate"));
		Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
		Specification<Article> spec = search(kw);
		return this.articleRepository.findAll(spec, pageable);
	}

	public List<Article> getRecentArticle() {
		Pageable pageable = PageRequest.of(0, 30, Sort.by(Sort.Order.desc("createDate")));
		Page<Article> recentArticlesPage = this.articleRepository.findAll(pageable);

		return recentArticlesPage.getContent()
			.stream()
			.filter(Article::isPublished)
			.toList();
	}

	public Article getArticle(Integer id) {
		Optional<Article> article = this.articleRepository.findById(id);

		if (article.isPresent()) {
			return article.get();
		} else {
			throw new RuntimeException("게시글을 찾을 수 없습니다.");
		}
	}

	public boolean articleIsNotPublished(Article article, Principal principal) {
		if (!article.isPublished()) {
			Member member = article.getAuthor();
			if (principal == null || !member.getUsername().equals(principal.getName())) {
				return false;
			}
		}

		return true;
	}
}