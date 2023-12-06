package com.ll.medium.domain.member.member.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ll.medium.domain.member.createform.MemberCreateForm;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
	@PreAuthorize("isAnonymous()")
	@GetMapping("/join")
	public String signup(MemberCreateForm memberCreateForm) {
		return "domain/member/member/join_form";
	}

	@PreAuthorize("isAnonymous()")
	@PostMapping("/join")
	public String signup(Model model,@Valid MemberCreateForm memberCreateForm, BindingResult bindingResult) {
		if(bindingResult.hasErrors()) {
			return "domain/member/member/join_form";
		}

		if(!memberCreateForm.getPassword1().equals(memberCreateForm.getPassword2())) {
			bindingResult.rejectValue("password2", "passwordInCorrect",
				"패스워드가 일치하지 않습니다.");
			return "domain/member/member/join_form";
		}

		model.addAttribute("memberCreateForm", memberCreateForm);

		return "redirect:/";
	}
}
