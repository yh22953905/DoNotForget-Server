package com.hungrybrothers.alarmforsubscription.security;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import lombok.SneakyThrows;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
	private final JwtTokenProvider jwtTokenProvider;

	public JwtAuthorizationFilter(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
		super(authenticationManager);
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@SneakyThrows
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
		String jwtToken = jwtTokenProvider.resolveToken(request);
		
		if (jwtTokenProvider.validateToken(jwtToken)) {
			Authentication authentication = jwtTokenProvider.getAuthentication(jwtToken);

			SecurityContextHolder.getContext().setAuthentication(authentication);
		}

		chain.doFilter(request, response);
	}
}
