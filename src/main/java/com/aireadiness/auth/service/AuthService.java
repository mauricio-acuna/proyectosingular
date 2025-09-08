package com.aireadiness.auth.service;

import com.aireadiness.auth.domain.User;
import com.aireadiness.auth.domain.UserRole;
import com.aireadiness.auth.domain.UserStatus;
import com.aireadiness.auth.dto.AuthResponse;
import com.aireadiness.auth.dto.LoginRequest;
import com.aireadiness.auth.dto.RegisterRequest;
import com.aireadiness.auth.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service for handling authentication operations
 * Manages user registration, login, and token operations
 */
@Service
@Transactional
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    
    public AuthService(UserRepository userRepository, 
                      PasswordEncoder passwordEncoder,
                      JwtService jwtService,
                      AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }
    
    /**
     * Register a new user
     */
    public AuthResponse register(RegisterRequest request) {
        
        // Validate that username and email are available
        if (userRepository.existsByUsernameIgnoreCase(request.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }
        
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }
        
        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(UserRole.USER);
        user.setStatus(UserStatus.ACTIVE);
        
        // For MVP, skip email verification
        user.setEmailVerifiedAt(LocalDateTime.now());
        
        // Save user
        user = userRepository.save(user);
        
        // Generate tokens
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        
        // Update last login
        user.updateLastLogin();
        userRepository.save(user);
        
        // Create user info for response
        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                user.getStatus(),
                user.isEmailVerified(),
                user.getLastLoginAt()
        );
        
        return new AuthResponse(
                accessToken, 
                refreshToken, 
                jwtService.getJwtExpirationMs() / 1000, // Convert to seconds
                userInfo
        );
    }
    
    /**
     * Authenticate user login
     */
    public AuthResponse login(LoginRequest request) {
        
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsernameOrEmail(),
                            request.getPassword()
                    )
            );
            
            // Get user details
            User user = (User) authentication.getPrincipal();
            
            // Check if user account is active
            if (!user.isEnabled()) {
                throw new BadCredentialsException("Account is disabled");
            }
            
            // Generate tokens
            String accessToken = jwtService.generateToken(user);
            String refreshToken = request.isRememberMe() ? jwtService.generateRefreshToken(user) : null;
            
            // Update last login
            user.updateLastLogin();
            userRepository.save(user);
            
            // Create user info for response
            AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getRole(),
                    user.getStatus(),
                    user.isEmailVerified(),
                    user.getLastLoginAt()
            );
            
            return new AuthResponse(
                    accessToken, 
                    refreshToken, 
                    jwtService.getJwtExpirationMs() / 1000, // Convert to seconds
                    userInfo
            );
            
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid username/email or password");
        }
    }
    
    /**
     * Refresh access token using refresh token
     */
    public AuthResponse refreshToken(String refreshToken) {
        
        try {
            // Validate refresh token
            if (!jwtService.isRefreshToken(refreshToken)) {
                throw new IllegalArgumentException("Invalid refresh token");
            }
            
            // Extract username from refresh token
            String username = jwtService.extractUsername(refreshToken);
            
            // Find user
            User user = userRepository.findByUsernameOrEmailIgnoreCase(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            
            // Validate refresh token against user
            if (!jwtService.isTokenValid(refreshToken, user)) {
                throw new IllegalArgumentException("Invalid or expired refresh token");
            }
            
            // Generate new access token
            String newAccessToken = jwtService.generateToken(user);
            
            // Create user info for response
            AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getRole(),
                    user.getStatus(),
                    user.isEmailVerified(),
                    user.getLastLoginAt()
            );
            
            return new AuthResponse(
                    newAccessToken, 
                    jwtService.getJwtExpirationMs() / 1000, // Convert to seconds
                    userInfo
            );
            
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid refresh token: " + e.getMessage());
        }
    }
    
    /**
     * Get current authenticated user
     */
    @Transactional(readOnly = true)
    public AuthResponse.UserInfo getCurrentUser(String username) {
        User user = userRepository.findByUsernameOrEmailIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        return new AuthResponse.UserInfo(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                user.getStatus(),
                user.isEmailVerified(),
                user.getLastLoginAt()
        );
    }
    
    /**
     * Check if username is available
     */
    @Transactional(readOnly = true)
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsernameIgnoreCase(username);
    }
    
    /**
     * Check if email is available
     */
    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmailIgnoreCase(email);
    }
    
    /**
     * Create default admin user if none exists
     */
    public void createDefaultAdminIfNotExists() {
        if (userRepository.countByRole(UserRole.ADMIN) == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@aireadiness.com");
            admin.setPassword(passwordEncoder.encode("admin123")); // Change in production
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setRole(UserRole.ADMIN);
            admin.setStatus(UserStatus.ACTIVE);
            admin.setEmailVerifiedAt(LocalDateTime.now());
            
            userRepository.save(admin);
        }
    }
}
