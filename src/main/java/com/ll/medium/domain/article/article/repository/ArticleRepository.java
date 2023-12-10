package com.ll.medium.domain.article.article.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ll.medium.domain.article.article.entity.Article;

public interface ArticleRepository extends JpaRepository<Article, Integer> {
}
