package com.aireadiness.telemetry.repository;

import com.aireadiness.telemetry.domain.Telemetry;
import com.aireadiness.telemetry.domain.Telemetry.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for Telemetry entity
 */
@Repository
public interface TelemetryRepository extends JpaRepository<Telemetry, Long> {
    
    /**
     * Count events by type after specific date
     */
    long countByEventTypeAndCreatedAtAfter(EventType eventType, LocalDateTime after);
    
    /**
     * Find events by assessment ID
     */
    List<Telemetry> findByAssessmentIdOrderByCreatedAtDesc(String assessmentId);
    
    /**
     * Count events by type in date range
     */
    @Query("SELECT COUNT(t) FROM Telemetry t WHERE t.eventType = :eventType AND t.createdAt BETWEEN :start AND :end")
    long countByEventTypeInDateRange(@Param("eventType") EventType eventType, 
                                    @Param("start") LocalDateTime start, 
                                    @Param("end") LocalDateTime end);
    
    /**
     * Get role selection counts for analytics
     */
    @Query("SELECT t.eventData, COUNT(t) FROM Telemetry t WHERE t.eventType = :eventType AND t.createdAt > :since GROUP BY t.eventData")
    List<Object[]> getRoleSelectionCounts(@Param("eventType") EventType eventType, @Param("since") LocalDateTime since);
}
