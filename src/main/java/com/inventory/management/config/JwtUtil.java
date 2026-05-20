package com.inventory.management.config;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	private static final String SECRET = "stockflowsecurejwtsecretstockflowsecurejwtsecret";

	private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

	public String generateToken(Long userId, Long tenantId, String username) {

		return Jwts.builder()

				.setSubject(username)

				.claim("userId", userId)

				.claim("tenantId", tenantId)

				.setIssuedAt(new Date())

				.setExpiration(new Date(System.currentTimeMillis() + 86400000))

				.signWith(key, SignatureAlgorithm.HS256)

				.compact();
	}

	public Claims extractClaims(String token) {

		return Jwts.parserBuilder()

				.setSigningKey(key)

				.build()

				.parseClaimsJws(token)

				.getBody();
	}

	public Claims extractAllClaims(String token) {

		return Jwts.parserBuilder()

				.setSigningKey(key)

				.build()

				.parseClaimsJws(token)

				.getBody();
	}

	public String extractUsername(String token) {

		return extractClaims(token).getSubject();
	}

	public Long extractTenantId(String token) {

		return extractClaims(token).get("tenantId", Long.class);
	}

	public boolean isValid(String token) {

		try {

			extractClaims(token);

			return true;

		} catch (Exception e) {

			return false;
		}
	}
}