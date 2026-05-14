package com.loanapp.service;

import com.loanapp.dto.AdminActionLog;
import com.loanapp.entity.Disbursement;
import com.loanapp.entity.LoanApplication;
import com.loanapp.entity.LoanStatus;
import com.loanapp.entity.User;
import com.loanapp.repository.DisbursementRepository;
import com.loanapp.repository.LoanApplicationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    @Mock
    private DisbursementRepository disbursementRepository;

    @InjectMocks
    private AdminService adminService;

    @Test
    void testGetAdminHistory() {
        // Arrange
        User user = new User();
        user.setFullName("Test User");

        LoanApplication approvedApp = new LoanApplication();
        approvedApp.setId(1L);
        approvedApp.setUser(user);
        approvedApp.setStatus(LoanStatus.APPROVED);
        approvedApp.setApprovedDate(LocalDateTime.now().minusDays(1));

        LoanApplication disbursedApp = new LoanApplication();
        disbursedApp.setId(2L);
        disbursedApp.setUser(user);

        Disbursement disbursement = new Disbursement();
        disbursement.setLoanApplication(disbursedApp);
        disbursement.setDisbursedByAdmin("admin");
        disbursement.setDisbursedDate(LocalDateTime.now());

        when(loanApplicationRepository.findByStatus(LoanStatus.APPROVED)).thenReturn(Arrays.asList(approvedApp));
        when(disbursementRepository.findAll()).thenReturn(Arrays.asList(disbursement));

        // Act
        List<AdminActionLog> history = adminService.getAdminHistory();

        // Assert
        assertEquals(2, history.size());
        // Verify the list is sorted by timestamp descending (most recent first)
        assertEquals("DISBURSED", history.get(0).getAction());
        assertEquals("APPROVED", history.get(1).getAction());
    }
}
