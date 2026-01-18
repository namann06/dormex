package com.dormex.controller;

import com.dormex.dto.ApiResponse;
import com.dormex.dto.auth.AuthResponse;
import com.dormex.dto.auth.LoginRequest;
import com.dormex.dto.auth.RefreshTokenRequest;
import com.dormex.dto.auth.RegisterRequest;
import com.dormex.entity.enums.Role;
import com.dormex.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Auth endpoints for login, register, and token management")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Login with email and password")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Register a new student (Admin only)")
    public ResponseEntity<ApiResponse<AuthResponse>> registerStudent(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request, Role.STUDENT);
        return ResponseEntity.ok(ApiResponse.success("Student registered successfully", response));
    }

    @PostMapping("/register/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Register a new admin (Admin only)")
    public ResponseEntity<ApiResponse<AuthResponse>> registerAdmin(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request, Role.ADMIN);
        return ResponseEntity.ok(ApiResponse.success("Admin registered successfully", response));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token using refresh token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed", response));
    }

    @GetMapping("/oauth2/google")
    @Operation(summary = "Initiate Google OAuth2 login", description = "Redirects to Google login page")
    public void googleLogin() {
        // Spring Security handles redirect to /oauth2/authorization/google
    }
}
