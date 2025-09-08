package com.aireadiness.auth.domain;

/**
 * Enum defining user roles in the AI Readiness Assessment Platform
 */
public enum UserRole {
    /**
     * Regular user - can take assessments, view their own reports
     */
    USER("User"),
    
    /**
     * Administrator - full access to admin features, user management, system configuration
     */
    ADMIN("Administrator"),
    
    /**
     * Moderator - can review and manage content, limited admin features
     */
    MODERATOR("Moderator");
    
    private final String displayName;
    
    UserRole(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Check if this role has admin privileges
     */
    public boolean hasAdminPrivileges() {
        return this == ADMIN || this == MODERATOR;
    }
    
    /**
     * Check if this role can access admin panel
     */
    public boolean canAccessAdminPanel() {
        return this == ADMIN || this == MODERATOR;
    }
    
    /**
     * Check if this role can manage users
     */
    public boolean canManageUsers() {
        return this == ADMIN;
    }
    
    /**
     * Check if this role can manage system configuration
     */
    public boolean canManageSystem() {
        return this == ADMIN;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
