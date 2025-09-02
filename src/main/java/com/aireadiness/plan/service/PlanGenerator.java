package com.aireadiness.plan.service;

import com.aireadiness.plan.dto.PlanDto;
import com.aireadiness.common.domain.Pillar;

import java.util.List;
import java.util.Map;

/**
 * Interface for plan generation according to PRD section 8
 * Pluggable provider pattern for different AI services
 */
public interface PlanGenerator {
    
    /**
     * Generate a 30/60/90 day plan based on assessment results
     * 
     * @param role The professional role being assessed
     * @param scores Pillar scores (0-100)
     * @param gaps List of question IDs with gaps
     * @param hoursPerWeek Available hours per week for learning
     * @param locale Language preference (es-ES or en-US)
     * @return Generated plan with max 5 priorities
     */
    PlanDto generatePlan(String role, 
                        Map<Pillar, Double> scores, 
                        List<String> gaps, 
                        Integer hoursPerWeek, 
                        String locale);
    
    /**
     * Validate generated plan according to PRD guardrails
     * 
     * @param plan The plan to validate
     * @return true if plan is valid, false otherwise
     */
    boolean validatePlan(PlanDto plan);
}
