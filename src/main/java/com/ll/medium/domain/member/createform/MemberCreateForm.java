package com.ll.medium.domain.member.createform;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MemberCreateForm {
	@Size(min = 3, max = 25)
	@NotEmpty(message = "사용자ID는 필수항목입니다.")
	private String username;

	@NotEmpty(message = "비밀번호는 필수항목입니다.")
	String password1;

	@NotEmpty(message = "비밀번호 확인은 필수항목입니다.")
	String password2;
}
