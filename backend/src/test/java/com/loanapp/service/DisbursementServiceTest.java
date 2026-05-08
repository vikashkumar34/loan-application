package com.loanapp.service;

import com.loanapp.dto.DisbursementResponse;
import com.loanapp.entity.Disbursement;
import com.loanapp.entity.LoanApplication;
import com.loanapp.entity.LoanStatus;
import com.loanapp.entity.User;
import com.loanapp.entity.Role;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DisbursementServiceTest {

    @Mock
    private DisbursementRepository disbursementRepository;

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    @InjectMocks
    private DisbursementService disbursementService;

    private LoanApplication loanApplication;

    @BeforeEach
    void setUp() {
        User user = new User(1L, "testuser", "password", "test@example.com", "Test User", Role.USER, null, null);
        loanApplication = new LoanApplication();
        loanApplication.setId(1L);
        loanApplication.setUser(user);
        loanApplication.setAmount(new BigDecimal("10000.0"));
        loanApplication.setTermMonths(12);
        loanApplication.setPurpose("Personal Loan");
        loanApplication.setStatus(LoanStatus.APPROVED);
        loanApplication.setBankAccountNumber("1234567890");
        loanApplication.setIfscCode("IFSC1234");
        loanApplication.setSubmittedDate(LocalDateTime.now().minusDays(5));
        loanApplication.setApprovedDate(LocalDateTime.now().minusDays(1));
    }

    @Test
    void testDisburseAmount_Success() throws Exception {
        when(loanApplicationRepository.findById(1L)).thenReturn(Optional.of(loanApplication));
        when(disbursementRepository.findByTransactionReference(anyString())).thenReturn(Optional.empty());
        when(disbursementRepository.save(any(Disbursement.class))).thenAnswer(i -> {
            Disbursement savedDisbursement = i.getArgument(0);
            savedDisbursement.setId(1L);
            return savedDisbursement;
        });

        DisbursementResponse response = disbursementService.disburseAmount(1L, "admin");

        assertNotNull(response);
        assertEquals(1L, response.getLoanApplicationId());
        assertNotNull(response.getTransactionReference());
        verify(loanApplicationRepository, times(1)).save(loanApplication);
        assertEquals(LoanStatus.DISBURSED, loanApplication.getStatus());
    }

    @Test
    void testDisburseAmount_LoanNotFound() {
        when(loanApplicationRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> disbursementService.disburseAmount(1L, "admin"));

        assertEquals("Loan application not found", exception.getMessage());
    }

    @Test
    void testDisburseAmount_LoanNotApproved() {
        loanApplication.setStatus(LoanStatus.SUBMITTED);
        when(loanApplicationRepository.findById(1L)).thenReturn(Optional.of(loanApplication));

        Exception exception = assertThrows(Exception.class, () -> disbursementService.disburseAmount(1L, "admin"));

        assertEquals("Loan application must be APPROVED before disbursement. Current status: " + LoanStatus.SUBMITTED, exception.getMessage());
    }

    @Test
    void testDisburseAmount_AlreadyDisbursed() {
        when(loanApplicationRepository.findById(1L)).thenReturn(Optional.of(loanApplication));
        when(disbursementRepository.findByLoanApplicationId(1L)).thenReturn(Optional.of(new Disbursement()));

        Exception exception = assertThrows(Exception.class, () -> disbursementService.disburseAmount(1L, "admin"));

        assertEquals("Loan application has already been disbursed", exception.getMessage());
    }

    @Test
    void testGetDisbursementByLoanId_Success() throws Exception {
        Disbursement disbursement = new Disbursement(1L, loanApplication, "123", null, null, null, "admin", null);
        when(disbursementRepository.findByLoanApplicationId(1L)).thenReturn(Optional.of(disbursement));

        DisbursementResponse response = disbursementService.getDisbursementByLoanId(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void testGetDisbursementByLoanId_NotFound() {
        when(disbursementRepository.findByLoanApplicationId(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> disbursementService.getDisbursementByLoanId(1L));

        assertEquals("Disbursement record not found for this loan application", exception.getMessage());
    }
}
