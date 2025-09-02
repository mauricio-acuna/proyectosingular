package com.aireadiness.plan.controller;

import com.aireadiness.plan.dto.PlanDto;
import com.aireadiness.plan.service.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for plan operations
 * Based on PRD section 12.3 - Plan API
 */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Plan", description = "API for AI-generated 30/60/90 day plans")
public class PlanController {
    
    private final PlanService planService;
    
    public PlanController(PlanService planService) {
        this.planService = planService;
    }
    
    /**
     * Generate plan for assessment
     * POST /api/v1/assessments/{id}/plan
     */
    @PostMapping("/assessments/{assessmentId}/plan")
    @Operation(summary = "Generate plan", 
               description = "Generates AI-powered 30/60/90 day learning plan based on assessment results")
    public ResponseEntity<PlanDto> generatePlan(
            @PathVariable String assessmentId,
            @Parameter(description = "Hours per week available for learning (optional, uses assessment default)")
            @RequestBody(required = false) PlanRequest request) {
        
        try {
            Integer hoursPerWeek = request != null ? request.getHoursPerWeek() : null;
            PlanDto plan = planService.generatePlan(assessmentId, hoursPerWeek);
            return ResponseEntity.status(HttpStatus.CREATED).body(plan);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get existing plan for assessment
     * GET /api/v1/assessments/{id}/plan
     */
    @GetMapping("/assessments/{assessmentId}/plan")
    @Operation(summary = "Get plan", 
               description = "Retrieves existing plan for assessment")
    public ResponseEntity<PlanDto> getPlan(@PathVariable String assessmentId) {
        
        return planService.getPlan(assessmentId)
                .map(plan -> ResponseEntity.ok(plan))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Request DTO for plan generation
     */
    public static class PlanRequest {
        private Integer hoursPerWeek;
        
        public PlanRequest() {}
        
        public Integer getHoursPerWeek() { return hoursPerWeek; }
        public void setHoursPerWeek(Integer hoursPerWeek) { this.hoursPerWeek = hoursPerWeek; }
    }
}
