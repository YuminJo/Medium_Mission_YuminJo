package com.ll.medium.domain.home.home.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.ll.medium.domain.article.article.service.ArticleService;
import com.ll.medium.domain.member.member.entity.Member;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class HomeController {
	private ArticleService articleService;
	@DisplayName("테스트 테이터 생성")
	@Test
	void CreateSampleData() {
		Member member = new Member("sampleuser","1234");

		for (int i = 1; i <= 300; i++) {
			String subject = String.format("이것은 제목인데 제목이고 제목입니다.:[%03d]", i);
			String content = "내용무";
			this.articleService.create(subject, content, member);
		}
	}
}
