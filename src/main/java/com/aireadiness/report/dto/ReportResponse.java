package com.aireadiness.report.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Response DTO for report generation
 */
public class ReportResponse {
    
    private String reportId;
    private String assessmentId;
    private String title;
    private ReportStatus status;
    private String downloadUrl;
    private Map<String, Double> scores;
    private List<String> recommendations;
    private List<String> gaps;
    private String filePath;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    
    // Default constructor
    public ReportResponse() {}
    
    // Constructor for successful report generation
    public ReportResponse(String reportId, String assessmentId, String title, 
                         String downloadUrl, Map<String, Double> scores,
                         List<String> recommendations, List<String> gaps) {
        this.reportId = reportId;
        this.assessmentId = assessmentId;
        this.title = title;
        this.status = ReportStatus.COMPLETED;
        this.downloadUrl = downloadUrl;
        this.scores = scores;
        this.recommendations = recommendations;
        this.gaps = gaps;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusDays(30); // Reports expire after 30 days
    }
    
    // Constructor for report in progress
    public ReportResponse(String reportId, String assessmentId, ReportStatus status) {
        this.reportId = reportId;
        this.assessmentId = assessmentId;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getReportId() {
        return reportId;
    }
    
    public void setReportId(String reportId) {
        this.reportId = reportId;
    }
    
    public String getAssessmentId() {
        return assessmentId;
    }
    
    public void setAssessmentId(String assessmentId) {
        this.assessmentId = assessmentId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public ReportStatus getStatus() {
        return status;
    }
    
    public void setStatus(ReportStatus status) {
        this.status = status;
    }
    
    public String getDownloadUrl() {
        return downloadUrl;
    }
    
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
    
    public Map<String, Double> getScores() {
        return scores;
    }
    
    public void setScores(Map<String, Double> scores) {
        this.scores = scores;
    }
    
    public List<String> getRecommendations() {
        return recommendations;
    }
    
    public void setRecommendations(List<String> recommendations) {
        this.recommendations = recommendations;
    }
    
    public List<String> getGaps() {
        return gaps;
    }
    
    public void setGaps(List<String> gaps) {
        this.gaps = gaps;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    /**
     * Check if report is expired
     */
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
    
    /**
     * Check if report is ready for download
     */
    public boolean isReady() {
        return status == ReportStatus.COMPLETED && downloadUrl != null;
    }
    
    @Override
    public String toString() {
        return "ReportResponse{" +
                "reportId='" + reportId + '\'' +
                ", assessmentId='" + assessmentId + '\'' +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", createdAt=" + createdAt +
                ", expiresAt=" + expiresAt +
                '}';
    }
}
