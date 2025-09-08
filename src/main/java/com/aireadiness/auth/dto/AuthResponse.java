package com.aireadiness.auth.dto;

import com.aireadiness.auth.domain.UserRole;
import com.aireadiness.auth.domain.UserStatus;

import java.time.LocalDateTime;

/**
 * Response DTO for authentication operations
 */
public class AuthResponse {
    
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private long expiresIn; // Token expiration time in seconds
    private UserInfo user;
    
    // Default constructor
    public AuthResponse() {}
    
    // Constructor for successful authentication
    public AuthResponse(String accessToken, String refreshToken, long expiresIn, UserInfo user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.user = user;
    }
    
    // Constructor for access token only
    public AuthResponse(String accessToken, long expiresIn, UserInfo user) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.user = user;
    }
    
    // Getters and Setters
    public String getAccessToken() {
        return accessToken;
    }
    
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    
    public String getRefreshToken() {
        return refreshToken;
    }
    
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    public String getTokenType() {
        return tokenType;
    }
    
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
    
    public long getExpiresIn() {
        return expiresIn;
    }
    
    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }
    
    public UserInfo getUser() {
        return user;
    }
    
    public void setUser(UserInfo user) {
        this.user = user;
    }
    
    /**
     * Nested class for user information in auth response
     */
    public static class UserInfo {
        private String id;
        private String username;
        private String email;
        private String firstName;
        private String lastName;
        private String fullName;
        private UserRole role;
        private UserStatus status;
        private boolean emailVerified;
        private LocalDateTime lastLoginAt;
        
        // Default constructor
        public UserInfo() {}
        
        // Constructor
        public UserInfo(String id, String username, String email, String firstName, 
                       String lastName, UserRole role, UserStatus status, 
                       boolean emailVerified, LocalDateTime lastLoginAt) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.role = role;
            this.status = status;
            this.emailVerified = emailVerified;
            this.lastLoginAt = lastLoginAt;
            
            // Set full name
            if (firstName != null && lastName != null) {
                this.fullName = firstName + " " + lastName;
            } else if (firstName != null) {
                this.fullName = firstName;
            } else if (lastName != null) {
                this.fullName = lastName;
            } else {
                this.fullName = username;
            }
        }
        
        // Getters and Setters
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        public String getFirstName() {
            return firstName;
        }
        
        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }
        
        public String getLastName() {
            return lastName;
        }
        
        public void setLastName(String lastName) {
            this.lastName = lastName;
        }
        
        public String getFullName() {
            return fullName;
        }
        
        public void setFullName(String fullName) {
            this.fullName = fullName;
        }
        
        public UserRole getRole() {
            return role;
        }
        
        public void setRole(UserRole role) {
            this.role = role;
        }
        
        public UserStatus getStatus() {
            return status;
        }
        
        public void setStatus(UserStatus status) {
            this.status = status;
        }
        
        public boolean isEmailVerified() {
            return emailVerified;
        }
        
        public void setEmailVerified(boolean emailVerified) {
            this.emailVerified = emailVerified;
        }
        
        public LocalDateTime getLastLoginAt() {
            return lastLoginAt;
        }
        
        public void setLastLoginAt(LocalDateTime lastLoginAt) {
            this.lastLoginAt = lastLoginAt;
        }
    }
    
    @Override
    public String toString() {
        return "AuthResponse{" +
                "tokenType='" + tokenType + '\'' +
                ", expiresIn=" + expiresIn +
                ", user=" + (user != null ? user.username : null) +
                '}';
    }
}
