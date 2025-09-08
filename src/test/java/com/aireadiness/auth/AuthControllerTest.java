package com.aireadiness.auth;

import com.aireadiness.auth.controller.AuthController;
import com.aireadiness.auth.dto.AuthResponse;
import com.aireadiness.auth.dto.LoginRequest;
import com.aireadiness.auth.dto.RegisterRequest;
import com.aireadiness.auth.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for AuthController
 */
@WebMvcTest(controllers = AuthController.class, 
            excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
            })
@AutoConfigureWebMvc
class AuthControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private AuthService authService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void register_ShouldReturnAuthResponse_WhenValidRequest() throws Exception {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setFirstName("Test");
        request.setLastName("User");
        
        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo();
        userInfo.setId("user-id");
        userInfo.setUsername("testuser");
        userInfo.setEmail("test@example.com");
        
        AuthResponse response = new AuthResponse(
                "access-token", 
                "refresh-token", 
                3600, 
                userInfo
        );
        
        when(authService.register(any(RegisterRequest.class))).thenReturn(response);
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.expiresIn").value(3600))
                .andExpect(jsonPath("$.user.username").value("testuser"));
    }
    
    @Test
    void login_ShouldReturnAuthResponse_WhenValidCredentials() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUsernameOrEmail("testuser");
        request.setPassword("password123");
        
        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo();
        userInfo.setId("user-id");
        userInfo.setUsername("testuser");
        userInfo.setEmail("test@example.com");
        
        AuthResponse response = new AuthResponse(
                "access-token", 
                "refresh-token", 
                3600, 
                userInfo
        );
        
        when(authService.login(any(LoginRequest.class))).thenReturn(response);
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.user.username").value("testuser"));
    }
    
    @Test
    void register_ShouldReturn400_WhenInvalidRequest() throws Exception {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        // Missing required fields
        
        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new IllegalArgumentException("Username is required"));
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void checkUsernameAvailability_ShouldReturnAvailability() throws Exception {
        // Arrange
        when(authService.isUsernameAvailable("newuser")).thenReturn(true);
        
        // Act & Assert
        mockMvc.perform(get("/api/v1/auth/check-username/newuser"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.available").value(true));
    }
    
    @Test
    void checkEmailAvailability_ShouldReturnAvailability() throws Exception {
        // Arrange
        when(authService.isEmailAvailable("new@example.com")).thenReturn(true);
        
        // Act & Assert
        mockMvc.perform(get("/api/v1/auth/check-email/new@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.available").value(true));
    }
    
    @Test
    void health_ShouldReturnHealthStatus() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/auth/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("Authentication Service"));
    }
}
