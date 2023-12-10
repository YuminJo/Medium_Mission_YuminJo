package com.ll.medium.domain.article.article.entity;

import java.time.LocalDateTime;

import com.ll.medium.domain.member.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Article {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 50)
	private String title;

	@Column(columnDefinition = "TEXT")
	private String body;

	@ManyToOne
	private Member author;

	private Long voteCount;

	private LocalDateTime createDate;
	private LocalDateTime modifyDate;
}
