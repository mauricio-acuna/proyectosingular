package com.aireadiness.assessment.dto;

import java.util.List;
import java.util.Map;

/**
 * DTO for assessment response according to PRD section 12.2
 */
public class AssessmentResponse {
    
    private String assessmentId;
    private Map<String, Double> scores;
    private List<String> gaps;
    private String pdfUrl; // Optional, generated later
    
    public AssessmentResponse() {}
    
    public AssessmentResponse(String assessmentId, Map<String, Double> scores, List<String> gaps) {
        this.assessmentId = assessmentId;
        this.scores = scores;
        this.gaps = gaps;
    }
    
    // Getters and setters
    public String getAssessmentId() { return assessmentId; }
    public void setAssessmentId(String assessmentId) { this.assessmentId = assessmentId; }
    
    public Map<String, Double> getScores() { return scores; }
    public void setScores(Map<String, Double> scores) { this.scores = scores; }
    
    public List<String> getGaps() { return gaps; }
    public void setGaps(List<String> gaps) { this.gaps = gaps; }
    
    public String getPdfUrl() { return pdfUrl; }
    public void setPdfUrl(String pdfUrl) { this.pdfUrl = pdfUrl; }
}
