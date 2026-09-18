package com.berkaykomur.backend.controller;

import com.berkaykomur.backend.dto.AuthResponse;
import com.berkaykomur.backend.dto.LoginRequest;
import com.berkaykomur.backend.dto.RefreshTokenRequest;
import com.berkaykomur.backend.dto.RegisterRequest;
import com.berkaykomur.backend.service.AuthenticationService;
import com.berkaykomur.backend.service.impl.AuthenticationServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authenticationService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authenticationService.login(request);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        AuthResponse response = authenticationService.refreshAccessToken(request);
        return ResponseEntity.ok(response);
    }
}