package com.aireadiness.assessment.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Assessment entity representing a completed evaluation
 * Based on PRD section 13 - Database model
 */
@Entity
@Table(name = "assessment")
public class Assessment {
    
    @Id
    private String id; // Format: a_01H... (ULID or similar)
    
    @Column(name = "role_id", nullable = false)
    private String roleId;
    
    @Column(nullable = false)
    private String version;
    
    @Column(name = "tenant_id")
    private String tenantId;
    
    @Column(name = "user_id")
    private String userId; // Optional, for registered users
    
    @Column(nullable = false)
    private String locale;
    
    @Column(name = "hours_per_week", nullable = false)
    private Integer hoursPerWeek;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "email_hash")
    private String emailHash; // Hashed email for privacy
    
    @Column(nullable = false)
    private Boolean consent;
    
    @Column(name = "prev_assessment_id")
    private String prevAssessmentId; // For comparison feature
    
    @OneToMany(mappedBy = "assessment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Answer> answers = new ArrayList<>();
    
    public Assessment() {
        this.createdAt = LocalDateTime.now();
        this.consent = false;
    }
    
    public Assessment(String id, String roleId, String version, String locale, Integer hoursPerWeek) {
        this();
        this.id = id;
        this.roleId = roleId;
        this.version = version;
        this.locale = locale;
        this.hoursPerWeek = hoursPerWeek;
    }
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getRoleId() { return roleId; }
    public void setRoleId(String roleId) { this.roleId = roleId; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getLocale() { return locale; }
    public void setLocale(String locale) { this.locale = locale; }
    
    public Integer getHoursPerWeek() { return hoursPerWeek; }
    public void setHoursPerWeek(Integer hoursPerWeek) { this.hoursPerWeek = hoursPerWeek; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public String getEmailHash() { return emailHash; }
    public void setEmailHash(String emailHash) { this.emailHash = emailHash; }
    
    public Boolean getConsent() { return consent; }
    public void setConsent(Boolean consent) { this.consent = consent; }
    
    public String getPrevAssessmentId() { return prevAssessmentId; }
    public void setPrevAssessmentId(String prevAssessmentId) { this.prevAssessmentId = prevAssessmentId; }
    
    public List<Answer> getAnswers() { return answers; }
    public void setAnswers(List<Answer> answers) { this.answers = answers; }
}
