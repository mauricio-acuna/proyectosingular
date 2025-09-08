package com.aireadiness.report.controller;

import com.aireadiness.report.dto.ReportRequest;
import com.aireadiness.report.dto.ReportResponse;
import com.aireadiness.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for report generation and management
 * Based on PRD section 1.2 - Basic Reporting Engine
 */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Reports", description = "API for generating and managing assessment reports")
public class ReportController {
    
    private final ReportService reportService;
    
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }
    
    /**
     * Generate comprehensive assessment report
     * POST /api/v1/assessments/{id}/report
     */
    @PostMapping("/assessments/{assessmentId}/report")
    @Operation(summary = "Generate assessment report", 
               description = "Generates a comprehensive PDF report with scores, analysis, and recommendations")
    public ResponseEntity<ReportResponse> generateReport(
            @PathVariable String assessmentId,
            @Valid @RequestBody(required = false) ReportRequest request) {
        
        try {
            ReportResponse response = reportService.generateReport(assessmentId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Download PDF report
     * GET /api/v1/reports/{reportId}/download
     */
    @GetMapping("/reports/{reportId}/download")
    @Operation(summary = "Download PDF report", 
               description = "Downloads the generated PDF report file")
    public ResponseEntity<Resource> downloadReport(@PathVariable String reportId) {
        
        try {
            Resource resource = reportService.downloadReport(reportId);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                           "attachment; filename=\"ai-readiness-report-" + reportId + ".pdf\"")
                    .body(resource);
                    
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get report metadata
     * GET /api/v1/reports/{reportId}
     */
    @GetMapping("/reports/{reportId}")
    @Operation(summary = "Get report metadata", 
               description = "Retrieves report metadata and status")
    public ResponseEntity<ReportResponse> getReport(@PathVariable String reportId) {
        
        try {
            ReportResponse response = reportService.getReportMetadata(reportId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Email report to user
     * POST /api/v1/reports/{reportId}/email
     */
    @PostMapping("/reports/{reportId}/email")
    @Operation(summary = "Email report", 
               description = "Sends the PDF report via email to the specified address")
    public ResponseEntity<Void> emailReport(
            @PathVariable String reportId,
            @Parameter(description = "Email address to send report to")
            @RequestParam String email) {
        
        try {
            reportService.emailReport(reportId, email);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get assessment summary for dashboard
     * GET /api/v1/assessments/{id}/summary
     */
    @GetMapping("/assessments/{assessmentId}/summary")
    @Operation(summary = "Get assessment summary", 
               description = "Returns key metrics and insights for dashboard display")
    public ResponseEntity<Object> getAssessmentSummary(@PathVariable String assessmentId) {
        
        try {
            var summary = reportService.getAssessmentSummary(assessmentId);
            return ResponseEntity.ok(summary);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
