package com.aireadiness.plan;

import com.aireadiness.plan.dto.PlanDto;
import com.aireadiness.plan.service.MockPlanGenerator;
import com.aireadiness.common.domain.Pillar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for MockPlanGenerator
 */
class MockPlanGeneratorTest {
    
    private MockPlanGenerator planGenerator;
    
    @BeforeEach
    void setUp() {
        planGenerator = new MockPlanGenerator();
    }
    
    @Test
    void generatePlan_WithTechGaps_ShouldIncludeTechPriority() {
        // Given: Low tech score
        Map<Pillar, Double> scores = new HashMap<>();
        scores.put(Pillar.TECH, 50.0); // Below 70 threshold
        scores.put(Pillar.AI, 80.0);
        scores.put(Pillar.COMMUNICATION, 75.0);
        scores.put(Pillar.PORTFOLIO, 85.0);
        
        // When
        PlanDto plan = planGenerator.generatePlan("backend-java", scores, Arrays.asList("q1"), 8, "es-ES");
        
        // Then
        assertNotNull(plan);
        assertNotNull(plan.getPriorities());
        assertFalse(plan.getPriorities().isEmpty());
        assertTrue(plan.getPriorities().stream()
                .anyMatch(p -> p.getName().toLowerCase().contains("técnicas") || 
                              p.getName().toLowerCase().contains("technical")));
    }
    
    @Test
    void generatePlan_WithAIGaps_ShouldIncludeAIPriority() {
        // Given: Low AI score
        Map<Pillar, Double> scores = new HashMap<>();
        scores.put(Pillar.TECH, 80.0);
        scores.put(Pillar.AI, 40.0); // Below 70 threshold
        scores.put(Pillar.COMMUNICATION, 75.0);
        scores.put(Pillar.PORTFOLIO, 85.0);
        
        // When
        PlanDto plan = planGenerator.generatePlan("backend-java", scores, Arrays.asList("q-ai"), 8, "en-US");
        
        // Then
        assertNotNull(plan);
        assertTrue(plan.getPriorities().stream()
                .anyMatch(p -> p.getName().toLowerCase().contains("ai") || 
                              p.getName().toLowerCase().contains("ia")));
    }
    
    @Test
    void generatePlan_NoGaps_ShouldCreateGeneralPlan() {
        // Given: All good scores
        Map<Pillar, Double> scores = new HashMap<>();
        scores.put(Pillar.TECH, 85.0);
        scores.put(Pillar.AI, 80.0);
        scores.put(Pillar.COMMUNICATION, 75.0);
        scores.put(Pillar.PORTFOLIO, 85.0);
        
        // When
        PlanDto plan = planGenerator.generatePlan("backend-java", scores, Arrays.asList(), 8, "es-ES");
        
        // Then
        assertNotNull(plan);
        assertEquals(1, plan.getPriorities().size()); // General improvement priority
    }
    
    @Test
    void validatePlan_ValidPlan_ShouldReturnTrue() {
        // Given: Valid plan
        PlanDto.TaskDto task = new PlanDto.TaskDto("Task", "Deliverable", "Resource");
        PlanDto.MilestonesDto milestones = new PlanDto.MilestonesDto(Arrays.asList(task), Arrays.asList(), Arrays.asList());
        PlanDto.PriorityDto priority = new PlanDto.PriorityDto("Priority", "Why", milestones, Arrays.asList());
        PlanDto plan = new PlanDto("Summary", 8, Arrays.asList(priority));
        
        // When
        boolean isValid = planGenerator.validatePlan(plan);
        
        // Then
        assertTrue(isValid);
    }
    
    @Test
    void validatePlan_TooManyPriorities_ShouldReturnFalse() {
        // Given: Plan with too many priorities
        PlanDto.TaskDto task = new PlanDto.TaskDto("Task", "Deliverable", "Resource");
        PlanDto.MilestonesDto milestones = new PlanDto.MilestonesDto(Arrays.asList(task), Arrays.asList(), Arrays.asList());
        
        // Create 6 priorities (exceeds max of 5)
        PlanDto.PriorityDto[] priorities = new PlanDto.PriorityDto[6];
        for (int i = 0; i < 6; i++) {
            priorities[i] = new PlanDto.PriorityDto("Priority " + i, "Why", milestones, Arrays.asList());
        }
        
        PlanDto plan = new PlanDto("Summary", 8, Arrays.asList(priorities));
        
        // When
        boolean isValid = planGenerator.validatePlan(plan);
        
        // Then
        assertFalse(isValid);
    }
    
    @Test
    void validatePlan_EmptyPriorities_ShouldReturnFalse() {
        // Given: Plan with no priorities
        PlanDto plan = new PlanDto("Summary", 8, Arrays.asList());
        
        // When
        boolean isValid = planGenerator.validatePlan(plan);
        
        // Then
        assertFalse(isValid);
    }
}
