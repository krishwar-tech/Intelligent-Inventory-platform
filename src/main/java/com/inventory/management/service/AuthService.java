package com.inventory.management.service;

import com.inventory.management.dto.AuthResponse;
import com.inventory.management.dto.LoginRequest;
import com.inventory.management.dto.RegisterRequest;

public interface AuthService {

	String register(RegisterRequest req);

	AuthResponse login(LoginRequest req);
}