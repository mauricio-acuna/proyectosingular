package com.aireadiness.plan.service;

import com.aireadiness.assessment.domain.Assessment;
import com.aireadiness.assessment.service.AssessmentService;
import com.aireadiness.assessment.service.ScoringService;
import com.aireadiness.catalog.repository.RoleVersionRepository;
import com.aireadiness.plan.domain.Plan;
import com.aireadiness.plan.dto.PlanDto;
import com.aireadiness.plan.repository.PlanRepository;
import com.aireadiness.telemetry.service.TelemetryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service for managing AI-generated plans
 * Based on PRD section 12.3 - Plan API
 */
@Service
public class PlanService {
    
    private final PlanRepository planRepository;
    private final AssessmentService assessmentService;
    private final RoleVersionRepository roleVersionRepository;
    private final PlanGenerator planGenerator;
    private final TelemetryService telemetryService;
    private final ObjectMapper objectMapper;
    
    public PlanService(PlanRepository planRepository,
                      AssessmentService assessmentService,
                      RoleVersionRepository roleVersionRepository,
                      PlanGenerator planGenerator,
                      TelemetryService telemetryService,
                      ObjectMapper objectMapper) {
        this.planRepository = planRepository;
        this.assessmentService = assessmentService;
        this.roleVersionRepository = roleVersionRepository;
        this.planGenerator = planGenerator;
        this.telemetryService = telemetryService;
        this.objectMapper = objectMapper;
    }
    
    /**
     * Generate and store plan for assessment
     * Endpoint: POST /api/v1/assessments/{id}/plan
     */
    @Transactional
    public PlanDto generatePlan(String assessmentId, Integer hoursPerWeek) {
        
        // Check if plan already exists
        Optional<Plan> existingPlan = planRepository.findByAssessmentId(assessmentId);
        if (existingPlan.isPresent()) {
            return deserializePlan(existingPlan.get().getPlanJson());
        }
        
        // Get assessment with answers
        Assessment assessment = assessmentService.getAssessment(assessmentId);
        
        // Get role questions for scoring
        var roleVersionOpt = roleVersionRepository.findByRoleIdAndVersionWithQuestions(
            assessment.getRoleId(), assessment.getVersion()
        );
        
        if (roleVersionOpt.isEmpty()) {
            throw new IllegalArgumentException("Role version not found for assessment");
        }
        
        var roleVersion = roleVersionOpt.get();
        
        // Calculate scores for plan generation
        ScoringService scoringService = new ScoringService();
        ScoringService.AssessmentScores scores = scoringService.calculateScores(
            assessment.getAnswers(), 
            roleVersion.getQuestions()
        );
        
        // Use provided hours or default from assessment
        Integer planHours = hoursPerWeek != null ? hoursPerWeek : assessment.getHoursPerWeek();
        
        // Generate plan using AI provider
        PlanDto planDto = planGenerator.generatePlan(
            assessment.getRoleId(),
            scores.getPillarScores(),
            scores.getGaps(),
            planHours,
            assessment.getLocale()
        );
        
        // Validate plan
        if (!planGenerator.validatePlan(planDto)) {
            throw new RuntimeException("Generated plan failed validation");
        }
        
        // Store plan in database
        String planJson = serializePlan(planDto);
        Plan plan = new Plan(assessmentId, planJson);
        planRepository.save(plan);
        
        // Track plan generation
        telemetryService.trackPlanGenerated(assessmentId, planDto.getPriorities().size());
        
        return planDto;
    }
    
    /**
     * Get existing plan for assessment
     */
    public Optional<PlanDto> getPlan(String assessmentId) {
        return planRepository.findByAssessmentId(assessmentId)
                .map(plan -> deserializePlan(plan.getPlanJson()));
    }
    
    /**
     * Serialize plan to JSON for storage
     */
    private String serializePlan(PlanDto planDto) {
        try {
            return objectMapper.writeValueAsString(planDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize plan", e);
        }
    }
    
    /**
     * Deserialize plan from JSON
     */
    private PlanDto deserializePlan(String planJson) {
        try {
            return objectMapper.readValue(planJson, PlanDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize plan", e);
        }
    }
}
