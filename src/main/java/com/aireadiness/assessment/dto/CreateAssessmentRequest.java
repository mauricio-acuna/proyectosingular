package com.aireadiness.assessment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * DTO for assessment creation request according to PRD section 12.2
 */
public class CreateAssessmentRequest {
    
    @NotBlank
    private String roleId;
    
    @NotBlank
    private String version;
    
    @NotNull
    @Size(min = 1)
    private List<AnswerDto> answers;
    
    @NotBlank
    private String locale;
    
    @Positive
    private Integer hoursPerWeek;
    
    private String email; // Optional
    
    @NotNull
    private Boolean consent;
    
    private String prevAssessmentId; // Optional, for comparison
    
    public CreateAssessmentRequest() {}
    
    // Getters and setters
    public String getRoleId() { return roleId; }
    public void setRoleId(String roleId) { this.roleId = roleId; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public List<AnswerDto> getAnswers() { return answers; }
    public void setAnswers(List<AnswerDto> answers) { this.answers = answers; }
    
    public String getLocale() { return locale; }
    public void setLocale(String locale) { this.locale = locale; }
    
    public Integer getHoursPerWeek() { return hoursPerWeek; }
    public void setHoursPerWeek(Integer hoursPerWeek) { this.hoursPerWeek = hoursPerWeek; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public Boolean getConsent() { return consent; }
    public void setConsent(Boolean consent) { this.consent = consent; }
    
    public String getPrevAssessmentId() { return prevAssessmentId; }
    public void setPrevAssessmentId(String prevAssessmentId) { this.prevAssessmentId = prevAssessmentId; }
}
