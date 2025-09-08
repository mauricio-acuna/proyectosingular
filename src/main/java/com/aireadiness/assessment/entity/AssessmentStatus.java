package com.aireadiness.assessment.entity;

public enum AssessmentStatus {
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    ABANDONED("Abandoned"),
    EXPIRED("Expired");
    
    private final String displayName;
    
    AssessmentStatus(String displayName) {
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
