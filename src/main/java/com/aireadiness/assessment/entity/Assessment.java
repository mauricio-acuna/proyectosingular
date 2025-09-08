package com.aireadiness.assessment.entity;

import com.aireadiness.catalog.domain.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "assessments")
public class Assessment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @NotNull(message = "Role is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AssessmentStatus status = AssessmentStatus.IN_PROGRESS;
    
    @Column(name = "current_question_index")
    private Integer currentQuestionIndex = 0;
    
    @Column(name = "total_questions")
    private Integer totalQuestions;
    
    @Column(name = "completion_percentage")
    private Double completionPercentage = 0.0;
    
    @Column(name = "overall_score")
    private Double overallScore;
    
    @Column(name = "tech_score")
    private Double techScore;
    
    @Column(name = "ai_score")
    private Double aiScore;
    
    @Column(name = "communication_score")
    private Double communicationScore;
    
    @Column(name = "portfolio_score")
    private Double portfolioScore;
    
    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    @Column(name = "notes")
    private String notes;
    
    @Column(name = "time_spent_minutes")
    private Integer timeSpentMinutes;
    
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "assessment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AssessmentResponse> responses = new ArrayList<>();
    
    // Constructors
    public Assessment() {}
    
    public Assessment(User user, Role role) {
        this.user = user;
        this.role = role;
        this.startedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
    
    public AssessmentStatus getStatus() {
        return status;
    }
    
    public void setStatus(AssessmentStatus status) {
        this.status = status;
    }
    
    public Integer getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }
    
    public void setCurrentQuestionIndex(Integer currentQuestionIndex) {
        this.currentQuestionIndex = currentQuestionIndex;
    }
    
    public Integer getTotalQuestions() {
        return totalQuestions;
    }
    
    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }
    
    public Double getCompletionPercentage() {
        return completionPercentage;
    }
    
    public void setCompletionPercentage(Double completionPercentage) {
        this.completionPercentage = completionPercentage;
    }
    
    public Double getOverallScore() {
        return overallScore;
    }
    
    public void setOverallScore(Double overallScore) {
        this.overallScore = overallScore;
    }
    
    public Double getTechScore() {
        return techScore;
    }
    
    public void setTechScore(Double techScore) {
        this.techScore = techScore;
    }
    
    public Double getAiScore() {
        return aiScore;
    }
    
    public void setAiScore(Double aiScore) {
        this.aiScore = aiScore;
    }
    
    public Double getCommunicationScore() {
        return communicationScore;
    }
    
    public void setCommunicationScore(Double communicationScore) {
        this.communicationScore = communicationScore;
    }
    
    public Double getPortfolioScore() {
        return portfolioScore;
    }
    
    public void setPortfolioScore(Double portfolioScore) {
        this.portfolioScore = portfolioScore;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public Integer getTimeSpentMinutes() {
        return timeSpentMinutes;
    }
    
    public void setTimeSpentMinutes(Integer timeSpentMinutes) {
        this.timeSpentMinutes = timeSpentMinutes;
    }
    
    public LocalDateTime getStartedAt() {
        return startedAt;
    }
    
    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }
    
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
    
    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public List<AssessmentResponse> getResponses() {
        return responses;
    }
    
    public void setResponses(List<AssessmentResponse> responses) {
        this.responses = responses;
    }
    
    // Helper methods
    public void addResponse(AssessmentResponse response) {
        responses.add(response);
        response.setAssessment(this);
    }
    
    public void removeResponse(AssessmentResponse response) {
        responses.remove(response);
        response.setAssessment(null);
    }
    
    public void updateProgress() {
        if (totalQuestions != null && totalQuestions > 0) {
            this.completionPercentage = (double) responses.size() / totalQuestions * 100;
        }
    }
    
    public void markCompleted() {
        this.status = AssessmentStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.currentQuestionIndex = this.totalQuestions;
        this.completionPercentage = 100.0;
        
        if (startedAt != null && completedAt != null) {
            this.timeSpentMinutes = (int) java.time.Duration.between(startedAt, completedAt).toMinutes();
        }
    }
    
    public boolean isCompleted() {
        return status == AssessmentStatus.COMPLETED;
    }
    
    public boolean isInProgress() {
        return status == AssessmentStatus.IN_PROGRESS;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Assessment)) return false;
        Assessment assessment = (Assessment) o;
        return id != null && id.equals(assessment.getId());
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    
    @Override
    public String toString() {
        return "Assessment{" +
                "id=" + id +
                ", user=" + (user != null ? user.getEmail() : null) +
                ", role=" + (role != null ? role.getName() : null) +
                ", status=" + status +
                ", completionPercentage=" + completionPercentage +
                ", overallScore=" + overallScore +
                ", createdAt=" + createdAt +
                '}';
    }
}
