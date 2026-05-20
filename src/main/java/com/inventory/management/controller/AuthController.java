package com.inventory.management.controller;

import org.springframework.web.bind.annotation.*;

import com.inventory.management.dto.AuthResponse;
import com.inventory.management.dto.LoginRequest;
import com.inventory.management.dto.RegisterRequest;

import com.inventory.management.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

	private final AuthService service;

	public AuthController(AuthService service) {

		this.service = service;
	}

	@PostMapping("/register")
	public String register(@RequestBody RegisterRequest req) {

		return service.register(req);
	}

	@PostMapping("/login")
	public AuthResponse login(@RequestBody LoginRequest req) {

		return service.login(req);
	}
}