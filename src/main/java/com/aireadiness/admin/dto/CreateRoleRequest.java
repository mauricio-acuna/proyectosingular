package com.aireadiness.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateRoleRequest(
    @NotBlank(message = "Role name is required")
    @Size(max = 100, message = "Role name must be less than 100 characters")
    String name,
    
    @Size(max = 500, message = "Description must be less than 500 characters")
    String description,
    
    @Size(max = 50, message = "Category must be less than 50 characters")
    String category
) {
}
