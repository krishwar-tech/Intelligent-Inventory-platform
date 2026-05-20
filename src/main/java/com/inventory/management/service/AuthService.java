package com.inventory.management.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.inventory.management.exception.*;
import com.inventory.management.config.JwtUtil;

import com.inventory.management.dto.AuthResponse;
import com.inventory.management.dto.LoginRequest;
import com.inventory.management.dto.RegisterRequest;

import com.inventory.management.entity.Tenant;
import com.inventory.management.entity.User;

import com.inventory.management.repository.TenantRepository;
import com.inventory.management.repository.UserRepository;

@Service
public class AuthService {

	private final UserRepository userRepo;

	private final TenantRepository tenantRepo;

	private final PasswordEncoder passwordEncoder;

	private final JwtUtil jwtUtil;

	public AuthService(UserRepository userRepo, TenantRepository tenantRepo, PasswordEncoder passwordEncoder,
			JwtUtil jwtUtil) {

		this.userRepo = userRepo;

		this.tenantRepo = tenantRepo;

		this.passwordEncoder = passwordEncoder;

		this.jwtUtil = jwtUtil;
	}

	// REGISTER

	public String register(RegisterRequest req) {

		if (userRepo.findByUsername(req.getUsername()).isPresent()) {

			throw new DuplicateResourceException("Username already exists");
		}

		Tenant tenant = new Tenant();

		tenant.setCompanyName(req.getCompanyName());

		tenant.setEmail(req.getEmail());

		tenant.setPhone(req.getPhone());

		tenant.setStatus("ACTIVE");

		tenant = tenantRepo.save(tenant);

		User user = new User();

		user.setUsername(req.getUsername());

		user.setPassword(passwordEncoder.encode(req.getPassword()));

		user.setRole("ADMIN");

		user.setStatus("ACTIVE");

		user.setTenant(tenant);

		userRepo.save(user);

		return "Registration successful";
	}

	// LOGIN

	public AuthResponse login(LoginRequest req) {

		User user = userRepo.findByUsername(req.getUsername())

				.orElseThrow(() -> new UserNotFoundException("Invalid username"));

		boolean matches = passwordEncoder.matches(req.getPassword(), user.getPassword());

		if (!matches) {

			throw new InvalidPasswordException("Invalid password");
		}

		String token = jwtUtil.generateToken(

				user.getId(),

				user.getTenant().getId(),

				user.getUsername());

		return new AuthResponse(

				token,

				user.getUsername(),

				user.getTenant().getId());
	}
}