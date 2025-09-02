package com.aireadiness.assessment.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for answer in assessment request
 */
public class AnswerDto {
    
    @NotBlank
    private String questionId;
    
    private Integer value; // For Likert scale and multiple choice
    
    private String text; // For text answers
    
    public AnswerDto() {}
    
    public AnswerDto(String questionId, Integer value) {
        this.questionId = questionId;
        this.value = value;
    }
    
    public AnswerDto(String questionId, String text) {
        this.questionId = questionId;
        this.text = text;
    }
    
    // Getters and setters
    public String getQuestionId() { return questionId; }
    public void setQuestionId(String questionId) { this.questionId = questionId; }
    
    public Integer getValue() { return value; }
    public void setValue(Integer value) { this.value = value; }
    
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}
