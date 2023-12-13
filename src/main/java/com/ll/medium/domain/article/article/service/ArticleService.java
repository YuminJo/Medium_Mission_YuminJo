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
import com.ll.medium.global.errors.UserErrorMessage;
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

	private Specification<Article> search(String kw, boolean isPublished) {
		return new Specification<>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Predicate toPredicate(Root<Article> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<Article, Member> u1 = q.join("author", JoinType.LEFT);

				Predicate keywordPredicate = cb.like(u1.get("username"), "%" + kw + "%");

				if (isPublished) {
					Predicate isPublishedPredicate = cb.isTrue(q.get("isPublished"));
					return cb.and(keywordPredicate, isPublishedPredicate);
				} else {
					return keywordPredicate;
				}
			}
		};
	}

	private String limitTitleLength(String title) {
		return title.length() > 50 ? title.substring(0, 47) + "..." : title;
	}

	public RsData<Article> create(String title, String body, boolean isPublished, Member member) {
		title = limitTitleLength(title);

		Article article = Article.builder()
			.title(title)
			.body(body)
			.author(member)
			.isPublished(isPublished)
			.createDate(LocalDateTime.now())
			.build();

		articleRepository.save(article);
		return RsData.of("200", "게시글 작성 완료.");
	}

	public Page<Article> getList(int page, String kw, boolean isPublished) {
		int pageSize = 10;
		Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Order.desc("createDate")));
		Specification<Article> spec = search(kw, isPublished);

		Page<Article> resultPage = articleRepository.findAll(spec, pageable);

		if (page > resultPage.getTotalPages()-1 && resultPage.getTotalPages() != 0) {
			throw new IllegalArgumentException("Requested page number exceeds available pages.");
		}

		return resultPage;
	}


	public List<Article> getRecentArticle() {
		Pageable pageable = PageRequest.of(0, 30, Sort.by(Sort.Order.desc("createDate")));
		List<Article> recentArticles = articleRepository.findAll(pageable).getContent();

		return recentArticles.stream()
			.filter(Article::isPublished)
			.toList();
	}

	public RsData<Article> getArticle(Integer id) {
		Optional<Article> article = articleRepository.findById(id);

		if(article.isEmpty()) {
			throw new IllegalArgumentException(UserErrorMessage.NO_ARTICLE);
		}
		return RsData.of("200", "게시글 조회 완료.", article.get());
	}

	public boolean articleIsNotPublished(Article article, Principal principal) {
		if (!article.isPublished()) {
			Member author = article.getAuthor();
			if(principal == null || !author.getUsername().equals(principal.getName()))
				return false;
		}

		return true;
	}

	public void modify(Article article, String title, String body, Boolean isPublished) {
		title = limitTitleLength(title);

		article.setTitle(title);
		article.setBody(body);
		article.setPublished(isPublished);
		articleRepository.save(article);
	}

	public void delete(Article article) {
		articleRepository.delete(article);
	}

	public void increaseHit(Article article) {
		article.setViewCount(article.getViewCount() + 1L);
		articleRepository.save(article);
	}
}