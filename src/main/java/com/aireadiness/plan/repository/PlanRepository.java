package com.aireadiness.plan.repository;

import com.aireadiness.plan.domain.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Plan entity
 */
@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    
    /**
     * Find plan by assessment ID
     */
    Optional<Plan> findByAssessmentId(String assessmentId);
    
    /**
     * Check if plan exists for assessment
     */
    boolean existsByAssessmentId(String assessmentId);
}
