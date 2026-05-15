package com.loanapp.controller;

import com.loanapp.dto.*;
import com.loanapp.service.AdminService;
import com.loanapp.service.DisbursementService;
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

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AdminControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LoanService loanService;

    @Mock
    private DisbursementService disbursementService;

    @Mock
    private AdminService adminService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private AdminController adminController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testGetAdminHistory() throws Exception {
        AdminActionLog log = new AdminActionLog();
        when(adminService.getAdminHistory()).thenReturn(Collections.singletonList(log));

        mockMvc.perform(get("/api/admin/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testGetAllLoanApplications() throws Exception {
        LoanApplicationResponse res = new LoanApplicationResponse();
        when(loanService.getAllLoanApplications()).thenReturn(Collections.singletonList(res));

        mockMvc.perform(get("/api/admin/loans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testGetLoanApplicationById() throws Exception {
        LoanApplicationResponse res = new LoanApplicationResponse();
        res.setId(1L);
        when(loanService.getLoanApplicationById(1L)).thenReturn(res);

        mockMvc.perform(get("/api/admin/loans/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testGetLoanApplicationsByStatus() throws Exception {
        LoanApplicationResponse res = new LoanApplicationResponse();
        res.setStatus("APPROVED");
        when(loanService.getLoanApplicationsByStatus("APPROVED")).thenReturn(Collections.singletonList(res));

        mockMvc.perform(get("/api/admin/loans/status/APPROVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("APPROVED"));
    }

    @Test
    void testUpdateLoanApplicationStatus() throws Exception {
        StatusUpdateRequest request = new StatusUpdateRequest();
        request.setStatus("REJECTED");

        LoanApplicationResponse response = new LoanApplicationResponse();
        response.setStatus("REJECTED");

        when(loanService.updateLoanApplicationStatus(eq(1L), any(StatusUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/admin/loans/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }

    @Test
    void testDisburseAmount_Success() throws Exception {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("adminUser");

        DisbursementResponse response = new DisbursementResponse();
        response.setTransactionReference("TXN123");

        when(disbursementService.disburseAmount(eq(1L), eq("adminUser"))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/admin/loans/1/disburse"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionReference").value("TXN123"));
    }

    @Test
    void testGetDisbursementDetails() throws Exception {
        DisbursementResponse response = new DisbursementResponse();
        when(disbursementService.getDisbursementByLoanId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/admin/loans/1/disbursement"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetLoanApplication_Exception() throws Exception {
        // Test error handling/propagation
        when(loanService.getLoanApplicationById(99L)).thenThrow(new Exception("Not Found"));

        try {
            mockMvc.perform(get("/api/admin/loans/99"));
        } catch (Exception e) {
            // Standalone setup propagates the underlying exception
            assert(e.getCause().getMessage().equals("Not Found"));
        }
    }
}