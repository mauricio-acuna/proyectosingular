package com.aireadiness.assessment.domain;

import jakarta.persistence.*;

/**
 * Answer entity representing a user's response to a question
 * Based on PRD section 13 - Database model
 */
@Entity
@Table(name = "answer")
public class Answer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id", nullable = false)
    private Assessment assessment;
    
    @Column(name = "question_id", nullable = false)
    private String questionId;
    
    @Column(name = "value_numeric")
    private Integer valueNumeric; // For Likert scale (1-5) and multiple choice
    
    @Column(name = "value_text", length = 1000)
    private String valueText; // For text answers
    
    public Answer() {}
    
    public Answer(Assessment assessment, String questionId, Integer valueNumeric, String valueText) {
        this.assessment = assessment;
        this.questionId = questionId;
        this.valueNumeric = valueNumeric;
        this.valueText = valueText;
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Assessment getAssessment() { return assessment; }
    public void setAssessment(Assessment assessment) { this.assessment = assessment; }
    
    public String getQuestionId() { return questionId; }
    public void setQuestionId(String questionId) { this.questionId = questionId; }
    
    public Integer getValueNumeric() { return valueNumeric; }
    public void setValueNumeric(Integer valueNumeric) { this.valueNumeric = valueNumeric; }
    
    public String getValueText() { return valueText; }
    public void setValueText(String valueText) { this.valueText = valueText; }
}
