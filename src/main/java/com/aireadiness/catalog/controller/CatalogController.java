package com.aireadiness.catalog.controller;

import com.aireadiness.catalog.dto.QuestionDto;
import com.aireadiness.catalog.dto.RoleDto;
import com.aireadiness.catalog.service.CatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for catalog operations (roles and questions)
 * Based on PRD section 12 - API contracts
 */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Catalog", description = "API for roles and questions management")
public class CatalogController {
    
    private final CatalogService catalogService;
    
    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }
    
    /**
     * Get all available roles with published versions
     * GET /api/v1/roles
     */
    @GetMapping("/roles")
    @Operation(summary = "Get all roles", description = "Returns all roles that have published versions")
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        List<RoleDto> roles = catalogService.getAllRoles();
        return ResponseEntity.ok(roles);
    }
    
    /**
     * Get questions for a specific role
     * GET /api/v1/roles/{id}/questions?version=X
     */
    @GetMapping("/roles/{roleId}/questions")
    @Operation(summary = "Get questions for role", description = "Returns questions for a specific role and version")
    public ResponseEntity<List<QuestionDto>> getQuestionsForRole(
            @PathVariable String roleId,
            @Parameter(description = "Version of the role (optional, defaults to published version)")
            @RequestParam(required = false) String version,
            @Parameter(description = "Locale for question text (es-ES or en-US)")
            @RequestHeader(value = "Accept-Language", defaultValue = "es-ES") String locale) {
        
        try {
            List<QuestionDto> questions = catalogService.getQuestionsForRole(roleId, version, locale);
            return ResponseEntity.ok(questions);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
