package com.aireadiness.telemetry.controller;

import com.aireadiness.telemetry.service.TelemetryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * REST Controller for telemetry and metrics
 * Based on PRD metrics requirements
 */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Telemetry", description = "API for metrics and analytics")
public class TelemetryController {
    
    private final TelemetryService telemetryService;
    
    public TelemetryController(TelemetryService telemetryService) {
        this.telemetryService = telemetryService;
    }
    
    /**
     * Get telemetry metrics
     * GET /api/v1/metrics
     */
    @GetMapping("/metrics")
    @Operation(summary = "Get metrics", 
               description = "Returns telemetry metrics for monitoring dashboard")
    public ResponseEntity<TelemetryService.TelemetryMetrics> getMetrics(
            @Parameter(description = "Hours to look back (default: 24)")
            @RequestParam(defaultValue = "24") int hoursBack) {
        
        LocalDateTime since = LocalDateTime.now().minusHours(hoursBack);
        TelemetryService.TelemetryMetrics metrics = telemetryService.getMetrics(since);
        
        return ResponseEntity.ok(metrics);
    }
    
    /**
     * Track role selection (called from frontend)
     * POST /api/v1/analytics/role-selected
     */
    @PostMapping("/analytics/role-selected")
    @Operation(summary = "Track role selection", 
               description = "Records role selection for analytics")
    public ResponseEntity<Void> trackRoleSelection(@RequestBody RoleSelectionRequest request) {
        
        telemetryService.trackRoleSelected(request.getRoleId());
        return ResponseEntity.ok().build();
    }
    
    /**
     * Request DTO for role selection tracking
     */
    public static class RoleSelectionRequest {
        private String roleId;
        
        public RoleSelectionRequest() {}
        
        public String getRoleId() { return roleId; }
        public void setRoleId(String roleId) { this.roleId = roleId; }
    }
}
