package com.inventory.management.feature.auth.service;


import com.inventory.management.feature.auth.dto.AuthResponse;
import com.inventory.management.feature.auth.dto.LoginRequest;
import com.inventory.management.feature.auth.dto.RegisterRequest;

public interface AuthService {

	String register(RegisterRequest req);

	AuthResponse login(LoginRequest req);
}