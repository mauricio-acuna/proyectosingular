package com.aireadiness.admin.dto;

import com.aireadiness.common.domain.Pillar;
import com.aireadiness.common.domain.QuestionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdateQuestionRequest(
    @NotNull(message = "Question ID is required")
    Long id,
    
    @NotBlank(message = "Question text is required")
    @Size(max = 1000, message = "Question text must be less than 1000 characters")
    String text,
    
    @NotNull(message = "Question type is required")
    QuestionType type,
    
    @NotNull(message = "Pillar is required")
    Pillar pillar,
    
    List<String> options,
    
    @Size(max = 500, message = "Context must be less than 500 characters")
    String context
) {
}
