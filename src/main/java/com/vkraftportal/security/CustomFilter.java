package com.vkraftportal.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class CustomFilter extends UsernamePasswordAuthenticationFilter {

	@Override
	protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
		if (response.getStatus() == HttpServletResponse.SC_NOT_FOUND) {
			handle404Error(response);
			return false;
		}
		return super.requiresAuthentication(request, response);
	}

	private void handle404Error(HttpServletResponse response) {
		try {
			response.setContentType("application/json");
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().write("{\"error\": \"Resource not found\"}");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
