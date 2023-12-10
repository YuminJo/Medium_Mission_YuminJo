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
import com.ll.medium.global.errors.UserErrorMessage;
import com.ll.medium.global.rq.Rq;

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
	public String signup(Model model,@Valid MemberCreateForm memberCreateForm, BindingResult bindingResult) {
		model.addAttribute("memberCreateForm", memberCreateForm);

		if(bindingResult.hasErrors()) {
			return rq.returnToJoinForm();
		}

		if (!memberCreateForm.getPassword1().equals(memberCreateForm.getPassword2())) {
			bindingResult.rejectValue("password2", UserErrorMessage.PASSWORD_INCORRECT, UserErrorMessage.PASSWORD_MISMATCH);
			return rq.returnToJoinForm();
		}

		if(memberService.findByusername(memberCreateForm.getUsername()).isPresent()) {
			bindingResult.rejectValue("username", UserErrorMessage.ALREADY_REGISTERED_USERS, UserErrorMessage.USER_ALREADY_REGISTERED);
			return rq.returnToJoinForm();
		}

		try {
			memberService.create(memberCreateForm.getUsername(), memberCreateForm.getPassword1());
		} catch (Exception e) {
			handleSignupError(model, e.getMessage());
			return rq.returnToJoinForm();
		}

		return "redirect:/";
	}

	private void handleSignupError(Model model, String errorMessage) {
		model.addAttribute("signupFailed", errorMessage);
	}
}
