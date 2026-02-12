package com.bidesh.OJ.Gusion.controller;

import com.bidesh.OJ.Gusion.dto.auth.AuthResponse;
import com.bidesh.OJ.Gusion.dto.auth.LoginRequest;
import com.bidesh.OJ.Gusion.dto.auth.RegisterRequest; // 🟢 Using RegisterRequest
import com.bidesh.OJ.Gusion.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 🟢 Changed to /register to match Frontend
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}