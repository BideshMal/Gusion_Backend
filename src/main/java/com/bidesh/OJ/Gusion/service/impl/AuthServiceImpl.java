package com.bidesh.OJ.Gusion.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.bidesh.OJ.Gusion.dto.auth.AuthResponse;
import com.bidesh.OJ.Gusion.dto.auth.LoginRequest;
import com.bidesh.OJ.Gusion.dto.auth.SignupRequest;
import com.bidesh.OJ.Gusion.entity.User;
import com.bidesh.OJ.Gusion.entity.UserRole;
import com.bidesh.OJ.Gusion.repository.UserRepository;
import com.bidesh.OJ.Gusion.service.AuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    @Override
    public AuthResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + request.getEmail());
        }
        UserRole role = "ADMIN".equalsIgnoreCase(request.getRole()) ? UserRole.ADMIN : UserRole.STUDENT;
        User user = User.builder()
                .email(request.getEmail())
                .role(role)
                .build();
        user = userRepository.save(user);

        return AuthResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .token("mock-jwt-" + UUID.randomUUID())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        // Mock: no password validation
        return AuthResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .token("mock-jwt-" + UUID.randomUUID())
                .build();
    }
}
