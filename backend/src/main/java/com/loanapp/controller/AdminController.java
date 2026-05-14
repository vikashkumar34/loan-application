package com.loanapp.controller;

import com.loanapp.dto.AdminActionLog;
import com.loanapp.dto.LoanApplicationResponse;
import com.loanapp.dto.StatusUpdateRequest;
import com.loanapp.dto.DisbursementResponse;
import com.loanapp.service.AdminService;
import com.loanapp.service.LoanService;
import com.loanapp.service.DisbursementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private LoanService loanService;

    @Autowired
    private DisbursementService disbursementService;

    @Autowired
    private AdminService adminService;

    @GetMapping("/history")
    public ResponseEntity<List<AdminActionLog>> getAdminHistory() {
        List<AdminActionLog> history = adminService.getAdminHistory();
        return ResponseEntity.ok(history);
    }

    @GetMapping("/loans")
    public ResponseEntity<List<LoanApplicationResponse>> getAllLoanApplications() {
        return ResponseEntity.ok(loanService.getAllLoanApplications());
    }

    @GetMapping("/loans/{id}")
    public ResponseEntity<LoanApplicationResponse> getLoanApplication(@PathVariable Long id) throws Exception {
        return ResponseEntity.ok(loanService.getLoanApplicationById(id));
    }

    @GetMapping("/loans/status/{status}")
    public ResponseEntity<List<LoanApplicationResponse>> getLoanApplicationsByStatus(@PathVariable String status) throws Exception {
        return ResponseEntity.ok(loanService.getLoanApplicationsByStatus(status));
    }

    @PutMapping("/loans/{id}/status")
    public ResponseEntity<LoanApplicationResponse> updateLoanApplicationStatus(
            @PathVariable Long id,
            @RequestBody StatusUpdateRequest request) throws Exception {
        return ResponseEntity.ok(loanService.updateLoanApplicationStatus(id, request));
    }

    @PostMapping("/loans/{id}/disburse")
    public ResponseEntity<DisbursementResponse> disburseAmount(@PathVariable Long id) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String adminUsername = auth.getName();
        return ResponseEntity.ok(disbursementService.disburseAmount(id, adminUsername));
    }

    @GetMapping("/loans/{id}/disbursement")
    public ResponseEntity<DisbursementResponse> getDisbursementDetails(@PathVariable Long id) throws Exception {
        return ResponseEntity.ok(disbursementService.getDisbursementByLoanId(id));
    }
}
