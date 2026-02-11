package com.bidesh.OJ.Gusion.service;

import com.bidesh.OJ.Gusion.dto.auth.AuthResponse;
import com.bidesh.OJ.Gusion.dto.auth.LoginRequest;
import com.bidesh.OJ.Gusion.dto.auth.SignupRequest;

/**
 * Mock auth service for Signup/Login.
 * In production, integrate with JWT and password hashing.
 */
public interface AuthService {

    AuthResponse signup(SignupRequest request);

    AuthResponse login(LoginRequest request);
}
