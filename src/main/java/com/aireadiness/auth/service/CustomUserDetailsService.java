package com.aireadiness.auth.service;

import com.aireadiness.auth.domain.User;
import com.aireadiness.auth.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Custom UserDetailsService implementation for Spring Security
 * Loads user details from database for authentication
 */
@Service
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    /**
     * Load user by username or email for Spring Security authentication
     */
    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        
        User user = userRepository.findByUsernameOrEmailIgnoreCase(usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException(
                    "User not found with username or email: " + usernameOrEmail
                ));
        
        // Update last login time when user is loaded for authentication
        // Note: This will be handled in the authentication success handler instead
        // to avoid updating on every token validation
        
        return user;
    }
    
    /**
     * Load user by ID (useful for token-based authentication)
     */
    public UserDetails loadUserById(String userId) throws UsernameNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(
                    "User not found with id: " + userId
                ));
        
        return user;
    }
    
    /**
     * Check if user exists by username or email
     */
    public boolean existsByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmailIgnoreCase(usernameOrEmail).isPresent();
    }
    
    /**
     * Check if username is available
     */
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsernameIgnoreCase(username);
    }
    
    /**
     * Check if email is available
     */
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmailIgnoreCase(email);
    }
}
