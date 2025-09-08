package com.aireadiness.assessment;

import com.aireadiness.assessment.controller.AssessmentController;
import com.aireadiness.assessment.dto.AssessmentResponse;
import com.aireadiness.assessment.dto.CreateAssessmentRequest;
import com.aireadiness.assessment.dto.AnswerDto;
import com.aireadiness.assessment.service.AssessmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for AssessmentController
 */
@WebMvcTest(controllers = AssessmentController.class, 
            excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
            })
@AutoConfigureWebMvc
class AssessmentControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private AssessmentService assessmentService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void createAssessment_ShouldReturnAssessmentResponse_WhenValidRequest() throws Exception {
        // Arrange
        AnswerDto answer = new AnswerDto("q1", 3);
        
        CreateAssessmentRequest request = new CreateAssessmentRequest();
        request.setRoleId("software-engineer");
        request.setVersion("1.0");
        request.setEmail("test@example.com");
        request.setLocale("es-ES");
        request.setHoursPerWeek(10);
        request.setConsent(true);
        request.setAnswers(List.of(answer));
        
        AssessmentResponse response = new AssessmentResponse(
                "test-assessment-id", 
                Map.of("technical", 75.0, "ai", 65.0), 
                Collections.emptyList()
        );
        
        when(assessmentService.createAssessment(any(CreateAssessmentRequest.class)))
                .thenReturn(response);
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/assessments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.assessmentId").value("test-assessment-id"))
                .andExpect(jsonPath("$.scores.technical").value(75.0))
                .andExpect(jsonPath("$.scores.ai").value(65.0));
    }
    
    @Test
    void createAssessment_ShouldReturn400_WhenInvalidRequest() throws Exception {
        // Arrange
        CreateAssessmentRequest request = new CreateAssessmentRequest();
        // Missing required fields
        
        when(assessmentService.createAssessment(any(CreateAssessmentRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid request"));
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/assessments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
