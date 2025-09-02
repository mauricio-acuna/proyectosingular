package com.aireadiness.assessment.repository;

import com.aireadiness.assessment.domain.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Assessment entity
 */
@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, String> {
    
    /**
     * Find assessment with answers
     */
    @Query("SELECT a FROM Assessment a LEFT JOIN FETCH a.answers WHERE a.id = :id")
    Optional<Assessment> findByIdWithAnswers(@Param("id") String id);
    
    /**
     * Find assessments by role for analytics
     */
    List<Assessment> findByRoleIdAndCreatedAtAfter(String roleId, LocalDateTime after);
    
    /**
     * Find assessments by tenant (for multi-tenant support)
     */
    List<Assessment> findByTenantId(String tenantId);
    
    /**
     * Count total assessments for metrics
     */
    long countByCreatedAtAfter(LocalDateTime after);
    
    /**
     * Count completed assessments by role
     */
    @Query("SELECT COUNT(a) FROM Assessment a WHERE a.roleId = :roleId AND SIZE(a.answers) > 0")
    long countCompletedByRoleId(@Param("roleId") String roleId);
}
