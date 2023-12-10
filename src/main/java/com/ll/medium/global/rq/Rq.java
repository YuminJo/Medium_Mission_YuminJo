package com.ll.medium.global.rq;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequestScope
@RequiredArgsConstructor
public class Rq {
	private final HttpServletRequest request;
	private final HttpServletResponse response;

	public String redirect(String url, String msg)
	{
		msg = URLEncoder.encode(msg, StandardCharsets.UTF_8);

		StringBuilder sb = new StringBuilder();

		sb.append("redirect:");
		sb.append(url);

		if (msg != null) {
			sb.append("?msg=");
			sb.append(msg);
		}

		return sb.toString();
	}

	public String returnToJoinForm() {
		return "domain/member/member/join_form";
	}
}
