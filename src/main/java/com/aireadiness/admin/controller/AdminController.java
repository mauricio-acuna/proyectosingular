package com.aireadiness.admin.controller;

import com.aireadiness.admin.dto.*;
import com.aireadiness.admin.service.AdminService;
import com.aireadiness.catalog.domain.Question;
import com.aireadiness.catalog.domain.Role;
import com.aireadiness.catalog.domain.RoleVersion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin Panel", description = "Administrative endpoints for managing roles, questions, and platform content")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // === ROLE MANAGEMENT ===

    @Operation(summary = "Create a new role", description = "Creates a new professional role with initial version")
    @PostMapping("/roles")
    public ResponseEntity<Role> createRole(@Valid @RequestBody CreateRoleRequest request) {
        Role role = adminService.createRole(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(role);
    }

    @Operation(summary = "Update existing role", description = "Updates role information (name, description, category)")
    @PutMapping("/roles")
    public ResponseEntity<Role> updateRole(@Valid @RequestBody UpdateRoleRequest request) {
        return adminService.updateRole(request)
                .map(role -> ResponseEntity.ok(role))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete role", description = "Soft deletes a role by setting active=false")
    @DeleteMapping("/roles/{roleId}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long roleId) {
        boolean deleted = adminService.deleteRole(roleId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Get all roles", description = "Returns paginated list of all active roles")
    @GetMapping("/roles")
    public ResponseEntity<Page<Role>> getAllRoles(Pageable pageable) {
        Page<Role> roles = adminService.getAllRoles(pageable);
        return ResponseEntity.ok(roles);
    }

    @Operation(summary = "Get role by ID", description = "Returns role details by ID")
    @GetMapping("/roles/{roleId}")
    public ResponseEntity<Role> getRoleById(@PathVariable Long roleId) {
        return adminService.getRoleById(roleId)
                .map(role -> ResponseEntity.ok(role))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get roles by category", description = "Returns all roles in a specific category")
    @GetMapping("/roles/category/{category}")
    public ResponseEntity<List<Role>> getRolesByCategory(@PathVariable String category) {
        List<Role> roles = adminService.getRolesByCategory(category);
        return ResponseEntity.ok(roles);
    }

    // === QUESTION MANAGEMENT ===

    @Operation(summary = "Create a new question", description = "Creates a new question with specified type and pillar")
    @PostMapping("/questions")
    public ResponseEntity<Question> createQuestion(@Valid @RequestBody CreateQuestionRequest request) {
        Question question = adminService.createQuestion(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(question);
    }

    @Operation(summary = "Update existing question", description = "Updates question content, type, pillar, or options")
    @PutMapping("/questions")
    public ResponseEntity<Question> updateQuestion(@Valid @RequestBody UpdateQuestionRequest request) {
        return adminService.updateQuestion(request)
                .map(question -> ResponseEntity.ok(question))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete question", description = "Soft deletes a question by setting active=false")
    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long questionId) {
        boolean deleted = adminService.deleteQuestion(questionId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Get all questions", description = "Returns paginated list of all active questions")
    @GetMapping("/questions")
    public ResponseEntity<Page<Question>> getAllQuestions(Pageable pageable) {
        Page<Question> questions = adminService.getAllQuestions(pageable);
        return ResponseEntity.ok(questions);
    }

    @Operation(summary = "Get question by ID", description = "Returns question details by ID")
    @GetMapping("/questions/{questionId}")
    public ResponseEntity<Question> getQuestionById(@PathVariable Long questionId) {
        return adminService.getQuestionById(questionId)
                .map(question -> ResponseEntity.ok(question))
                .orElse(ResponseEntity.notFound().build());
    }

    // === ROLE-QUESTION ASSIGNMENT ===

    @Operation(summary = "Assign question to role", description = "Creates new role version with additional question")
    @PostMapping("/roles/questions/assign")
    public ResponseEntity<Void> assignQuestionToRole(@Valid @RequestBody AssignQuestionToRoleRequest request) {
        boolean assigned = adminService.assignQuestionToRole(request);
        return assigned ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @Operation(summary = "Remove question from role", description = "Creates new role version without specified question")
    @DeleteMapping("/roles/{roleId}/questions/{questionId}")
    public ResponseEntity<Void> removeQuestionFromRole(@PathVariable Long roleId, @PathVariable Long questionId) {
        boolean removed = adminService.removeQuestionFromRole(roleId, questionId);
        return removed ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Get questions for role", description = "Returns all questions in the current active version of a role")
    @GetMapping("/roles/{roleId}/questions")
    public ResponseEntity<List<Question>> getQuestionsForRole(@PathVariable Long roleId) {
        List<Question> questions = adminService.getQuestionsForRole(roleId);
        return ResponseEntity.ok(questions);
    }

    // === ROLE VERSION MANAGEMENT ===

    @Operation(summary = "Get role version history", description = "Returns all versions of a role ordered by version number desc")
    @GetMapping("/roles/{roleId}/versions")
    public ResponseEntity<List<RoleVersion>> getRoleVersionHistory(@PathVariable Long roleId) {
        List<RoleVersion> versions = adminService.getRoleVersionHistory(roleId);
        return ResponseEntity.ok(versions);
    }

    @Operation(summary = "Activate role version", description = "Sets specified version as active and deactivates current version")
    @PostMapping("/roles/{roleId}/versions/{versionNumber}/activate")
    public ResponseEntity<Void> activateRoleVersion(@PathVariable Long roleId, @PathVariable Integer versionNumber) {
        boolean activated = adminService.activateRoleVersion(roleId, versionNumber);
        return activated ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // === HEALTH CHECK ===

    @Operation(summary = "Admin panel health check", description = "Simple endpoint to verify admin panel is working")
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Admin panel is operational");
    }
}
