package com.aireadiness.telemetry.service;

import com.aireadiness.telemetry.domain.Telemetry;
import com.aireadiness.telemetry.domain.Telemetry.EventType;
import com.aireadiness.telemetry.repository.TelemetryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for tracking telemetry events according to PRD metrics
 * M1: % of cuestionarios iniciados que se completan (objetivo ≥60%)
 * M2: % of resultados con descarga PDF (≥50%)
 * M3: # evaluaciones/rol/semana (≥10 en demo)
 */
@Service
public class TelemetryService {
    
    private static final Logger logger = LoggerFactory.getLogger(TelemetryService.class);
    
    private final TelemetryRepository telemetryRepository;
    private final ObjectMapper objectMapper;
    
    public TelemetryService(TelemetryRepository telemetryRepository, ObjectMapper objectMapper) {
        this.telemetryRepository = telemetryRepository;
        this.objectMapper = objectMapper;
    }
    
    /**
     * Track assessment started event
     */
    public void trackAssessmentStarted(String assessmentId, String roleId, String version) {
        Map<String, Object> data = new HashMap<>();
        data.put("roleId", roleId);
        data.put("version", version);
        
        trackEvent(assessmentId, EventType.ASSESSMENT_STARTED, data);
        logger.info("Assessment started: {} for role: {}", assessmentId, roleId);
    }
    
    /**
     * Track assessment completed event
     */
    public void trackAssessmentCompleted(String assessmentId, String roleId, int answerCount, double globalScore) {
        Map<String, Object> data = new HashMap<>();
        data.put("roleId", roleId);
        data.put("answerCount", answerCount);
        data.put("globalScore", globalScore);
        
        trackEvent(assessmentId, EventType.ASSESSMENT_COMPLETED, data);
        logger.info("Assessment completed: {} with score: {}", assessmentId, globalScore);
    }
    
    /**
     * Track plan generated event
     */
    public void trackPlanGenerated(String assessmentId, int priorityCount) {
        Map<String, Object> data = new HashMap<>();
        data.put("priorityCount", priorityCount);
        
        trackEvent(assessmentId, EventType.PLAN_GENERATED, data);
        logger.info("Plan generated: {} with {} priorities", assessmentId, priorityCount);
    }
    
    /**
     * Track PDF download event
     */
    public void trackPdfDownloaded(String assessmentId) {
        trackEvent(assessmentId, EventType.PDF_DOWNLOADED, null);
        logger.info("PDF downloaded for assessment: {}", assessmentId);
    }
    
    /**
     * Track role selection event (for analytics)
     */
    public void trackRoleSelected(String roleId) {
        Map<String, Object> data = new HashMap<>();
        data.put("roleId", roleId);
        
        trackEvent(null, EventType.ROLE_SELECTED, data);
        logger.debug("Role selected: {}", roleId);
    }
    
    /**
     * Track API error event
     */
    public void trackApiError(String assessmentId, String error, String endpoint) {
        Map<String, Object> data = new HashMap<>();
        data.put("error", error);
        data.put("endpoint", endpoint);
        
        trackEvent(assessmentId, EventType.API_ERROR, data);
        logger.warn("API error tracked: {} for assessment: {}", error, assessmentId);
    }
    
    /**
     * Get basic metrics for monitoring
     */
    public TelemetryMetrics getMetrics(LocalDateTime since) {
        long assessmentsStarted = telemetryRepository.countByEventTypeAndCreatedAtAfter(
            EventType.ASSESSMENT_STARTED, since);
        
        long assessmentsCompleted = telemetryRepository.countByEventTypeAndCreatedAtAfter(
            EventType.ASSESSMENT_COMPLETED, since);
        
        long pdfDownloads = telemetryRepository.countByEventTypeAndCreatedAtAfter(
            EventType.PDF_DOWNLOADED, since);
        
        double completionRate = assessmentsStarted > 0 ? 
            (double) assessmentsCompleted / assessmentsStarted * 100 : 0.0;
        
        double pdfDownloadRate = assessmentsCompleted > 0 ? 
            (double) pdfDownloads / assessmentsCompleted * 100 : 0.0;
        
        return new TelemetryMetrics(
            assessmentsStarted,
            assessmentsCompleted,
            pdfDownloads,
            completionRate,
            pdfDownloadRate
        );
    }
    
    /**
     * Generic method to track any event
     */
    private void trackEvent(String assessmentId, EventType eventType, Map<String, Object> data) {
        try {
            String eventData = data != null ? objectMapper.writeValueAsString(data) : null;
            Telemetry telemetry = new Telemetry(assessmentId, eventType, eventData);
            telemetryRepository.save(telemetry);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize telemetry data for event: {}", eventType, e);
            // Save event without data to avoid losing the metric
            Telemetry telemetry = new Telemetry(assessmentId, eventType, null);
            telemetryRepository.save(telemetry);
        } catch (Exception e) {
            logger.error("Failed to save telemetry event: {}", eventType, e);
        }
    }
    
    /**
     * DTO for telemetry metrics
     */
    public static class TelemetryMetrics {
        private final long assessmentsStarted;
        private final long assessmentsCompleted;
        private final long pdfDownloads;
        private final double completionRate;
        private final double pdfDownloadRate;
        
        public TelemetryMetrics(long assessmentsStarted, long assessmentsCompleted, 
                               long pdfDownloads, double completionRate, double pdfDownloadRate) {
            this.assessmentsStarted = assessmentsStarted;
            this.assessmentsCompleted = assessmentsCompleted;
            this.pdfDownloads = pdfDownloads;
            this.completionRate = completionRate;
            this.pdfDownloadRate = pdfDownloadRate;
        }
        
        // Getters
        public long getAssessmentsStarted() { return assessmentsStarted; }
        public long getAssessmentsCompleted() { return assessmentsCompleted; }
        public long getPdfDownloads() { return pdfDownloads; }
        public double getCompletionRate() { return completionRate; }
        public double getPdfDownloadRate() { return pdfDownloadRate; }
    }
}
