package com.ll.medium.domain.article.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleForm {
	@NotBlank(message = "제목은 필수항목입니다.")
	@Size(max = 200)
	private String subject;

	private String content;

	@NotNull(message = "isPublished는 null일 수 없습니다.")
	private Boolean isPublished;
}