package com.ll.medium.domain.member.member.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ll.medium.domain.member.form.MemberCreateForm;
import com.ll.medium.domain.member.member.entity.Member;
import com.ll.medium.domain.member.member.service.MemberService;
import com.ll.medium.global.rq.Rq;
import com.ll.medium.global.rsData.RsData.RsData;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
	private final MemberService memberService;
	private final Rq rq;

	@PreAuthorize("isAnonymous()")
	@GetMapping("/login")
	public String login() {
		return "domain/member/member/login_form";
	}

	@PreAuthorize("isAnonymous()")
	@GetMapping("/join")
	public String signup(MemberCreateForm memberCreateForm) {
		return "domain/member/member/join_form";
	}

	@PreAuthorize("isAnonymous()")
	@PostMapping("/join")
	public String signup(Model model, @Valid MemberCreateForm memberCreateForm, BindingResult bindingResult) {
		model.addAttribute("memberCreateForm", memberCreateForm);

		if(bindingResult.hasErrors()) {
			return "domain/member/member/join_form";
		}

		RsData<Member> joinRs = memberService.join(memberCreateForm.getUsername(), memberCreateForm.getPassword1(),
			memberCreateForm.getPassword2());

		if (joinRs.isFail()) {
			bindingResult.rejectValue(memberService.getUserErrorField(joinRs), joinRs.getResultCode(), joinRs.getMsg());
			return "domain/member/member/join_form";
		}
		return rq.redirectOrBack(joinRs, "/");
	}
}