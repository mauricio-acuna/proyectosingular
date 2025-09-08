package com.aireadiness.report.service;

import com.aireadiness.assessment.domain.Assessment;
import com.aireadiness.assessment.service.AssessmentService;
import com.aireadiness.assessment.service.ScoringService;
import com.aireadiness.report.dto.ReportRequest;
import com.aireadiness.report.dto.ReportResponse;
import com.aireadiness.report.dto.ReportStatus;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Service for generating and managing assessment reports
 * Based on PRD section 1.2 - Basic Reporting Engine
 */
@Service
public class ReportService {
    
    private final AssessmentService assessmentService;
    private final PdfGenerationService pdfGenerationService;
    private final EmailService emailService;
    
    // In-memory storage for report metadata (for MVP - could be database in production)
    private final Map<String, ReportResponse> reportStore = new HashMap<>();
    
    // Directory for storing generated reports
    private final String reportsDirectory;
    
    public ReportService(AssessmentService assessmentService,
                        PdfGenerationService pdfGenerationService,
                        EmailService emailService) {
        this.assessmentService = assessmentService;
        this.pdfGenerationService = pdfGenerationService;
        this.emailService = emailService;
        
        // Create reports directory
        this.reportsDirectory = System.getProperty("user.dir") + "/reports";
        try {
            Files.createDirectories(Paths.get(reportsDirectory));
        } catch (IOException e) {
            throw new RuntimeException("Failed to create reports directory", e);
        }
    }
    
    /**
     * Generate comprehensive assessment report
     */
    public ReportResponse generateReport(String assessmentId, ReportRequest request) {
        
        // Get assessment data
        Assessment assessment = assessmentService.getAssessment(assessmentId);
        
        // Generate report ID
        String reportId = "r_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        
        // Create initial response (status: GENERATING)
        ReportResponse response = new ReportResponse(reportId, assessmentId, ReportStatus.GENERATING);
        reportStore.put(reportId, response);
        
        try {
            // Set report title
            String title = (request != null && request.getTitle() != null) 
                ? request.getTitle() 
                : "AI Readiness Assessment Report";
            
            // Generate PDF report
            String fileName = generatePdfReport(assessment, reportId, title, request);
            
            // Create download URL
            String downloadUrl = "/api/v1/reports/" + reportId + "/download";
            
            // Update response with completion data
            response.setTitle(title);
            response.setStatus(ReportStatus.COMPLETED);
            response.setDownloadUrl(downloadUrl);
            response.setFilePath(fileName);
            response.setCreatedAt(LocalDateTime.now());
            response.setExpiresAt(LocalDateTime.now().plusDays(30));
            
            // Add scores and insights (mock data for MVP)
            response.setScores(generateMockScores());
            response.setRecommendations(generateMockRecommendations());
            response.setGaps(generateMockGaps());
            
            // Update stored response
            reportStore.put(reportId, response);
            
            return response;
            
        } catch (Exception e) {
            // Update status to FAILED
            response.setStatus(ReportStatus.FAILED);
            reportStore.put(reportId, response);
            throw new RuntimeException("Failed to generate report", e);
        }
    }
    
    /**
     * Download generated PDF report
     */
    public Resource downloadReport(String reportId) throws FileNotFoundException {
        ReportResponse report = reportStore.get(reportId);
        
        if (report == null) {
            throw new IllegalArgumentException("Report not found: " + reportId);
        }
        
        if (report.isExpired()) {
            throw new IllegalArgumentException("Report has expired: " + reportId);
        }
        
        if (report.getFilePath() == null) {
            throw new IllegalArgumentException("Report file not available: " + reportId);
        }
        
        File file = new File(report.getFilePath());
        if (!file.exists()) {
            throw new FileNotFoundException("Report file not found: " + report.getFilePath());
        }
        
        return new FileSystemResource(file);
    }
    
    /**
     * Get report metadata
     */
    public ReportResponse getReportMetadata(String reportId) {
        ReportResponse report = reportStore.get(reportId);
        
        if (report == null) {
            throw new IllegalArgumentException("Report not found: " + reportId);
        }
        
        return report;
    }
    
    /**
     * Email report to specified address
     */
    public void emailReport(String reportId, String emailAddress) {
        ReportResponse report = getReportMetadata(reportId);
        
        if (!report.isReady()) {
            throw new IllegalArgumentException("Report is not ready for delivery: " + reportId);
        }
        
        try {
            Resource reportFile = downloadReport(reportId);
            emailService.sendReportEmail(emailAddress, report, reportFile);
        } catch (Exception e) {
            throw new RuntimeException("Failed to email report", e);
        }
    }
    
    /**
     * Get assessment summary for dashboard
     */
    public Map<String, Object> getAssessmentSummary(String assessmentId) {
        Assessment assessment = assessmentService.getAssessment(assessmentId);
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("assessmentId", assessmentId);
        summary.put("completedAt", assessment.getCreatedAt());
        summary.put("answerCount", assessment.getAnswers().size());
        summary.put("scores", generateMockScores());
        summary.put("topGaps", generateMockGaps().subList(0, Math.min(3, generateMockGaps().size())));
        summary.put("topRecommendations", generateMockRecommendations().subList(0, Math.min(3, generateMockRecommendations().size())));
        
        return summary;
    }
    
    /**
     * Generate PDF report file
     */
    private String generatePdfReport(Assessment assessment, String reportId, String title, ReportRequest request) {
        try {
            String fileName = reportsDirectory + "/report_" + reportId + ".pdf";
            
            // Use PDF generation service
            pdfGenerationService.generateAssessmentReport(assessment, fileName, title, request);
            
            return fileName;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF report", e);
        }
    }
    
    /**
     * Generate mock scores for MVP (replace with real calculation)
     */
    private Map<String, Double> generateMockScores() {
        Map<String, Double> scores = new HashMap<>();
        scores.put("TECH", 75.0);
        scores.put("AI", 65.0);
        scores.put("COMMUNICATION", 80.0);
        scores.put("PORTFOLIO", 60.0);
        scores.put("OVERALL", 70.0);
        return scores;
    }
    
    /**
     * Generate mock recommendations for MVP
     */
    private List<String> generateMockRecommendations() {
        return Arrays.asList(
            "Focus on AI fundamentals through online courses",
            "Build practical AI projects for your portfolio",
            "Improve data analysis and visualization skills",
            "Develop stronger communication of technical concepts"
        );
    }
    
    /**
     * Generate mock gaps for MVP
     */
    private List<String> generateMockGaps() {
        return Arrays.asList(
            "Limited experience with machine learning frameworks",
            "Need stronger foundation in statistics and data science", 
            "Lack of practical AI project experience",
            "Could improve technical presentation skills"
        );
    }
}
