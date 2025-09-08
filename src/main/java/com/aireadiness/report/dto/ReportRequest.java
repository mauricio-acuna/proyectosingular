package com.aireadiness.report.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for report generation
 */
public class ReportRequest {
    
    @Size(max = 100, message = "Report title cannot exceed 100 characters")
    private String title;
    
    @Email(message = "Must be a valid email address")
    private String emailTo;
    
    private boolean includeCharts = true;
    
    private boolean includeRecommendations = true;
    
    private boolean includeBenchmarking = false;
    
    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;
    
    // Default constructor
    public ReportRequest() {}
    
    // Constructor with required fields
    public ReportRequest(String title, String emailTo) {
        this.title = title;
        this.emailTo = emailTo;
    }
    
    // Getters and Setters
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getEmailTo() {
        return emailTo;
    }
    
    public void setEmailTo(String emailTo) {
        this.emailTo = emailTo;
    }
    
    public boolean isIncludeCharts() {
        return includeCharts;
    }
    
    public void setIncludeCharts(boolean includeCharts) {
        this.includeCharts = includeCharts;
    }
    
    public boolean isIncludeRecommendations() {
        return includeRecommendations;
    }
    
    public void setIncludeRecommendations(boolean includeRecommendations) {
        this.includeRecommendations = includeRecommendations;
    }
    
    public boolean isIncludeBenchmarking() {
        return includeBenchmarking;
    }
    
    public void setIncludeBenchmarking(boolean includeBenchmarking) {
        this.includeBenchmarking = includeBenchmarking;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    @Override
    public String toString() {
        return "ReportRequest{" +
                "title='" + title + '\'' +
                ", emailTo='" + emailTo + '\'' +
                ", includeCharts=" + includeCharts +
                ", includeRecommendations=" + includeRecommendations +
                ", includeBenchmarking=" + includeBenchmarking +
                ", notes='" + notes + '\'' +
                '}';
    }
}
