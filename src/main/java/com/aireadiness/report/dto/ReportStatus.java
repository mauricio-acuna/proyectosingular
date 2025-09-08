package com.aireadiness.report.dto;

/**
 * Enum for report generation status
 */
public enum ReportStatus {
    PENDING("Pending"),
    GENERATING("Generating"),
    COMPLETED("Completed"),
    FAILED("Failed"),
    EXPIRED("Expired");
    
    private final String displayName;
    
    ReportStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
