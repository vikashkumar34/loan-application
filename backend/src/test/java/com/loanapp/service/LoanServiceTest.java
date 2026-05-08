package com.loanapp.service;

import com.loanapp.dto.LoanApplicationRequest;
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
    }

    @Test
    void testSubmitLoanApplication_Success() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(loanApplicationRepository.save(any(LoanApplication.class))).thenReturn(loanApplication);

        LoanApplication result = loanService.submitLoanApplication(loanApplicationRequest);

        assertNotNull(result);
        assertEquals(LoanStatus.SUBMITTED, result.getStatus());
        verify(loanApplicationRepository, times(1)).save(any(LoanApplication.class));
    }

    @Test
    void testSubmitLoanApplication_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> loanService.submitLoanApplication(loanApplicationRequest));

        assertEquals("User not found with id: 1", exception.getMessage());
    }

    @Test
    void testGetLoanApplicationsByUserId() {
        when(loanApplicationRepository.findByUserId(1L)).thenReturn(Collections.singletonList(loanApplication));
        List<LoanApplication> result = loanService.getLoanApplicationsByUserId(1L);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testGetAllLoanApplications() {
        when(loanApplicationRepository.findAll()).thenReturn(Collections.singletonList(loanApplication));
        List<LoanApplication> result = loanService.getAllLoanApplications();
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testApproveLoanApplication_Success() throws Exception {
        when(loanApplicationRepository.findById(1L)).thenReturn(Optional.of(loanApplication));
        when(loanApplicationRepository.save(any(LoanApplication.class))).thenReturn(loanApplication);

        LoanApplication result = loanService.approveLoanApplication(1L);

        assertNotNull(result);
        assertEquals(LoanStatus.APPROVED, result.getStatus());
    }

    @Test
    void testApproveLoanApplication_NotFound() {
        when(loanApplicationRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> loanService.approveLoanApplication(1L));

        assertEquals("Loan application not found with id: 1", exception.getMessage());
    }

    @Test
    void testRejectLoanApplication_Success() throws Exception {
        when(loanApplicationRepository.findById(1L)).thenReturn(Optional.of(loanApplication));
        when(loanApplicationRepository.save(any(LoanApplication.class))).thenReturn(loanApplication);

        LoanApplication result = loanService.rejectLoanApplication(1L, "Reason");

        assertNotNull(result);
        assertEquals(LoanStatus.REJECTED, result.getStatus());
        assertEquals("Reason", result.getRejectionReason());
    }

    @Test
    void testRejectLoanApplication_NotFound() {
        when(loanApplicationRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> loanService.rejectLoanApplication(1L, "Reason"));

        assertEquals("Loan application not found with id: 1", exception.getMessage());
    }
}
