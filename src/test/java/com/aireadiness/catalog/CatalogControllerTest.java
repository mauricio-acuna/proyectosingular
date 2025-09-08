package com.aireadiness.catalog;

import com.aireadiness.catalog.controller.CatalogController;
import com.aireadiness.catalog.service.CatalogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for CatalogController
 */
@WebMvcTest(controllers = CatalogController.class, 
            excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
            })
@AutoConfigureWebMvc
class CatalogControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private CatalogService catalogService;
    
    @Test
    void getAllRoles_ShouldReturnEmptyList_WhenNoRoles() throws Exception {
        when(catalogService.getAllRoles()).thenReturn(Collections.emptyList());
        
        mockMvc.perform(get("/api/v1/roles"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
    
    @Test
    void getQuestionsForRole_ShouldReturn404_WhenRoleNotFound() throws Exception {
        when(catalogService.getQuestionsForRole("non-existent", null, "es-ES"))
                .thenThrow(new IllegalArgumentException("Role not found"));
        
        mockMvc.perform(get("/api/v1/roles/non-existent/questions")
                        .header("Accept-Language", "es-ES"))
                .andExpect(status().isNotFound());
    }
}
