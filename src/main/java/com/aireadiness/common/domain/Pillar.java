package com.aireadiness.common.domain;

/**
 * Enum for the four evaluation pillars according to PRD section 7.1
 */
public enum Pillar {
    TECH("Técnico", 0.35),
    AI("IA aplicada", 0.35),
    COMMUNICATION("Comunicación", 0.15),
    PORTFOLIO("Portafolio/Entrega", 0.15);

    private final String displayName;
    private final double defaultWeight;

    Pillar(String displayName, double defaultWeight) {
        this.displayName = displayName;
        this.defaultWeight = defaultWeight;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getDefaultWeight() {
        return defaultWeight;
    }
}
