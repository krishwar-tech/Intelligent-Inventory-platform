package com.inventory.management.config;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.context.SecurityContextHolder;

@Component
public class JwtFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;

	public JwtFilter(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String authHeader = request.getHeader("Authorization");

		if (authHeader != null && authHeader.startsWith("Bearer ")) {

			String token = authHeader.substring(7);

			try {

				Claims claims = jwtUtil.extractAllClaims(token);

				Integer tenantIdInt = claims.get("tenantId", Integer.class);

				Long tenantId = tenantIdInt.longValue();

				String username = claims.getSubject();

				TenantContext.setTenantId(tenantId);

				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username,
						null, new ArrayList<>());

				SecurityContextHolder.getContext().setAuthentication(authentication);

			} catch (Exception e) {

				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

				response.getWriter().write("Invalid JWT Token");

				return;
			}
		}

		filterChain.doFilter(request, response);

		TenantContext.clear();
	}
}