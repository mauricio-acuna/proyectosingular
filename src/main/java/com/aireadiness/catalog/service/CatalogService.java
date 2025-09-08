package com.aireadiness.catalog.service;

import com.aireadiness.catalog.domain.Role;
import com.aireadiness.catalog.domain.RoleQuestion;
import com.aireadiness.catalog.domain.RoleVersion;
import com.aireadiness.catalog.dto.QuestionDto;
import com.aireadiness.catalog.dto.RoleDto;
import com.aireadiness.catalog.repository.RoleRepository;
import com.aireadiness.catalog.repository.RoleVersionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for catalog operations (roles and questions)
 * Based on PRD section 12 - API contracts
 */
@Service
public class CatalogService {
    
    private final RoleRepository roleRepository;
    private final RoleVersionRepository roleVersionRepository;
    
    public CatalogService(RoleRepository roleRepository, RoleVersionRepository roleVersionRepository) {
        this.roleRepository = roleRepository;
        this.roleVersionRepository = roleVersionRepository;
    }
    
    /**
     * Get all roles with published versions
     * Endpoint: GET /api/v1/roles
     */
    public List<RoleDto> getAllRoles() {
        return roleRepository.findAllWithPublishedVersions().stream()
                .map(this::toRoleDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get questions for a specific role and version
     * Endpoint: GET /api/v1/roles/{id}/questions?version=X
     */
    public List<QuestionDto> getQuestionsForRole(String roleId, String version, String locale) {
        Optional<RoleVersion> roleVersionOpt;
        
        if (version != null) {
            roleVersionOpt = roleVersionRepository.findByRoleIdAndVersionWithQuestions(roleId, version);
        } else {
            roleVersionOpt = roleVersionRepository.findPublishedByRoleId(roleId);
            if (roleVersionOpt.isPresent()) {
                // Fetch with questions
                roleVersionOpt = roleVersionRepository.findByRoleIdAndVersionWithQuestions(
                    roleId, roleVersionOpt.get().getVersionNumber().toString()
                );
            }
        }
        
        if (roleVersionOpt.isEmpty()) {
            throw new IllegalArgumentException("Role or version not found: " + roleId + " v" + version);
        }
        
        return roleVersionOpt.get().getQuestions().stream()
                .sorted((a, b) -> a.getOrder().compareTo(b.getOrder()))
                .map(rq -> toQuestionDto(rq, locale))
                .collect(Collectors.toList());
    }
    
    private RoleDto toRoleDto(Role role) {
        // Get published version
        Optional<RoleVersion> publishedVersion = role.getVersions().stream()
                .filter(RoleVersion::getActive)
                .findFirst();
        
        String version = publishedVersion.map(rv -> rv.getVersionNumber().toString()).orElse("1");
        
        return new RoleDto(role.getId().toString(), role.getName(), version, role.getDescription());
    }
    
    private QuestionDto toQuestionDto(RoleQuestion roleQuestion, String locale) {
        var question = roleQuestion.getQuestion();
        String text = question.getText(); // Use single text for MVP, could add i18n later
        
        return new QuestionDto(
            question.getId().toString(),
            text,
            question.getType(),
            question.getPillar(),
            roleQuestion.getWeight(),
            question.getContext(), // Use context as help text
            roleQuestion.getOrder()
        );
    }
}
