package com.loanapp.service;

import com.loanapp.dto.LoanApplicationRequest;
import com.loanapp.dto.LoanApplicationResponse;
import com.loanapp.dto.StatusUpdateRequest;
import com.loanapp.entity.*;
import com.loanapp.repository.LoanApplicationRepository;
import com.loanapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanServiceTest {

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private LoanService loanService;

    private User testUser;
    private LoanApplication loanApp;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setFullName("John Doe");
        testUser.setRole(Role.USER);

        loanApp = new LoanApplication();
        loanApp.setId(10L);
        loanApp.setUser(testUser);
        // Explicitly set status to prevent NPE during mapping in tests
        loanApp.setStatus(LoanStatus.SUBMITTED);
        loanApp.setAnnualIncome(new BigDecimal("500000"));
        loanApp.setLoanType("Personal Loan");
    }

    @Test
    void testSubmitLoanApplication_Success() throws Exception {
        // Arrange
        LoanApplicationRequest request = new LoanApplicationRequest();
        request.setAmount(new BigDecimal("10000"));
        request.setAnnualIncome(new BigDecimal("6000000"));
        request.setLoanType("Personal Loan");
        request.setCibilScore(800);

        User admin = new User();
        admin.setId(2L);
        admin.setRole(Role.ADMIN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByRole(Role.ADMIN)).thenReturn(Collections.singletonList(admin));

        // Fix: Ensure the saved object returned by the mock has a status
        when(loanApplicationRepository.save(any(LoanApplication.class))).thenAnswer(i -> {
            LoanApplication saved = i.getArgument(0);
            saved.setId(10L);
            if (saved.getStatus() == null) {
                saved.setStatus(LoanStatus.SUBMITTED);
            }
            return saved;
        });

        // Act
        LoanApplicationResponse response = loanService.submitLoanApplication(1L, request);

        // Assert
        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("SUBMITTED", response.getStatus());
        verify(notificationService).createNotification(eq(admin), anyString(), eq(10L));
    }

    @Test
    void testCalculateInterestRate_CibilImpact() throws Exception {
        // Arrange
        LoanApplicationRequest request = new LoanApplicationRequest();
        request.setAnnualIncome(new BigDecimal("1000000"));
        request.setLoanType("Home Loan");
        request.setCibilScore(600);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(loanApplicationRepository.save(any(LoanApplication.class))).thenAnswer(i -> {
            LoanApplication saved = i.getArgument(0);
            if (saved.getStatus() == null) saved.setStatus(LoanStatus.SUBMITTED);
            return saved;
        });

        // Act
        loanService.submitLoanApplication(1L, request);

        // Assert: Home Loan + < 2M income = 9.0% base. + 1% low CIBIL = 10.0%
        verify(loanApplicationRepository).save(argThat(app ->
                app.getInterestRate().compareTo(new BigDecimal("10.0")) == 0
        ));
    }

    @Test
    void testUpdateLoanApplicationStatus_Approved() throws Exception {
        // Arrange
        StatusUpdateRequest request = new StatusUpdateRequest();
        request.setStatus("APPROVED");

        when(loanApplicationRepository.findById(10L)).thenReturn(Optional.of(loanApp));
        when(loanApplicationRepository.save(any(LoanApplication.class))).thenReturn(loanApp);

        // Act
        LoanApplicationResponse response = loanService.updateLoanApplicationStatus(10L, request);

        // Assert
        assertEquals("APPROVED", response.getStatus());
        verify(notificationService).createNotification(any(), contains("APPROVED"), eq(10L));
    }

    @Test
    void testUpdateLoanApplicationStatus_Rejected() throws Exception {
        // Arrange
        StatusUpdateRequest request = new StatusUpdateRequest();
        request.setStatus("REJECTED");
        request.setRejectionReason("Income too low");

        when(loanApplicationRepository.findById(10L)).thenReturn(Optional.of(loanApp));
        when(loanApplicationRepository.save(any(LoanApplication.class))).thenReturn(loanApp);

        // Act
        LoanApplicationResponse response = loanService.updateLoanApplicationStatus(10L, request);

        // Assert
        assertEquals("REJECTED", response.getStatus());
        verify(notificationService).createNotification(any(), contains("REJECTED"), eq(10L));
    }

    @Test
    void testGetLoanApplicationById_Success() throws Exception {
        when(loanApplicationRepository.findById(10L)).thenReturn(Optional.of(loanApp));

        LoanApplicationResponse response = loanService.getLoanApplicationById(10L);

        assertNotNull(response);
        assertEquals(10L, response.getId());
    }

    @Test
    void testGetLoanApplicationById_NotFound() {
        when(loanApplicationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> loanService.getLoanApplicationById(99L));
    }

    @Test
    void testGetUserLoanApplications_Success() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(loanApplicationRepository.findByUser(testUser)).thenReturn(Collections.singletonList(loanApp));

        List<LoanApplicationResponse> result = loanService.getUserLoanApplications(1L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testGetLoanApplicationsByStatus_Success() throws Exception {
        when(loanApplicationRepository.findByStatus(LoanStatus.SUBMITTED))
                .thenReturn(Collections.singletonList(loanApp));

        List<LoanApplicationResponse> result = loanService.getLoanApplicationsByStatus("SUBMITTED");

        assertEquals(1, result.size());
    }
}