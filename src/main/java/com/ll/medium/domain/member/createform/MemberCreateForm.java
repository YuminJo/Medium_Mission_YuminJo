package com.ll.medium.domain.member.createform;

import com.ll.medium.global.errors.UserErrorMessage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MemberCreateForm {
	@Size(min = 3, max = 25)
	@NotBlank(message = UserErrorMessage.NEED_USER_ID)
	private String username;

	@NotBlank(message = UserErrorMessage.NEED_PASSWORD)
	String password1;

	@NotBlank(message = UserErrorMessage.NEED_PASSWORD2)
	String password2;
}
