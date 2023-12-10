package com.ll.medium.domain.member.member.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.medium.domain.member.member.entity.Member;
import com.ll.medium.domain.member.member.repository.MemberRepository;
import com.ll.medium.global.errors.UserErrorMessage;
import com.ll.medium.global.rsData.RsData.RsData;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public RsData<Member> join(String username, String password, String password2) {

		if(List.of("admin", "system").contains(username)) {
			return RsData.of("400-1",("%s"+ UserErrorMessage.SYSTEM_USERNAME).formatted(username));
		}

		if(findByUsername(username).isPresent()) {
			return RsData.of("400-2",UserErrorMessage.USER_ALREADY_REGISTERED);
		}

		if(!password.equals(password2)) {
			return RsData.of("400-3",UserErrorMessage.PASSWORD_MISMATCH);
		}

		Member member = Member.builder()
			.username(username)
			.password(passwordEncoder.encode(password))
			.build();
		memberRepository.save(member);

		return RsData.of("200","회원가입 완료.");
	}

	public Optional<Member> findByUsername(String username) {
		return memberRepository.findByUsername(username);
	}

	public String getUserErrorField(RsData<Member> rsData) {
		String resultField = "";

		switch(rsData.getResultCode()) {
			case "400-1", "400-2" -> resultField = "username";
			case "400-3" -> resultField = "password";
		}
		return resultField;
	}
}
