package com.ll.medium.domain.member.member.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ll.medium.domain.member.createform.MemberCreateForm;
import com.ll.medium.domain.member.member.service.MemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
	private final MemberService memberService;
	@PreAuthorize("isAnonymous()")
	@GetMapping("/login")
	public String login() {
		return "member/member/login_form";
	}

	@PreAuthorize("isAnonymous()")
	@GetMapping("/join")
	public String signup(MemberCreateForm memberCreateForm) {
		return "member/member/join_form";
	}

	@PreAuthorize("isAnonymous()")
	@PostMapping("/join")
	public String signup(Model model,@Valid MemberCreateForm memberCreateForm, BindingResult bindingResult) {
		model.addAttribute("memberCreateForm", memberCreateForm);

		if(bindingResult.hasErrors()) {
			return "member/member/join_form";
		}

		if (!memberCreateForm.getPassword1().equals(memberCreateForm.getPassword2())) {
			bindingResult.rejectValue("password2", "passwordInCorrect", "패스워드가 일치하지 않습니다.");
			return "member/member/join_form";
		}

		try {
			memberService.create(memberCreateForm.getUsername(), memberCreateForm.getPassword1());
		} catch (DataIntegrityViolationException e) {
			handleSignupError(model, "이미 등록된 사용자입니다.");
			return "member/member/join_form";
		} catch (Exception e) {
			handleSignupError(model, e.getMessage());
			return "member/member/join_form";
		}

		return "redirect:/";
	}

	private void handleSignupError(Model model, String errorMessage) {
		model.addAttribute("signupFailed", errorMessage);
	}
}
