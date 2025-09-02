package com.aireadiness.admin.service;

import com.aireadiness.admin.dto.*;
import com.aireadiness.catalog.domain.Question;
import com.aireadiness.catalog.domain.Role;
import com.aireadiness.catalog.domain.RoleQuestion;
import com.aireadiness.catalog.domain.RoleVersion;
import com.aireadiness.catalog.repository.QuestionRepository;
import com.aireadiness.catalog.repository.RoleRepository;
import com.aireadiness.catalog.repository.RoleVersionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AdminService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleVersionRepository roleVersionRepository;
    
    @Autowired
    private QuestionRepository questionRepository;

    // === ROLE MANAGEMENT ===

    public Role createRole(CreateRoleRequest request) {
        Role role = new Role();
        role.setName(request.name());
        role.setDescription(request.description());
        role.setCategory(request.category());
        role.setActive(true);
        role.setCreatedAt(LocalDateTime.now());
        
        Role savedRole = roleRepository.save(role);
        
        // Create initial version
        createInitialRoleVersion(savedRole);
        
        return savedRole;
    }

    public Optional<Role> updateRole(UpdateRoleRequest request) {
        return roleRepository.findById(request.id())
                .map(role -> {
                    role.setName(request.name());
                    role.setDescription(request.description());
                    role.setCategory(request.category());
                    role.setUpdatedAt(LocalDateTime.now());
                    return roleRepository.save(role);
                });
    }

    public boolean deleteRole(Long roleId) {
        return roleRepository.findById(roleId)
                .map(role -> {
                    role.setActive(false);
                    role.setUpdatedAt(LocalDateTime.now());
                    roleRepository.save(role);
                    return true;
                })
                .orElse(false);
    }

    public Page<Role> getAllRoles(Pageable pageable) {
        return roleRepository.findByActiveTrue(pageable);
    }

    public Optional<Role> getRoleById(Long roleId) {
        return roleRepository.findByIdAndActiveTrue(roleId);
    }

    public List<Role> getRolesByCategory(String category) {
        return roleRepository.findByActiveTrueAndCategory(category);
    }

    // === QUESTION MANAGEMENT ===

    public Question createQuestion(CreateQuestionRequest request) {
        Question question = new Question();
        question.setText(request.text());
        question.setType(request.type());
        question.setPillar(request.pillar());
        question.setOptions(request.options());
        question.setContext(request.context());
        question.setActive(true);
        question.setCreatedAt(LocalDateTime.now());
        
        return questionRepository.save(question);
    }

    public Optional<Question> updateQuestion(UpdateQuestionRequest request) {
        return questionRepository.findById(request.id())
                .map(question -> {
                    question.setText(request.text());
                    question.setType(request.type());
                    question.setPillar(request.pillar());
                    question.setOptions(request.options());
                    question.setContext(request.context());
                    question.setUpdatedAt(LocalDateTime.now());
                    return questionRepository.save(question);
                });
    }

    public boolean deleteQuestion(Long questionId) {
        return questionRepository.findById(questionId)
                .map(question -> {
                    question.setActive(false);
                    question.setUpdatedAt(LocalDateTime.now());
                    questionRepository.save(question);
                    return true;
                })
                .orElse(false);
    }

    public Page<Question> getAllQuestions(Pageable pageable) {
        return questionRepository.findByActiveTrue(pageable);
    }

    public Optional<Question> getQuestionById(Long questionId) {
        return questionRepository.findByIdAndActiveTrue(questionId);
    }

    // === ROLE-QUESTION ASSIGNMENT ===

    public boolean assignQuestionToRole(AssignQuestionToRoleRequest request) {
        Optional<Role> roleOpt = roleRepository.findById(request.roleId());
        Optional<Question> questionOpt = questionRepository.findById(request.questionId());
        
        if (roleOpt.isPresent() && questionOpt.isPresent()) {
            Role role = roleOpt.get();
            Question question = questionOpt.get();
            
            // Check if already assigned
            RoleVersion currentVersion = getCurrentVersion(role);
            if (currentVersion != null) {
                boolean alreadyAssigned = currentVersion.getQuestions().stream()
                        .anyMatch(rq -> rq.getQuestion().getId().equals(question.getId()));
                
                if (!alreadyAssigned) {
                    // Create new version with the additional question
                    createNewRoleVersionWithQuestion(role, question);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean removeQuestionFromRole(Long roleId, Long questionId) {
        return roleRepository.findById(roleId)
                .map(role -> {
                    // Create new version without the specified question
                    createNewRoleVersionWithoutQuestion(role, questionId);
                    return true;
                })
                .orElse(false);
    }

    public List<Question> getQuestionsForRole(Long roleId) {
        return roleRepository.findById(roleId)
                .map(role -> {
                    RoleVersion currentVersion = getCurrentVersion(role);
                    if (currentVersion != null) {
                        return currentVersion.getQuestions().stream()
                                .map(RoleQuestion::getQuestion)
                                .toList();
                    }
                    return List.<Question>of();
                })
                .orElse(List.of());
    }

    // === ROLE VERSION MANAGEMENT ===

    public List<RoleVersion> getRoleVersionHistory(Long roleId) {
        return roleVersionRepository.findByRoleIdOrderByVersionNumberDesc(roleId);
    }

    public boolean activateRoleVersion(Long roleId, Integer versionNumber) {
        Optional<RoleVersion> versionOpt = roleVersionRepository
                .findByRoleIdAndVersionNumber(roleId, versionNumber);
        
        if (versionOpt.isPresent()) {
            RoleVersion newActiveVersion = versionOpt.get();
            
            // Deactivate current version
            roleVersionRepository.findByRoleIdAndActiveTrue(roleId)
                    .ifPresent(currentVersion -> {
                        currentVersion.setActive(false);
                        roleVersionRepository.save(currentVersion);
                    });
            
            // Activate new version
            newActiveVersion.setActive(true);
            roleVersionRepository.save(newActiveVersion);
            
            return true;
        }
        return false;
    }

    // === PRIVATE HELPER METHODS ===

    private RoleVersion getCurrentVersion(Role role) {
        return roleVersionRepository.findByRoleIdAndActiveTrue(role.getId()).orElse(null);
    }

    private void createInitialRoleVersion(Role role) {
        RoleVersion version = new RoleVersion();
        version.setRole(role);
        version.setVersionNumber(1);
        version.setActive(true);
        version.setCreatedAt(LocalDateTime.now());
        roleVersionRepository.save(version);
    }

    private void createNewRoleVersionWithQuestion(Role role, Question question) {
        RoleVersion currentVersion = getCurrentVersion(role);
        if (currentVersion == null) return;
        
        // Deactivate current version
        currentVersion.setActive(false);
        roleVersionRepository.save(currentVersion);
        
        // Create new version
        RoleVersion newVersion = new RoleVersion();
        newVersion.setRole(role);
        newVersion.setVersionNumber(currentVersion.getVersionNumber() + 1);
        newVersion.setActive(true);
        newVersion.setCreatedAt(LocalDateTime.now());
        
        RoleVersion savedVersion = roleVersionRepository.save(newVersion);
        
        // Copy existing questions
        for (RoleQuestion existingRq : currentVersion.getQuestions()) {
            RoleQuestion newRq = new RoleQuestion();
            newRq.setRoleVersion(savedVersion);
            newRq.setQuestion(existingRq.getQuestion());
            savedVersion.getQuestions().add(newRq);
        }
        
        // Add new question
        RoleQuestion newRoleQuestion = new RoleQuestion();
        newRoleQuestion.setRoleVersion(savedVersion);
        newRoleQuestion.setQuestion(question);
        savedVersion.getQuestions().add(newRoleQuestion);
        
        roleVersionRepository.save(savedVersion);
    }

    private void createNewRoleVersionWithoutQuestion(Role role, Long questionId) {
        RoleVersion currentVersion = getCurrentVersion(role);
        if (currentVersion == null) return;
        
        // Deactivate current version
        currentVersion.setActive(false);
        roleVersionRepository.save(currentVersion);
        
        // Create new version
        RoleVersion newVersion = new RoleVersion();
        newVersion.setRole(role);
        newVersion.setVersionNumber(currentVersion.getVersionNumber() + 1);
        newVersion.setActive(true);
        newVersion.setCreatedAt(LocalDateTime.now());
        
        RoleVersion savedVersion = roleVersionRepository.save(newVersion);
        
        // Copy existing questions except the one to remove
        for (RoleQuestion existingRq : currentVersion.getQuestions()) {
            if (!existingRq.getQuestion().getId().equals(questionId)) {
                RoleQuestion newRq = new RoleQuestion();
                newRq.setRoleVersion(savedVersion);
                newRq.setQuestion(existingRq.getQuestion());
                savedVersion.getQuestions().add(newRq);
            }
        }
        
        roleVersionRepository.save(savedVersion);
    }
}
