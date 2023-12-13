package com.ll.medium.domain.article.article.entity;

import static lombok.AccessLevel.*;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.ll.medium.domain.member.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PROTECTED)
@Builder
@Getter @Setter
@EntityListeners(AuditingEntityListener.class)
public class Article {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 50)
	private String title;

	@Column(columnDefinition = "TEXT")
	private String body;

	@ManyToOne
	private Member author;

	private boolean isPublished;

	private Long voteCount;

	private LocalDateTime createDate;

	private Long viewCount = 0L;
}
