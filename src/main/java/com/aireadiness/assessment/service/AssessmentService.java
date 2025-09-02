package com.aireadiness.assessment.service;

import com.aireadiness.assessment.domain.Answer;
import com.aireadiness.assessment.domain.Assessment;
import com.aireadiness.assessment.dto.AnswerDto;
import com.aireadiness.assessment.dto.AssessmentResponse;
import com.aireadiness.assessment.dto.CreateAssessmentRequest;
import com.aireadiness.assessment.repository.AssessmentRepository;
import com.aireadiness.catalog.domain.RoleQuestion;
import com.aireadiness.catalog.repository.RoleVersionRepository;
import com.aireadiness.telemetry.service.TelemetryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for managing assessments
 * Based on PRD section 12.2 - Assessment API
 */
@Service
public class AssessmentService {
    
    private final AssessmentRepository assessmentRepository;
    private final RoleVersionRepository roleVersionRepository;
    private final ScoringService scoringService;
    private final TelemetryService telemetryService;
    
    public AssessmentService(AssessmentRepository assessmentRepository,
                           RoleVersionRepository roleVersionRepository,
                           ScoringService scoringService,
                           TelemetryService telemetryService) {
        this.assessmentRepository = assessmentRepository;
        this.roleVersionRepository = roleVersionRepository;
        this.scoringService = scoringService;
        this.telemetryService = telemetryService;
    }
    
    /**
     * Create new assessment and calculate scores
     * Endpoint: POST /api/v1/assessments
     */
    @Transactional
    public AssessmentResponse createAssessment(CreateAssessmentRequest request) {
        
        // Validate role and version exist
        var roleVersionOpt = roleVersionRepository.findByRoleIdAndVersionWithQuestions(
            request.getRoleId(), request.getVersion()
        );
        
        if (roleVersionOpt.isEmpty()) {
            throw new IllegalArgumentException("Role or version not found: " + 
                request.getRoleId() + " v" + request.getVersion());
        }
        
        var roleVersion = roleVersionOpt.get();
        
        // Generate assessment ID (using UUID for MVP, could use ULID in production)
        String assessmentId = "a_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        
        // Create assessment entity
        Assessment assessment = new Assessment(
            assessmentId,
            request.getRoleId(),
            request.getVersion(),
            request.getLocale(),
            request.getHoursPerWeek()
        );
        
        assessment.setConsent(request.getConsent());
        assessment.setPrevAssessmentId(request.getPrevAssessmentId());
        
        // Hash email if provided (GDPR compliance)
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            assessment.setEmailHash(hashEmail(request.getEmail()));
        }
        
        // Create answer entities
        List<Answer> answers = request.getAnswers().stream()
                .map(dto -> new Answer(
                    assessment,
                    dto.getQuestionId(),
                    dto.getValue(),
                    dto.getText()
                ))
                .collect(Collectors.toList());
        
        assessment.setAnswers(answers);
        
        // Save assessment
        assessmentRepository.save(assessment);
        
        // Track assessment started
        telemetryService.trackAssessmentStarted(assessmentId, request.getRoleId(), request.getVersion());
        
        // Calculate scores
        List<RoleQuestion> roleQuestions = roleVersion.getQuestions();
        ScoringService.AssessmentScores scores = scoringService.calculateScores(answers, roleQuestions);
        
        // Track assessment completed
        telemetryService.trackAssessmentCompleted(
            assessmentId, 
            request.getRoleId(), 
            answers.size(), 
            scores.getGlobalScore()
        );
        
        // Return response
        return new AssessmentResponse(
            assessmentId,
            scores.getPillarScoresAsMap(),
            scores.getGaps()
        );
    }
    
    /**
     * Get assessment by ID
     */
    public Assessment getAssessment(String assessmentId) {
        return assessmentRepository.findByIdWithAnswers(assessmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assessment not found: " + assessmentId));
    }
    
    /**
     * Hash email for privacy compliance
     */
    private String hashEmail(String email) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(email.toLowerCase().trim().getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
