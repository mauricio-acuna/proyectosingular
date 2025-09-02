package com.aireadiness.admin.dto;

import jakarta.validation.constraints.NotNull;

public record AssignQuestionToRoleRequest(
    @NotNull(message = "Role ID is required")
    Long roleId,
    
    @NotNull(message = "Question ID is required")
    Long questionId
) {
}
