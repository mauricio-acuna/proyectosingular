package com.aireadiness.assessment.entity;

import com.aireadiness.catalog.domain.Question;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "assessment_responses")
public class AssessmentResponse {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Assessment is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id", nullable = false)
    private Assessment assessment;
    
    @NotNull(message = "Question is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;
    
    @Column(name = "question_order")
    private Integer questionOrder;
    
    @Size(max = 2000, message = "Response text cannot exceed 2000 characters")
    @Column(name = "response_text", columnDefinition = "TEXT")
    private String responseText;
    
    @Column(name = "selected_option")
    private String selectedOption;
    
    @Column(name = "numeric_value")
    private Double numericValue;
    
    @Column(name = "score")
    private Double score;
    
    @Column(name = "time_spent_seconds")
    private Integer timeSpentSeconds;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // Constructors
    public AssessmentResponse() {}
    
    public AssessmentResponse(Assessment assessment, Question question, Integer questionOrder) {
        this.assessment = assessment;
        this.question = question;
        this.questionOrder = questionOrder;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Assessment getAssessment() {
        return assessment;
    }
    
    public void setAssessment(Assessment assessment) {
        this.assessment = assessment;
    }
    
    public Question getQuestion() {
        return question;
    }
    
    public void setQuestion(Question question) {
        this.question = question;
    }
    
    public Integer getQuestionOrder() {
        return questionOrder;
    }
    
    public void setQuestionOrder(Integer questionOrder) {
        this.questionOrder = questionOrder;
    }
    
    public String getResponseText() {
        return responseText;
    }
    
    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }
    
    public String getSelectedOption() {
        return selectedOption;
    }
    
    public void setSelectedOption(String selectedOption) {
        this.selectedOption = selectedOption;
    }
    
    public Double getNumericValue() {
        return numericValue;
    }
    
    public void setNumericValue(Double numericValue) {
        this.numericValue = numericValue;
    }
    
    public Double getScore() {
        return score;
    }
    
    public void setScore(Double score) {
        this.score = score;
    }
    
    public Integer getTimeSpentSeconds() {
        return timeSpentSeconds;
    }
    
    public void setTimeSpentSeconds(Integer timeSpentSeconds) {
        this.timeSpentSeconds = timeSpentSeconds;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // Helper methods
    public boolean hasTextResponse() {
        return responseText != null && !responseText.trim().isEmpty();
    }
    
    public boolean hasSelectedOption() {
        return selectedOption != null && !selectedOption.trim().isEmpty();
    }
    
    public boolean hasNumericValue() {
        return numericValue != null;
    }
    
    public boolean isAnswered() {
        return hasTextResponse() || hasSelectedOption() || hasNumericValue();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AssessmentResponse)) return false;
        AssessmentResponse that = (AssessmentResponse) o;
        return id != null && id.equals(that.getId());
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    
    @Override
    public String toString() {
        return "AssessmentResponse{" +
                "id=" + id +
                ", assessment=" + (assessment != null ? assessment.getId() : null) +
                ", question=" + (question != null ? question.getId() : null) +
                ", questionOrder=" + questionOrder +
                ", hasResponse=" + isAnswered() +
                ", score=" + score +
                ", createdAt=" + createdAt +
                '}';
    }
}
