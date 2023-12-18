package com.ll.medium.domain.home.home.controller;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.ll.medium.domain.article.article.entity.Article;
import com.ll.medium.domain.article.article.repository.ArticleRepository;
import com.ll.medium.domain.article.article.service.ArticleService;
import com.ll.medium.domain.member.member.entity.Member;
import com.ll.medium.domain.member.member.repository.MemberRepository;

@SpringBootTest
class HomeControllerTest{
	@Autowired
	private ArticleService articleService;

	@Autowired
	private ArticleRepository articleRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Test
	@DisplayName("테스트 테이터 생성")
	void CreateSampleData() {
		Member member = new Member("sampleuser","1234");
		this.memberRepository.save(member);

		for (int i = 1; i <= 300; i++) {
			String subject = String.format("이것은 제목인데 제목이고 제목입니다.:[%03d]", i);
			String content = "내용무";
			Article article = Article.builder()
				.title(subject)
				.body(content)
				.author(member)
				.isPublished(true)
				.createDate(LocalDateTime.now())
				.build();
			this.articleRepository.save(article);
		}
	}
}
