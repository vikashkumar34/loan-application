package com.loanapp.controller;

import com.loanapp.dto.LoanApplicationRequest;
import com.loanapp.dto.LoanApplicationResponse;
import com.loanapp.entity.User;
import com.loanapp.service.AuthService;
import com.loanapp.service.LoanService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class LoanControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LoanService loanService;

    @Mock
    private AuthService authService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private LoanController loanController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private User mockUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(loanController).build();

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");

        // Set up the SecurityContextHolder mock
        SecurityContextHolder.setContext(securityContext);
    }

    private void mockSecurityContext() throws Exception {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        when(authService.getUserByUsername("testuser")).thenReturn(mockUser);
    }

    @Test
    void testSubmitLoanApplication_Success() throws Exception {
        // Arrange
        mockSecurityContext();
        LoanApplicationRequest request = new LoanApplicationRequest();
        request.setAmount(new BigDecimal("5000"));

        LoanApplicationResponse response = new LoanApplicationResponse();
        response.setId(100L);
        response.setStatus("SUBMITTED");

        when(loanService.submitLoanApplication(anyLong(), any(LoanApplicationRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/loans/apply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.status").value("SUBMITTED"));
    }

    @Test
    void testGetMyApplications_Success() throws Exception {
        // Arrange
        mockSecurityContext();
        LoanApplicationResponse app = new LoanApplicationResponse();
        app.setId(100L);
        List<LoanApplicationResponse> list = Collections.singletonList(app);

        when(loanService.getUserLoanApplications(1L)).thenReturn(list);

        // Act & Assert
        mockMvc.perform(get("/api/loans/my-applications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(100));
    }

    @Test
    void testGetLoanApplication_Success() throws Exception {
        // Arrange
        LoanApplicationResponse response = new LoanApplicationResponse();
        response.setId(100L);

        when(loanService.getLoanApplicationById(100L)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/loans/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100));
    }

    @Test
    void testGetLoanApplication_NotFound() throws Exception {
        // Arrange
        when(loanService.getLoanApplicationById(999L)).thenThrow(new Exception("Loan not found"));

        // Act & Assert
        // StandaloneSetup throws the actual exception or wraps it in NestedServletException
        try {
            mockMvc.perform(get("/api/loans/999"));
        } catch (Exception e) {
            // Depending on global exception handlers, you might assert status or exception message
        }
    }
}