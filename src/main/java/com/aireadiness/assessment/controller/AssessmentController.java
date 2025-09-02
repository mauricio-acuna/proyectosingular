package com.aireadiness.assessment.controller;

import com.aireadiness.assessment.dto.AssessmentResponse;
import com.aireadiness.assessment.dto.CreateAssessmentRequest;
import com.aireadiness.assessment.service.AssessmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for assessment operations
 * Based on PRD section 12.2 - Assessment API
 */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Assessment", description = "API for creating and managing assessments")
public class AssessmentController {
    
    private final AssessmentService assessmentService;
    
    public AssessmentController(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }
    
    /**
     * Create new assessment and calculate scores
     * POST /api/v1/assessments
     */
    @PostMapping("/assessments")
    @Operation(summary = "Create assessment", 
               description = "Creates a new assessment, calculates scores and identifies gaps")
    public ResponseEntity<AssessmentResponse> createAssessment(
            @Valid @RequestBody CreateAssessmentRequest request) {
        
        try {
            AssessmentResponse response = assessmentService.createAssessment(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get assessment by ID (for future comparison feature)
     * GET /api/v1/assessments/{id}
     */
    @GetMapping("/assessments/{id}")
    @Operation(summary = "Get assessment", 
               description = "Retrieves assessment details by ID")
    public ResponseEntity<Object> getAssessment(@PathVariable String id) {
        try {
            var assessment = assessmentService.getAssessment(id);
            // For MVP, return basic info
            return ResponseEntity.ok(new AssessmentBasicInfo(
                assessment.getId(),
                assessment.getRoleId(),
                assessment.getVersion(),
                assessment.getCreatedAt().toString(),
                assessment.getAnswers().size()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Simple DTO for assessment basic info
     */
    public static class AssessmentBasicInfo {
        private String id;
        private String roleId;
        private String version;
        private String createdAt;
        private int answerCount;
        
        public AssessmentBasicInfo(String id, String roleId, String version, String createdAt, int answerCount) {
            this.id = id;
            this.roleId = roleId;
            this.version = version;
            this.createdAt = createdAt;
            this.answerCount = answerCount;
        }
        
        // Getters
        public String getId() { return id; }
        public String getRoleId() { return roleId; }
        public String getVersion() { return version; }
        public String getCreatedAt() { return createdAt; }
        public int getAnswerCount() { return answerCount; }
    }
}
