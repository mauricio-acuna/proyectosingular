package com.aireadiness.plan.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Plan entity representing AI-generated 30/60/90 day plans
 * Based on PRD section 13 - Database model and section 8 - Plan format
 */
@Entity
@Table(name = "plan")
public class Plan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "assessment_id", nullable = false, unique = true)
    private String assessmentId;
    
    @Column(name = "plan_json", nullable = false, columnDefinition = "jsonb")
    private String planJson;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    public Plan() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Plan(String assessmentId, String planJson) {
        this();
        this.assessmentId = assessmentId;
        this.planJson = planJson;
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getAssessmentId() { return assessmentId; }
    public void setAssessmentId(String assessmentId) { this.assessmentId = assessmentId; }
    
    public String getPlanJson() { return planJson; }
    public void setPlanJson(String planJson) { this.planJson = planJson; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
