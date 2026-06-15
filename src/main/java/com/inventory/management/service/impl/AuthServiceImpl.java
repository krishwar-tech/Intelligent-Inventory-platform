package com.inventory.management.service.impl;

import com.inventory.management.config.JwtUtil;
import com.inventory.management.dto.AuthResponse;
import com.inventory.management.dto.LoginRequest;
import com.inventory.management.dto.RegisterRequest;
import com.inventory.management.entity.Tenant;
import com.inventory.management.entity.User;
import com.inventory.management.exception.DuplicateResourceException;
import com.inventory.management.exception.InvalidPasswordException;
import com.inventory.management.exception.UserNotFoundException;
import com.inventory.management.repository.TenantRepository;
import com.inventory.management.repository.UserRepository;
import com.inventory.management.service.AuthService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepo;

    private final TenantRepository tenantRepo;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    public AuthServiceImpl(
            UserRepository userRepo,
            TenantRepository tenantRepo,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil) {

        this.userRepo = userRepo;

        this.tenantRepo = tenantRepo;

        this.passwordEncoder = passwordEncoder;

        this.jwtUtil = jwtUtil;
    }

    // REGISTER

    @Override
    public String register(RegisterRequest req) {

        if (userRepo.findByUsername(req.getUsername()).isPresent()) {

            throw new DuplicateResourceException(
                    "Username already exists");
        }

        Tenant tenant = new Tenant();

        tenant.setCompanyName(
                req.getCompanyName());

        tenant.setEmail(
                req.getEmail());

        tenant.setPhone(
                req.getPhone());

        tenant.setStatus(
                "ACTIVE");

        tenant =
                tenantRepo.save(tenant);

        User user = new User();

        user.setUsername(
                req.getUsername());

        user.setPassword(
                passwordEncoder.encode(
                        req.getPassword()));

        user.setRole(
                "ADMIN");

        user.setStatus(
                "ACTIVE");

        user.setTenant(tenant);

        userRepo.save(user);

        return "Registration successful";
    }

    // LOGIN

    @Override
    public AuthResponse login(LoginRequest req) {

        System.out.println("========== LOGIN DEBUG ==========");

        System.out.println("INPUT USERNAME: " + req.getUsername());

        System.out.println("INPUT PASSWORD: " + req.getPassword());

        User user =
                userRepo.findByUsername(
                                req.getUsername())
                        .orElseThrow(() -> {

                            System.out.println("USER NOT FOUND");

                            return new UserNotFoundException(
                                    "Invalid username");
                        });

        System.out.println("DB USER FOUND: " + user.getUsername());

        System.out.println("DB HASH PASSWORD: " + user.getPassword());

        boolean matches =
                passwordEncoder.matches(
                        req.getPassword(),
                        user.getPassword());

        System.out.println("PASSWORD MATCH RESULT: " + matches);

        if (!matches) {

            throw new InvalidPasswordException(
                    "Invalid password");
        }

        String token =
                jwtUtil.generateToken(

                        user.getId(),

                        user.getTenant().getId(),

                        user.getUsername());

        return new AuthResponse(

                token,

                user.getUsername(),

                user.getTenant().getId());
    }
}