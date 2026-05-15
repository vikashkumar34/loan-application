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
import java.util.Arrays;
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
        loanApp.setStatus(LoanStatus.SUBMITTED);
        loanApp.setAnnualIncome(new BigDecimal("500000"));
        loanApp.setLoanType("Personal Loan");
    }

    // --- 1. TEST ALL INTEREST RATE BRANCHES (The "Big Green Block") ---

    @Test
    void testInterestRate_AllPaths() throws Exception {
        // Personal Loan Brackets
        runInterestTest("Personal Loan", "6000000", 800, "10.0");
        runInterestTest("Personal Loan", "4500000", 800, "11.0");
        runInterestTest("Personal Loan", "1000000", 800, "12.0");

        // Home Loan Brackets (Every single else-if)
        runInterestTest("Home Loan", "6000000", 800, "7.0");
        runInterestTest("Home Loan", "4500000", 800, "7.5");
        runInterestTest("Home Loan", "3500000", 800, "8.0");
        runInterestTest("Home Loan", "2500000", 800, "8.5");
        runInterestTest("Home Loan", "1000000", 800, "9.0");

        // Education Loan Brackets
        runInterestTest("Education Loan", "6000000", 800, "7.0");
        runInterestTest("Education Loan", "4500000", 800, "7.5");
        runInterestTest("Education Loan", "3500000", 800, "8.0");
        runInterestTest("Education Loan", "1000000", 800, "8.5");

        // Null/Low CIBIL check
        runInterestTest("Personal Loan", "1000000", 600, "13.0"); // 12 + 1
        runInterestTest("Personal Loan", "1000000", null, "12.0"); // No impact if null
    }

    private void runInterestTest(String type, String income, Integer cibil, String expectedRate) throws Exception {
        LoanApplicationRequest req = new LoanApplicationRequest();
        req.setLoanType(type);
        req.setAnnualIncome(new BigDecimal(income));
        req.setCibilScore(cibil);
        req.setAmount(new BigDecimal("1000"));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByRole(Role.ADMIN)).thenReturn(Collections.emptyList());
        when(loanApplicationRepository.save(any(LoanApplication.class))).thenAnswer(inv -> {
            LoanApplication s = inv.getArgument(0);
            s.setId(10L);
            s.setStatus(LoanStatus.SUBMITTED);
            return s;
        });

        loanService.submitLoanApplication(1L, req);

        verify(loanApplicationRepository, atLeastOnce()).save(argThat(app ->
                app.getInterestRate() != null && app.getInterestRate().compareTo(new BigDecimal(expectedRate)) == 0
        ));
        clearInvocations(loanApplicationRepository, userRepository);
    }

    // --- 2. TEST STATUS UPDATES (APPROVED, REJECTED, INVALID) ---

    @Test
    void testUpdateStatus_Approved() throws Exception {
        StatusUpdateRequest req = new StatusUpdateRequest();
        req.setStatus("approved");

        when(loanApplicationRepository.findById(10L)).thenReturn(Optional.of(loanApp));
        when(loanApplicationRepository.save(any())).thenReturn(loanApp);

        loanService.updateLoanApplicationStatus(10L, req);
        assertEquals(LoanStatus.APPROVED, loanApp.getStatus());
        assertNotNull(loanApp.getApprovedDate());
    }

    @Test
    void testUpdateStatus_Rejected() throws Exception {
        StatusUpdateRequest req = new StatusUpdateRequest();
        req.setStatus("rejected");
        req.setRejectionReason("Bad history");

        when(loanApplicationRepository.findById(10L)).thenReturn(Optional.of(loanApp));
        when(loanApplicationRepository.save(any())).thenReturn(loanApp);

        loanService.updateLoanApplicationStatus(10L, req);
        assertEquals(LoanStatus.REJECTED, loanApp.getStatus());
        assertEquals("Bad history", loanApp.getRejectionReason());
    }

    @Test
    void testUpdateStatus_Invalid() {
        StatusUpdateRequest req = new StatusUpdateRequest();
        req.setStatus("PENDING"); // Only APPROVED/REJECTED handled in your if/else

        when(loanApplicationRepository.findById(10L)).thenReturn(Optional.of(loanApp));
        assertThrows(Exception.class, () -> loanService.updateLoanApplicationStatus(10L, req));
    }

    // --- 3. TEST LIST & FIND METHODS (Exception coverage) ---

    @Test
    void testGetLoanApplicationsByStatus_InvalidEnum() {
        // This hits the valueOf() exception branch
        assertThrows(IllegalArgumentException.class, () ->
                loanService.getLoanApplicationsByStatus("INVALID_STATUS_NAME")
        );
    }

    @Test
    void testGetAllLoanApplications() {
        when(loanApplicationRepository.findAllByOrderBySubmittedDateDesc()).thenReturn(Arrays.asList(loanApp));
        assertEquals(1, loanService.getAllLoanApplications().size());
    }

    @Test
    void testGetLoanApplicationById_NotFound() {
        when(loanApplicationRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> loanService.getLoanApplicationById(99L));
    }

    @Test
    void testGetUserLoanApplications_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> loanService.getUserLoanApplications(1L));
    }
}