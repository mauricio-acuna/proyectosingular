package com.aireadiness.auth.domain;

/**
 * Enum defining user account status
 */
public enum UserStatus {
    /**
     * Active user account - can login and use the system
     */
    ACTIVE("Active"),
    
    /**
     * Inactive user account - temporarily disabled
     */
    INACTIVE("Inactive"),
    
    /**
     * Locked user account - locked due to security reasons
     */
    LOCKED("Locked"),
    
    /**
     * Expired user account - account has expired
     */
    EXPIRED("Expired"),
    
    /**
     * Pending email verification - user registered but email not verified
     */
    PENDING_VERIFICATION("Pending Verification"),
    
    /**
     * Suspended user account - suspended by admin
     */
    SUSPENDED("Suspended");
    
    private final String displayName;
    
    UserStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Check if user can login with this status
     */
    public boolean canLogin() {
        return this == ACTIVE;
    }
    
    /**
     * Check if this status requires email verification
     */
    public boolean requiresEmailVerification() {
        return this == PENDING_VERIFICATION;
    }
    
    /**
     * Check if this status is a blocked state
     */
    public boolean isBlocked() {
        return this == LOCKED || this == SUSPENDED || this == EXPIRED;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
