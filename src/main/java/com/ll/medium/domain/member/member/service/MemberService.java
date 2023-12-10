package com.ll.medium.domain.member.member.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.medium.domain.member.member.entity.Member;
import com.ll.medium.domain.member.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public Member create(String username, String password)
	{
		Member member = new Member(username, password);
		member.setPassword(passwordEncoder.encode(password));
		this.memberRepository.save(member);
		return null;
	}

	public Optional<Member> findByusername(String name) {
		return memberRepository.findByusername(name);
	}
}
