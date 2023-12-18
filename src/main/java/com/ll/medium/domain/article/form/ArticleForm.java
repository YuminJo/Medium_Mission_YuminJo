package com.ll.medium.domain.article.form;

import com.ll.medium.global.errors.UserErrorMessage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleForm {
	@NotBlank(message = UserErrorMessage.NEED_SUBJECT)
	private String subject;

	private String content;

	@NotNull()
	private Boolean isPublished;
}