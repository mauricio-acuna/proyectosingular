package com.aireadiness.common.domain;

/**
 * Question types according to PRD section 7.2
 */
public enum QuestionType {
    LIKERT,      // 1-5 scale self-perception
    MULTIPLE,    // Multiple choice with optional correct answers
    TEXT         // Brief text (enriches plan, doesn't score directly)
}
