package com.aireadiness.auth.controller;

import com.aireadiness.auth.dto.AuthResponse;
import com.aireadiness.auth.dto.LoginRequest;
import com.aireadiness.auth.dto.RegisterRequest;
import com.aireadiness.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller for authentication operations
 * Handles user registration, login, token refresh, and user info
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "API for user authentication and registration")
public class AuthController {
    
    private final AuthService authService;
    
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    /**
     * Register a new user
     * POST /api/v1/auth/register
     */
    @PostMapping("/register")
    @Operation(summary = "Register new user", 
               description = "Creates a new user account and returns authentication tokens")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Authenticate user login
     * POST /api/v1/auth/login
     */
    @PostMapping("/login")
    @Operation(summary = "User login", 
               description = "Authenticates user credentials and returns access tokens")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    /**
     * Refresh access token
     * POST /api/v1/auth/refresh
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", 
               description = "Generates a new access token using a valid refresh token")
    public ResponseEntity<AuthResponse> refresh(
            @Parameter(description = "Refresh token")
            @RequestBody Map<String, String> request) {
        
        try {
            String refreshToken = request.get("refreshToken");
            if (refreshToken == null || refreshToken.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            AuthResponse response = authService.refreshToken(refreshToken);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    /**
     * Get current user information
     * GET /api/v1/auth/me
     */
    @GetMapping("/me")
    @Operation(summary = "Get current user", 
               description = "Returns current authenticated user information")
    public ResponseEntity<AuthResponse.UserInfo> getCurrentUser() {
        
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            String username = authentication.getName();
            AuthResponse.UserInfo userInfo = authService.getCurrentUser(username);
            
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    /**
     * Check username availability
     * GET /api/v1/auth/check-username/{username}
     */
    @GetMapping("/check-username/{username}")
    @Operation(summary = "Check username availability", 
               description = "Checks if a username is available for registration")
    public ResponseEntity<Map<String, Boolean>> checkUsernameAvailability(@PathVariable String username) {
        
        boolean available = authService.isUsernameAvailable(username);
        return ResponseEntity.ok(Map.of("available", available));
    }
    
    /**
     * Check email availability
     * GET /api/v1/auth/check-email/{email}
     */
    @GetMapping("/check-email/{email}")
    @Operation(summary = "Check email availability", 
               description = "Checks if an email is available for registration")
    public ResponseEntity<Map<String, Boolean>> checkEmailAvailability(@PathVariable String email) {
        
        boolean available = authService.isEmailAvailable(email);
        return ResponseEntity.ok(Map.of("available", available));
    }
    
    /**
     * Logout user (client-side token invalidation)
     * POST /api/v1/auth/logout
     */
    @PostMapping("/logout")
    @Operation(summary = "User logout", 
               description = "Logs out the current user (client should discard tokens)")
    public ResponseEntity<Map<String, String>> logout() {
        
        // For JWT-based authentication, logout is primarily handled client-side
        // by discarding the tokens. In a production system, you might want to
        // maintain a blacklist of revoked tokens.
        
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
    
    /**
     * Health check for auth service
     * GET /api/v1/auth/health
     */
    @GetMapping("/health")
    @Operation(summary = "Auth service health check", 
               description = "Returns the health status of the authentication service")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "Authentication Service",
                "timestamp", java.time.LocalDateTime.now().toString()
        ));
    }
}
