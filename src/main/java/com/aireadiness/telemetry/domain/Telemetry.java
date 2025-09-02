package com.aireadiness.telemetry.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Telemetry entity for tracking events and metrics
 * Based on PRD section 13 - Database model
 */
@Entity
@Table(name = "telemetry")
public class Telemetry {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "assessment_id")
    private String assessmentId;
    
    @Column(name = "event_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private EventType eventType;
    
    @Column(name = "event_data", columnDefinition = "jsonb")
    private String eventData; // Additional data as JSON
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    public Telemetry() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Telemetry(String assessmentId, EventType eventType, String eventData) {
        this();
        this.assessmentId = assessmentId;
        this.eventType = eventType;
        this.eventData = eventData;
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getAssessmentId() { return assessmentId; }
    public void setAssessmentId(String assessmentId) { this.assessmentId = assessmentId; }
    
    public EventType getEventType() { return eventType; }
    public void setEventType(EventType eventType) { this.eventType = eventType; }
    
    public String getEventData() { return eventData; }
    public void setEventData(String eventData) { this.eventData = eventData; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    /**
     * Event types for telemetry according to PRD metrics
     */
    public enum EventType {
        ASSESSMENT_STARTED,    // M1: iniciados
        ASSESSMENT_COMPLETED,  // M1: completados
        PLAN_GENERATED,        // Plan creation
        PDF_DOWNLOADED,        // M2: descargas PDF
        API_ERROR,             // Error tracking
        ROLE_SELECTED          // Role selection analytics
    }
}
