package com.loanapp.service;

import com.loanapp.dto.LoanApplicationRequest;
import com.loanapp.dto.LoanApplicationResponse;
import com.loanapp.dto.StatusUpdateRequest;
import com.loanapp.entity.LoanApplication;
import com.loanapp.entity.LoanStatus;
import com.loanapp.entity.User;
import com.loanapp.entity.Role;
import com.loanapp.repository.LoanApplicationRepository;
import com.loanapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    @InjectMocks
    private LoanService loanService;

    private User user;
    private LoanApplication loanApplication;
    private LoanApplicationRequest loanApplicationRequest;
    private LoanApplicationResponse loanApplicationResponse;

    @BeforeEach
    void setUp() {
        user = new User(1L, "testuser", "password", "test@example.com", "Test User", Role.USER, null, null);

        loanApplicationRequest = new LoanApplicationRequest(new BigDecimal("10000.0"), 12, "Personal Loan", "1234567890", "IFSC1234");

        loanApplication = new LoanApplication();
        loanApplication.setId(1L);
        loanApplication.setUser(user);
        loanApplication.setAmount(new BigDecimal("10000.0"));
        loanApplication.setTermMonths(12);
        loanApplication.setPurpose("Personal Loan");
        loanApplication.setStatus(LoanStatus.SUBMITTED);
        loanApplication.setBankAccountNumber("1234567890");
        loanApplication.setIfscCode("IFSC1234");
        loanApplication.setSubmittedDate(LocalDateTime.now());

        loanApplicationResponse = new LoanApplicationResponse(
            loanApplication.getId(),
            loanApplication.getUser().getId(),
            loanApplication.getAmount(),
            loanApplication.getTermMonths(),
            loanApplication.getPurpose(),
            loanApplication.getBankAccountNumber(),
            loanApplication.getIfscCode(),
            loanApplication.getStatus().toString(),
            loanApplication.getSubmittedDate(),
            null, null, null, null, null
        );
    }

    @Test
    void testSubmitLoanApplication_Success() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(loanApplicationRepository.findTopByUserAndStatusInOrderBySubmittedDateDesc(eq(user), anyList())).thenReturn(Optional.empty());
        when(loanApplicationRepository.save(any(LoanApplication.class))).thenReturn(loanApplication);

        LoanApplicationResponse result = loanService.submitLoanApplication(1L, loanApplicationRequest);

        assertNotNull(result);
        assertEquals(LoanStatus.SUBMITTED.toString(), result.getStatus());
        verify(loanApplicationRepository, times(1)).save(any(LoanApplication.class));
    }

    @Test
    void testSubmitLoanApplication_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> loanService.submitLoanApplication(1L, loanApplicationRequest));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testSubmitLoanApplication_ExistingPendingOrApprovedLoan() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(loanApplicationRepository.findTopByUserAndStatusInOrderBySubmittedDateDesc(eq(user), anyList()))
            .thenReturn(Optional.of(loanApplication));

        Exception exception = assertThrows(Exception.class, () -> loanService.submitLoanApplication(1L, loanApplicationRequest));

        assertEquals("User cannot submit a new loan while an existing PENDING or APPROVED application exists", exception.getMessage());
        verify(loanApplicationRepository, never()).save(any(LoanApplication.class));
    }

    @Test
    void testGetUserLoanApplications_Success() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(loanApplicationRepository.findByUser(user)).thenReturn(Collections.singletonList(loanApplication));

        List<LoanApplicationResponse> result = loanService.getUserLoanApplications(1L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(loanApplicationResponse.getId(), result.get(0).getId());
    }

    @Test
    void testGetUserLoanApplications_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> loanService.getUserLoanApplications(1L));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testGetAllLoanApplications_Success() {
        when(loanApplicationRepository.findAllByOrderBySubmittedDateDesc()).thenReturn(Collections.singletonList(loanApplication));

        List<LoanApplicationResponse> result = loanService.getAllLoanApplications();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(loanApplicationResponse.getId(), result.get(0).getId());
    }

    @Test
    void testApproveLoanApplication_Success() throws Exception {
        loanApplication.setStatus(LoanStatus.SUBMITTED); // Ensure it's in a state that can be approved
        when(loanApplicationRepository.findById(1L)).thenReturn(Optional.of(loanApplication));
        when(loanApplicationRepository.save(any(LoanApplication.class))).thenReturn(loanApplication);

        LoanApplicationResponse result = loanService.updateLoanApplicationStatus(1L, new StatusUpdateRequest(LoanStatus.APPROVED.toString(), null));

        assertNotNull(result);
        assertEquals(LoanStatus.APPROVED.toString(), result.getStatus());
        assertNotNull(loanApplication.getApprovedDate());
    }

    @Test
    void testApproveLoanApplication_NotFound() {
        when(loanApplicationRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> loanService.updateLoanApplicationStatus(1L, new StatusUpdateRequest(LoanStatus.APPROVED.toString(), null)));

        assertEquals("Loan application not found", exception.getMessage());
    }

    @Test
    void testRejectLoanApplication_Success() throws Exception {
        loanApplication.setStatus(LoanStatus.SUBMITTED); // Ensure it's in a state that can be rejected
        when(loanApplicationRepository.findById(1L)).thenReturn(Optional.of(loanApplication));
        when(loanApplicationRepository.save(any(LoanApplication.class))).thenReturn(loanApplication);

        LoanApplicationResponse result = loanService.updateLoanApplicationStatus(1L, new StatusUpdateRequest(LoanStatus.REJECTED.toString(), "Reason"));

        assertNotNull(result);
        assertEquals(LoanStatus.REJECTED.toString(), result.getStatus());
        assertEquals("Reason", loanApplication.getRejectionReason());
        assertNotNull(loanApplication.getRejectedDate());
    }

    @Test
    void testRejectLoanApplication_NotFound() {
        when(loanApplicationRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> loanService.updateLoanApplicationStatus(1L, new StatusUpdateRequest(LoanStatus.REJECTED.toString(), "Reason")));

        assertEquals("Loan application not found", exception.getMessage());
    }

    @Test
    void testUpdateLoanApplicationStatus_InvalidTransitionFromDisbursed() {
        loanApplication.setStatus(LoanStatus.DISBURSED);
        when(loanApplicationRepository.findById(1L)).thenReturn(Optional.of(loanApplication));

        Exception exception = assertThrows(Exception.class, () -> loanService.updateLoanApplicationStatus(1L, new StatusUpdateRequest(LoanStatus.APPROVED.toString(), null)));

        assertEquals("Cannot change status of a disbursed application", exception.getMessage());
    }

    @Test
    void testUpdateLoanApplicationStatus_InvalidTransitionFromRejected() {
        loanApplication.setStatus(LoanStatus.REJECTED);
        when(loanApplicationRepository.findById(1L)).thenReturn(Optional.of(loanApplication));

        Exception exception = assertThrows(Exception.class, () -> loanService.updateLoanApplicationStatus(1L, new StatusUpdateRequest(LoanStatus.APPROVED.toString(), null)));

        assertEquals("Cannot change status of a rejected application", exception.getMessage());
    }

    @Test
    void testGetLoanApplicationById_Success() throws Exception {
        when(loanApplicationRepository.findById(1L)).thenReturn(Optional.of(loanApplication));
        LoanApplicationResponse result = loanService.getLoanApplicationById(1L);
        assertNotNull(result);
        assertEquals(loanApplicationResponse.getId(), result.getId());
    }

    @Test
    void testGetLoanApplicationById_NotFound() {
        when(loanApplicationRepository.findById(1L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(Exception.class, () -> loanService.getLoanApplicationById(1L));
        assertEquals("Loan application not found", exception.getMessage());
    }

    @Test
    void testGetLoanApplicationsByStatus_Success() throws Exception {
        when(loanApplicationRepository.findByStatus(LoanStatus.SUBMITTED)).thenReturn(Collections.singletonList(loanApplication));
        List<LoanApplicationResponse> result = loanService.getLoanApplicationsByStatus(LoanStatus.SUBMITTED.toString());
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(loanApplicationResponse.getId(), result.get(0).getId());
    }

    @Test
    void testGetLoanApplicationsByStatus_InvalidStatus() {
        Exception exception = assertThrows(Exception.class, () -> loanService.getLoanApplicationsByStatus("INVALID_STATUS"));
        assertEquals("Invalid loan status: INVALID_STATUS", exception.getMessage());
    }
}
