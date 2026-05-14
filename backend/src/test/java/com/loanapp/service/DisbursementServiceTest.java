package com.loanapp.service;

import com.loanapp.dto.DisbursementResponse;
import com.loanapp.entity.*;
import com.loanapp.repository.DisbursementRepository;
import com.loanapp.repository.LoanApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DisbursementServiceTest {

    @Mock
    private DisbursementRepository disbursementRepository;

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private DisbursementService disbursementService;

    private LoanApplication loanApplication;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        loanApplication = new LoanApplication();
        loanApplication.setId(1L);
        loanApplication.setUser(user);
        loanApplication.setAmount(new BigDecimal("10000.0"));
        loanApplication.setStatus(LoanStatus.APPROVED);
        loanApplication.setSubmittedDate(LocalDateTime.now().minusDays(5));
        loanApplication.setApprovedDate(LocalDateTime.now().minusDays(1));
    }

    @Test
    void testDisburseAmount_Success() throws Exception {
        // Arrange
        when(loanApplicationRepository.findById(1L)).thenReturn(Optional.of(loanApplication));

        // Mock the check for existing disbursement (must return empty to proceed)
        when(disbursementRepository.findByLoanApplicationId(1L)).thenReturn(Optional.empty());

        // Mock the loop for unique transaction reference generation
        when(disbursementRepository.findByTransactionReference(anyString())).thenReturn(Optional.empty());

        when(disbursementRepository.save(any(Disbursement.class))).thenAnswer(i -> {
            Disbursement savedDisbursement = i.getArgument(0);
            savedDisbursement.setId(100L); // Give it a mock ID
            return savedDisbursement;
        });

        // Act
        DisbursementResponse response = disbursementService.disburseAmount(1L, "admin");

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getLoanApplicationId());
        assertNotNull(response.getTransactionReference());
        assertEquals(LoanStatus.DISBURSED, loanApplication.getStatus());

        // Verify Interactions
        verify(loanApplicationRepository, times(1)).save(loanApplication);
        verify(notificationService, times(1)).createNotification(eq(user), anyString(), eq(1L));
    }

    @Test
    void testDisburseAmount_LoanNotFound() {
        when(loanApplicationRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () ->
                disbursementService.disburseAmount(1L, "admin")
        );
        assertEquals("Loan application not found", exception.getMessage());
    }

    @Test
    void testDisburseAmount_LoanNotApproved() {
        loanApplication.setStatus(LoanStatus.SUBMITTED);
        when(loanApplicationRepository.findById(1L)).thenReturn(Optional.of(loanApplication));

        Exception exception = assertThrows(Exception.class, () ->
                disbursementService.disburseAmount(1L, "admin")
        );
        assertTrue(exception.getMessage().contains("must be APPROVED"));
    }

    @Test
    void testDisburseAmount_AlreadyDisbursed() {
        when(loanApplicationRepository.findById(1L)).thenReturn(Optional.of(loanApplication));
        // Mock that a record ALREADY exists
        when(disbursementRepository.findByLoanApplicationId(1L)).thenReturn(Optional.of(new Disbursement()));

        Exception exception = assertThrows(Exception.class, () ->
                disbursementService.disburseAmount(1L, "admin")
        );
        assertEquals("Loan application has already been disbursed", exception.getMessage());
    }

    @Test
    void testGetDisbursementByLoanId_NotFound() {
        when(disbursementRepository.findByLoanApplicationId(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () ->
                disbursementService.getDisbursementByLoanId(1L)
        );
        assertEquals("Disbursement record not found for this loan application", exception.getMessage());
    }
}