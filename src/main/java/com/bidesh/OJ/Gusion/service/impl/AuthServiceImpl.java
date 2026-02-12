package com.bidesh.OJ.Gusion.service.impl;

import com.bidesh.OJ.Gusion.dto.auth.AuthResponse;
import com.bidesh.OJ.Gusion.dto.auth.LoginRequest;
import com.bidesh.OJ.Gusion.dto.auth.RegisterRequest;
import com.bidesh.OJ.Gusion.entity.User;
import com.bidesh.OJ.Gusion.repository.UserRepository;
import com.bidesh.OJ.Gusion.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // 1. Create the User
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .role("STUDENT") // 🟢 FIX: Passed as String "STUDENT", not Enum!
                .build();

        userRepository.save(user);

        // 2. Return Flat Response
        return AuthResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())      // 🟢 This will work once you update the DTO below
                .role(user.getRole())
                .token("dummy-jwt-token")
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return AuthResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .token("dummy-jwt-token")
                .build();
    }
}