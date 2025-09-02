package com.aireadiness.admin;

import com.aireadiness.admin.controller.AdminController;
import com.aireadiness.admin.dto.CreateQuestionRequest;
import com.aireadiness.admin.dto.CreateRoleRequest;
import com.aireadiness.admin.service.AdminService;
import com.aireadiness.catalog.domain.Question;
import com.aireadiness.catalog.domain.Role;
import com.aireadiness.common.domain.Pillar;
import com.aireadiness.common.domain.QuestionType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createRole_ShouldReturnCreatedRole() throws Exception {
        // Given
        CreateRoleRequest request = new CreateRoleRequest(
                "Backend Java Developer",
                "Senior Java developer with Spring Boot expertise",
                "Development"
        );

        Role mockRole = new Role();
        mockRole.setId(1L);
        mockRole.setName(request.name());
        mockRole.setDescription(request.description());
        mockRole.setCategory(request.category());
        mockRole.setActive(true);
        mockRole.setCreatedAt(LocalDateTime.now());

        when(adminService.createRole(any(CreateRoleRequest.class))).thenReturn(mockRole);

        // When & Then
        mockMvc.perform(post("/api/admin/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Backend Java Developer"))
                .andExpect(jsonPath("$.category").value("Development"));
    }

    @Test
    void createRole_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given - empty name should trigger validation error
        CreateRoleRequest request = new CreateRoleRequest("", "Description", "Category");

        // When & Then
        mockMvc.perform(post("/api/admin/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllRoles_ShouldReturnPagedRoles() throws Exception {
        // Given
        Role role1 = new Role();
        role1.setId(1L);
        role1.setName("Frontend Developer");
        role1.setCategory("Development");

        Role role2 = new Role();
        role2.setId(2L);
        role2.setName("Data Scientist");
        role2.setCategory("Data Science");

        Page<Role> mockPage = new PageImpl<>(List.of(role1, role2), PageRequest.of(0, 10), 2);
        when(adminService.getAllRoles(any())).thenReturn(mockPage);

        // When & Then
        mockMvc.perform(get("/api/admin/roles")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void getRoleById_WhenExists_ShouldReturnRole() throws Exception {
        // Given
        Role mockRole = new Role();
        mockRole.setId(1L);
        mockRole.setName("Backend Java Developer");
        mockRole.setCategory("Development");

        when(adminService.getRoleById(1L)).thenReturn(Optional.of(mockRole));

        // When & Then
        mockMvc.perform(get("/api/admin/roles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Backend Java Developer"));
    }

    @Test
    void getRoleById_WhenNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        when(adminService.getRoleById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/admin/roles/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createQuestion_ShouldReturnCreatedQuestion() throws Exception {
        // Given
        CreateQuestionRequest request = new CreateQuestionRequest(
                "What is your experience with Spring Boot?",
                QuestionType.MULTIPLE,
                Pillar.TECH,
                List.of("Beginner", "Intermediate", "Advanced", "Expert"),
                "Technical assessment"
        );

        Question mockQuestion = new Question();
        mockQuestion.setId(1L);
        mockQuestion.setText(request.text());
        mockQuestion.setType(request.type());
        mockQuestion.setPillar(request.pillar());
        mockQuestion.setOptions(request.options());
        mockQuestion.setContext(request.context());
        mockQuestion.setActive(true);
        mockQuestion.setCreatedAt(LocalDateTime.now());

        when(adminService.createQuestion(any(CreateQuestionRequest.class))).thenReturn(mockQuestion);

        // When & Then
        mockMvc.perform(post("/api/admin/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("What is your experience with Spring Boot?"))
                .andExpect(jsonPath("$.type").value("MULTIPLE"))
                .andExpect(jsonPath("$.pillar").value("TECH"));
    }

    @Test
    void deleteRole_WhenExists_ShouldReturnNoContent() throws Exception {
        // Given
        when(adminService.deleteRole(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/admin/roles/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteRole_WhenNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        when(adminService.deleteRole(999L)).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/admin/roles/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void healthCheck_ShouldReturnOk() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/admin/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Admin panel is operational"));
    }
}
