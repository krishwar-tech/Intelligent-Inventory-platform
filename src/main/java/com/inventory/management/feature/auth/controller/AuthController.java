package com.inventory.management.feature.auth.controller;
import java.time.LocalDateTime;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.management.common.dto.ApiResponse;
import com.inventory.management.feature.auth.dto.AuthResponse;
import com.inventory.management.feature.auth.dto.LoginRequest;
import com.inventory.management.feature.auth.dto.RegisterRequest;
import com.inventory.management.feature.auth.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService service;

	public AuthController(AuthService service) {
		this.service = service;
	}

	@PostMapping("/register")
	public ResponseEntity<ApiResponse<String>> register(
			@Valid @RequestBody RegisterRequest req) {

		String result = service.register(req);

		ApiResponse<String> response =
				new ApiResponse<>(
						true,
						"User registered successfully",
						HttpStatus.CREATED.value(),
						result,
						LocalDateTime.now());

		return new ResponseEntity<>(
				response,
				HttpStatus.CREATED);
	}

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<AuthResponse>> login(
			@Valid @RequestBody LoginRequest req) {

		AuthResponse auth = service.login(req);

		ApiResponse<AuthResponse> response =
				new ApiResponse<>(
						true,
						"Login successful",
						HttpStatus.OK.value(),
						auth,
						LocalDateTime.now());

		return ResponseEntity.ok(response);
	}
}