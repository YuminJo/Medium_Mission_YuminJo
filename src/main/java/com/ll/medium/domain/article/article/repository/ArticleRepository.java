package com.ll.medium.domain.article.article.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ll.medium.domain.article.article.entity.Article;

public interface ArticleRepository extends JpaRepository<Article, Integer> {
	Page<Article> findAll(Specification<Article> spec, Pageable pageable);
}