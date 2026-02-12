package com.bidesh.OJ.Gusion.service;

import com.bidesh.OJ.Gusion.dto.auth.AuthResponse;
import com.bidesh.OJ.Gusion.dto.auth.LoginRequest;
import com.bidesh.OJ.Gusion.dto.auth.RegisterRequest;

/**
 * Mock auth service for Signup/Login.
 * In production, integrate with JWT and password hashing.
 */
public interface AuthService {


    AuthResponse register(RegisterRequest request); // Changed from signup
    AuthResponse login(LoginRequest request);
}
