package com.aireadiness.auth.repository;

import com.aireadiness.auth.domain.User;
import com.aireadiness.auth.domain.UserRole;
import com.aireadiness.auth.domain.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity
 * Provides data access methods for user management
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {
    
    /**
     * Find user by username (case-insensitive)
     */
    Optional<User> findByUsernameIgnoreCase(String username);
    
    /**
     * Find user by email (case-insensitive)
     */
    Optional<User> findByEmailIgnoreCase(String email);
    
    /**
     * Find user by username or email (case-insensitive)
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.username) = LOWER(:usernameOrEmail) OR LOWER(u.email) = LOWER(:usernameOrEmail)")
    Optional<User> findByUsernameOrEmailIgnoreCase(@Param("usernameOrEmail") String usernameOrEmail);
    
    /**
     * Check if username exists (case-insensitive)
     */
    boolean existsByUsernameIgnoreCase(String username);
    
    /**
     * Check if email exists (case-insensitive)
     */
    boolean existsByEmailIgnoreCase(String email);
    
    /**
     * Find user by email verification token
     */
    Optional<User> findByEmailVerificationToken(String token);
    
    /**
     * Find user by password reset token
     */
    Optional<User> findByPasswordResetToken(String token);
    
    /**
     * Find users by role
     */
    List<User> findByRole(UserRole role);
    
    /**
     * Find users by status
     */
    List<User> findByStatus(UserStatus status);
    
    /**
     * Find users by role and status
     */
    List<User> findByRoleAndStatus(UserRole role, UserStatus status);
    
    /**
     * Find users created after a specific date
     */
    List<User> findByCreatedAtAfter(LocalDateTime date);
    
    /**
     * Find users with recent activity (logged in within timeframe)
     */
    @Query("SELECT u FROM User u WHERE u.lastLoginAt > :since")
    List<User> findUsersWithRecentActivity(@Param("since") LocalDateTime since);
    
    /**
     * Find users without email verification
     */
    @Query("SELECT u FROM User u WHERE u.emailVerifiedAt IS NULL")
    List<User> findUnverifiedUsers();
    
    /**
     * Count users by status
     */
    long countByStatus(UserStatus status);
    
    /**
     * Count users by role
     */
    long countByRole(UserRole role);
    
    /**
     * Search users by username, email, first name, or last name (case-insensitive)
     */
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<User> searchUsers(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    /**
     * Find all users ordered by creation date (newest first)
     */
    @Query("SELECT u FROM User u ORDER BY u.createdAt DESC")
    Page<User> findAllOrderByCreatedAtDesc(Pageable pageable);
    
    /**
     * Find expired password reset tokens
     */
    @Query("SELECT u FROM User u WHERE u.passwordResetToken IS NOT NULL AND u.passwordResetExpiresAt < :now")
    List<User> findExpiredPasswordResetTokens(@Param("now") LocalDateTime now);
    
    /**
     * Delete expired password reset tokens
     */
    @Query("UPDATE User u SET u.passwordResetToken = NULL, u.passwordResetExpiresAt = NULL " +
           "WHERE u.passwordResetToken IS NOT NULL AND u.passwordResetExpiresAt < :now")
    int cleanupExpiredPasswordResetTokens(@Param("now") LocalDateTime now);
    
    /**
     * Get user statistics
     */
    @Query("SELECT " +
           "COUNT(u) as totalUsers, " +
           "COUNT(CASE WHEN u.status = 'ACTIVE' THEN 1 END) as activeUsers, " +
           "COUNT(CASE WHEN u.emailVerifiedAt IS NOT NULL THEN 1 END) as verifiedUsers, " +
           "COUNT(CASE WHEN u.role = 'ADMIN' THEN 1 END) as adminUsers " +
           "FROM User u")
    Object[] getUserStatistics();
}
